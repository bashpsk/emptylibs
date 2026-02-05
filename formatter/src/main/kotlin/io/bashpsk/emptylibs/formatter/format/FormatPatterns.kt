package io.bashpsk.emptylibs.formatter.format

import androidx.compose.runtime.Stable
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

/**
 * Enum class defining the various formatting patterns supported by `EmptyFormat`.
 */
enum class DateTimePattern {

    /**
     * Time in 12-hour format.
     * Ex: 07:30.
     */
    TIME_HH_MM,

    /**
     * Time in 24-hour format.
     * Ex: 19:30.
     */
    TIME_HH_MM_24,

    /**
     * Time in 12-hour format, including seconds.
     * Ex: 07:30:33.
     */
    TIME_HH_MM_SS,

    /**
     * Time in 24-hour format, including seconds.
     * Ex: 19:30:33.
     */
    TIME_HH_MM_SS_24,

    /**
     * Time in 12-hour format with AM/PM indicator.
     * Ex: 07:30:33 PM.
     */
    TIME_12,

    /**
     * Time in 24-hour format.
     * Ex: 19:30:33.
     */
    TIME_24,

    /**
     * Short date format.
     * Ex: 09:12:2000.
     */
    SHORT_DATE,

    /**
     * Human-readable full date format.
     * Ex: Dec 09, 2000.
     */
    LONG_DATE,

    /**
     * Short date and time in 12-hour format.
     * Ex: 09:12:2000 07:30 PM.
     */
    SHORT_DATE_TIME,

    /**
     * Short date and time in 24-hour format.
     * Ex: 09:12:2000 19:30.
     */
    SHORT_DATE_TIME_24,

    /**
     * Full date and time in 12-hour format.
     * Ex: Sun, Dec 09, 2000 07:30 PM.
     */
    LONG_DATE_TIME,

    /**
     * Full date and time in 24-hour format.
     * Ex: Sun, Dec 09, 2000 19:30.
     */
    LONG_DATE_TIME_24,

    /**
     * Extended full date-time format with milliseconds.
     * Ex: Sun, Dec 09, 2000 07:30:33.333 PM.
     */
    LONG_DATE_TIME_MILLIS,

    /**
     * Extended full date-time format with milliseconds in 24-hour format.
     * Ex: Sun, Dec 09, 2000 19:30:33.333.
     */
    LONG_DATE_TIME_MILLIS_24,

    /**
     * Date and time format suitable for file names, in 24-hour format.
     * Ex: 19-30-33 09-12-2000.
     */
    FILE_NAME,

    /**
     * Day of the week only.
     * Ex: Mon.
     */
    DAY_ONLY,

    /**
     * Month of the year only.
     * Ex: Dec.
     */
    MONTH_ONLY,

    /**
     * Year only.
     * Ex: 2000.
     */
    YEAR_ONLY,

    /**
     * Month and Year only.
     * Ex: Dec 09.
     */
    MONTH_DAY,

    /**
     * Short year and month format.
     * Ex: 12/00.
     */
    SHORT_MONTH_YEAR,

    /**
     * Month and Year only.
     * Ex: Dec 2000.
     */
    MONTH_YEAR,

    /**
     * Day-of-year format.
     * Ex: 343 (343rd day of the year).
     */
    DAY_OF_YEAR,

    /**
     * Day-of-month format.
     * Ex: 09 (09th day of the month).
     */
    DAY_OF_MONTH,

    /**
     * Month of the year format.
     * Ex: 12 (12th month of the year).
     */
    MONTH_OF_YEAR,

    /**
     * Compact timestamp format.
     * Ex: 20001209193033 (YYYYMMDDHHMMSS).
     */
    TIMESTAMP_COMPACT;
    
    companion object {

        /**
         * Retrieves the appropriate `DateTimeFormat` for a given time pattern.
         *
         * This function maps various `Pattern` enums to their respective
         * `LocalTime.Format` definitions using kotlinx-datetime formatting.
         *
         * @param pattern The `Pattern` enum specifying the desired time format.
         * @return The corresponding `DateTimeFormat<LocalTime>`.
         *
         * Supported patterns:
         * - `Pattern.TIME_HH_MM` → Time in 12-hour format. Ex: 07:30.
         * - `Pattern.TIME_HH_MM_24` → Time in 24-hour format. Ex: 19:30.
         * - `Pattern.TIME_HH_MM_SS` → Time in 12-hour format, including seconds. Ex: 07:30:33.
         * - `Pattern.TIME_HH_MM_SS_24` → Time in 24-hour format, including seconds. Ex: 19:30:33.
         * - `Pattern.TIME_12` → Time in 12-hour format with AM/PM indicator. Ex: 07:30:33 PM.
         * - `Pattern.TIME_24` → Time in 24-hour format. Ex: 19:30:33.
         * - `else` → `Pattern.TIME_12`.
         */
        @JvmStatic
        fun DateTimePattern.findTimeFormat(): DateTimeFormat<LocalTime> {

            return when (this) {

                TIME_HH_MM -> LocalTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                }

                TIME_HH_MM_24 -> LocalTime.Format {

                    hour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                }

                TIME_HH_MM_SS -> LocalTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                }

                TIME_HH_MM_SS_24 -> LocalTime.Format {

                    hour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                }

                TIME_12 -> LocalTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                    char(' ')
                    amPmMarker(am = "AM", pm = "PM")
                }

                TIME_24 -> LocalTime.Format {

                    hour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                }

                else -> LocalTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                    char(' ')
                    amPmMarker(am = "AM", pm = "PM")
                }
            }
        }

        /**
         * Retrieves the appropriate `DateTimeFormat` for a given pattern.
         *
         * This function maps various `Pattern` enums to their respective
         * `LocalDateTime.Format` definitions using kotlinx-datetime formatting.
         * @param pattern The `Pattern` enum specifying the desired date-time format.
         * @return The corresponding `DateTimeFormat<LocalDateTime>`.
         */
        @JvmStatic
        fun DateTimePattern.findDateTimeFormat(
            dayOfWeekNames: DayOfWeekNames = defaultDayOfWeekNames,
            monthNames: MonthNames = defaultMonthNames
        ): DateTimeFormat<LocalDateTime> {

            return when (this) {

                TIME_HH_MM -> LocalDateTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                }

                TIME_HH_MM_24 -> LocalDateTime.Format {

                    hour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                }

                TIME_HH_MM_SS -> LocalDateTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                }

                TIME_HH_MM_SS_24 -> LocalDateTime.Format {

                    hour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                }

                TIME_12 -> LocalDateTime.Format {

                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                    char(' ')
                    amPmMarker(am = "AM", pm = "PM")
                }

                TIME_24 -> LocalDateTime.Format {

                    hour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                }

                SHORT_DATE -> LocalDateTime.Format {

                    day(padding = Padding.ZERO)
                    char(value = ':')
                    monthNumber(padding = Padding.ZERO)
                    char(value = ':')
                    year(padding = Padding.ZERO)
                }

                LONG_DATE -> LocalDateTime.Format {

                    monthName(names = monthNames)
                    char(value = ' ')
                    day(padding = Padding.ZERO)
                    char(value = ',')
                    char(value = ' ')
                    year(padding = Padding.ZERO)
                }

                SHORT_DATE_TIME -> LocalDateTime.Format {

                    day(padding = Padding.ZERO)
                    char(value = ':')
                    monthNumber(padding = Padding.ZERO)
                    char(value = ':')
                    year(padding = Padding.ZERO)
                    char(value = ' ')
                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ' ')
                    amPmMarker(am = "AM", pm = "PM")
                }

                SHORT_DATE_TIME_24 -> LocalDateTime.Format {

                    day(padding = Padding.ZERO)
                    char(value = ':')
                    monthNumber(padding = Padding.ZERO)
                    char(value = ':')
                    year(padding = Padding.ZERO)
                    char(value = ' ')
                    hour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                }

                LONG_DATE_TIME -> LocalDateTime.Format {

                    dayOfWeek(names = dayOfWeekNames)
                    char(value = ',')
                    char(value = ' ')
                    monthName(names = monthNames)
                    char(value = ' ')
                    day(padding = Padding.ZERO)
                    char(value = ',')
                    char(value = ' ')
                    year(padding = Padding.ZERO)
                    char(value = ' ')
                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ' ')
                    amPmMarker(am = "AM", pm = "PM")
                }

                LONG_DATE_TIME_24 -> LocalDateTime.Format {

                    dayOfWeek(names = dayOfWeekNames)
                    char(value = ',')
                    char(value = ' ')
                    monthName(names = monthNames)
                    char(value = ' ')
                    day(padding = Padding.ZERO)
                    char(value = ',')
                    char(value = ' ')
                    year(padding = Padding.ZERO)
                    char(value = ' ')
                    hour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                }

                LONG_DATE_TIME_MILLIS -> LocalDateTime.Format {

                    dayOfWeek(names = dayOfWeekNames)
                    char(value = ',')
                    char(value = ' ')
                    monthName(names = monthNames)
                    char(value = ' ')
                    day(padding = Padding.ZERO)
                    char(value = ',')
                    char(value = ' ')
                    year(padding = Padding.ZERO)
                    char(value = ' ')
                    amPmHour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                    char(value = '.')
                    secondFraction(fixedLength = 3)
                    char(value = ' ')
                    amPmMarker(am = "AM", pm = "PM")
                }

                LONG_DATE_TIME_MILLIS_24 -> LocalDateTime.Format {

                    dayOfWeek(names = dayOfWeekNames)
                    char(value = ',')
                    char(value = ' ')
                    monthName(names = monthNames)
                    char(value = ' ')
                    day(padding = Padding.ZERO)
                    char(value = ',')
                    char(value = ' ')
                    year(padding = Padding.ZERO)
                    char(value = ' ')
                    hour(padding = Padding.ZERO)
                    char(value = ':')
                    minute(padding = Padding.ZERO)
                    char(value = ':')
                    second(padding = Padding.ZERO)
                    char(value = '.')
                    secondFraction(fixedLength = 3)
                }

                FILE_NAME -> LocalDateTime.Format {

                    day(padding = Padding.ZERO)
                    char(value = '-')
                    monthNumber(padding = Padding.ZERO)
                    char(value = '-')
                    year(padding = Padding.ZERO)
                    char(value = ' ')
                    hour(padding = Padding.ZERO)
                    char(value = '-')
                    minute(padding = Padding.ZERO)
                    char(value = '-')
                    second(padding = Padding.ZERO)
                }

                DAY_ONLY -> LocalDateTime.Format {

                    dayOfWeek(names = dayOfWeekNames)
                }

                MONTH_ONLY -> LocalDateTime.Format {

                    monthName(names = monthNames)
                }

                YEAR_ONLY -> LocalDateTime.Format {

                    year(padding = Padding.ZERO)
                }

                MONTH_DAY -> LocalDateTime.Format {

                    monthName(names = monthNames)
                    char(value = ' ')
                    day(padding = Padding.ZERO)
                }

                SHORT_MONTH_YEAR -> LocalDateTime.Format {

                    monthName(names = monthNames)
                    char(value = ' ')
                    yearTwoDigits(baseYear = 1960)
                }

                MONTH_YEAR -> LocalDateTime.Format {

                    monthName(names = monthNames)
                    char(value = ' ')
                    year(padding = Padding.ZERO)
                }

                DAY_OF_YEAR -> LocalDateTime.Format {

                    dayOfYear(padding = Padding.ZERO)
                }

                DAY_OF_MONTH -> LocalDateTime.Format {

                    day(padding = Padding.ZERO)
                }

                MONTH_OF_YEAR -> LocalDateTime.Format {

                    monthNumber(padding = Padding.ZERO)
                }

                TIMESTAMP_COMPACT -> LocalDateTime.Format {

                    year(padding = Padding.ZERO)
                    monthNumber(padding = Padding.ZERO)
                    day(padding = Padding.ZERO)
                    hour(padding = Padding.ZERO)
                    minute(padding = Padding.ZERO)
                    second(padding = Padding.ZERO)
                }
            }
        }
    }
}

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