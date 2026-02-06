package io.bashpsk.emptylibs.pdftemplate.pdf

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle

/**
 * Represents text input for a PDF template.
 *
 * @property text The actual string content to be displayed.
 * @property style The [TextStyle] to be applied to the text.
 * @property alignment The [Alignment] of the text within its container.
 */
@Immutable
data class PdfTextInput(
    val text: String,
    val style: TextStyle = TextStyle.Default,
    val alignment: Alignment = Alignment.TopCenter
)