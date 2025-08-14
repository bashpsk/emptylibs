package io.bashpsk.emptylibs.canvasslate.slate

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.abs

internal fun DrawScope.drawPathData(pathData: PathData) {

    val smoothedPath = Path().apply {

        val smoothness = 0

        pathData.path.takeIf { paths -> paths.isNotEmpty() }?.let { points ->

            moveTo(x = points.first().x, y = points.first().y)

            points.size.takeIf { counts -> counts == 1 }?.let {

                lineTo(x = points.first().x, y = points.first().y)
            }

            points.zipWithNext().forEach { (from, to) ->

                val dx = abs(from.x - to.x)
                val dy = abs(from.y - to.y)

                (dx >= smoothness || dy >= smoothness).takeIf { hasValid -> hasValid }?.let {

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
        color = pathData.color,
        style = Stroke(
            width = pathData.thickness.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

internal val ColorList = persistentListOf(
    Color.Black,
    Color.Red,
    Color.Green,
    Color.Blue,
    Color.Yellow,
    Color.Magenta,
    Color.Cyan,
    Color.DarkGray,
    Color.Gray,
    Color.LightGray,
    Color.White
).toImmutableList()