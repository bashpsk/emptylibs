package io.bashpsk.emptylibs.composewidgets.clock.analog

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Draws the static elements of the analog clock, including the border, the clock face background,
 * tick marks, and numbers.
 *
 * @param properties The [AnalogClockProperties] defining the visual characteristics of the clock.
 * @param colors The [AnalogClockColors] used for drawing the clock's components.
 * @param textMeasurer A [TextMeasurer] to calculate the size of the clock's numbers.
 * @param clockRadius The radius of the clock face.
 * @param borderWidth The width of the clock's border.
 * @param clockPath The [Path] defining the shape of the clock face.
 * @param borderPath The [Path] defining the shape of the clock's border.
 */
internal fun DrawScope.drawClockFace(
    properties: AnalogClockProperties,
    colors: AnalogClockColors,
    textMeasurer: TextMeasurer,
    clockRadius: Float,
    borderWidth: Float,
    borderPath: Path,
    clockPath: Path
) {

    drawPath(path = borderPath, color = colors.borderColor, style = Stroke(width = borderWidth))

    drawPath(path = clockPath, color = colors.containerColor)

    drawClockTicks(
        properties = properties,
        textMeasurer = textMeasurer,
        clockRadius = clockRadius,
        borderWidth = borderWidth,
        clockPath = clockPath
    )

    drawClockNumbers(
        properties = properties,
        colors = colors,
        clockRadius = clockRadius,
        clockPath = clockPath
    )
}

/**
 * Draws the tick marks for each hour on the clock face.
 *
 * This function iterates through the hours, calculates their positions, and draws them on the
 * canvas.
 *
 * @param properties The [AnalogClockProperties] defining the visual characteristics of the clock.
 * @param textMeasurer A [TextMeasurer] to calculate the size of the clock's numbers for
 * positioning.
 * @param clockRadius The radius of the clock face.
 * @param borderWidth The width of the clock's border.
 * @param clockPath The [Path] defining the shape of the clock face.
 */
private fun DrawScope.drawClockTicks(
    properties: AnalogClockProperties,
    textMeasurer: TextMeasurer,
    clockRadius: Float,
    borderWidth: Float,
    clockPath: Path
) {

    for (division in 1..60) {

        if (division % 5 == 0) {

            val angleInRad = (division * 6 * PI / 180.0F - PI / 2.0F).toFloat()

            val intersectionOffset = clockPath.getIntersectionOffset(
                center = center,
                angle = angleInRad,
                clockRadius = clockRadius
            )

            val directionVector = intersectionOffset - center

            val directionMagnitude = sqrt(
                directionVector.x * directionVector.x + directionVector.y * directionVector.y
            )

            if (directionMagnitude == 0.0F) continue

            val hour = division / 5
            val divisionWidth = properties.majorDivisionWidth.toPx()
            val labelRadius = directionMagnitude - (borderWidth / 2) - divisionWidth - 16.dp.toPx()
            val textPosition = center + ((directionVector / directionMagnitude) * labelRadius)

            val measuredText = textMeasurer.measure(
                text = hour.toString(),
                style = properties.numberTextStyle
            )

            val textCenter = Offset(
                x = textPosition.x - measuredText.size.width / 2,
                y = textPosition.y - measuredText.size.height / 2
            )

            drawText(
                textMeasurer = textMeasurer,
                topLeft = textCenter,
                text = hour.toString(),
                style = properties.numberTextStyle
            )
        }
    }
}

/**
 * Draws the hour and minute tick marks on the clock face.
 *
 * This function distinguishes between major (hour) and minor (minute) ticks, applying the
 * appropriate styles and colors for each.
 *
 * @param properties The [AnalogClockProperties] that define the appearance of the tick marks.
 * @param colors The [AnalogClockColors] used for the tick marks.
 * @param clockRadius The radius of the clock face.
 * @param clockPath The [Path] that defines the shape of the clock face.
 */
private fun DrawScope.drawClockNumbers(
    properties: AnalogClockProperties,
    colors: AnalogClockColors,
    clockRadius: Float,
    clockPath: Path
) {

    for (division in 1..60) {

        val angleInRad = (division * 6 * PI / 180.0F - PI / 2.0F).toFloat()

        val intersectionOffset = clockPath.getIntersectionOffset(
            center = center,
            angle = angleInRad,
            clockRadius = clockRadius
        )

        val directionVector = intersectionOffset - center

        val directionMagnitude = sqrt(
            directionVector.x * directionVector.x + directionVector.y * directionVector.y
        )

        if (directionMagnitude == 0.0F) continue

        val isHourDivision = division % 5 == 0

        val divisionWidth = properties.majorDivisionWidth.toPx().takeIf {
            isHourDivision
        } ?: properties.minorDivisionWidth.toPx()

        val divisionThickness = properties.majorDivisionThickness.toPx().takeIf {
            isHourDivision
        } ?: properties.minorDivisionThickness.toPx()

        val divisionColor = colors.majorTickColor.takeIf { isHourDivision } ?: colors.minorTickColor

        rotate(degrees = (angleInRad * 180.0F / PI).toFloat() + 90.0F, pivot = intersectionOffset) {

            drawRoundRect(
                topLeft = Offset(
                    x = intersectionOffset.x - (divisionThickness / 2.0F),
                    y = intersectionOffset.y
                ),
                size = Size(width = divisionThickness, height = divisionWidth),
                cornerRadius = CornerRadius(x = divisionThickness / 2F, y = divisionThickness / 2F),
                color = divisionColor
            )
        }
    }
}

/**
 * Draws the hour, minute, and second hands of the clock.
 *
 * This function calculates the rotation of each hand based on the current time and draws them on
 * the canvas.
 *
 * @param properties The [AnalogClockProperties] defining the thickness of the hands.
 * @param colors The [AnalogClockColors] used to color the hands.
 * @param localDateTime The current [LocalDateTime] to determine the position of the hands.
 * @param handRadius The effective radius for drawing the clock hands.
 */
internal fun DrawScope.drawClockHands(
    properties: AnalogClockProperties,
    colors: AnalogClockColors,
    localDateTime: LocalDateTime,
    handRadius: Float
) {

    val secondRatio = localDateTime.second / 60.0F
    val minuteRatio = (localDateTime.minute + secondRatio) / 60.0F
    val hourRatio = ((localDateTime.hour % 12) + minuteRatio) / 12.0F

    drawHand(
        handRadius = handRadius,
        angle = hourRatio * 360.0F,
        lengthRatio = 0.5F,
        tailRatio = 0.15F,
        color = colors.hourHandColor,
        thickness = properties.hourHandThickness.toPx()
    )

    drawHand(
        handRadius = handRadius,
        angle = minuteRatio * 360.0F,
        lengthRatio = 0.75F,
        tailRatio = 0.20F,
        color = colors.minuteHandColor,
        thickness = properties.minuteHandThickness.toPx()
    )

    drawHand(
        handRadius = handRadius,
        angle = secondRatio * 360.0F,
        lengthRatio = 0.9F,
        tailRatio = 0.25F,
        color = colors.secondHandColor,
        thickness = properties.secondHandThickness.toPx()
    )
}

/**
 * Draws a single clock hand on the canvas.
 *
 * This is a utility function used by [drawClockHands] to draw each hand with a specified length,
 * thickness, and color.
 *
 * @param handRadius The maximum radius available for the hand.
 * @param angle The angle of the hand in degrees.
 * @param lengthRatio The ratio of the hand's length to the [handRadius].
 * @param tailRatio The ratio of the hand's tail length to the [handRadius].
 * @param color The [Color] of the hand.
 * @param thickness The thickness of the hand in pixels.
 */
private fun DrawScope.drawHand(
    handRadius: Float,
    angle: Float,
    lengthRatio: Float,
    tailRatio: Float,
    color: Color,
    thickness: Float
) {

    val angleInRad = (angle * PI / 180.0F - PI / 2.0F).toFloat()
    val handLength = handRadius * lengthRatio
    val tailLength = handRadius * tailRatio
    val totalHandLength = handLength + tailLength

    rotate(degrees = (angleInRad * 180.0F / PI).toFloat() + 90.0F, pivot = center) {

        drawRoundRect(
            topLeft = Offset(x = center.x - (thickness / 2), y = center.y - handLength),
            size = Size(width = thickness, height = totalHandLength),
            cornerRadius = CornerRadius(x = thickness / 2, y = thickness / 2),
            color = color
        )
    }

    drawCircle(color = color, radius = thickness * 1.75F, center = center)
}

/**
 * Calculates the intersection point of a line and a path, which is essential for drawing elements
 * on the edge of a non-circular clock face.
 *
 * This function works by sampling points along the path and finding the one that is closest to the
 * desired angle from the center.
 *
 * @param center The center [Offset] of the clock.
 * @param angle The angle in radians for which to find the intersection point.
 * @param clockRadius The radius of the clock, used as a fallback.
 * @return The [Offset] of the intersection point on the path.
 */
private fun Path.getIntersectionOffset(center: Offset, angle: Float, clockRadius: Float): Offset {

    val pathSegments = 300

    var closestPoint = Offset(
        x = center.x + clockRadius * cos(angle),
        y = center.y + clockRadius * sin(angle)
    )

    var minimumAngleDifference = Float.MAX_VALUE

    val pathMeasure = PathMeasure().apply {

        setPath(path = this@getIntersectionOffset, forceClosed = false)
    }

    for (step in 0..pathSegments) {

        val distance = (step.toFloat() / pathSegments) * pathMeasure.length
        val point = pathMeasure.getPosition(distance)

        if (point == Offset.Unspecified) continue

        val position = point - center
        val pointAngle = (atan2(position.y, position.x) + 2 * PI).toFloat() % (2 * PI).toFloat()
        val targetAngle = (angle + 2 * PI).toFloat() % (2 * PI).toFloat()
        var angleDifference = pointAngle - targetAngle

        when {
            angleDifference > PI -> angleDifference -= (2 * PI).toFloat()
            angleDifference < -PI -> angleDifference += (2 * PI).toFloat()
        }

        val absoluteAngleDifference = abs(angleDifference)

        if (absoluteAngleDifference < minimumAngleDifference) {
            minimumAngleDifference = absoluteAngleDifference
            closestPoint = point
        }
    }

    return closestPoint
}