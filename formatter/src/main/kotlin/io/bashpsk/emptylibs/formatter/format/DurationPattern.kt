package io.bashpsk.emptylibs.formatter.format

import androidx.compose.runtime.Stable

/**
 * Enum class defining patterns for formatting durations.
 *
 * This enum provides various predefined formats for displaying time durations.
 * Each pattern specifies how hours, minutes, seconds, and milliseconds
 * should be represented in the output string.
 */
@Stable
sealed interface DurationPattern {

    /**
     * Represents a duration format using custom labels for each time unit.
     *
     * This pattern allows for flexible representation of durations where each component
     * (days, hours, minutes, seconds) is followed by a specific label. For example,
     * a duration of 1 day, 2 hours, and 30 minutes could be formatted as "1d 02h 30m 00s".
     *
     * @property days The label for the days component. Defaults to "d ".
     * @property hours The label for the hours component. Defaults to "h ".
     * @property minutes The label for the minutes component. Defaults to "m ".
     * @property seconds The label for the seconds component. Defaults to "s".
     */
    data class TimeLabel(
        val days: String = "d ",
        val hours: String = "h ",
        val minutes: String = "m ",
        val seconds: String = "s"
    ) : DurationPattern

    /**
     * A pattern component that defines a separator character to be used between time units.
     * This allows for custom separators like ":" or "-" in the formatted duration string.
     *
     * For example, using `Separator(":")` would result in a format like `HH:mm:ss`.
     *
     * @property char The character or string to use as a separator.
     */
    data class Separator(val char: String) : DurationPattern
}