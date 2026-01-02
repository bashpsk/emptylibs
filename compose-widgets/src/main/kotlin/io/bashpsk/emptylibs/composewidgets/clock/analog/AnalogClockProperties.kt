package io.bashpsk.emptylibs.composewidgets.clock.analog

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Defines the visual properties of the various components of the [AnalogClock].
 *
 * This class allows for fine-grained control over the appearance of the clock's hands, tick marks,
 * and numbers, ensuring that it can be styled to fit any design.
 *
 * @property borderWidth The width of the clock's outer border.
 * @property hourHandThickness The thickness of the hour hand.
 * @property minuteHandThickness The thickness of the minute hand.
 * @property secondHandThickness The thickness of the second hand.
 * @property numberTextStyle The [TextStyle] for the hour numbers on the clock face.
 * @property minorDivisionWidth The width of the minor tick marks (minutes).
 * @property minorDivisionThickness The thickness of the minor tick marks (minutes).
 * @property majorDivisionWidth The width of the major tick marks (hours).
 * @property majorDivisionThickness The thickness of the major tick marks (hours).
 */
@Immutable
data class AnalogClockProperties(
    val borderWidth: Dp = 2.dp,
    val hourHandThickness: Dp = 4.dp,
    val minuteHandThickness: Dp = 2.dp,
    val secondHandThickness: Dp = 1.dp,
    val numberTextStyle: TextStyle = TextStyle.Default,
    val minorDivisionWidth: Dp = 4.dp,
    val minorDivisionThickness: Dp = 1.dp,
    val majorDivisionWidth: Dp = 6.dp,
    val majorDivisionThickness: Dp = 1.5.dp
)