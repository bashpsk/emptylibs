package io.bashpsk.emptylibs.pdfviewer.renderer

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import io.bashpsk.emptylibs.pdfviewer.page.PdfScaledPage

/**
 * Internal interface for rendering PDF pages and page fragments.
 */
internal interface PageRenderer {

    /**
     * Renders a full PDF page into an [ImageBitmap].
     *
     * @param index The zero-based page index.
     * @param pageSize The desired size of the rendered page.
     * @return The rendered [ImageBitmap], or null if rendering failed.
     */
    suspend fun renderPage(index: Int, pageSize: IntSize): ImageBitmap?

    /**
     * Renders a fragment (region) of a PDF page at a specific scale.
     *
     * @param index The zero-based page index.
     * @param pageSize The full size of the page.
     * @param scaledFragment The rectangle representing the fragment within the page.
     * @param scale The zoom level to apply to the fragment.
     * @return A [PdfScaledPage] containing the rendered fragment, or null if rendering failed.
     */
    suspend fun renderPageFragment(
        index: Int,
        pageSize: IntRect,
        scaledFragment: IntRect,
        scale: Float
    ): PdfScaledPage?
}