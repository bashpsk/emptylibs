package io.bashpsk.emptylibs.pdfviewer.source

import android.net.Uri
import androidx.compose.runtime.Stable

/**
 * Represents the source of a PDF file.
 */
@Stable
sealed interface PdfSource {

    /**
     * Represents an empty PDF source.
     */
    data object Empty : PdfSource

    /**
     * Represents a PDF source from a [android.net.Uri].
     *
     * @property uri The URI of the PDF file.
     */
    data class URI(val uri: Uri?) : PdfSource

    /**
     * Represents a PDF source from a file path.
     *
     * @property path The path to the PDF file.
     */
    data class Path(val path: String?) : PdfSource
}