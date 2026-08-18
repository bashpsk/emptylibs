package io.bashpsk.emptylibs.pdfviewer.layout

import androidx.compose.runtime.Stable

/**
 * Represents the various loading states of the PDF viewer.
 */
@Stable
sealed interface PdfLoadingState {

    /**
     * Initial state before loading begins.
     */
    data object Init : PdfLoadingState

    /**
     * State representing that the PDF is currently loading.
     *
     * @property totalPage Total number of pages to load.
     * @property loadedPage Number of pages currently loaded.
     */
    data class Loading(val totalPage: Int, val loadedPage: Int) : PdfLoadingState

    /**
     * State representing an error during the loading process.
     *
     * @property exception The exception that caused the error, if any.
     */
    data class Error(val exception: Exception?) : PdfLoadingState

    /**
     * State representing that the PDF is ready for viewing.
     */
    data object Ready : PdfLoadingState
}