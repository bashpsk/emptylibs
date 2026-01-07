package io.bashpsk.emptylibs.pdfviewer.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.createBitmap
import io.bashpsk.emptylibs.formatter.format.EmptyFormat
import io.bashpsk.emptylibs.gestureui.transform.TransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.rememberTransformableGesturesState
import io.bashpsk.emptylibs.lrucachemanager.manager.EmptyCacheManager
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Creates and remembers a [PdfLazyColumnState] instance.
 *
 * @param uri The URI of the PDF file.
 * @param cacheSize The size of the bitmap cache.
 * @param enableZoom Whether to enable zoom gestures.
 * @param enableDoubleTapZoom Whether to enable double-tap to zoom.
 * @param zoomRange The allowed zoom range.
 * @return A new [PdfLazyColumnState] instance.
 */
@Composable
fun rememberPdfLazyColumnState(
    uri: Uri?,
    cacheSize: Int = 10,
    enableZoom: Boolean = true,
    enableDoubleTapZoom: Boolean = true,
    zoomRange: ClosedFloatingPointRange<Float> = 0.4F..8.0F
): PdfLazyColumnState {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val transformableState = rememberTransformableGesturesState(
        enableZoom = enableZoom,
        enableDoubleTapZoom = enableDoubleTapZoom,
        enablePan = true,
        enableRotation = false,
        zoomRange = zoomRange
    )

    val state = retain(transformableState) {
        PdfLazyColumnState(
            context = context,
            coroutineScope = coroutineScope,
            transformableState = transformableState
        )
    }

    LaunchedEffect(uri) {

        uri?.let { state.loadPdfFile(it) }
    }

    LaunchedEffect(cacheSize) {

        state.qualityPageManager.resize(maxSize = cacheSize)
    }

    DisposableEffect(Unit) {

        onDispose { state.close() }
    }

    return state
}

/**
 * A state object that can be hoisted to control and observe scrolling and zooming of a PDF.
 *
 * @param context The application context.
 * @param coroutineScope A coroutine scope for managing background tasks.
 * @param transformableState The state for transformable gestures.
 */
@Stable
class PdfLazyColumnState(
    private val context: Context,
    internal val coroutineScope: CoroutineScope,
    internal val transformableState: TransformableGesturesState
) {

    /**
     * A mutex to ensure thread-safe access to PDF rendering operations.
     */
    private val mutex = Mutex()

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
    internal val qualityPageManager = EmptyCacheManager<PdfQualityPageData>(maxSize = 7)

    /**
     * The width of the container in pixels.
     */
    internal var containerWidth by mutableIntStateOf(0)

    /**
     * The height of the container in pixels.
     */
    internal var containerHeight by mutableIntStateOf(0)

    /**
     * Loads a PDF file from a URI.
     *
     * @param uri The URI of the PDF file.
     */
    internal fun loadPdfFile(uri: Uri) {

        fileLoadJob?.cancel()
        close()

        fileLoadJob = coroutineScope.launch {

            mutex.withLock {

                try {

                    fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                    pdfRenderer = fileDescriptor?.let { descriptor -> PdfRenderer(descriptor) }

                    val renderer = pdfRenderer ?: return@withLock

                    pageDataList = (0 until renderer.pageCount).associate { pageIndex ->

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
                    Log.e("PDF-VIEWER", exception.message, exception)
                }
            }
        }
    }

    /**
     * Renders a low-quality version of a page.
     *
     * @param pageIndex The index of the page to render.
     */
    internal fun setRenderLowQuality(pageIndex: Int) {

        coroutineScope.launch(context = Dispatchers.IO) {

            setRenderPage(pageIndex = pageIndex, highQuality = false)
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
    internal suspend fun getHighQualityBitmap(pageIndex: Int): ImageBitmap? {

        return coroutineScope.async(context = Dispatchers.IO) {

            if (hasNeedHighQualityBitmap(pageData = getQualityPageData(pageIndex = pageIndex))) {

                setRenderPage(pageIndex = pageIndex, highQuality = true)
            }

            getQualityPageData(pageIndex = pageIndex)?.bitmap
        }.await()
    }

    /**
     * Checks if a new high-quality bitmap is needed for a page.
     *
     * @param pageData The quality page data to check.
     * @return True if a new high-quality bitmap is needed, false otherwise.
     */
    private fun hasNeedHighQualityBitmap(pageData: PdfQualityPageData?): Boolean {

        return pageData == null
                || qualityPageManager.exist(pageData.page.toString()).not()
                || pageData.quality !in
                (transformableState.zoom - 0.25F)..(transformableState.zoom + 0.25F)
    }

    /**
     * Determines the quality for rendering the content based on the current zoom level.
     *
     * @return The rendering quality as a float.
     */
    private fun findContentQuality(): Float {

        return EmptyFormat.toRoundedDecimal(decimal = transformableState.zoom, fraction = 1)
    }

    /**
     * Retrieves the quality page data for a specific page from the cache.
     *
     * @param pageIndex The index of the page.
     * @return The quality page data, or null if it's not in the cache.
     */
    private fun getQualityPageData(pageIndex: Int): PdfQualityPageData? {

        return qualityPageManager.get(pageIndex.toString())
    }

    /**
     * Renders a page with the specified quality.
     *
     * @param pageIndex The index of the page to render.
     * @param highQuality Whether to render a high-quality version of the page.
     */
    private suspend fun setRenderPage(
        pageIndex: Int,
        highQuality: Boolean
    ) = withContext(context = Dispatchers.IO) {

        mutex.withLock {

            val renderer = pdfRenderer ?: return@withLock
            val pageData = pageDataList[pageIndex] ?: return@withLock

            when (highQuality) {

                true -> {

                    val targetWidth = (containerWidth * findContentQuality()).toInt()

                    getRenderBitmap(
                        renderer = renderer,
                        pageIndex = pageIndex,
                        targetWidth = targetWidth
                    )?.let { bitmap ->

                        val newPageData = PdfQualityPageData(
                            page = pageIndex,
                            quality = findContentQuality(),
                            bitmap = bitmap
                        )

                        qualityPageManager.add(newPageData.page.toString(), newPageData)
                    }
                }

                false -> {

                    val targetWidth = (containerWidth * 0.25F).toInt()

                    if (targetWidth <= 0 || (pageData.bitmap != null)) return@withLock

                    getRenderBitmap(
                        renderer = renderer,
                        pageIndex = pageIndex,
                        targetWidth = targetWidth
                    )?.let { bitmap ->

                        pageDataList = pageDataList.put(pageIndex, pageData.copy(bitmap = bitmap))
                    }
                }
            }
        }
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
        targetWidth: Int
    ): ImageBitmap? = withContext(context = Dispatchers.IO) {

        return@withContext renderer.openPage(pageIndex).use { currentPage ->

            val pageData = pageDataList[pageIndex] ?: return@use null
            val bitmapHeight = (targetWidth.toFloat() / pageData.width * pageData.height).toInt()

            if (bitmapHeight <= 0) return@use null

            val bitmap = createBitmap(targetWidth, bitmapHeight)

            Canvas(bitmap).apply {

                drawColor(Color.White.toArgb())
            }

            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmap.asImageBitmap()
        }
    }

    /**
     * Closes the PDF renderer and releases all resources.
     */
    internal fun close() {

        transformableState.resetAllValues()
        fileLoadJob?.cancel()
        pdfRenderer?.close()
        fileDescriptor?.close()
        pdfRenderer = null
        fileDescriptor = null
        pageDataList = persistentMapOf()
        qualityPageManager.evictAll()
    }
}