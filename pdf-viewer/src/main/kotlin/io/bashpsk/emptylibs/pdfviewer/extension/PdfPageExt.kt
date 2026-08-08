package io.bashpsk.emptylibs.pdfviewer.extension

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.pdfviewer.page.PdfPageData
import io.bashpsk.emptylibs.pdfviewer.pdf.PdfLazyColumnProperties
import kotlinx.collections.immutable.ImmutableList

/**
 * Draws the list of selection rectangles onto the [DrawScope].
 *
 * This function scales the selection rectangles from the PDF page's coordinate system
 * to the current canvas size and renders them using the selection color defined in [properties].
 *
 * @param pageData The data containing the original dimensions and the list of selection rectangles
 * for the page.
 * @param properties The configuration properties containing the color used for drawing selection
 * highlights.
 */
internal fun DrawScope.drawSelectRectList(
    pageData: PdfPageData,
    properties: PdfLazyColumnProperties
) {

    drawRectList(
        pageWidth = pageData.width,
        pageHeight = pageData.height,
        color = properties.selectBoxColor,
        rectList = pageData.selectRectList
    )
}

/**
 * Draws a list of rounded rectangles onto the [DrawScope], scaling them from the original page
 * coordinates to the current canvas dimensions.
 *
 * @param pageWidth The original width of the PDF page.
 * @param pageHeight The original height of the PDF page.
 * @param color The color to use for drawing the rectangles.
 * @param rectList A nested list of [Rect] objects representing the areas to be drawn
 * (e.g., search results or selections).
 */
private fun DrawScope.drawRectList(
    pageWidth: Int,
    pageHeight: Int,
    color: Color,
    rectList: ImmutableList<ImmutableList<Rect>>
) {

    if (rectList.isEmpty()) return

    val scaleFactorX = size.width / pageWidth
    val scaleFactorY = size.height / pageHeight

    rectList.forEach { match ->

        match.forEach { rect ->

            val scaledRect = Rect(
                left = rect.left * scaleFactorX,
                top = rect.top * scaleFactorY,
                right = rect.right * scaleFactorX,
                bottom = rect.bottom * scaleFactorY
            )

            drawRoundRect(
                topLeft = Offset(x = scaledRect.left, y = scaledRect.top),
                size = Size(width = scaledRect.width, height = scaledRect.height),
                cornerRadius = CornerRadius(x = 0.4.dp.toPx()),
                color = color
            )
        }
    }
}