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
import io.bashpsk.emptylibs.gestureui.transform.TransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.rememberTransformableGesturesState
import io.bashpsk.emptylibs.imageutils.extension.toSize
import io.bashpsk.emptylibs.lrucachemanager.manager.EmptyCacheManager
import io.bashpsk.emptylibs.pdfviewer.page.PdfBitmapData
import io.bashpsk.emptylibs.pdfviewer.page.PdfPageData
import io.bashpsk.emptylibs.pdfviewer.page.PdfPageRequest
import io.bashpsk.emptylibs.pdfviewer.utils.LOG_TAG
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

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
     * A channel used to queue and process requests for rendering normal-quality (base) bitmaps
     * of PDF pages.
     */
    private val normalRenderChannel = Channel<PdfPageRequest>(capacity = Channel.BUFFERED)

    /**
     * A channel used to queue and process requests for rendering high-quality (scale) bitmaps
     * of PDF pages.
     */
    private val scaledRenderChannel = Channel<PdfPageRequest>(capacity = Channel.BUFFERED)

    /**
     * A map that tracks pending low-quality (normal) rendering requests.
     *
     * This queue ensures that each page is only requested for rendering once at a time
     * and allows for the cancellation of pending tasks when a page is no longer visible.
     */
    private var normalRenderQueue by mutableStateOf(persistentMapOf<Int, PdfPageRequest>())

    /**
     * A map that tracks pending or active high-quality (scaled) rendering requests.
     *
     * This queue prevents duplicate rendering tasks for the same page at the same zoom level
     * and is used to manage the lifecycle of background rendering jobs.
     */
    private var scaledRenderQueue by mutableStateOf(persistentMapOf<Int, PdfPageRequest>())

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
    internal val scaledBitmapManager = EmptyCacheManager<Int, PdfBitmapData>(
        onEntryRemoved = { cache, _, page, _ ->

            coroutineScope.launch(context = Dispatchers.IO) {

                updatePageData(index = page) { data ->

                    if (!cache.contains(page)) data.copy(scaledImage = null) else data
                }
            }
        }
    )

    /**
     * The width of the container in pixels.
     */
    internal var containerWidth by mutableIntStateOf(0)

    /**
     * The height of the container in pixels.
     */
    internal var containerHeight by mutableIntStateOf(0)

    init {

        setNormalRenderWorker()
        setScaledRenderWorker()
    }

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
     * Starts a background worker coroutine that listens for and processes normal-quality
     * bitmap rendering requests.
     *
     * This worker runs indefinitely in the [coroutineScope], consuming [PdfPageRequest]
     * objects from the [normalRenderChannel] and invoking [renderNormalBitmap] for each.
     */
    private fun setNormalRenderWorker() = coroutineScope.launch(context = Dispatchers.IO) {

        for (request in normalRenderChannel) {

            renderNormalBitmap(request = request)
        }
    }

    /**
     * Starts a background worker that listens to the [scaledRenderChannel] for rendering
     * high-quality, scaled versions of PDF pages.
     *
     * The worker runs within the [coroutineScope] using [Dispatchers.IO] and continuously
     * processes [PdfPageRequest] elements as they are received.
     */
    private fun setScaledRenderWorker() = coroutineScope.launch(context = Dispatchers.IO) {

        for (request in scaledRenderChannel) {

            renderScaledBitmap(request = request)
        }
    }

    /**
     * Enqueues a request to render a normal-quality bitmap for the specified page.
     *
     * This function initiates a background task to render the base version of a PDF page.
     * It ensures that a render request is only sent to the worker if the bitmap is not
     * already present in [pageData] and there isn't an existing request for the same
     * page in the queue.
     *
     * @param pageData The data object representing the page to be rendered.
     */
    internal fun enqueueNormalRender(
        pageData: PdfPageData
    ) = coroutineScope.launch(context = Dispatchers.Default) {

        if (pageData.normalImage != null) return@launch
        if (normalRenderQueue.containsKey(key = pageData.page)) return@launch

        val newRequest = PdfPageRequest(page = pageData.page, quality = 1.0F)
        normalRenderQueue = normalRenderQueue.putting(key = pageData.page, value = newRequest)
        normalRenderChannel.send(element = newRequest)
    }

    /**
     * Enqueues a high-quality version of a page for rendering based on the current zoom level.
     *
     * This function calculates a target quality based on the [zoomLevel], rounding to the nearest
     * 0.5 increment.
     * If the quality is at the base level (1.0 or less), any existing scaled image is cleared.
     * It checks the [scaledBitmapManager] cache and the current [scaledRenderQueue] to avoid
     * redundant rendering of the same quality level.
     *
     * @param pageData The data of the page to be rendered.
     * @param zoomLevel The current zoom level used to determine the rendering quality.
     */
    internal fun enqueueScaledRender(
        pageData: PdfPageData,
        zoomLevel: Float
    ) = coroutineScope.launch(context = Dispatchers.IO) {

        val targetQuality = (((zoomLevel - 1.0F) * 2F).roundToInt() / 2.0F + 1.0F)
            .coerceAtLeast(1.0F)

        if (targetQuality <= 1.0F) {

            updatePageData(index = pageData.page) { it.copy(scaledImage = null) }
            return@launch
        }

        val cachedData = scaledBitmapManager[pageData.page]

        if (cachedData?.quality == targetQuality) {

            updatePageData(index = pageData.page) { it.copy(scaledImage = cachedData) }
            return@launch
        }

        if (scaledRenderQueue[pageData.page]?.quality == targetQuality) return@launch

        val newRequest = PdfPageRequest(page = pageData.page, quality = targetQuality)
        scaledRenderQueue = scaledRenderQueue.putting(key = pageData.page, value = newRequest)
        scaledRenderChannel.send(element = newRequest)
    }

    /**
     * Removes a specific page from the pending rendering queues.
     *
     * This function prevents future rendering tasks for the given [pageIndex] from starting by
     * removing them from both the normal and scaled render queues. Note that this does not
     * interrupt rendering operations that are already in progress.
     *
     * @param pageIndex The index of the PDF page for which to cancel pending render requests.
     */
    internal fun cancelEnqueueRender(
        pageIndex: Int
    ) = coroutineScope.launch(context = Dispatchers.Default) {

        scaledRenderQueue = scaledRenderQueue.removing(key = pageIndex)
    }

    /**
     * Checks if the PDF content is currently zoomed in beyond the initial zoom level.
     *
     * @return True if the content is zoomed in, false otherwise.
     */
    internal fun hasImageZoomed(): Boolean {

        return transformable.zoom >= 1.25F
    }

    /**
     * Renders a standard-resolution bitmap for a specific PDF page.
     *
     * This function checks if the request is still valid in the [normalRenderQueue],
     * performs the rendering using the [PdfRenderer] at the page's original dimensions,
     * updates the [pageDataList] with the resulting [ImageBitmap], and finally
     * removes the request from the queue.
     *
     * @param request The [PdfPageRequest] containing the index of the page to render.
     */
    private suspend fun renderNormalBitmap(request: PdfPageRequest) {

        if (!normalRenderQueue.containsKey(key = request.page)) return

        val renderer = pdfRenderer ?: return
        val pageData = pageDataList[request.page] ?: return

        val bitmap = getRenderBitmap(
            renderer = renderer,
            pageIndex = request.page,
            targetWidth = pageData.width,
            targetHeight = pageData.height
        )

        updatePageData(index = request.page) { it.copy(normalImage = bitmap) }
        normalRenderQueue = normalRenderQueue.removing(key = request.page)
    }

    /**
     * Renders a high-quality scaled bitmap for a specific page based on the requested quality.
     *
     * This function performs the rendering if the request matches the current queue, calculates the
     * target dimensions, generates the bitmap via the PDF renderer, and then caches the result
     * in [scaledBitmapManager] before updating the [pageDataList].
     *
     * @param request The [PdfPageRequest] containing the page index and the target quality/zoom
     * level.
     */
    private suspend fun renderScaledBitmap(request: PdfPageRequest) {

        if (scaledRenderQueue[request.page]?.quality != request.quality) return

        val renderer = pdfRenderer ?: return

        val pageData = pageDataList[request.page] ?: return

        val bitmap = getRenderBitmap(
            renderer = renderer,
            pageIndex = request.page,
            targetWidth = (pageData.width * request.quality).roundToInt(),
            targetHeight = (pageData.height * request.quality).roundToInt()
        )

        val scaledData = PdfBitmapData(bitmap = bitmap, quality = request.quality)

        scaledBitmapManager.set(key = request.page, value = scaledData)

        updatePageData(index = request.page) { it.copy(scaledImage = scaledData) }

        scaledRenderQueue = scaledRenderQueue.removing(key = request.page)
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
     * Updates the [PdfPageData] for a specific page index within the [pageDataList].
     *
     * This function applies the provided [transform] to the current data of the page at [index]
     * and updates the state map. If no data exists for the given index, no action is taken.
     *
     * @param index The index of the page whose data is to be updated.
     * @param transform A lambda function that receives the current [PdfPageData] and returns
     * the updated [PdfPageData].
     */
    private fun updatePageData(index: Int, transform: (page: PdfPageData) -> PdfPageData) {

        pageDataList[index]?.let { data ->

            pageDataList = pageDataList.putting(key = index, value = transform(data))
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
        normalRenderQueue = persistentMapOf()
        scaledRenderQueue = persistentMapOf()
    }
}