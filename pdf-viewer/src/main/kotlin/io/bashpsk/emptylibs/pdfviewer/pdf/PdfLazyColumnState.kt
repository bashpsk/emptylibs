package io.bashpsk.emptylibs.pdfviewer.pdf

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.bashpsk.emptylibs.formatter.format.toRoundedDecimal
import io.bashpsk.emptylibs.formatter.resolution.ResolutionType
import io.bashpsk.emptylibs.gestureui.transform.TransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.rememberTransformableGesturesState
import io.bashpsk.emptylibs.imageutils.extension.toSize
import io.bashpsk.emptylibs.lrucachemanager.manager.EmptyCacheManager
import io.bashpsk.emptylibs.pdfviewer.page.PdfPageData
import io.bashpsk.emptylibs.pdfviewer.page.PdfScaledPageData
import io.bashpsk.emptylibs.pdfviewer.search.getSearchRectList
import io.bashpsk.emptylibs.pdfviewer.utils.LOG_TAG
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Creates and remembers a [PdfLazyColumnState] instance.
 *
 * @param source The source of the PDF file.
 * @param cacheSize The size of the bitmap cache.
 * @param initialZoom The initial zoom level.
 * @param enableZoom Whether to enable zoom gestures.
 * @param enableDoubleTapZoom Whether to enable double-tap to zoom.
 * @param zoomRange The allowed zoom range.
 * @return A new [PdfLazyColumnState] instance.
 */
@Composable
fun rememberPdfLazyColumnState(
    source: PdfSource,
    cacheSize: Int = 10,
    initialZoom: Float = 1.0F,
    enableZoom: Boolean = true,
    enableDoubleTapZoom: Boolean = true,
    zoomRange: ClosedFloatingPointRange<Float> = 0.5F..4.0F
): PdfLazyColumnState {

    val context = LocalContext.current
    val density = LocalDensity.current

    val transformableState = rememberTransformableGesturesState(
        initialZoom = initialZoom,
        enableZoom = enableZoom,
        enableDoubleTapZoom = enableDoubleTapZoom,
        enablePan = true,
        enableRotation = false,
        zoomRange = zoomRange
    )

    val state = retain(density, transformableState) {
        PdfLazyColumnState(density = density, transformable = transformableState)
    }

    RetainedEffect(cacheSize) {

        state.scaledBitmapManager.resize(maxSize = cacheSize)

        onRetire { }
    }

    RetainedEffect(context, source) {

        state.setLoadPdfSource(context = context, source = source)

        onRetire { }
    }

    return state
}

/**
 * A state object that can be hoisted to control and observe scrolling and zooming of a PDF.
 *
 * @param density The density of the display.
 * @param transformable The state for transformable gestures.
 */
@Stable
class PdfLazyColumnState(
    internal val density: Density,
    internal val transformable: TransformableGesturesState
) {

    /**
     * A mutex to ensure thread-safe access to PDF rendering operations.
     */
    private val mutex = Mutex()

    /**
     * A coroutine scope for managing background tasks.
     */
    internal val coroutineScope = CoroutineScope(context = SupervisorJob() + Dispatchers.Default)

    /**
     * The file descriptor of the PDF file.
     */
    private var fileDescriptor by mutableStateOf<ParcelFileDescriptor?>(null)

    /**
     * The PDF renderer instance.
     */
    private var pdfRenderer by mutableStateOf<PdfRenderer?>(null)

    /**
     * The job that loads the PDF file.
     */
    private var fileLoadJob by mutableStateOf<Job?>(null)

    /**
     * The job that performs text search.
     */
    private var textSearchJob by mutableStateOf<Job?>(null)

    /**
     * A map of page data for each page in the PDF.
     */
    internal var pageDataList by mutableStateOf(persistentMapOf<Int, PdfPageData>())
        private set

    /**
     * A cache for high-quality page bitmaps.
     */
    internal val scaledBitmapManager = EmptyCacheManager<Int, PdfScaledPageData>()

    /**
     * The width of the container in pixels.
     */
    internal var containerWidth by mutableIntStateOf(0)

    /**
     * The height of the container in pixels.
     */
    internal var containerHeight by mutableIntStateOf(0)

    /**
     * Whether the search interface is currently expanded and visible.
     */
    internal var isSearchExpanded by mutableStateOf(false)
        private set

    /**
     * The current text query string used for searching within the PDF document.
     */
    internal var searchQuery by mutableStateOf("")
        private set

    /**
     * Sets the PDF source to be loaded.
     *
     * This function cancels any previous loading job, closes the current PDF, and then starts a new
     * coroutine to load the PDF from the given source. The PDF is loaded in the IO dispatcher.
     * After loading, the [pageDataList] is populated with the data of each page.
     *
     * @param context The application context.
     * @param source The [PdfSource] to load.
     * It can be a [PdfSource.URI], [PdfSource.Path], or [PdfSource.Empty].
     */
    internal fun setLoadPdfSource(context: Context, source: PdfSource) {

        fileLoadJob?.cancel()
        close()

        fileLoadJob = coroutineScope.launch(context = Dispatchers.IO) {

            mutex.withLock {

                try {

                    fileDescriptor = when (source) {

                        is PdfSource.Empty -> null

                        is PdfSource.URI -> source.uri?.let { uri ->
                            context.contentResolver.openFileDescriptor(uri, "r")
                        }

                        is PdfSource.Path -> source.path?.let { path ->
                            ParcelFileDescriptor.open(
                                File(path),
                                ParcelFileDescriptor.MODE_READ_ONLY
                            )
                        }
                    }

                    pdfRenderer = fileDescriptor?.let { descriptor -> PdfRenderer(descriptor) }

                    pageDataList = pdfRenderer?.let { renderer ->

                        (0 until renderer.pageCount).associate { pageIndex ->

                            currentCoroutineContext().ensureActive()

                            renderer.openPage(pageIndex).use { page ->

                                page.index to PdfPageData(
                                    page = page.index,
                                    width = page.width,
                                    height = page.height
                                )
                            }
                        }.toPersistentMap()
                    } ?: persistentMapOf()
                } catch (exception: Exception) {

                    currentCoroutineContext().ensureActive()
                    Log.e(LOG_TAG, exception.message, exception)
                }
            }
        }
    }

    /**
     * Renders a low-quality version of a page.
     *
     * @param pageIndex The index of the page to render.
     */
    internal suspend fun setRenderNormalBitmap(
        pageIndex: Int
    ) = withContext(context = Dispatchers.IO) {

        val renderer = pdfRenderer ?: return@withContext
        val pageData = pageDataList[pageIndex] ?: return@withContext

        val targetWidth = containerWidth * transformable.initialZoom.toInt()
        val targetHeight = ((targetWidth.toFloat() / pageData.width) * pageData.height).toInt()

        if ((targetWidth > 0 || targetHeight > 0) && pageData.bitmap == null) getRenderBitmap(
            renderer = renderer,
            pageIndex = pageIndex,
            targetWidth = targetWidth,
            targetHeight = targetHeight
        )?.let { bitmap ->

            pageDataList = pageDataList.put(pageIndex, pageData.copy(bitmap = bitmap))
        }
    }

    /**
     * Gets a high-quality bitmap for a page.
     *
     * If a high-quality bitmap is not available or the quality is not sufficient for the current
     * zoom level, a new one is rendered.
     *
     * @param pageIndex The index of the page.
     * @return The high-quality bitmap, or null if it's not available.
     */
    internal suspend fun getScaledImageBitmap(
        pageIndex: Int
    ): ImageBitmap? = withContext(context = Dispatchers.IO) {

        if (hasNeedScaledBitmap(pageData = getScaledPageData(pageIndex = pageIndex))) {

            val renderer = pdfRenderer ?: return@withContext null
            val pageData = pageDataList[pageIndex] ?: return@withContext null

            val quality = findContentQuality()
            val targetWidth = (containerWidth * quality).toInt().coerceAtMost(
                ResolutionType._4K_UHD.width
            )
            val targetHeight = ((targetWidth.toFloat() / pageData.width) * pageData.height).toInt()

            getRenderBitmap(
                renderer = renderer,
                pageIndex = pageIndex,
                targetWidth = targetWidth,
                targetHeight = targetHeight
            )?.let { bitmap ->

                val newPageData = PdfScaledPageData(
                    page = pageIndex,
                    quality = quality,
                    bitmap = bitmap
                )

                scaledBitmapManager[newPageData.page] = newPageData
            }
        }

        getScaledPageData(pageIndex = pageIndex)?.bitmap
    }

    /**
     * Updates the expansion state of the search interface.
     *
     * @param isExpanded Whether the search bar should be expanded or collapsed.
     */
    internal fun onSearchExpandedChange(isExpanded: Boolean) {
        isSearchExpanded = isExpanded
    }

    /**
     * Updates the current search query string.
     *
     * @param query The new search string to be stored in [searchQuery].
     */
    internal fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    /**
     * Performs a text search across all pages of the PDF.
     *
     * This function updates the current [searchQuery], cancels any ongoing search job, and
     * launches a new coroutine to find matches. It first clears existing search results from
     * [pageDataList]. If the query is not empty, it iterates through all pages using the
     * [pdfRenderer] to identify bounding rectangles of the matching text.
     *
     * @param query The text string to search for within the PDF document.
     */
    internal fun onTextSearch(query: String) {

        searchQuery = query
        textSearchJob?.cancel()

        textSearchJob = coroutineScope.launch(context = Dispatchers.IO) {

            mutex.withLock {

                pageDataList = pageDataList.mapValues { (page, pageData) ->
                    pageData.copy(searchRectList = persistentListOf())
                }.toPersistentMap()

                try {

                    if (searchQuery.isNotEmpty()) pdfRenderer?.let { renderer ->

                        pageDataList = pageDataList.mapValues { (pageIndex, pageData) ->

                            renderer.openPage(pageData.page).use { page ->

                                val newRectList = page.getSearchRectList(query = searchQuery)

                                pageData.copy(searchRectList = newRectList)
                            }
                        }.toPersistentMap()
                    } else return@withLock
                } catch (exception: Exception) {

                    currentCoroutineContext().ensureActive()
                    Log.e(LOG_TAG, exception.message, exception)
                }
            }
        }
    }

    /**
     * Checks if the PDF content is currently zoomed in beyond the initial zoom level.
     *
     * @return True if the content is zoomed in, false otherwise.
     */
    internal fun hasImageZoomed(): Boolean {

        return transformable.zoom in (transformable.initialZoom + 0.25F)..
                transformable.zoomRange.endInclusive
    }

    /**
     * Checks if a new high-quality bitmap is needed for a page.
     *
     * @param pageData The quality page data to check.
     * @return True if a new high-quality bitmap is needed, false otherwise.
     */
    private fun hasNeedScaledBitmap(pageData: PdfScaledPageData?): Boolean {

        return pageData == null
                || scaledBitmapManager.contains(pageData.page).not()
                || pageData.quality !in
                (transformable.zoom - 0.25F)..(transformable.zoom + 0.25F)
    }

    /**
     * Determines the quality for rendering the content based on the current zoom level.
     *
     * @return The rendering quality as a float.
     */
    private fun findContentQuality(): Float {

        return transformable.zoom.toRoundedDecimal(fraction = 1)
    }

    /**
     * Retrieves the quality page data for a specific page from the cache.
     *
     * @param pageIndex The index of the page.
     * @return The quality page data, or null if it's not in the cache.
     */
    private fun getScaledPageData(pageIndex: Int): PdfScaledPageData? {

        return scaledBitmapManager[pageIndex]
    }

    /**
     * Renders a bitmap for a given page with a target width.
     *
     * @param renderer The PDF renderer.
     * @param pageIndex The index of the page to render.
     * @param targetWidth The target width of the bitmap.
     * @return The rendered bitmap, or null if rendering fails.
     */
    private suspend fun getRenderBitmap(
        renderer: PdfRenderer,
        pageIndex: Int,
        targetWidth: Int,
        targetHeight: Int
    ): ImageBitmap? = withContext(context = Dispatchers.IO) {

        return@withContext mutex.withLock {

            renderer.openPage(pageIndex).use { pdfPage ->

                if (targetWidth <= 0 || targetHeight <= 0) return@use null

                val newImageBitmap = ImageBitmap(width = targetWidth, height = targetHeight)

                CanvasDrawScope().draw(
                    density = density,
                    layoutDirection = LayoutDirection.Ltr,
                    canvas = Canvas(image = newImageBitmap),
                    size = newImageBitmap.toSize()
                ) {

                    drawRect(color = Color.White)
                }

                pdfPage.render(
                    newImageBitmap.asAndroidBitmap(),
                    null,
                    null,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )

                newImageBitmap
            }
        }
    }

    /**
     * Closes the PDF renderer and releases all resources.
     */
    internal fun close() {

        fileLoadJob?.cancel()
        textSearchJob?.cancel()
        pdfRenderer?.close()
        fileDescriptor?.close()
        pdfRenderer = null
        fileDescriptor = null
        scaledBitmapManager.evictAll()
    }
}