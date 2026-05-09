package io.bashpsk.emptylibs.composewidgets.clock.analog

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.time.Duration.Companion.milliseconds

/**
 * Provides default values for the [AnalogClock] composable, ensuring a consistent and visually
 * pleasing appearance out of the box.
 *
 * This object offers default settings for the clock's shape, update interval, colors, and
 * properties.
 * These defaults are designed to align with Material Design guidelines and can be easily
 * customized.
 */
object AnalogClockDefault {

    /**
     * The default shape for the clock, which is a circle.
     */
    @Stable
    val shape: ClockShape = AnalogClockShape.Circle

    /**
     * The default update interval for the clock, set to 250 milliseconds for smooth hand movement.
     */
    @Stable
    val interval = 250.milliseconds

    /**
     * Creates a default [AnalogClockColors] instance.
     *
     * The colors are derived from the current [MaterialTheme], ensuring that the clock's appearance
     * matches the overall application theme.
     *
     * @param containerColor The background color of the clock face.
     * @param majorTickColor The color of the major tick marks (hours).
     * @param minorTickColor The color of the minor tick marks (minutes).
     * @param hourHandColor The color of the hour hand.
     * @param minuteHandColor The color of the minute hand.
     * @param secondHandColor The color of the second hand.
     * @param borderColor The color of the clock's outer border.
     * @return A fully configured [AnalogClockColors] instance.
     */
    @Stable
    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surface,
        majorTickColor: Color = contentColorFor(containerColor),
        minorTickColor: Color = contentColorFor(containerColor),
        hourHandColor: Color = MaterialTheme.colorScheme.onSurface,
        minuteHandColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        secondHandColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        borderColor: Color = MaterialTheme.colorScheme.scrim,
    ): AnalogClockColors {

        return AnalogClockColors(
            containerColor = containerColor,
            majorTickColor = majorTickColor,
            minorTickColor = minorTickColor,
            hourHandColor = hourHandColor,
            minuteHandColor = minuteHandColor,
            secondHandColor = secondHandColor,
            borderColor = borderColor,
        )
    }

    /**
     * Creates a default [AnalogClockProperties] instance.
     *
     * This function defines the default sizes and styles for the various components of the clock,
     * such as the hands, tick marks, and numbers.
     *
     * @param borderWidth The width of the clock's outer border.
     * @param hourHandThickness The thickness of the hour hand.
     * @param minuteHandThickness The thickness of the minute hand.
     * @param secondHandThickness The thickness of the second hand.
     * @param numberTextStyle The [TextStyle] for the hour numbers.
     * @param minorDivisionWidth The width of the minor tick marks.
     * @param minorDivisionThickness The thickness of the minor tick marks.
     * @param majorDivisionWidth The width of the major tick marks.
     * @param majorDivisionThickness The thickness of the major tick marks.
     * @return A fully configured [AnalogClockProperties] instance.
     */
    @Stable
    fun properties(
        borderWidth: Dp = 2.dp,
        hourHandThickness: Dp = 4.dp,
        minuteHandThickness: Dp = 2.dp,
        secondHandThickness: Dp = 1.dp,
        numberTextStyle: TextStyle = TextStyle.Default,
        minorDivisionWidth: Dp = 4.dp,
        minorDivisionThickness: Dp = 1.dp,
        majorDivisionWidth: Dp = 6.dp,
        majorDivisionThickness: Dp = 1.5.dp
    ): AnalogClockProperties {

        return AnalogClockProperties(
            borderWidth = borderWidth,
            hourHandThickness = hourHandThickness,
            minuteHandThickness = minuteHandThickness,
            secondHandThickness = secondHandThickness,
            numberTextStyle = numberTextStyle,
            minorDivisionWidth = minorDivisionWidth,
            minorDivisionThickness = minorDivisionThickness,
            majorDivisionWidth = majorDivisionWidth,
            majorDivisionThickness = majorDivisionThickness,
        )
    }
}