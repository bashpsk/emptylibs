package io.bashpsk.emptylibs.formatter.format

/**
 * Enum class defining the various formatting patterns supported by `EmptyFormat`.
 */
enum class DateTimePattern {

    /**
     * Time in 12-hour format.
     * Example: 07:30.
     */
    TIME_HH_MM,

    /**
     * Time in 24-hour format.
     * Example: 19:30.
     */
    TIME_HH_MM_24,

    /**
     * Time in 12-hour format, including seconds.
     * Example: 07:30:33.
     */
    TIME_HH_MM_SS,

    /**
     * Time in 24-hour format, including seconds.
     * Example: 19:30:33.
     */
    TIME_HH_MM_SS_24,

    /**
     * Time in 12-hour format with AM/PM indicator.
     * Example: 07:30:33 PM.
     */
    TIME_12,

    /**
     * Time in 24-hour format.
     * Example: 19:30:33.
     */
    TIME_24,

    /**
     * Short date format.
     * Example: 09:12:2000.
     */
    SHORT_DATE,

    /**
     * Human-readable full date format.
     * Example: Dec 09, 2000.
     */
    LONG_DATE,

    /**
     * Short date and time in 12-hour format.
     * Example: 09:12:2000 07:30 PM.
     */
    SHORT_DATE_TIME,

    /**
     * Short date and time in 24-hour format.
     * Example: 09:12:2000 19:30.
     */
    SHORT_DATE_TIME_24,

    /**
     * Full date and time in 12-hour format.
     * Example: Sun, Dec 09, 2000 07:30 PM.
     */
    LONG_DATE_TIME,

    /**
     * Full date and time in 24-hour format.
     * Example: Sun, Dec 09, 2000 19:30.
     */
    LONG_DATE_TIME_24,

    /**
     * Extended full date-time format with milliseconds.
     * Example: Sun, Dec 09, 2000 07:30:33.333 PM.
     */
    LONG_DATE_TIME_MILLIS,

    /**
     * Extended full date-time format with milliseconds in 24-hour format.
     * Example: Sun, Dec 09, 2000 19:30:33.333.
     */
    LONG_DATE_TIME_MILLIS_24,

    /**
     * Date and time format suitable for file names, in 24-hour format.
     * Example: 19-30-33 09-12-2000.
     */
    FILE_NAME,

    /**
     * Day of the week only.
     * Example: Mon.
     */
    DAY_ONLY,

    /**
     * Month of the year only.
     * Example: Dec.
     */
    MONTH_ONLY,

    /**
     * Year only.
     * Example: 2000.
     */
    YEAR_ONLY,

    /**
     * Month and Year only.
     * Example: Dec 09.
     */
    MONTH_DAY,

    /**
     * Short year and month format.
     * Example: 12/00.
     */
    SHORT_MONTH_YEAR,

    /**
     * Month and Year only.
     * Example: Dec 2000.
     */
    MONTH_YEAR,

    /**
     * Day-of-year format.
     * Example: 343 (343rd day of the year).
     */
    DAY_OF_YEAR,

    /**
     * Day-of-month format.
     * Example: 09 (09th day of the month).
     */
    DAY_OF_MONTH,

    /**
     * Month of the year format.
     * Example: 12 (12th month of the year).
     */
    MONTH_OF_YEAR,

    /**
     * Compact timestamp format.
     * Example: 20001209193033 (YYYYMMDDHHMMSS).
     */
    TIMESTAMP_COMPACT,
}

/**
 * Enum class defining patterns for formatting durations.
 *
 * This enum provides various predefined formats for displaying time durations.
 * Each pattern specifies how hours, minutes, seconds, and milliseconds
 * should be represented in the output string.
 */
sealed interface DurationPattern {

    /**
     * Represents a duration format using custom labels for each time unit.
     *
     * This pattern allows for flexible representation of durations where each component
     * (days, hours, minutes, seconds) is followed by a specific label. For example,
     * a duration of 1 day, 2 hours, and 30 minutes could be formatted as "1d 02h 30m 00s".
     *
     * @property days The label for the days component. Defaults to "d".
     * @property hours The label for the hours component. Defaults to "h".
     * @property minutes The label for the minutes component. Defaults to "m".
     * @property seconds The label for the seconds component. Defaults to "s".
     */
    data class TimeLabel(
        val days: String = "d",
        val hours: String = "h",
        val minutes: String = "m",
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