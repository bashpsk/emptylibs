package io.bashpsk.emptylibs.imagekrop.crop

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.imagekrop.offset.toBottomCenter
import io.bashpsk.emptylibs.imagekrop.offset.toBottomLeft
import io.bashpsk.emptylibs.imagekrop.offset.toBottomRight
import io.bashpsk.emptylibs.imagekrop.offset.toLeftCenter
import io.bashpsk.emptylibs.imagekrop.offset.toRightCenter
import io.bashpsk.emptylibs.imagekrop.offset.toTopCenter
import io.bashpsk.emptylibs.imagekrop.offset.toTopRight
import io.bashpsk.emptylibs.imageutils.shape.ImageShape
import io.bashpsk.emptylibs.imageutils.shape.toPath

/**
 * Draws the complete Krop (crop) UI, including the overlay, borders, handles, and target.
 *
 * This function orchestrates the drawing of all visual elements of the cropping interface.
 * It calculates the positions of all handles and then calls individual drawing functions
 * to render each component.
 *
 * @param kropShape The [ImageShape] defining the shape of the crop area (e.g., Rectangle, Oval).
 * @param topLeft The [Offset] of the top-left corner of the cropping rectangle.
 * @param rectSize The [Size] of the cropping rectangle.
 * @param config The [KropConfig] object containing all the styling parameters for the
 * Krop UI elements.
 */
internal fun DrawScope.drawKropHandle(
    kropShape: ImageShape,
    topLeft: Offset,
    rectSize: Size,
    config: KropConfig
) {

    val topLeft = topLeft
    val topRight = topLeft.toTopRight(size = rectSize)
    val bottomLeft = topLeft.toBottomLeft(size = rectSize)
    val bottomRight = topLeft.toBottomRight(size = rectSize)

    val topCenter = topLeft.toTopCenter(size = rectSize)
    val bottomCenter = topLeft.toBottomCenter(size = rectSize)
    val leftCenter = topLeft.toLeftCenter(size = rectSize)
    val rightCenter = topLeft.toRightCenter(size = rectSize)

    drawIntoCanvas {

        drawKropOverlay(
            kropShape = kropShape,
            topLeft = topLeft,
            rectSize = rectSize,
            config = config
        )

        drawKropShapeBorder(
            kropShape = kropShape,
            topLeft = topLeft,
            rectSize = rectSize,
            config = config
        )

        drawKropBorder(topLeft = topLeft, rectSize = rectSize, config = config)
        drawPlus(topLeft = topLeft, rectSize = rectSize, config = config)
        drawHandle(corner = KropCorner.TOP_LEFT, center = topLeft, config = config)
        drawHandle(corner = KropCorner.TOP_RIGHT, center = topRight, config = config)
        drawHandle(corner = KropCorner.BOTTOM_LEFT, center = bottomLeft, config = config)
        drawHandle(corner = KropCorner.BOTTOM_RIGHT, center = bottomRight, config = config)
        drawHandle(corner = KropCorner.TOP_CENTRE, center = topCenter, config = config)
        drawHandle(corner = KropCorner.BOTTOM_CENTRE, center = bottomCenter, config = config)
        drawHandle(corner = KropCorner.LEFT_CENTRE, center = leftCenter, config = config)
        drawHandle(corner = KropCorner.RIGHT_CENTRE, center = rightCenter, config = config)
    }
}

/**
 * Draws a handle for a specific corner of the crop selection.
 *
 * @param corner The [KropCorner] for which to draw the handle.
 * @param center The center [Offset] of the handle.
 * @param config The [KropConfig] to use for styling the handle.
 */
internal fun DrawScope.drawHandle(corner: KropCorner, center: Offset, config: KropConfig) {

    val handleLength = when (corner) {

        KropCorner.LEFT_CENTRE, KropCorner.RIGHT_CENTRE -> config.centerHandleWidth.toPx()
        KropCorner.TOP_CENTRE, KropCorner.BOTTOM_CENTRE -> config.centerHandleWidth.toPx()
        else -> config.handleWidth.toPx()
    }

    when (corner) {

        KropCorner.TOP_LEFT -> {

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

        KropCorner.TOP_RIGHT -> {

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

        KropCorner.BOTTOM_LEFT -> {

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

        KropCorner.BOTTOM_RIGHT -> {

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

        KropCorner.TOP_CENTRE, KropCorner.BOTTOM_CENTRE -> {

            drawKropLine(
                start = Offset(center.x - handleLength / 2, center.y),
                end = Offset(center.x + handleLength / 2, center.y),
                config = config
            )
        }

        KropCorner.LEFT_CENTRE, KropCorner.RIGHT_CENTRE -> {

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
 * [KropConfig].
 *
 * @param topLeft The [Offset] representing the top-left corner of the rectangle within which the
 * plus symbol will be drawn.
 * @param rectSize The [Size] of the rectangle. The plus symbol will be centered within this size.
 * @param config The [KropConfig] object that defines the visual properties of the plus symbol,
 * such as its size, color, and thickness.
 */
internal fun DrawScope.drawPlus(
    topLeft: Offset,
    rectSize: Size,
    config: KropConfig
) {

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
 * @param config The [KropConfig] object containing styling parameters for the border, such as
 * thickness and color.
 */
internal fun DrawScope.drawKropBorder(topLeft: Offset, rectSize: Size, config: KropConfig) {

    drawRect(
        topLeft = topLeft,
        size = rectSize,
        style = Stroke(width = config.borderThickness.toPx()),
        color = config.borderColor
    )
}

/**
 * Draws the overlay outside the cropping shape.
 *
 * This function creates a visual effect where the area outside the selected cropping shape is
 * dimmed or colored, highlighting the area to be cropped.
 *
 * @param kropShape The shape of the cropping area (e.g., Rectangle, Oval).
 * @param topLeft The top-left [Offset] of the cropping rectangle.
 * @param bottomRight The bottom-right [Offset] of the cropping rectangle.
 * @param config The [KropConfig] containing styling information for the overlay, such as the
 * overlay color.
 */
internal fun DrawScope.drawKropOverlay(
    kropShape: ImageShape,
    topLeft: Offset,
    rectSize: Size,
    config: KropConfig
) {

    val shapePath = kropShape.toPath(canvasSize = rectSize)

    translate(left = topLeft.x, top = topLeft.y) {

        clipPath(path = shapePath, clipOp = ClipOp.Difference) {

            drawRect(
                topLeft = Offset(x = -topLeft.x, y = -topLeft.y),
                size = size,
                color = config.overlayColor
            )
        }
    }
}

/**
 * Draws the border of the krop shape.
 *
 * @param kropShape The [ImageShape] to draw the border for.
 * @param topLeft The top-left [Offset] of the crop area.
 * @param bottomRight The bottom-right [Offset] of the crop area.
 * @param config The [KropConfig] containing styling for the border.
 */
internal fun DrawScope.drawKropShapeBorder(
    kropShape: ImageShape,
    topLeft: Offset,
    rectSize: Size,
    config: KropConfig
) {

    val shapePath = kropShape.toPath(canvasSize = rectSize)

    translate(left = topLeft.x, top = topLeft.y) {

        drawPath(
            path = shapePath,
            style = Stroke(width = config.shapeBorder.toPx()),
            color = config.shapeColor
        )
    }
}

/**
 * Draws a single line segment for a crop handle.
 *
 * This is a helper function used by [drawHandle] to construct the visual representation of the crop
 * handles.
 *
 * @param start The starting [Offset] of the line.
 * @param end The ending [Offset] of the line.
 * @param config The [KropConfig] containing styling information for the line, such as color and
 * thickness.
 */
private fun DrawScope.drawKropLine(start: Offset, end: Offset, config: KropConfig) {

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
 * @param kropShape The [ImageShape] to draw.
 * @param shapeColor The [Color] to use for drawing the shape's path.
 */
internal fun DrawScope.drawKropShapePreview(kropShape: ImageShape, shapeColor: Color) {

    val shapePath = kropShape.toPath(canvasSize = size)

    drawPath(
        path = shapePath,
        color = shapeColor,
        style = Stroke(width = 2.dp.toPx())
    )
}