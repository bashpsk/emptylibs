package io.bashpsk.emptylibs.pdfviewer.renderer

import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.util.Log
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toIntSize
import androidx.compose.ui.unit.toSize
import io.bashpsk.emptylibs.imageutils.extension.toSize
import io.bashpsk.emptylibs.pdfviewer.page.PdfScaledPage
import io.bashpsk.emptylibs.pdfviewer.renderer.PdfPageRenderer.Companion.MAX_BITMAP_SIZE
import io.bashpsk.emptylibs.pdfviewer.utils.LOG_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/**
 * Android implementation of [PageRenderer] using [PdfRenderer].
 *
 * @property rendererScope The scope providing access to the [PdfRenderer].
 * @property density The display density used for bitmap creation.
 */
internal class PdfPageRenderer(
    private val rendererScope: RendererScope,
    private val density: Density
) : PageRenderer {

    /**
     * Matrix used for fragment rendering transformations.
     */
    private val matrix = Matrix()

    /**
     * Renders the specified page at the given size.
     *
     * @param index The page index.
     * @param pageSize The target dimensions.
     * @return The rendered [ImageBitmap].
     */
    override suspend fun renderPage(
        index: Int,
        pageSize: IntSize
    ): ImageBitmap? = withContext(context = Dispatchers.IO) {

        return@withContext rendererScope.use { pdfRenderer ->

            pdfRenderer.openPage(index).use { page ->

                val bitmap = createBitmap(imageSize = pageSize)

                try {

                    currentCoroutineContext().ensureActive()

                    page.render(
                        bitmap.asAndroidBitmap(),
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                    )

                    bitmap
                } catch (exception: Exception) {

                    currentCoroutineContext().ensureActive()
                    bitmap.asAndroidBitmap().recycle()
                    Log.w(LOG_TAG, exception.message, exception)
                    null
                }
            }
        }
    }

    /**
     * Renders a specific fragment of a page with scaling applied.
     *
     * @param index The page index.
     * @param pageSize The rectangle defining the full page bounds.
     * @param scaledFragment The rectangle defining the fragment to render.
     * @param scale The scale factor for the fragment.
     * @return A [PdfScaledPage] instance.
     */
    override suspend fun renderPageFragment(
        index: Int,
        pageSize: IntRect,
        scaledFragment: IntRect,
        scale: Float,
    ): PdfScaledPage? = withContext(context = Dispatchers.IO) {

        return@withContext rendererScope.use { pdfRenderer ->

            pdfRenderer.openPage(index).use { page ->

                val scaledSize = (scaledFragment.size.toSize() * scale).toIntSize()
                val imageBitmap = createBitmap(imageSize = scaledSize)

                try {

                    val sx = (pageSize.width / page.width.toFloat()) * scale
                    val sy = (pageSize.height / page.height.toFloat()) * scale

                    matrix.postScale(sx, sy)

                    val dx = (pageSize.left - scaledFragment.left.toFloat()) * scale
                    val dy = (pageSize.top - scaledFragment.top.toFloat()) * scale

                    matrix.postTranslate(dx, dy)

                    currentCoroutineContext().ensureActive()

                    page.render(
                        imageBitmap.asAndroidBitmap(),
                        null,
                        matrix,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                    )

                    PdfScaledPage(
                        pageSize = pageSize,
                        scaledFragment = scaledFragment,
                        bitmap = imageBitmap
                    )
                } catch (exception: Exception) {

                    currentCoroutineContext().ensureActive()
                    imageBitmap.asAndroidBitmap().recycle()
                    Log.w(LOG_TAG, exception.message, exception)
                    null
                } finally {

                    matrix.reset()
                }
            }
        }
    }

    /**
     * Creates an [ImageBitmap] with the specified dimensions, ensuring it doesn't exceed
     * [MAX_BITMAP_SIZE].
     *
     * @param imageSize The desired size of the bitmap.
     * @return A white-filled [ImageBitmap].
     */
    private suspend fun createBitmap(
        imageSize: IntSize
    ): ImageBitmap = withContext(context = Dispatchers.IO) {

        val sizeInBytes = imageSize.width * imageSize.height * 4

        val (targetWidth, targetHeight) = if (sizeInBytes > MAX_BITMAP_SIZE) {

            val scale = MAX_BITMAP_SIZE / sizeInBytes.toDouble()

            IntSize(
                width = (imageSize.width * scale).toInt(),
                height = (imageSize.height * scale).toInt()
            )
        } else imageSize

        val newImageBitmap = ImageBitmap(width = targetWidth, height = targetHeight)

        CanvasDrawScope().draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = Canvas(image = newImageBitmap),
            size = newImageBitmap.toSize()
        ) {

            drawRect(color = Color.White)
        }

        newImageBitmap
    }

    companion object {

        /**
         * Maximum bitmap size in bytes to prevent OutOfMemory errors.
         */
        const val MAX_BITMAP_SIZE = 40 * 1024 * 1024 // 40MB
    }
}