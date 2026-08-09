package io.bashpsk.emptylibs.jetpackui.sevensegment

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Draws a 7-segment display element on the canvas.
 *
 * @param data The 7-segment data to be displayed.
 * @param colors The colors to be used for the active and inactive segments.
 * @param properties The properties of the segments, such as thickness and spacing.
 */
internal fun DrawScope.drawSegmentElements(
    data: SevenSegmentData = SevenSegmentData.Empty,
    colors: SevenSegmentColors = SevenSegmentColors(),
    properties: SevenSegmentProperties = SevenSegmentProperties()
) {

    data.activeElements.forEach { element ->

        drawElement(element = element, color = colors.active, properties = properties)
    }

    data.inactiveElements.forEach { element ->

        drawElement(element = element, color = colors.inactive, properties = properties)
    }
}

/**
 * Draws the dot elements for a 7-segment display, such as a colon or a decimal point.
 *
 * @param data The 7-segment data, which should represent a dot or a colon.
 * @param colors The colors to be used for the active and inactive segments.
 * @param properties The properties of the segments, such as thickness.
 */
internal fun DrawScope.drawDotElements(
    data: SevenSegmentData = SevenSegmentData.Empty,
    colors: SevenSegmentColors = SevenSegmentColors(),
    properties: SevenSegmentProperties = SevenSegmentProperties()
) {

    data.activeElements.forEach { element ->

        drawDot(element = element, color = colors.active, properties = properties)
    }

    data.inactiveElements.forEach { element ->

        drawDot(element = element, color = colors.inactive, properties = properties)
    }
}

/**
 * Draws a single segment of the 7-segment display.
 *
 * @param element The segment to be drawn.
 * @param color The color of the segment.
 * @param properties The properties of the segment, such as thickness and spacing.
 */
private fun DrawScope.drawElement(
    element: SevenSegmentElement,
    color: Color = Color.Unspecified,
    properties: SevenSegmentProperties = SevenSegmentProperties()
) {

    val segmentWidth = size.width
    val segmentHeight = size.height / 2F
    val strokeWidth = properties.thickness.toPx()
    val space = properties.space.toPx()
    val halfSpace = space / 2F
    val halfStroke = strokeWidth / 2F

    val path = Path().apply {

        when (element) {

            SevenSegmentElement._1 -> {

                moveTo(space, 0F)
                lineTo(segmentWidth - space, 0F)
                lineTo(segmentWidth - strokeWidth - space, strokeWidth)
                lineTo(strokeWidth + space, strokeWidth)
                close()
            }

            SevenSegmentElement._2 -> {

                moveTo(segmentWidth, space)
                lineTo(segmentWidth, segmentHeight - halfSpace)
                lineTo(segmentWidth - strokeWidth, segmentHeight - halfStroke - halfSpace)
                lineTo(segmentWidth - strokeWidth, strokeWidth + space)
                close()
            }

            SevenSegmentElement._3 -> {

                moveTo(segmentWidth, segmentHeight + halfSpace)
                lineTo(segmentWidth, size.height - space)
                lineTo(segmentWidth - strokeWidth, size.height - strokeWidth - space)
                lineTo(segmentWidth - strokeWidth, segmentHeight + halfStroke + halfSpace)
                close()
            }

            SevenSegmentElement._4 -> {

                moveTo(space, size.height)
                lineTo(segmentWidth - space, size.height)
                lineTo(segmentWidth - strokeWidth - space, size.height - strokeWidth)
                lineTo(strokeWidth + space, size.height - strokeWidth)
                close()
            }

            SevenSegmentElement._5 -> {

                moveTo(0F, segmentHeight + halfSpace)
                lineTo(0F, size.height - space)
                lineTo(strokeWidth, size.height - strokeWidth - space)
                lineTo(strokeWidth, segmentHeight + halfStroke + halfSpace)
                close()
            }

            SevenSegmentElement._6 -> {

                moveTo(0F, space)
                lineTo(0F, segmentHeight - halfSpace)
                lineTo(strokeWidth, segmentHeight - halfStroke - halfSpace)
                lineTo(strokeWidth, strokeWidth + space)
                close()
            }

            SevenSegmentElement._7 -> {

                moveTo(space, segmentHeight)
                lineTo(strokeWidth + space, segmentHeight - halfStroke)
                lineTo(segmentWidth - strokeWidth - space, segmentHeight - halfStroke)
                lineTo(segmentWidth - space, segmentHeight)
                lineTo(segmentWidth - strokeWidth - space, segmentHeight + halfStroke)
                lineTo(strokeWidth + space, segmentHeight + halfStroke)
                close()
            }
        }
    }

    drawPath(path = path, color = color)
}

/**
 * Draws a single dot of the 7-segment display, used for colons and decimal points.
 *
 * @param element The dot element to be drawn.
 * @param color The color of the dot.
 * @param properties The properties of the segment, such as thickness.
 */
private fun DrawScope.drawDot(
    element: SevenSegmentElement,
    color: Color = Color.Unspecified,
    properties: SevenSegmentProperties = SevenSegmentProperties()
) {

    val segmentWidth = size.width
    val segmentHeight = size.height / 2F
    val dotSize = properties.thickness.toPx()
    val halfDotSize = dotSize / 2F
    val radius = if (properties.isRoundedDot) halfDotSize else 0F

    when (element) {

        SevenSegmentElement._1 -> {

            drawRoundRect(
                topLeft = Offset(
                    x = (segmentWidth / 2F) - halfDotSize,
                    y = (segmentHeight / 2F) - halfDotSize
                ),
                size = Size(width = dotSize, height = dotSize),
                cornerRadius = CornerRadius(x = radius, y = radius),
                color = color
            )
        }

        SevenSegmentElement._2 -> {

            drawRoundRect(
                topLeft = Offset(
                    x = (segmentWidth / 2F) - halfDotSize,
                    y = (segmentHeight + (segmentHeight / 2F)) - halfDotSize
                ),
                size = Size(width = dotSize, height = dotSize),
                cornerRadius = CornerRadius(x = radius, y = radius),
                color = color
            )
        }

        else -> {}
    }
}