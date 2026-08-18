package io.bashpsk.emptylibs.pdfviewer.layout

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.util.fastMap
import io.bashpsk.emptylibs.lrucachemanager.manager.EmptyCacheManager
import io.bashpsk.emptylibs.pdfviewer.info.AndroidPdfInfo
import io.bashpsk.emptylibs.pdfviewer.info.PdfInfo
import io.bashpsk.emptylibs.pdfviewer.page.PdfPage
import io.bashpsk.emptylibs.pdfviewer.source.PdfSource
import io.bashpsk.emptylibs.pdfviewer.utils.LOG_TAG
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

/**
 * Remembers and creates a [PdfViewerState] for managing a PDF document.
 *
 * @param source The [PdfSource] of the PDF file.
 * @param cacheSize The maximum number of page bitmaps to keep in memory.
 * @return A [PdfViewerState] instance.
 */
@Composable
fun rememberPdfViewerState(
    source: PdfSource,
    cacheSize: Int = 20
): PdfViewerState {

    val context = LocalContext.current
    val density = LocalDensity.current

    val state = retain(source, density) {
        PdfViewerState(context = context, density = density, source = source)
    }

    RetainedEffect(cacheSize) {

        state.cacheManager.resize(maxSize = cacheSize)

        onRetire { }
    }

    return state
}

/**
 * State class for the PDF viewer, managing document loading, page list, and caching.
 *
 * @property context The application context.
 * @property density The display density.
 * @property source The source of the PDF document.
 */
@Stable
class PdfViewerState internal constructor(
    private val context: Context,
    private val density: Density,
    private val source: PdfSource
) {

    /**
     * Scope for managing background loading and rendering tasks.
     */
    private val coroutineScope = CoroutineScope(context = SupervisorJob() + Dispatchers.IO)

    /**
     * The internal PDF document information.
     */
    private var pdfInfo by mutableStateOf<PdfInfo?>(null)

    /**
     * Cache manager for PDF page bitmaps.
     */
    internal val cacheManager = EmptyCacheManager<String, ImageBitmap>(
        maxSize = 20,
        onEntryRemoved = { _, _, _, bitmap ->

            bitmap.asAndroidBitmap().recycle()
        }
    )

    /**
     * The list of [PdfPage]s in the document.
     */
    var pdfPages by mutableStateOf<ImmutableList<PdfPage>>(persistentListOf())
        private set

    /**
     * The current loading state of the PDF document.
     */
    var loadingState by mutableStateOf<PdfLoadingState>(PdfLoadingState.Init)
        private set

    init {

        setSourceLoad()
    }

    /**
     * Initiates the asynchronous loading of the PDF document and its pages.
     */
    private fun setSourceLoad() = coroutineScope.launch {

        try {

            pdfInfo = AndroidPdfInfo.create(context = context, density = density, source = source)

            pdfInfo?.let { info ->

                val newPdfPages = List(size = info.pageCount) { index ->

                    async {

                        PdfPage(
                            pageRenderer = info.pageRenderer,
                            cache = cacheManager,
                            ratio = info.getPageAspectRatio(index = index),
                            index = index
                        ).apply {

                            onLoad()
                            loadingState = PdfLoadingState.Loading(
                                totalPage = info.pageCount,
                                loadedPage = index + 1
                            )
                        }
                    }
                }.fastMap { job -> job.await() }.toImmutableList()

                pdfPages = newPdfPages
                loadingState = PdfLoadingState.Ready
            }
        } catch (exception: Exception) {

            currentCoroutineContext().ensureActive()
            Log.e(LOG_TAG, exception.message, exception)
            loadingState = PdfLoadingState.Error(exception = exception)
        }
    }

    /**
     * Disposes of the state, cancelling background tasks and clearing caches.
     */
    fun onDispose() {

        coroutineScope.cancel()
        pdfPages.forEach { page -> page.onDispose() }
        pdfInfo?.close()
        cacheManager.evictAll()
    }
}