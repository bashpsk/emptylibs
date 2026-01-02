package io.bashpsk.emptylibs.jetpackui.sevensegment

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
internal fun DrawScope.drawSegmentElement(
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

                moveTo(strokeWidth + space, strokeWidth)
                lineTo(segmentWidth - strokeWidth - space, strokeWidth)
                lineTo(segmentWidth - strokeWidth * 2 - space, strokeWidth * 2)
                lineTo(strokeWidth * 2 + space, strokeWidth * 2)
                close()
            }

            SevenSegmentElement._2 -> {

                moveTo(segmentWidth - strokeWidth, strokeWidth + space)
                lineTo(segmentWidth - strokeWidth, segmentHeight - halfSpace)
                lineTo(segmentWidth - strokeWidth * 2, segmentHeight - strokeWidth - halfSpace)
                lineTo(segmentWidth - strokeWidth * 2, strokeWidth * 2 + space)
                close()
            }

            SevenSegmentElement._3 -> {

                moveTo(segmentWidth - strokeWidth, segmentHeight + halfSpace)
                lineTo(segmentWidth - strokeWidth, size.height - strokeWidth - space)
                lineTo(segmentWidth - strokeWidth * 2, size.height - strokeWidth * 2 - space)
                lineTo(segmentWidth - strokeWidth * 2, segmentHeight + strokeWidth + halfSpace)
                close()
            }

            SevenSegmentElement._4 -> {

                moveTo(strokeWidth + space, size.height - strokeWidth)
                lineTo(segmentWidth - strokeWidth - space, size.height - strokeWidth)
                lineTo(segmentWidth - strokeWidth * 2 - space, size.height - strokeWidth * 2)
                lineTo(strokeWidth * 2 + space, size.height - strokeWidth * 2)
                close()
            }

            SevenSegmentElement._5 -> {

                moveTo(strokeWidth, segmentHeight + halfSpace)
                lineTo(strokeWidth, size.height - strokeWidth - space)
                lineTo(strokeWidth * 2, size.height - strokeWidth * 2 - space)
                lineTo(strokeWidth * 2, segmentHeight + strokeWidth + halfSpace)
                close()
            }

            SevenSegmentElement._6 -> {

                moveTo(strokeWidth, strokeWidth + space)
                lineTo(strokeWidth, segmentHeight - halfSpace)
                lineTo(strokeWidth * 2, segmentHeight - strokeWidth - halfSpace)
                lineTo(strokeWidth * 2, strokeWidth * 2 + space)
                close()
            }

            SevenSegmentElement._7 -> {

                moveTo(strokeWidth + space, segmentHeight)
                lineTo(strokeWidth + space + halfStroke, segmentHeight - halfStroke)
                lineTo(segmentWidth - strokeWidth - space - halfStroke, segmentHeight - halfStroke)
                lineTo(segmentWidth - strokeWidth - space, segmentHeight)
                lineTo(segmentWidth - strokeWidth - space - halfStroke, segmentHeight + halfStroke)
                lineTo(strokeWidth + space + halfStroke, segmentHeight + halfStroke)
                close()
            }
        }
    }

    drawPath(path = path, color = color)
}