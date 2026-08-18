package io.bashpsk.emptylibs.pdfviewer.info

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.ui.unit.Density
import io.bashpsk.emptylibs.formatter.format.findAspectRatio
import io.bashpsk.emptylibs.pdfviewer.renderer.PageRenderer
import io.bashpsk.emptylibs.pdfviewer.renderer.PdfPageRenderer
import io.bashpsk.emptylibs.pdfviewer.renderer.RendererScope
import io.bashpsk.emptylibs.pdfviewer.source.PdfSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Android implementation of [PdfInfo] using [PdfRenderer].
 *
 * @property rendererScope The scope managing the lifecycle of the [PdfRenderer].
 * @property density The density of the display for coordinate conversions.
 * @property pageCount Total number of pages in the PDF.
 */
internal class AndroidPdfInfo(
    private val rendererScope: RendererScope,
    private val density: Density,
    override val pageCount: Int,
) : PdfInfo {

    companion object {

        /**
         * Creates an instance of [AndroidPdfInfo] from a [PdfSource].
         *
         * @param context The application context.
         * @param density The display density.
         * @param source The source of the PDF document.
         * @return A new instance of [AndroidPdfInfo].
         * @throws IllegalStateException if the source cannot be opened.
         */
        suspend fun create(
            context: Context,
            density: Density,
            source: PdfSource
        ): AndroidPdfInfo = withContext(context = Dispatchers.IO) {

            val fileDescriptor = when (source) {

                is PdfSource.Empty -> null

                is PdfSource.URI -> source.uri?.let { uri ->

                    context.contentResolver.openFileDescriptor(
                        uri,
                        "r"
                    ) ?: throw IllegalStateException("Could not open file descriptor for $uri")
                }

                is PdfSource.Path -> source.path?.let { path ->

                    ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_ONLY)
                }
            } ?: throw IllegalStateException("Could not open source $source")

            val renderer = RendererScope(
                onCreate = { PdfRenderer(fileDescriptor) },
                fileDescriptor = fileDescriptor
            )

            val pageCount = renderer.use { renderer -> renderer.pageCount }

            return@withContext AndroidPdfInfo(
                rendererScope = renderer,
                density = density,
                pageCount = pageCount
            )
        }
    }

    /**
     * The renderer for individual PDF pages.
     */
    override val pageRenderer: PageRenderer = PdfPageRenderer(
        rendererScope = rendererScope,
        density = density
    )

    /**
     * Gets the aspect ratio of the page at the given index.
     *
     * @param index The zero-based page index.
     * @return The aspect ratio (width / height).
     */
    override suspend fun getPageAspectRatio(index: Int): Float {

        return rendererScope.use { renderer ->

            renderer.openPage(index).use { page ->

                findAspectRatio(width = page.height, height = page.width)
            }
        }
    }

    /**
     * Closes the underlying renderer scope.
     */
    override fun close() {

        rendererScope.close()
    }
}