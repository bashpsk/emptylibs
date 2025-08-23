package io.bashpsk.emptylibs.imageedit.edit

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import kotlin.math.abs

internal fun DrawScope.drawImageEditItem(items: ImageEditItems) {

    when (items) {

        is ImageEditItems.EraseItem -> drawEraseArea(item = items)
        is ImageEditItems.ImageItem -> drawEditImage(item = items)
        is ImageEditItems.PathItem -> drawEditPath(item = items)
        is ImageEditItems.ShapeItem -> drawEditShape(item = items)
        is ImageEditItems.TextItem -> drawEditText(item = items)
    }
}

private fun DrawScope.drawEraseArea(item: ImageEditItems.EraseItem) {

}

private fun DrawScope.drawEditImage(item: ImageEditItems.ImageItem) {

    clipRect {

        drawImage(image = item.bitmap)
    }
}

private fun DrawScope.drawEditPath(item: ImageEditItems.PathItem) {

    val smoothedPath = Path().apply {

        val smoothness = 3

        item.path.takeIf { paths -> paths.isNotEmpty() }?.let { points ->

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
        color = item.color,
        style = Stroke(
            width = item.thickness.toPx(),
            cap = item.strokeCap,
            join = item.strokeJoin
        )
    )
}

private fun DrawScope.drawEditShape(item: ImageEditItems.ShapeItem) {

}

private fun DrawScope.drawEditText(item: ImageEditItems.TextItem) {

}