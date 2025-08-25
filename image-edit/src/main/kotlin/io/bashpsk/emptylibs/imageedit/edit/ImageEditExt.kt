package io.bashpsk.emptylibs.imageedit.edit

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntSize
import io.bashpsk.emptylibs.imageedit.extension.toBottomCenter
import io.bashpsk.emptylibs.imageedit.extension.toBottomLeft
import io.bashpsk.emptylibs.imageedit.extension.toBottomRight
import io.bashpsk.emptylibs.imageedit.extension.toLeftCenter
import io.bashpsk.emptylibs.imageedit.extension.toRightCenter
import io.bashpsk.emptylibs.imageedit.extension.toTopCenter
import io.bashpsk.emptylibs.imageedit.extension.toTopRight
import io.bashpsk.emptylibs.imageutils.shape.ImageShape
import io.bashpsk.emptylibs.imageutils.shape.toPath
import kotlin.math.abs

internal fun DrawScope.drawImageEditItem(items: ImageEditItems, textMeasurer: TextMeasurer) {

    clipRect {

        when (items) {

            is ImageEditItems.EraseItem -> drawEditErase(item = items)
            is ImageEditItems.ImageItem -> drawEditImage(item = items)
            is ImageEditItems.PathItem -> drawEditPath(item = items)
            is ImageEditItems.ShapeItem -> drawEditShape(item = items)
            is ImageEditItems.TextItem -> drawEditText(item = items, textMeasurer = textMeasurer)
        }
    }
}

internal fun DrawScope.drawImageEditItemHandle(items: ImageEditItems, config: ImageEditConfig) {

    when (items) {

        is ImageEditItems.EraseItem -> {}
        is ImageEditItems.ImageItem -> drawEditImageHandle(item = items, config = config)
        is ImageEditItems.PathItem -> {}
        is ImageEditItems.ShapeItem -> drawEditShapeHandle(item = items, config = config)
        is ImageEditItems.TextItem -> drawEditTextHandle(item = items, config = config)
    }
}

private fun DrawScope.drawEditErase(item: ImageEditItems.EraseItem) {

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
        color = Color.Transparent,
        style = item.style,
        blendMode = BlendMode.Clear
    )
}

private fun DrawScope.drawEditImage(item: ImageEditItems.ImageItem) {

    clipRect {

        translate(left = item.position.x, top = item.position.y) {

            drawImage(
                image = item.bitmap,
                dstOffset = Offset.Zero.round(),
                dstSize = item.size.toIntSize()
            )
        }
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

    drawPath(path = smoothedPath, color = item.color, style = item.style)
}

private fun DrawScope.drawEditShape(item: ImageEditItems.ShapeItem) {

    translate(left = item.position.x, top = item.position.y) {

        drawPath(
            path = item.shape.toPath(canvasSize = item.size),
            color = item.color,
            style = item.style
        )
    }
}

private fun DrawScope.drawEditText(item: ImageEditItems.TextItem, textMeasurer: TextMeasurer) {

    translate(left = item.position.x, top = item.position.y) {

        val textLayoutResult = textMeasurer.measure(
            text = item.content,
            style = item.style,
            constraints = Constraints(maxWidth = item.size.width.toInt())
        )

        drawText(textLayoutResult = textLayoutResult)
    }
}

private fun DrawScope.drawEditImageHandle(item: ImageEditItems.ImageItem, config: ImageEditConfig) {

    val topLeft = item.position
    val topRight = item.position.toTopRight(size = item.size)
    val bottomLeft = item.position.toBottomLeft(size = item.size)
    val bottomRight = item.position.toBottomRight(size = item.size)

    val topCenter = item.position.toTopCenter(size = item.size)
    val bottomCenter = item.position.toBottomCenter(size = item.size)
    val leftCenter = item.position.toLeftCenter(size = item.size)
    val rightCenter = item.position.toRightCenter(size = item.size)

    drawEditBox(topLeft = topLeft, rectSize = item.size, config = config)
    drawEditBorder(topLeft = topLeft, rectSize = item.size, config = config)
    drawHandle(corner = EditItemCorner.TOP_LEFT, center = topLeft, config = config)
    drawHandle(corner = EditItemCorner.TOP_RIGHT, center = topRight, config = config)
    drawHandle(corner = EditItemCorner.BOTTOM_LEFT, center = bottomLeft, config = config)
    drawHandle(corner = EditItemCorner.BOTTOM_RIGHT, center = bottomRight, config = config)
    drawHandle(corner = EditItemCorner.TOP_CENTRE, center = topCenter, config = config)
    drawHandle(corner = EditItemCorner.BOTTOM_CENTRE, center = bottomCenter, config = config)
    drawHandle(corner = EditItemCorner.LEFT_CENTRE, center = leftCenter, config = config)
    drawHandle(corner = EditItemCorner.RIGHT_CENTRE, center = rightCenter, config = config)
    drawPlus(topLeft = topLeft, rectSize = item.size, config = config)
}

private fun DrawScope.drawEditShapeHandle(item: ImageEditItems.ShapeItem, config: ImageEditConfig) {

    val topLeft = item.position
    val topRight = item.position.toTopRight(size = item.size)
    val bottomLeft = item.position.toBottomLeft(size = item.size)
    val bottomRight = item.position.toBottomRight(size = item.size)

    val topCenter = item.position.toTopCenter(size = item.size)
    val bottomCenter = item.position.toBottomCenter(size = item.size)
    val leftCenter = item.position.toLeftCenter(size = item.size)
    val rightCenter = item.position.toRightCenter(size = item.size)

    drawEditBox(topLeft = topLeft, rectSize = item.size, config = config)
    drawEditBorder(topLeft = topLeft, rectSize = item.size, config = config)
    drawHandle(corner = EditItemCorner.TOP_LEFT, center = topLeft, config = config)
    drawHandle(corner = EditItemCorner.TOP_RIGHT, center = topRight, config = config)
    drawHandle(corner = EditItemCorner.BOTTOM_LEFT, center = bottomLeft, config = config)
    drawHandle(corner = EditItemCorner.BOTTOM_RIGHT, center = bottomRight, config = config)
    drawHandle(corner = EditItemCorner.TOP_CENTRE, center = topCenter, config = config)
    drawHandle(corner = EditItemCorner.BOTTOM_CENTRE, center = bottomCenter, config = config)
    drawHandle(corner = EditItemCorner.LEFT_CENTRE, center = leftCenter, config = config)
    drawHandle(corner = EditItemCorner.RIGHT_CENTRE, center = rightCenter, config = config)
    drawPlus(topLeft = topLeft, rectSize = item.size, config = config)
}

private fun DrawScope.drawEditTextHandle(item: ImageEditItems.TextItem, config: ImageEditConfig) {

    val topLeft = item.position
    val topRight = item.position.toTopRight(size = item.size)
    val bottomLeft = item.position.toBottomLeft(size = item.size)
    val bottomRight = item.position.toBottomRight(size = item.size)

    val topCenter = item.position.toTopCenter(size = item.size)
    val bottomCenter = item.position.toBottomCenter(size = item.size)
    val leftCenter = item.position.toLeftCenter(size = item.size)
    val rightCenter = item.position.toRightCenter(size = item.size)

    drawEditBox(topLeft = topLeft, rectSize = item.size, config = config)
    drawEditBorder(topLeft = topLeft, rectSize = item.size, config = config)
    drawHandle(corner = EditItemCorner.TOP_LEFT, center = topLeft, config = config)
    drawHandle(corner = EditItemCorner.TOP_RIGHT, center = topRight, config = config)
    drawHandle(corner = EditItemCorner.BOTTOM_LEFT, center = bottomLeft, config = config)
    drawHandle(corner = EditItemCorner.BOTTOM_RIGHT, center = bottomRight, config = config)
    drawHandle(corner = EditItemCorner.TOP_CENTRE, center = topCenter, config = config)
    drawHandle(corner = EditItemCorner.BOTTOM_CENTRE, center = bottomCenter, config = config)
    drawHandle(corner = EditItemCorner.LEFT_CENTRE, center = leftCenter, config = config)
    drawHandle(corner = EditItemCorner.RIGHT_CENTRE, center = rightCenter, config = config)
    drawPlus(topLeft = topLeft, rectSize = item.size, config = config)
}

/**
 * Draws a handle for a specific corner of the crop selection.
 *
 * @param corner The [EditItemCorner] for which to draw the handle.
 * @param center The center [Offset] of the handle.
 * @param config The [ImageEditConfig] to use for styling the handle.
 */
private fun DrawScope.drawHandle(corner: EditItemCorner, center: Offset, config: ImageEditConfig) {

    val handleLength = when (corner) {

        EditItemCorner.LEFT_CENTRE, EditItemCorner.RIGHT_CENTRE -> config.centerHandleWidth.toPx()
        EditItemCorner.TOP_CENTRE, EditItemCorner.BOTTOM_CENTRE -> config.centerHandleWidth.toPx()
        else -> config.handleWidth.toPx()
    }

    when (corner) {

        EditItemCorner.TOP_LEFT -> {

            drawKropLine(
                start = center,
                end = Offset(center.x + handleLength, center.y),
                config = config
            )

            drawKropLine(
                start = center,
                end = Offset(center.x, center.y + handleLength),
                config = config
            )
        }

        EditItemCorner.TOP_RIGHT -> {

            drawKropLine(
                start = center,
                end = Offset(center.x - handleLength, center.y),
                config = config
            )

            drawKropLine(
                start = center,
                end = Offset(center.x, center.y + handleLength),
                config = config
            )
        }

        EditItemCorner.BOTTOM_LEFT -> {

            drawKropLine(
                start = center,
                end = Offset(center.x + handleLength, center.y),
                config = config
            )

            drawKropLine(
                start = center,
                end = Offset(center.x, center.y - handleLength),
                config = config
            )
        }

        EditItemCorner.BOTTOM_RIGHT -> {

            drawKropLine(
                start = center,
                end = Offset(center.x - handleLength, center.y),
                config = config
            )

            drawKropLine(
                start = center,
                end = Offset(center.x, center.y - handleLength),
                config = config
            )
        }

        EditItemCorner.TOP_CENTRE, EditItemCorner.BOTTOM_CENTRE -> {

            drawKropLine(
                start = Offset(center.x - handleLength / 2, center.y),
                end = Offset(center.x + handleLength / 2, center.y),
                config = config
            )
        }

        EditItemCorner.LEFT_CENTRE, EditItemCorner.RIGHT_CENTRE -> {

            drawKropLine(
                start = Offset(center.x, center.y - handleLength / 2),
                end = Offset(center.x, center.y + handleLength / 2),
                config = config
            )
        }
    }
}

/**
 * Draws a plus (+) symbol centered within the specified rectangle.
 *
 * This function is used to indicate the center of the crop area or a target point.
 * The appearance of the plus symbol (size, color, thickness) is determined by the
 * [ImageEditConfig].
 *
 * @param topLeft The [Offset] representing the top-left corner of the rectangle within which the
 * plus symbol will be drawn.
 * @param rectSize The [Size] of the rectangle. The plus symbol will be centered within this size.
 * @param config The [ImageEditConfig] object that defines the visual properties of the plus symbol,
 * such as its size, color, and thickness.
 */
private fun DrawScope.drawPlus(topLeft: Offset, rectSize: Size, config: ImageEditConfig) {

    val centerX = topLeft.x + rectSize.width / 2
    val centerY = topLeft.y + rectSize.height / 2

    drawLine(
        start = Offset(centerX - config.targetSize.toPx() / 2, centerY),
        end = Offset(centerX + config.targetSize.toPx() / 2, centerY),
        color = config.targetColor,
        strokeWidth = config.targetThickness.toPx(),
        cap = StrokeCap.Round
    )

    drawLine(
        start = Offset(centerX, centerY - config.targetSize.toPx() / 2),
        end = Offset(centerX, centerY + config.targetSize.toPx() / 2),
        color = config.targetColor,
        strokeWidth = config.targetThickness.toPx(),
        cap = StrokeCap.Round
    )
}

/**
 * Draws a rectangular border for the cropping area.
 *
 * This function is an extension function for `DrawScope` and is used to draw the visual boundary of
 * the crop selection.
 *
 * @param topLeft The [Offset] representing the top-left corner of the border.
 * @param rectSize The [Size] of the rectangle to be drawn.
 * @param config The [ImageEditConfig] object containing styling parameters for the border, such as
 * thickness and color.
 */
private fun DrawScope.drawEditBorder(topLeft: Offset, rectSize: Size, config: ImageEditConfig) {

    drawRect(
        topLeft = topLeft,
        size = rectSize,
        style = Stroke(width = config.borderThickness.toPx()),
        color = config.borderColor
    )
}

private fun DrawScope.drawEditBox(topLeft: Offset, rectSize: Size, config: ImageEditConfig) {

    drawRect(topLeft = topLeft, size = rectSize, style = Fill, color = config.itemBoxColor)
}

/**
 * Draws a single line segment for a crop handle.
 *
 * This is a helper function used by [drawHandle] to construct the visual representation of the crop
 * handles.
 *
 * @param start The starting [Offset] of the line.
 * @param end The ending [Offset] of the line.
 * @param config The [ImageEditConfig] containing styling information for the line, such as color
 * and thickness.
 */
private fun DrawScope.drawKropLine(start: Offset, end: Offset, config: ImageEditConfig) {

    drawLine(
        start = start,
        end = end,
        color = config.handleColor,
        strokeWidth = config.handleHeight.toPx(),
        cap = StrokeCap.Round
    )
}

/**
 * Draws a preview of the krop shape.
 *
 * This function is used to display a visual representation of the selected crop shape.
 * It takes a [ImageShape] and a [Color] as input and draws the shape's path with a stroke style.
 *
 * @param shape The [ImageShape] to draw.
 * @param color The [Color] to use for drawing the shape's path.
 */
internal fun DrawScope.drawEditShapePreview(shape: ImageShape, color: Color) {

    val shapePath = shape.toPath(canvasSize = size)

    drawPath(path = shapePath, color = color, style = Fill)
}