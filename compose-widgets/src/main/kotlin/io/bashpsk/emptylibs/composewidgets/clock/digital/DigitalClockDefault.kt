package io.bashpsk.emptylibs.composewidgets.clock.digital

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import io.bashpsk.emptylibs.composewidgets.R

/**
 * Default values and styles for the digital clock.
 */
object DigitalClockDefault {

    /**
     * The alpha value for disabled text.
     */
    const val DISABLE_TEXT_ALPHA = 0.35F

    /**
     * The font family for the digital clock.
     */
    val Digital7Font = FontFamily(Font(R.font.digital_7))

    /**
     * Creates the default text styles for the digital clock.
     *
     * @param date The text style for the date.
     * @param time The text style for the time.
     * @param days The text style for the days of the week.
     * @param indicator The text style for the AM/PM indicator.
     * @return The [DigitalClockTextStyles] for the clock.
     */
    @Composable
    fun textStyles(
        date: TextStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.primary
        ),
        time: TextStyle = MaterialTheme.typography.displayLarge.copy(
            fontFamily = Digital7Font,
            color = MaterialTheme.colorScheme.primary
        ),
        days: TextStyle = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.secondary
        ),
        indicator: TextStyle = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.secondary
        )
    ): DigitalClockTextStyles {

        return DigitalClockTextStyles(date = date, time = time, days = days, indicator = indicator)
    }

    /**
     * Returns the appropriate text style based on whether the text is enabled or not.
     *
     * @param textStyle The base text style.
     * @param enabled Whether the text is enabled.
     * @param alpha The alpha to apply when the text is disabled.
     * @return The resulting [TextStyle].
     */
    internal fun getTextStyle(textStyle: TextStyle, enabled: Boolean, alpha: Float): TextStyle {

        return textStyle.takeIf {
            enabled
        } ?: textStyle.copy(color = textStyle.color.copy(alpha = alpha))
    }
}