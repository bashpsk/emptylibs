package io.bashpsk.emptylibs.imagekrop.crop

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp

/**
 * Draws a handle for a specific corner of the crop selection.
 *
 * @param corner The [KropCorner] for which to draw the handle.
 * @param center The center [Offset] of the handle.
 * @param kropConfig The [KropConfig] to use for styling the handle.
 */
internal fun DrawScope.drawHandle(corner: KropCorner, center: Offset, kropConfig: KropConfig) {

    val handleLength = when (corner) {

        KropCorner.LEFT_CENTRE, KropCorner.RIGHT_CENTRE -> kropConfig.centerHandleWidth.toPx()
        KropCorner.TOP_CENTRE, KropCorner.BOTTOM_CENTRE -> kropConfig.centerHandleWidth.toPx()
        else -> kropConfig.handleWidth.toPx()
    }

    when (corner) {

        KropCorner.TOP_LEFT -> {

            drawKropLine(
                start = center,
                end = Offset(center.x + handleLength, center.y),
                kropConfig = kropConfig
            )

            drawKropLine(
                start = center,
                end = Offset(center.x, center.y + handleLength),
                kropConfig = kropConfig
            )
        }

        KropCorner.TOP_RIGHT -> {

            drawKropLine(
                start = center,
                end = Offset(center.x - handleLength, center.y),
                kropConfig = kropConfig
            )

            drawKropLine(
                start = center,
                end = Offset(center.x, center.y + handleLength),
                kropConfig = kropConfig
            )
        }

        KropCorner.BOTTOM_LEFT -> {

            drawKropLine(
                start = center,
                end = Offset(center.x + handleLength, center.y),
                kropConfig = kropConfig
            )

            drawKropLine(
                start = center,
                end = Offset(center.x, center.y - handleLength),
                kropConfig = kropConfig
            )
        }

        KropCorner.BOTTOM_RIGHT -> {

            drawKropLine(
                start = center,
                end = Offset(center.x - handleLength, center.y),
                kropConfig = kropConfig
            )

            drawKropLine(
                start = center,
                end = Offset(center.x, center.y - handleLength),
                kropConfig = kropConfig
            )
        }

        KropCorner.TOP_CENTRE, KropCorner.BOTTOM_CENTRE -> {

            drawKropLine(
                start = Offset(center.x - handleLength / 2, center.y),
                end = Offset(center.x + handleLength / 2, center.y),
                kropConfig = kropConfig
            )
        }

        KropCorner.LEFT_CENTRE, KropCorner.RIGHT_CENTRE -> {

            drawKropLine(
                start = Offset(center.x, center.y - handleLength / 2),
                end = Offset(center.x, center.y + handleLength / 2),
                kropConfig = kropConfig
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
 * @param kropConfig The [KropConfig] object that defines the visual properties of the plus symbol,
 * such as its size, color, and thickness.
 */
internal fun DrawScope.drawPlus(
    topLeft: Offset,
    rectSize: Size,
    kropConfig: KropConfig
) {

    val centerX = topLeft.x + rectSize.width / 2
    val centerY = topLeft.y + rectSize.height / 2

    drawLine(
        start = Offset(centerX - kropConfig.targetSize.toPx() / 2, centerY),
        end = Offset(centerX + kropConfig.targetSize.toPx() / 2, centerY),
        color = kropConfig.targetColor,
        strokeWidth = kropConfig.targetThickness.toPx(),
        cap = StrokeCap.Round
    )

    drawLine(
        start = Offset(centerX, centerY - kropConfig.targetSize.toPx() / 2),
        end = Offset(centerX, centerY + kropConfig.targetSize.toPx() / 2),
        color = kropConfig.targetColor,
        strokeWidth = kropConfig.targetThickness.toPx(),
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
 * @param kropConfig The [KropConfig] object containing styling parameters for the border, such as
 * thickness and color.
 */
internal fun DrawScope.drawKropBorder(topLeft: Offset, rectSize: Size, kropConfig: KropConfig) {

    drawRect(
        topLeft = topLeft,
        size = rectSize,
        style = Stroke(width = kropConfig.borderThickness.toPx()),
        color = kropConfig.borderColor
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
 * @param kropConfig The [KropConfig] containing styling information for the overlay, such as the
 * overlay color.
 */
internal fun DrawScope.drawKropOverlay(
    kropShape: KropShape,
    topLeft: Offset,
    bottomRight: Offset,
    kropConfig: KropConfig
) {

    val canvasSize = Size(width = bottomRight.x - topLeft.x, height = bottomRight.y - topLeft.y)
    val shapePath = kropShape.toPath(canvasSize = canvasSize)

    translate(left = topLeft.x, top = topLeft.y) {

        clipPath(path = shapePath, clipOp = ClipOp.Difference) {

            drawRect(
                topLeft = Offset(x = -topLeft.x, y = -topLeft.y),
                size = size,
                color = kropConfig.overlayColor
            )
        }
    }
}

/**
 * Draws the border of the krop shape.
 *
 * @param kropShape The [KropShape] to draw the border for.
 * @param topLeft The top-left [Offset] of the crop area.
 * @param bottomRight The bottom-right [Offset] of the crop area.
 * @param kropConfig The [KropConfig] containing styling for the border.
 */
internal fun DrawScope.drawKropShapeBorder(
    kropShape: KropShape,
    topLeft: Offset,
    bottomRight: Offset,
    kropConfig: KropConfig
) {

    val canvasSize = Size(width = bottomRight.x - topLeft.x, height = bottomRight.y - topLeft.y)
    val shapePath = kropShape.toPath(canvasSize = canvasSize)

    translate(left = topLeft.x, top = topLeft.y) {

        drawPath(
            path = shapePath,
            style = Stroke(width = kropConfig.shapeBorder.toPx()),
            color = kropConfig.shapeColor
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
 * @param kropConfig The [KropConfig] containing styling information for the line, such as color and
 * thickness.
 */
private fun DrawScope.drawKropLine(start: Offset, end: Offset, kropConfig: KropConfig) {

    drawLine(
        start = start,
        end = end,
        color = kropConfig.handleColor,
        strokeWidth = kropConfig.handleHeight.toPx(),
        cap = StrokeCap.Round
    )
}

/**
 * Draws a preview of the krop shape.
 *
 * This function is used to display a visual representation of the selected crop shape.
 * It takes a [KropShape] and a [Color] as input and draws the shape's path with a stroke style.
 *
 * @param kropShape The [KropShape] to draw.
 * @param shapeColor The [Color] to use for drawing the shape's path.
 */
internal fun DrawScope.drawKropShapePreview(kropShape: KropShape, shapeColor: Color) {

    val shapePath = kropShape.toPath(canvasSize = size)

    drawPath(
        path = shapePath,
        color = shapeColor,
        style = Stroke(width = 2.dp.toPx())
    )
}

/**
 * Calculates the new top-left and bottom-right points of a rectangle
 * after a corner drag, maintaining a given aspect ratio.
 *
 * @param draggedCornerCurrent The current position of the corner being dragged.
 * @param anchorCorner The position of the corner opposite to the dragged corner
 * (this corner stays fixed).
 * @param dragDelta The amount by which the draggedCornerCurrent has been moved.
 * @param cornerType The specific corner being dragged
 * (TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT).
 * @param aspectRatio The desired aspect ratio (width / height).
 * @param minSize The minimum allowed size (width or height) for the rectangle.
 * @param canvasWidth The maximum width of the canvas.
 * @param canvasHeight The maximum height of the canvas.
 * @return A Pair of new topLeft and bottomRight Offsets, or null if the drag is invalid.
 */
internal fun calculateNewCropRect(
    draggedCornerCurrent: Offset,
    anchorCorner: Offset,
    dragDelta: Offset,
    cornerType: KropCorner,
    aspectRatio: Float,
    minSize: Float,
    canvasWidth: Float,
    canvasHeight: Float
): Pair<Offset, Offset>? {

    val newDraggedCorner = draggedCornerCurrent + dragDelta

    var proposedWidth = when (cornerType) {

        KropCorner.TOP_LEFT, KropCorner.BOTTOM_LEFT -> anchorCorner.x - newDraggedCorner.x
        KropCorner.TOP_RIGHT, KropCorner.BOTTOM_RIGHT -> newDraggedCorner.x - anchorCorner.x
        else -> return null
    }

    var proposedHeight = when (cornerType) {

        KropCorner.TOP_LEFT, KropCorner.TOP_RIGHT -> anchorCorner.y - newDraggedCorner.y
        KropCorner.BOTTOM_LEFT, KropCorner.BOTTOM_RIGHT -> newDraggedCorner.y - anchorCorner.y
        else -> return null
    }

    if (proposedWidth < minSize) proposedWidth = minSize
    if (proposedHeight < minSize) proposedHeight = minSize

    var adjustedHeight = proposedWidth / aspectRatio
    var adjustedWidth = proposedWidth

    if (adjustedHeight < minSize) {

        adjustedHeight = minSize
        adjustedWidth = adjustedHeight * aspectRatio
    }

    val newTopLeft: Offset
    val newBottomRight: Offset

    when (cornerType) {

        KropCorner.TOP_LEFT -> {

            newTopLeft = Offset(anchorCorner.x - adjustedWidth, anchorCorner.y - adjustedHeight)
            newBottomRight = anchorCorner
        }

        KropCorner.TOP_RIGHT -> {

            newTopLeft = Offset(anchorCorner.x, anchorCorner.y - adjustedHeight)
            newBottomRight = Offset(anchorCorner.x + adjustedWidth, anchorCorner.y)
        }

        KropCorner.BOTTOM_LEFT -> {

            newTopLeft = Offset(anchorCorner.x - adjustedWidth, anchorCorner.y)
            newBottomRight = Offset(anchorCorner.x, anchorCorner.y + adjustedHeight)
        }

        KropCorner.BOTTOM_RIGHT -> {

            newTopLeft = anchorCorner
            newBottomRight = Offset(anchorCorner.x + adjustedWidth, anchorCorner.y + adjustedHeight)
        }

        else -> return null
    }

    val finalTopLeftX = newTopLeft.x.coerceIn(0f, canvasWidth - minSize)
    val finalTopLeftY = newTopLeft.y.coerceIn(0f, canvasHeight - minSize)
    var finalBottomRightX = newBottomRight.x.coerceIn(minSize, canvasWidth)
    var finalBottomRightY = newBottomRight.y.coerceIn(minSize, canvasHeight)

    if (finalBottomRightX - finalTopLeftX < minSize) {

        finalBottomRightX = if (cornerType == KropCorner.TOP_LEFT
            || cornerType == KropCorner.BOTTOM_LEFT
        ) {

            (finalTopLeftX + minSize).coerceAtMost(canvasWidth)
        } else {

            (finalTopLeftX + minSize).coerceAtMost(canvasWidth)
        }
    }

    if (finalBottomRightY - finalTopLeftY < minSize) {

        finalBottomRightY = if (cornerType == KropCorner.TOP_LEFT
            || cornerType == KropCorner.TOP_RIGHT
        ) {

            (finalTopLeftY + minSize).coerceAtMost(canvasHeight)
        } else {

            (finalTopLeftY + minSize).coerceAtMost(canvasHeight)
        }
    }

    var finalWidth = finalBottomRightX - finalTopLeftX
    var finalHeight = finalBottomRightY - finalTopLeftY

    if (finalWidth / aspectRatio > finalHeight) {

        finalWidth = finalHeight * aspectRatio
    } else {

        finalHeight = finalWidth / aspectRatio
    }

    finalWidth = finalWidth.coerceAtLeast(minSize)
    finalHeight = finalHeight.coerceAtLeast(minSize)

    val resultTopLeft: Offset
    val resultBottomRight: Offset

    when (cornerType) {

        KropCorner.TOP_LEFT -> {

            resultBottomRight = anchorCorner
            resultTopLeft = Offset(anchorCorner.x - finalWidth, anchorCorner.y - finalHeight)
        }

        KropCorner.TOP_RIGHT -> {

            resultBottomRight = Offset(anchorCorner.x + finalWidth, anchorCorner.y)
            resultTopLeft = Offset(anchorCorner.x, anchorCorner.y - finalHeight)
        }

        KropCorner.BOTTOM_LEFT -> {

            resultBottomRight = Offset(anchorCorner.x, anchorCorner.y + finalHeight)
            resultTopLeft = Offset(anchorCorner.x - finalWidth, anchorCorner.y)
        }

        KropCorner.BOTTOM_RIGHT -> {

            resultTopLeft = anchorCorner
            resultBottomRight = Offset(anchorCorner.x + finalWidth, anchorCorner.y + finalHeight)
        }

        else -> return null
    }

    val clampedTopLeftX = resultTopLeft.x.coerceIn(0f, canvasWidth - minSize)
    val clampedTopLeftY = resultTopLeft.y.coerceIn(0f, canvasHeight - minSize)

    val clampedBottomRightX = resultBottomRight.x.coerceIn(
        clampedTopLeftX + minSize..canvasWidth
    )

    val clampedBottomRightY = resultBottomRight.y.coerceIn(
        clampedTopLeftY + minSize..canvasHeight
    )

    if (clampedBottomRightX - clampedTopLeftX < minSize
        || clampedBottomRightY - clampedTopLeftY < minSize
    ) {

        return null
    }

    return Pair(
        first = Offset(x = clampedTopLeftX, y = clampedTopLeftY),
        second = Offset(x = clampedBottomRightX, y = clampedBottomRightY)
    )
}