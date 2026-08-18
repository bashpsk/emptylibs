package io.bashpsk.emptylibs.pdfviewer.page

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toIntRect
import io.bashpsk.emptylibs.imageutils.extension.toIntSize
import io.bashpsk.emptylibs.imageutils.extension.toSize
import io.bashpsk.emptylibs.lrucachemanager.manager.EmptyCacheManager
import io.bashpsk.emptylibs.pdfviewer.renderer.PageRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Represents a single page in a PDF document. Handles rendering, caching, and fragment loading for
 * zoom.
 *
 * @property pageRenderer The renderer used to generate page bitmaps.
 * @property cache The cache manager for storing rendered page bitmaps.
 * @property ratio The aspect ratio (height / width) of the page.
 * @property index The zero-based index of the page in the document.
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@Stable
class PdfPage internal constructor(
    private val pageRenderer: PageRenderer,
    private val cache: EmptyCacheManager<String, ImageBitmap>,
    val ratio: Float,
    val index: Int
) {

    /**
     * Scope for managing asynchronous rendering tasks.
     */
    private val coroutineScope = CoroutineScope(context = SupervisorJob() + Dispatchers.Default)

    /**
     * Indicates whether the page is currently loaded and active in the layout.
     */
    private val isLoaded = MutableStateFlow(false)

    /**
     * The layout size of the page in pixels (unscaled).
     */
    internal val layoutSize = MutableStateFlow(IntSize.Zero)

    /**
     * The portion of the page that is currently visible on screen (unscaled layout units).
     */
    private val visibleRect = MutableStateFlow<IntRect?>(null)

    /**
     * Current zoom level applied to the page.
     */
    private val zoomScale = MutableStateFlow(1.0F)

    /**
     * A [StateFlow] providing the base [ImageBitmap] for the full page.
     */
    internal val imageBitmap = combine(
        flow = isLoaded,
        flow2 = layoutSize
    ) { loaded, newLayoutSize ->

        flow {

            if (!loaded || newLayoutSize.width <= 0 || newLayoutSize.height <= 0) {

                emit(value = null)
                return@flow
            }

            val cacheKey = "${index}_${newLayoutSize.width}_${newLayoutSize.height}"

            cache[cacheKey]?.let { savedBitmap ->

                emit(value = savedBitmap)
                if (savedBitmap.toIntSize() == newLayoutSize) return@flow
            }

            val imageBitmap = pageRenderer.renderPage(index = index, pageSize = newLayoutSize)

            emit(value = imageBitmap)
            imageBitmap?.let { bitmap -> cache[cacheKey] = bitmap }
        }
    }.flatMapLatest { bitmapFlow ->

        bitmapFlow
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 3000),
        initialValue = null
    )

    /**
     * A [StateFlow] providing a [PdfScaledPage] fragment for high-resolution zoom rendering.
     */
    internal val pdfScaledPage = isLoaded.flatMapLatest { loaded ->

        combine(flow = visibleRect, flow2 = zoomScale) { rect, zoom ->

            rect to zoom
        }.debounce { (rect, _) ->

            if (rect == null || !loaded) 0 else 200
        }.flatMapLatest { (rect, zoom) ->

            flow {

                if (!loaded || rect == null || rect.width <= 0 || rect.height <= 0 || zoom <= 1.2F) {

                    emit(value = null)
                    return@flow
                }

                val bitmapSize = imageBitmap.value?.toSize() ?: return@flow
                if (layoutSize.value == IntSize.Zero) return@flow

                if (bitmapSize.width >= layoutSize.value.width * zoom) {

                    emit(value = null)
                    return@flow
                }

                val scaledPage = pageRenderer.renderPageFragment(
                    index = index,
                    pageSize = layoutSize.value.toIntRect(),
                    scaledFragment = rect,
                    scale = zoom
                )

                emit(value = scaledPage)
            }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 3000),
        initialValue = null
    )

    /**
     * Updates the visible region and zoom scale of the page.
     *
     * @param visibleFragment The visible rectangle in layout coordinates.
     * @param zoom The current zoom level.
     */
    internal fun updateVisibleLayout(visibleFragment: IntRect?, zoom: Float) {

        coroutineScope.launch(context = Dispatchers.Default) {

            visibleRect.emit(value = visibleFragment)
            zoomScale.emit(value = zoom)
        }
    }

    /**
     * Updates the layout size of the page.
     *
     * @param size The new size in pixels.
     */
    internal fun updateLayoutSize(size: IntSize) {

        coroutineScope.launch(context = Dispatchers.Default) {

            layoutSize.emit(value = size)
        }
    }

    /**
     * Called when the page becomes visible or needs to start loading.
     */
    internal fun onLoad() {

        coroutineScope.launch(context = Dispatchers.Default) {

            isLoaded.emit(value = true)
        }
    }

    /**
     * Called when the page is disposed or removed from the layout.
     */
    internal fun onDispose() {

        coroutineScope.launch(context = Dispatchers.Default) {

            visibleRect.emit(value = null)
            isLoaded.emit(value = false)
        }
    }
}