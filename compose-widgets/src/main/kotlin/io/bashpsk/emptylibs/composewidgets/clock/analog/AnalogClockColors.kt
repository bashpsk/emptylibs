package io.bashpsk.emptylibs.composewidgets.clock.analog

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Defines the colors used for different parts of the [AnalogClock].
 *
 * This class allows for detailed customization of the clock's appearance, ensuring it integrates
 * seamlessly with any design theme.
 *
 * @property containerColor The background color of the clock face.
 * @property majorTickColor The color of the major tick marks, typically indicating hours.
 * @property minorTickColor The color of the minor tick marks, typically indicating minutes.
 * @property hourHandColor The color of the hour hand.
 * @property minuteHandColor The color of the minute hand.
 * @property secondHandColor The color of the second hand.
 * @property borderColor The color of the clock's outer border.
 */
@Immutable
data class AnalogClockColors(
    val containerColor: Color = Color.Unspecified,
    val majorTickColor: Color = Color.Unspecified,
    val minorTickColor: Color = Color.Unspecified,
    val hourHandColor: Color = Color.Unspecified,
    val minuteHandColor: Color = Color.Unspecified,
    val secondHandColor: Color = Color.Unspecified,
    val borderColor: Color = Color.Unspecified
)