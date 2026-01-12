package io.bashpsk.emptylibs.pdfviewer.pdf

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

/**
 * Configuration properties for styling the lazy column within a PDF viewer.
 *
 * @property searchBoxColor The [Color] used to highlight text matches during a search operation.
 * @property selectBoxColor The [Color] used to highlight the background of selected text.
 */
@Stable
data class PdfLazyColumnProperties(
    val searchBoxColor: Color = Color.Unspecified,
    val selectBoxColor: Color = Color.Unspecified
)