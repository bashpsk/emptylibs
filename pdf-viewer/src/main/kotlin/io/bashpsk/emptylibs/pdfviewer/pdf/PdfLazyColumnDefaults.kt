package io.bashpsk.emptylibs.pdfviewer.pdf

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Contains the default values used by a PDF lazy column.
 */
object PdfLazyColumnDefaults {

    /**
     * Creates a [PdfLazyColumnProperties] instance with configurable colors for search and
     * selection highlights.
     *
     * @param searchBoxColor The color applied to the highlight boxes when searching for text within
     * the PDF.
     * @param selectBoxColor The color applied to the highlight boxes when selecting text within the
     * PDF.
     * @return A [PdfLazyColumnProperties] object containing the specified color configurations.
     */
    @Composable
    fun properties(
        searchBoxColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5F),
        selectBoxColor: Color = MaterialTheme.colorScheme.primaryFixed.copy(alpha = 0.5F)
    ): PdfLazyColumnProperties {

        return PdfLazyColumnProperties(
            searchBoxColor = searchBoxColor,
            selectBoxColor = selectBoxColor
        )
    }
}