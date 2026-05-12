package io.bashpsk.emptylibs.canvasslate.slate

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import io.bashpsk.emptylibs.canvasslate.extension.toStrokeCap
import io.bashpsk.emptylibs.canvasslate.extension.toStrokeJoin
import kotlin.math.abs

/**
 * Draws a [CanvasSlatePath] on the [DrawScope].
 *
 * This function takes a [CanvasSlatePath] object, which contains the path points, color,
 * thickness, stroke cap, and stroke join. It then smooths the path using quadratic Bézier
 * curves if the distance between points is greater than or equal to a predefined `smoothness`
 * value. Finally, it draws the smoothed path onto the `DrawScope`.
 *
 * @param slatePath The [CanvasSlatePath] to be drawn.
 */
internal fun DrawScope.drawSlatePath(slatePath: CanvasSlatePath) {

    val smoothedPath = Path().apply {

        val smoothness = 3

        if (slatePath.path.isNotEmpty()) {

            moveTo(x = slatePath.path.first().x, y = slatePath.path.first().y)

            if (slatePath.path.size == 1) lineTo(
                x = slatePath.path.first().x,
                y = slatePath.path.first().y
            )

            slatePath.path.zipWithNext().forEach { (from, to) ->

                val dx = abs(from.x - to.x)
                val dy = abs(from.y - to.y)

                if (dx >= smoothness || dy >= smoothness) quadraticTo(
                    x1 = (from.x + to.x) / 2,
                    y1 = (from.y + to.y) / 2,
                    x2 = to.x,
                    y2 = to.y
                )
            }
        }
    }

    drawPath(
        path = smoothedPath,
        color = Color(slatePath.color),
        style = Stroke(
            width = slatePath.thickness,
            cap = slatePath.strokeCap.toStrokeCap(),
            join = slatePath.strokeJoin.toStrokeJoin()
        )
    )
}