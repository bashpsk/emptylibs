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
import io.bashpsk.emptylibs.imageutils.shape.ImageShape
import io.bashpsk.emptylibs.imageutils.shape.toPath

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
    kropShape: ImageShape,
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
 * @param kropShape The [ImageShape] to draw the border for.
 * @param topLeft The top-left [Offset] of the crop area.
 * @param bottomRight The bottom-right [Offset] of the crop area.
 * @param kropConfig The [KropConfig] containing styling for the border.
 */
internal fun DrawScope.drawKropShapeBorder(
    kropShape: ImageShape,
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

/**
 * Calculates the new top-left and bottom-right points of a rectangle
 * after a corner drag, maintaining a given aspect ratio.
 *
 * @param draggedCorner The current position of the corner being dragged.
 * @param fixedCorner The position of the corner opposite to the dragged corner
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
    draggedCorner: Offset,
    fixedCorner: Offset,
    dragDelta: Offset,
    cornerType: KropCorner,
    aspectRatio: Float,
    minSize: Float,
    canvasWidth: Float,
    canvasHeight: Float
): Pair<Offset, Offset>? {

    val newPosition = draggedCorner + dragDelta

    val proposedWidth = when (cornerType) {

        KropCorner.TOP_LEFT, KropCorner.BOTTOM_LEFT -> fixedCorner.x - newPosition.x
        KropCorner.TOP_RIGHT, KropCorner.BOTTOM_RIGHT -> newPosition.x - fixedCorner.x
        else -> return null
    }.coerceAtLeast(minSize)

    var adjustedWidth = proposedWidth
    var adjustedHeight = proposedWidth / aspectRatio

    (adjustedHeight < minSize).takeIf { it }?.run {

        adjustedHeight = minSize
        adjustedWidth = adjustedHeight * aspectRatio
    }

    adjustedWidth = adjustedWidth.coerceAtLeast(minSize)

    val (initialTopLeft, initialBottomRight) = when (cornerType) {

        KropCorner.TOP_LEFT -> Offset(
            fixedCorner.x - adjustedWidth,
            fixedCorner.y - adjustedHeight
        ) to fixedCorner

        KropCorner.TOP_RIGHT -> Offset(
            fixedCorner.x,
            fixedCorner.y - adjustedHeight
        ) to Offset(fixedCorner.x + adjustedWidth, fixedCorner.y)

        KropCorner.BOTTOM_LEFT -> Offset(
            fixedCorner.x - adjustedWidth,
            fixedCorner.y
        ) to Offset(fixedCorner.x, fixedCorner.y + adjustedHeight)

        KropCorner.BOTTOM_RIGHT -> fixedCorner to Offset(
            fixedCorner.x + adjustedWidth,
            fixedCorner.y + adjustedHeight
        )

        else -> return null
    }

    var finalTopLeft = initialTopLeft.copy(
        x = initialTopLeft.x.coerceIn(0.0F, canvasWidth - minSize),
        y = initialTopLeft.y.coerceIn(0.0F, canvasHeight - minSize)
    )

    var finalBottomRight = initialBottomRight.copy(
        x = initialBottomRight.x.coerceIn(finalTopLeft.x + minSize, canvasWidth),
        y = initialBottomRight.y.coerceIn(finalTopLeft.y + minSize, canvasHeight)
    )

    finalTopLeft = finalTopLeft.copy(
        x = finalTopLeft.x.coerceAtMost(finalBottomRight.x - minSize),
        y = finalTopLeft.y.coerceAtMost(finalBottomRight.y - minSize)
    )

    finalBottomRight = finalBottomRight.copy(
        x = finalBottomRight.x.coerceAtLeast(finalTopLeft.x + minSize),
        y = finalBottomRight.y.coerceAtLeast(finalTopLeft.y + minSize)
    )

    var currentWidth = (finalBottomRight.x - finalTopLeft.x).coerceAtLeast(minSize)
    var currentHeight = (finalBottomRight.y - finalTopLeft.y).coerceAtLeast(minSize)

    (currentWidth / aspectRatio > currentHeight + 0.001F).takeIf { it }?.run {

        when (cornerType) {

            KropCorner.TOP_LEFT, KropCorner.BOTTOM_LEFT -> {

                currentWidth = currentHeight * aspectRatio
                finalTopLeft = finalTopLeft.copy(x = finalBottomRight.x - currentWidth)
            }

            KropCorner.TOP_RIGHT, KropCorner.BOTTOM_RIGHT -> {

                currentWidth = currentHeight * aspectRatio
                finalBottomRight = finalBottomRight.copy(x = finalTopLeft.x + currentWidth)
            }

            else -> {}
        }
    } ?: (currentHeight > currentWidth / aspectRatio + 0.001F).takeIf { it }?.run {

        when (cornerType) {

            KropCorner.TOP_LEFT, KropCorner.TOP_RIGHT -> {

                currentHeight = currentWidth / aspectRatio
                finalTopLeft = finalTopLeft.copy(y = finalBottomRight.y - currentHeight)
            }

            KropCorner.BOTTOM_LEFT, KropCorner.BOTTOM_RIGHT -> {

                currentHeight = currentWidth / aspectRatio
                finalBottomRight = finalBottomRight.copy(y = finalTopLeft.y + currentHeight)
            }

            else -> {}
        }
    }

    finalTopLeft = finalTopLeft.copy(
        x = finalTopLeft.x.coerceIn(0.0F, canvasWidth - minSize),
        y = finalTopLeft.y.coerceIn(0.0F, canvasHeight - minSize)
    )

    finalBottomRight = finalBottomRight.copy(
        x = (finalTopLeft.x + currentWidth.coerceAtLeast(minSize)).coerceIn(
            finalTopLeft.x + minSize,
            canvasWidth
        ),
        y = (finalTopLeft.y + currentHeight.coerceAtLeast(minSize)).coerceIn(
            finalTopLeft.y + minSize,
            canvasHeight
        )
    )

    val finalWidth = (finalBottomRight.x - finalTopLeft.x).coerceAtLeast(minSize)
    val finalHeight = (finalBottomRight.y - finalTopLeft.y).coerceAtLeast(minSize)

    (finalWidth < minSize || finalHeight < minSize).takeIf { it }?.run {

        return null
    }

    val resultBottomRight = Offset(
        x = (finalTopLeft.x + finalWidth).coerceIn(finalTopLeft.x + minSize, canvasWidth),
        y = (finalTopLeft.y + finalHeight).coerceIn(finalTopLeft.y + minSize, canvasHeight)
    )

    val resultTopLeft = finalTopLeft.copy(
        x = (resultBottomRight.x - finalWidth).coerceIn(0.0F, canvasWidth - minSize),
        y = (resultBottomRight.y - finalHeight).coerceIn(0.0F, canvasHeight - minSize)
    )

    ((resultBottomRight.x - resultTopLeft.x) < minSize ||
            (resultBottomRight.y - resultTopLeft.y) < minSize).takeIf { it }?.run {

        return null
    }

    return Pair(resultTopLeft, resultBottomRight)
}