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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntSize
import io.bashpsk.emptylibs.composeutils.shape.PathShape
import io.bashpsk.emptylibs.composeutils.shape.toPath
import io.bashpsk.emptylibs.imageedit.extension.toBottomCenter
import io.bashpsk.emptylibs.imageedit.extension.toBottomLeft
import io.bashpsk.emptylibs.imageedit.extension.toBottomRight
import io.bashpsk.emptylibs.imageedit.extension.toLeftCenter
import io.bashpsk.emptylibs.imageedit.extension.toRightCenter
import io.bashpsk.emptylibs.imageedit.extension.toTopCenter
import io.bashpsk.emptylibs.imageedit.extension.toTopRight
import kotlin.math.abs

/**
 * Draws an [ImageEditItems] on the [DrawScope].
 *
 * This function acts as a dispatcher, calling the appropriate drawing function based on the type
 * of [ImageEditItems] provided. It clips the drawing to the bounds of the current [DrawScope].
 *
 * @param items The [ImageEditItems] to be drawn. This can be a brush stroke, an eraser mark,
 * an image, a shape, or text.
 * @param textMeasurer A [TextMeasurer] required for drawing text items, used to calculate text
 * layout.
 */
internal fun DrawScope.drawImageEditItem(items: ImageEditItems, textMeasurer: TextMeasurer) {

    clipRect {

        when (items) {

            is ImageEditItems.BrushItem -> drawEditBrush(item = items)
            is ImageEditItems.EraseItem -> drawEditErase(item = items)
            is ImageEditItems.ImageItem -> drawEditImage(item = items)
            is ImageEditItems.ShapeItem -> drawEditShape(item = items)
            is ImageEditItems.TextItem -> drawEditText(item = items, textMeasurer = textMeasurer)
        }
    }
}

/**
 * Draws the handles for an [ImageEditItems] on the canvas.
 *
 * This function determines the type of [ImageEditItems] and calls the appropriate
 * drawing function for its handles (e.g., `drawEditImageHandle`, `drawEditShapeHandle`,
 * or `drawEditTextHandle`). Brush and Erase items do not have handles, so they are ignored.
 *
 * @param items The [ImageEditItems] for which to draw the handles.
 * @param config The [ImageEditConfig] to use for styling the handles.
 */
internal fun DrawScope.drawImageEditItemHandle(items: ImageEditItems, config: ImageEditConfig) {

    when (items) {

        is ImageEditItems.BrushItem -> {}
        is ImageEditItems.EraseItem -> {}
        is ImageEditItems.ImageItem -> drawEditImageHandle(item = items, config = config)
        is ImageEditItems.ShapeItem -> drawEditShapeHandle(item = items, config = config)
        is ImageEditItems.TextItem -> drawEditTextHandle(item = items, config = config)
    }
}

/**
 * Draws a brush stroke on the canvas.
 *
 * This function takes an [ImageEditItems.BrushItem] and draws its path with smoothing.
 * The path is smoothed using quadratic Bezier curves if the distance between consecutive points
 * exceeds the specified smoothness threshold.
 *
 * @param item The [ImageEditItems.BrushItem] containing the path, color, style, and smoothness
 * information for the brush stroke.
 */
private fun DrawScope.drawEditBrush(item: ImageEditItems.BrushItem) {

    val smoothedPath = Path().apply {

        if (item.path.isNotEmpty()) {

            moveTo(x = item.path.first().x, y = item.path.first().y)
            if (item.path.size == 1) lineTo(x = item.path.first().x, y = item.path.first().y)

            item.path.zipWithNext().forEach { (from, to) ->

                val dx = abs(from.x - to.x)
                val dy = abs(from.y - to.y)

                if (dx >= item.smoothness || dy >= item.smoothness) quadraticTo(
                    x1 = (from.x + to.x) / 2,
                    y1 = (from.y + to.y) / 2,
                    x2 = to.x,
                    y2 = to.y
                )
            }
        }
    }

    drawPath(path = smoothedPath, color = item.color, style = item.style)
}

/**
 * Draws an erase path on the canvas.
 *
 * This function takes an [ImageEditItems.EraseItem] and renders it as a path with a transparent
 * color and a `BlendMode.Clear` to effectively erase portions of the underlying content. The path
 * is smoothed based on the `smoothness` property of the [item].
 *
 * @param item The [ImageEditItems.EraseItem] containing the path data, style, and smoothness.
 */
private fun DrawScope.drawEditErase(item: ImageEditItems.EraseItem) {

    val smoothedPath = Path().apply {

        if (item.path.isNotEmpty()) {

            moveTo(x = item.path.first().x, y = item.path.first().y)
            if (item.path.size == 1) lineTo(x = item.path.first().x, y = item.path.first().y)

            item.path.zipWithNext().forEach { (from, to) ->

                val dx = abs(from.x - to.x)
                val dy = abs(from.y - to.y)

                if (dx >= item.smoothness || dy >= item.smoothness) quadraticTo(
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
        color = Color.Transparent,
        style = item.style,
        blendMode = BlendMode.Clear
    )
}

/**
 * Draws an image item onto the canvas.
 *
 * This function handles the drawing of an [ImageEditItems.ImageItem]. It first checks if the
 * bitmap within the item is available. If it is, the function clips the drawing area to a rectangle
 * defined by the canvas size. It then translates the drawing context to the item's position and
 * clips the drawing path to the item's shape. Finally, it draws the bitmap image at the specified
 * offset and size within the clipped path.
 *
 * @param item The [ImageEditItems.ImageItem] to be drawn. This item contains the bitmap, position,
 * size, and shape information for the image.
 */
private fun DrawScope.drawEditImage(item: ImageEditItems.ImageItem) {

    item.bitmap?.let { bitmap ->

        clipRect {

            translate(left = item.position.x, top = item.position.y) {

                clipPath(path = item.shape.toPath(canvasSize = item.size)) {

                    drawImage(
                        image = bitmap,
                        dstOffset = Offset.Zero.round(),
                        dstSize = item.size.toIntSize()
                    )
                }
            }
        }
    }
}

/**
 * Draws a shape item on the canvas.
 *
 * This function translates the drawing context to the shape's position and then draws the shape's
 * path with the specified color and style.
 *
 * @param item The [ImageEditItems.ShapeItem] to be drawn, containing information about the
 * shape's path, color, style, position, and size.
 */
private fun DrawScope.drawEditShape(item: ImageEditItems.ShapeItem) {

    translate(left = item.position.x, top = item.position.y) {

        drawPath(
            path = item.shape.toPath(canvasSize = item.size),
            color = item.color,
            style = item.style
        )
    }
}

/**
 * Draws a text item onto the canvas.
 *
 * This function is an extension function for `DrawScope` and is responsible for rendering
 * a text item based on the provided [ImageEditItems.TextItem] and [TextMeasurer].
 * It translates the canvas to the text item's position, measures the text with the given
 * style and constraints, and then draws the measured text.
 *
 * @param item The [ImageEditItems.TextItem] containing the content, style, position, and size
 * of the text to be drawn.
 * @param textMeasurer The [TextMeasurer] used to calculate the layout of the text.
 */
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

/**
 * Draws the editing handles for an image item.
 *
 * This function calculates the positions of all eight handles (corners and midpoints)
 * around the image item. It then calls helper functions to draw the bounding box,
 * the border, each individual handle, and a central plus (+) marker.
 *
 * @param item The [ImageEditItems.ImageItem] for which to draw the handles.
 * This item contains the position and size of the image.
 * @param config The [ImageEditConfig] used to style the handles, border, and box.
 * This includes colors, thicknesses, and sizes for the various elements.
 */
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

/**
 * Draws the editing handles for a shape item.
 *
 * This function calculates the positions of the corners and center points of the shape's
 * bounding box and then draws the edit box, border, and individual handles for each of these
 * points. It also draws a plus symbol at the center of the shape.
 *
 * @param item The [ImageEditItems.ShapeItem] for which to draw the handles.
 * @param config The [ImageEditConfig] to use for styling the handles and other visual elements.
 */
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

/**
 * Draws the handles for an editable text item.
 *
 * This function calculates the positions of the corners and center points of the text item's
 * bounding box. It then calls helper functions to draw:
 * - A filled box representing the text item's area ([drawEditBox]).
 * - A border around the text item ([drawEditBorder]).
 * - Handles at each corner and center point of the border ([drawHandle]).
 * - A plus symbol at the center of the text item ([drawPlus]).
 *
 * The appearance of these elements is determined by the provided [ImageEditConfig].
 *
 * @param item The [ImageEditItems.TextItem] for which to draw the handles.
 * @param config The [ImageEditConfig] that defines the visual style of the handles.
 */
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
 * Draws a handle for a specific corner or edge of the editable item.
 *
 * Handles are visual cues that allow users to resize or manipulate the item.
 * The appearance and size of the handle are determined by the provided [ImageEditConfig].
 * For corner handles, two perpendicular lines are drawn. For edge handles (center handles),
 * a single line is drawn along the edge.
 *
 * @param corner The [EditItemCorner] specifying which handle to draw
 * (e.g., TOP_LEFT, RIGHT_CENTRE).
 * @param center The [Offset] representing the center point from which the handle lines will
 * originate.
 * @param config The [ImageEditConfig] used to style the handle, including its color, thickness, and
 * length.
 */
private fun DrawScope.drawHandle(corner: EditItemCorner, center: Offset, config: ImageEditConfig) {

    val handleLength = when (corner) {

        EditItemCorner.LEFT_CENTRE, EditItemCorner.RIGHT_CENTRE -> config.centerHandleWidth.toPx()
        EditItemCorner.TOP_CENTRE, EditItemCorner.BOTTOM_CENTRE -> config.centerHandleWidth.toPx()
        else -> config.handleWidth.toPx()
    }

    when (corner) {

        EditItemCorner.TOP_LEFT -> {

            drawEditLine(
                start = center,
                end = Offset(center.x + handleLength, center.y),
                config = config
            )

            drawEditLine(
                start = center,
                end = Offset(center.x, center.y + handleLength),
                config = config
            )
        }

        EditItemCorner.TOP_RIGHT -> {

            drawEditLine(
                start = center,
                end = Offset(center.x - handleLength, center.y),
                config = config
            )

            drawEditLine(
                start = center,
                end = Offset(center.x, center.y + handleLength),
                config = config
            )
        }

        EditItemCorner.BOTTOM_LEFT -> {

            drawEditLine(
                start = center,
                end = Offset(center.x + handleLength, center.y),
                config = config
            )

            drawEditLine(
                start = center,
                end = Offset(center.x, center.y - handleLength),
                config = config
            )
        }

        EditItemCorner.BOTTOM_RIGHT -> {

            drawEditLine(
                start = center,
                end = Offset(center.x - handleLength, center.y),
                config = config
            )

            drawEditLine(
                start = center,
                end = Offset(center.x, center.y - handleLength),
                config = config
            )
        }

        EditItemCorner.TOP_CENTRE, EditItemCorner.BOTTOM_CENTRE -> {

            drawEditLine(
                start = Offset(center.x - handleLength / 2, center.y),
                end = Offset(center.x + handleLength / 2, center.y),
                config = config
            )
        }

        EditItemCorner.LEFT_CENTRE, EditItemCorner.RIGHT_CENTRE -> {

            drawEditLine(
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
 * This function is used to indicate the center of an editable item or a target point.
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
 * Draws a rectangular border for an editable item.
 *
 * This function is an extension function for `DrawScope` and is used to draw the visual boundary of
 * an editable item, such as an image, shape, or text box.
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

/**
 * Draws the bounding box for an editable item.
 *
 * This function draws a filled rectangle that represents the editable area of an item.
 * The appearance of the box (e.g., color) is determined by the [ImageEditConfig].
 *
 * @param topLeft The [Offset] representing the top-left corner of the bounding box.
 * @param rectSize The [Size] of the bounding box.
 * @param config The [ImageEditConfig] object that defines the visual properties of the box,
 * such as its color.
 */
private fun DrawScope.drawEditBox(topLeft: Offset, rectSize: Size, config: ImageEditConfig) {

    drawRect(topLeft = topLeft, size = rectSize, style = Fill, color = config.itemBoxColor)
}

/**
 * Draws a single line segment for an edit item handle.
 *
 * This is a helper function used by [drawHandle] to construct the visual representation of the edit
 * item handles.
 *
 * @param start The starting [Offset] of the line.
 * @param end The ending [Offset] of the line.
 * @param config The [ImageEditConfig] containing styling information for the line, such as color
 * and thickness.
 */
private fun DrawScope.drawEditLine(start: Offset, end: Offset, config: ImageEditConfig) {

    drawLine(
        start = start,
        end = end,
        color = config.handleColor,
        strokeWidth = config.handleHeight.toPx(),
        cap = StrokeCap.Round
    )
}

/**
 * Draws a preview of the edit shape.
 *
 * This function is used to display a visual representation of the selected edit shape.
 * It takes an [PathShape] and a [Color] as input and draws the shape's path with a fill style.
 *
 * @param shape The [PathShape] to draw.
 * @param color The [Color] to use for drawing the shape's path.
 */
internal fun DrawScope.drawEditShapePreview(shape: PathShape, color: Color) {

    val shapePath = shape.toPath(canvasSize = size)

    drawPath(path = shapePath, color = color, style = Fill)
}