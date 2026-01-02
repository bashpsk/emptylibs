package io.bashpsk.emptylibs.composewidgets.clock.digital

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle

/**
 * Defines the text styles for the digital clock.
 *
 * @property date The text style for the date.
 * @property time The text style for the time.
 * @property days The text style for the days of the week.
 * @property indicator The text style for the AM/PM indicator.
 */
@Immutable
data class DigitalClockTextStyles(
    val date: TextStyle = TextStyle.Default,
    val time: TextStyle = TextStyle.Default,
    val days: TextStyle = TextStyle.Default,
    val indicator: TextStyle = TextStyle.Default
)