package io.bashpsk.emptylibs.jetpackui.picker

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws a selected box on the dial.
 *
 * @param end The end offset of the box.
 * @param rectSize The size of the box.
 * @param width The width of the box stroke.
 * @param color The color of the box.
 */
internal fun DrawScope.drawDialSelectedBox(
    end: Offset,
    rectSize: Size,
    width: Dp = 2.dp,
    color: Color = Color.Green
) {

    drawLine(
        start = Offset(x = end.x - rectSize.width, y = end.y - rectSize.height),
        end = Offset(x = end.x, y = end.y - rectSize.height),
        strokeWidth = width.toPx(),
        cap = StrokeCap.Round,
        color = color
    )

    drawLine(
        start = Offset(x = end.x - rectSize.width, y = end.y),
        end = end,
        strokeWidth = width.toPx(),
        cap = StrokeCap.Round,
        color = color
    )
}