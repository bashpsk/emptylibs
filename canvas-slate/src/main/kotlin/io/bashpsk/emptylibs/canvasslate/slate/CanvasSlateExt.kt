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
 * thickness, stroke cap, and stroke join. It then smooths the path using quadratic Bezier
 * curves if the distance between points is greater than or equal to a predefined `smoothness`
 * value. Finally, it draws the smoothed path onto the `DrawScope`.
 *
 * @param slatePath The [CanvasSlatePath] to be drawn.
 */
internal fun DrawScope.drawSlatePath(slatePath: CanvasSlatePath) {

    val smoothedPath = Path().apply {

        val smoothness = 3

        slatePath.path.takeIf { paths -> paths.isNotEmpty() }?.let { points ->

            moveTo(x = points.first().x, y = points.first().y)

            points.size.takeIf { counts -> counts == 1 }?.run {

                lineTo(x = points.first().x, y = points.first().y)
            }

            points.zipWithNext().forEach { (from, to) ->

                val dx = abs(from.x - to.x)
                val dy = abs(from.y - to.y)

                (dx >= smoothness || dy >= smoothness).takeIf { hasValid -> hasValid }?.run {

                    quadraticTo(
                        x1 = (from.x + to.x) / 2,
                        y1 = (from.y + to.y) / 2,
                        x2 = to.x,
                        y2 = to.y
                    )
                }
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