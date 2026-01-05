package io.bashpsk.emptylibs.pdfviewer.pdf

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable

/**
 * Represents the data for a single, high-quality page in a PDF document.
 *
 * @property page The page number.
 * @property quality The quality of the rendered bitmap, as a percentage of the original size.
 * @property bitmap A high-quality bitmap representation of the page.
 */
@Immutable
data class PdfQualityPageData(
    val page: Int = 0,
    val quality: Float = 1.0F,
    val bitmap: Bitmap? = null
)