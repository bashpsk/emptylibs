package io.bashpsk.emptylibs.pdfviewer.page

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Represents the data for a single, high-quality page in a PDF document.
 *
 * @property quality The quality of the rendered bitmap, as a percentage of the original size.
 * @property bitmap A high-quality bitmap representation of the page.
 */
@Immutable
internal data class PdfBitmapData(val bitmap: ImageBitmap? = null, val quality: Float = 1.0F)