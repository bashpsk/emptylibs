package io.bashpsk.emptylibs.pdfviewer.page

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize

/**
 * Represents a rendered fragment of a PDF page at a specific scale.
 *
 * @property pageSize The full bounds of the page.
 * @property scaledFragment The bounds of the fragment within the page.
 * @property bitmap The rendered [ImageBitmap] of the fragment.
 */
@Immutable
internal data class PdfScaledPage(
    val pageSize: IntRect,
    val scaledFragment: IntRect,
    val bitmap: ImageBitmap
) {

    /**
     * The offset of the fragment relative to the top-left of the page.
     */
    val topLeft: IntOffset = scaledFragment.topLeft - pageSize.topLeft

    /**
     * The size of the fragment in pixels.
     */
    val dstSize: IntSize = scaledFragment.size
}