package io.bashpsk.emptylibs.pdftemplate.pdf

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Defines the background for a PDF page.
 */
@Stable
sealed interface PdfTemplateBackground {

    /**
     * A background consisting of a single solid color.
     *
     * @property color The [Color] to fill the page with. Defaults to [Color.White].
     */
    data class SolidColor(val color: Color = Color.White) : PdfTemplateBackground

    /**
     * A background consisting of an image.
     *
     * @property bitmap The [ImageBitmap] to draw as the background. If null, no image is drawn.
     */
    data class Image(val bitmap: ImageBitmap? = null) : PdfTemplateBackground
}