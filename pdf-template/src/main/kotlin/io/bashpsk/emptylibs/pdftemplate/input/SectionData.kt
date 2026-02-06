package io.bashpsk.emptylibs.pdftemplate.input

import androidx.compose.ui.text.TextLayoutResult
import io.bashpsk.emptylibs.pdftemplate.pdf.PdfTextInput

/**
 * Internal data class used to track layout information for a section of the PDF.
 *
 * @property title The [PdfTextInput] for the section title.
 * @property content The [PdfTextInput] for the section content.
 * @property titleLayout The measured [TextLayoutResult] for the title.
 * @property contentLayout The measured [TextLayoutResult] for the content.
 * @property startingPage The page number where this section starts.
 * @property positionY The vertical offset (Y-coordinate) where drawing starts on the
 * [startingPage].
 */
internal data class SectionData(
    val title: PdfTextInput,
    val content: PdfTextInput,
    val titleLayout: TextLayoutResult,
    val contentLayout: TextLayoutResult,
    val startingPage: Int,
    val positionY: Float
)