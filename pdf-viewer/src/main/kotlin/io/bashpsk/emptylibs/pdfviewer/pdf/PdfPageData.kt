package io.bashpsk.emptylibs.pdfviewer.pdf

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable

/**
 * Represents the data for a single page in a PDF document.
 *
 * @property page The page number.
 * @property width The width of the page in pixels.
 * @property height The height of the page in pixels.
 * @property bitmap A low-quality bitmap representation of the page, used for previews.
 */
@Immutable
internal data class PdfPageData(
    val page: Int = 0,
    val width: Int = 0,
    val height: Int = 0,
    val bitmap: Bitmap? = null
)