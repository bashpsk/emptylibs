package io.bashpsk.emptylibs.pdftemplate.input

import androidx.compose.ui.text.TextLayoutResult
import io.bashpsk.emptylibs.pdftemplate.pdf.PdfTextInput

internal data class SectionData(
    val title: PdfTextInput,
    val content: PdfTextInput,
    val titleLayout: TextLayoutResult,
    val contentLayout: TextLayoutResult,
    val startingPage: Int,
    val positionY: Float
)