package io.bashpsk.emptylibs.pdfviewer.page

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Represents the data for a single page in a PDF document.
 *
 * @property page The page number.
 * @property width The width of the page in pixels.
 * @property height The height of the page in pixels.
 * @property bitmap A low-quality bitmap representation of the page, used for previews.
 * @property searchRectList A list of rectangles representing search matches in the page.
 * @property selectRectList A list of rectangles representing selected regions in the page.
 */
@Immutable
internal data class PdfPageData(
    val page: Int = 0,
    val width: Int = 0,
    val height: Int = 0,
    val bitmap: ImageBitmap? = null,
    val searchRectList: ImmutableList<ImmutableList<Rect>> = persistentListOf(),
    val selectRectList: ImmutableList<ImmutableList<Rect>> = persistentListOf()
)