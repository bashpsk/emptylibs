package io.bashpsk.emptylibs.pdftemplate.pdf

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

@Stable
sealed interface PdfTemplateBackground {

    data class SolidColor(val color: Color = Color.White) : PdfTemplateBackground

    data class Image(val bitmap: ImageBitmap? = null) : PdfTemplateBackground
}