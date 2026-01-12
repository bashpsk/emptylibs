package io.bashpsk.emptylibs.pdfviewer.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.createBitmap
import io.bashpsk.emptylibs.formatter.format.EmptyFormat
import io.bashpsk.emptylibs.formatter.resolution.ResolutionType
import io.bashpsk.emptylibs.gestureui.transform.TransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.rememberTransformableGesturesState
import io.bashpsk.emptylibs.lrucachemanager.manager.EmptyCacheManager
import io.bashpsk.emptylibs.pdfviewer.utils.LOG_TAG
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
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

    val transformableState = rememberTransformableGesturesState(
        initialZoom = initialZoom,
        enableZoom = enableZoom,
        enableDoubleTapZoom = enableDoubleTapZoom,
        enablePan = true,
        enableRotation = false,
        zoomRange = zoomRange
    )

    val state = retain(transformableState) {
        PdfLazyColumnState(transformable = transformableState)
    }

    LaunchedEffect(cacheSize) {

        state.scaledBitmapManager.resize(maxSize = cacheSize)
    }

    DisposableEffect(context, source) {

        state.setLoadPdfSource(context = context, source = source)

        onDispose { state.close() }
    }

    return state
}

/**
 * A state object that can be hoisted to control and observe scrolling and zooming of a PDF.
 *
 * @param transformable The state for transformable gestures.
 */
@Stable
class PdfLazyColumnState(internal val transformable: TransformableGesturesState) {

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
     * A map of page data for each page in the PDF.
     */
    internal var pageDataList by mutableStateOf(persistentMapOf<Int, PdfPageData>())
        private set

    /**
     * A cache for high-quality page bitmaps.
     */
    internal val scaledBitmapManager = EmptyCacheManager<PdfQualityPageData>(maxSize = 7)

    /**
     * The width of the container in pixels.
     */
    internal var containerWidth by mutableIntStateOf(0)

    /**
     * The height of the container in pixels.
     */
    internal var containerHeight by mutableIntStateOf(0)

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

                    val renderer = pdfRenderer ?: return@withLock

                    pageDataList = (0 until renderer.pageCount).associate { pageIndex ->

                        currentCoroutineContext().ensureActive()

                        renderer.openPage(pageIndex).use { page ->

                            page.index to PdfPageData(
                                page = page.index,
                                width = page.width,
                                height = page.height
                            )
                        }
                    }.toPersistentMap()
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
    internal fun setRenderNormalBitmap(pageIndex: Int) = coroutineScope.launch(Dispatchers.IO) {

        val renderer = pdfRenderer ?: return@launch
        val pageData = pageDataList[pageIndex] ?: return@launch

        val targetWidth = containerWidth * transformable.initialZoom.toInt()
        val targetHeight = ((targetWidth.toFloat() / pageData.width) * pageData.height).toInt()

        ((targetWidth > 0 || targetHeight > 0) && pageData.bitmap == null).takeIf { it }?.run {

            getRenderBitmap(
                renderer = renderer,
                pageIndex = pageIndex,
                targetWidth = targetWidth,
                targetHeight = targetHeight
            )?.let { bitmap ->

                pageDataList = pageDataList.put(pageIndex, pageData.copy(bitmap = bitmap))
            }
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
    ): ImageBitmap? = coroutineScope.async(context = Dispatchers.IO) {

        if (hasNeedHighQualityBitmap(pageData = getQualityPageData(pageIndex = pageIndex))) {

            val renderer = pdfRenderer ?: return@async null
            val pageData = pageDataList[pageIndex] ?: return@async null

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

                val newPageData = PdfQualityPageData(
                    page = pageIndex,
                    quality = quality,
                    bitmap = bitmap
                )

                scaledBitmapManager.add(newPageData.page.toString(), newPageData)
            }
        }

        getQualityPageData(pageIndex = pageIndex)?.bitmap
    }.await()

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
    private fun hasNeedHighQualityBitmap(pageData: PdfQualityPageData?): Boolean {

        return pageData == null
                || scaledBitmapManager.exist(pageData.page.toString()).not()
                || pageData.quality !in
                (transformable.zoom - 0.25F)..(transformable.zoom + 0.25F)
    }

    /**
     * Determines the quality for rendering the content based on the current zoom level.
     *
     * @return The rendering quality as a float.
     */
    private fun findContentQuality(): Float {

        return EmptyFormat.toRoundedDecimal(decimal = transformable.zoom, fraction = 1)
    }

    /**
     * Retrieves the quality page data for a specific page from the cache.
     *
     * @param pageIndex The index of the page.
     * @return The quality page data, or null if it's not in the cache.
     */
    private fun getQualityPageData(pageIndex: Int): PdfQualityPageData? {

        return scaledBitmapManager.get(pageIndex.toString())
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

                val newBitmap = createBitmap(width = targetWidth, height = targetHeight)

                Canvas(newBitmap).apply {

                    drawColor(Color.White.toArgb())
                }

                pdfPage.render(newBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                newBitmap.asImageBitmap()
            }
        }
    }

    /**
     * Closes the PDF renderer and releases all resources.
     */
    internal fun close() {

        fileLoadJob?.cancel()
        pdfRenderer?.close()
        fileDescriptor?.close()
        pdfRenderer = null
        fileDescriptor = null
        scaledBitmapManager.evictAll()
    }
}