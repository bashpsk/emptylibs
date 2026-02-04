package io.bashpsk.emptylibs.pdftemplate.pdf

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle

@Immutable
data class PdfTextInput(
    val text: String,
    val style: TextStyle = TextStyle.Default,
    val alignment: Alignment = Alignment.TopCenter
)