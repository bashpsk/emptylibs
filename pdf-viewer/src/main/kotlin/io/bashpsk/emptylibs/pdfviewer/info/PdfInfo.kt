package io.bashpsk.emptylibs.pdfviewer.info

import io.bashpsk.emptylibs.pdfviewer.renderer.PageRenderer

/**
 * Interface representing PDF document information and operations.
 */
internal interface PdfInfo {

    /**
     * Total number of pages in the PDF document.
     */
    val pageCount: Int

    /**
     * Renderer responsible for rendering PDF pages.
     */
    val pageRenderer: PageRenderer

    /**
     * Retrieves the aspect ratio of a specific page.
     *
     * @param index The index of the page.
     * @return The aspect ratio (width / height) of the page.
     */
    suspend fun getPageAspectRatio(index: Int): Float

    /**
     * Closes the PDF document and releases associated resources.
     */
    fun close()
}