package io.bashpsk.emptylibs.composewidgets.clock.analog

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.text.rememberTextMeasurer
import io.bashpsk.emptylibs.imageutils.shape.getMinimumRadius
import io.bashpsk.emptylibs.imageutils.shape.toPath
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * A composable function that displays a fully functional analog clock.
 *
 * This clock updates its hands every second to show the current time. The clock is lifecycle-aware
 * and will only update when the component is visible and the app is in the foreground.
 *
 * @param modifier The modifier to be applied to the clock.
 * @param localDateTime The local date and time to display, in milliseconds.
 * @param timeZone The time zone to use for displaying the time.
 * @param shape The [ClockShape] of the clock's border. Defines the outer boundary of the clock.
 * @param properties The [AnalogClockProperties] to customize the visual appearance of the clock's
 * components.
 * @param colors The [AnalogClockColors] to define the colors for various parts of the clock.
 */
@Composable
fun AnalogClock(
    modifier: Modifier = Modifier,
    localDateTime: Long,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    shape: ClockShape = AnalogClockDefault.shape,
    properties: AnalogClockProperties = AnalogClockDefault.properties(),
    colors: AnalogClockColors = AnalogClockDefault.colors()
) {

    val currentDateTime by remember(localDateTime, timeZone) {
        derivedStateOf {
            Instant.fromEpochMilliseconds(localDateTime).toLocalDateTime(timeZone = timeZone)
        }
    }

    AnalogClock(
        modifier = modifier,
        localDateTime = currentDateTime,
        shape = shape,
        properties = properties,
        colors = colors
    )
}

/**
 * A composable function that displays a fully functional analog clock.
 *
 * This clock updates its hands every second to show the current time. The clock is lifecycle-aware
 * and will only update when the component is visible and the app is in the foreground.
 *
 * @param modifier The modifier to be applied to the clock.
 * @param localDateTime The local date and time to display.
 * @param shape The [ClockShape] of the clock's border. Defines the outer boundary of the clock.
 * @param properties The [AnalogClockProperties] to customize the visual appearance of the clock's
 * components.
 * @param colors The [AnalogClockColors] to define the colors for various parts of the clock.
 */
@Composable
fun AnalogClock(
    modifier: Modifier = Modifier,
    localDateTime: LocalDateTime,
    shape: ClockShape = AnalogClockDefault.shape,
    properties: AnalogClockProperties = AnalogClockDefault.properties(),
    colors: AnalogClockColors = AnalogClockDefault.colors()
) {

    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier.clipToBounds(),
        contentDescription = "Analog Clock"
    ) {

        val borderWidth = properties.borderWidth.toPx()
        val halfBorderWidth = borderWidth / 2
        val canvasRadius = size.minDimension / 2
        val borderRadius = canvasRadius - halfBorderWidth
        val clockRadius = borderRadius - halfBorderWidth
        val handRadius = shape.getMinimumRadius(boundingRadius = clockRadius)

        val borderPath = shape.toPath(
            canvasSize = Size(width = borderRadius * 2, height = borderRadius * 2)
        ).apply {

            val matrix = Matrix().apply {

                translate(x = halfBorderWidth, y = halfBorderWidth)
            }

            transform(matrix = matrix)
        }

        val clockPath = shape.toPath(
            canvasSize = Size(width = clockRadius * 2, height = clockRadius * 2)
        ).apply {

            val matrix = Matrix().apply {

                translate(x = borderWidth, y = borderWidth)
            }

            transform(matrix = matrix)
        }

        drawClockFace(
            properties = properties,
            colors = colors,
            textMeasurer = textMeasurer,
            clockRadius = clockRadius,
            borderWidth = borderWidth,
            borderPath = borderPath,
            clockPath = clockPath
        )

        drawClockHands(
            properties = properties,
            colors = colors,
            localDateTime = localDateTime,
            handRadius = handRadius
        )
    }
}