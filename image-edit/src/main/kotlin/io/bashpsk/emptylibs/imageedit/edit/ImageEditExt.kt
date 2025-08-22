package io.bashpsk.emptylibs.imageedit.edit

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import kotlin.math.abs

internal fun DrawScope.drawImageEditItem(items: ImageEditItems) {

    when (items) {

        is ImageEditItems.Image -> drawEditImage(item = items)
        is ImageEditItems.Path -> drawEditPath(item = items)
        is ImageEditItems.Shape -> drawEditShape(item = items)
        is ImageEditItems.Text -> drawEditText(item = items)
    }
}

internal fun DrawScope.drawEditImage(item: ImageEditItems.Image) {

    item.image.bitmap?.let { bitmap ->

        clipRect {

            drawImage(image = bitmap)
        }
    }
}

internal fun DrawScope.drawEditPath(item: ImageEditItems.Path) {

    val smoothedPath = Path().apply {

        val smoothness = 3

        item.path.path.takeIf { paths -> paths.isNotEmpty() }?.let { points ->

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
        color = item.path.color,
        style = Stroke(
            width = item.path.thickness.toPx(),
            cap = item.path.strokeCap,
            join = item.path.strokeJoin
        )
    )
}

internal fun DrawScope.drawEditShape(item: ImageEditItems.Shape) {

}

internal fun DrawScope.drawEditText(item: ImageEditItems.Text) {

}