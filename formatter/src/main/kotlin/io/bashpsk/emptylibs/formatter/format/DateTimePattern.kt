package io.bashpsk.emptylibs.formatter.format

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

/**
 * Defines a set of predefined date and time formatting patterns.
 *
 * This enum is used to map specific formatting requirements to `kotlinx-datetime`
 * [DateTimeFormat] instances for both [LocalTime] and [LocalDateTime].
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
         * Retrieves the appropriate [DateTimeFormat] for the specified [DateTimePattern].
         *
         * This extension function maps [DateTimePattern] enum constants to their corresponding
         * [LocalTime.Format] definitions using `kotlinx-datetime` formatting logic.
         *
         * @return The [DateTimeFormat] used to format or parse [LocalTime] objects.
         *
         * Supported patterns:
         * - [DateTimePattern.TIME_HH_MM] → 12-hour format (e.g., 07:30).
         * - [DateTimePattern.TIME_HH_MM_24] → 24-hour format (e.g., 19:30).
         * - [DateTimePattern.TIME_HH_MM_SS] → 12-hour format with seconds (e.g., 07:30:33).
         * - [DateTimePattern.TIME_HH_MM_SS_24] → 24-hour format with seconds (e.g., 19:30:33).
         * - [DateTimePattern.TIME_12] → 12-hour format with AM/PM (e.g., 07:30:33 PM).
         * - [DateTimePattern.TIME_24] → 24-hour format with seconds (e.g., 19:30:33).
         * - Defaults to [DateTimePattern.TIME_12] for unsupported patterns.
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
         * Retrieves the appropriate `DateTimeFormat` for a given `DateTimePattern`.
         *
         * This function is an extension on `DateTimePattern` and maps each enum variant to its
         * corresponding `LocalDateTime.Format` definition using the `kotlinx-datetime` library.
         * It provides a comprehensive set of predefined formatters for dates, times, or both.
         *
         * The formatters for days of the week and months can be customized by providing
         * `DayOfWeekNames` and `MonthNames` respectively.
         *
         * Example usage:
         * ```kotlin
         * val pattern = DateTimePattern.LONG_DATE_TIME
         * val formatter = pattern.findDateTimeFormat()
         * val formattedString = formatter.format(LocalDateTime.now())
         * println(formattedString) // e.g., "Mon, Aug 12, 2024 02:30 PM"
         * ```
         *
         * @param dayOfWeekNames The names to use for formatting the day of the week
         * (e.g., "Mon", "Monday").
         * Defaults to a set of three-letter abbreviations [DayOfWeekNames.ENGLISH_ABBREVIATED].
         * @param monthNames The names to use for formatting the month (e.g., "Aug", "August").
         * Defaults to a set of three-letter abbreviations [MonthNames.ENGLISH_ABBREVIATED].
         * @return The corresponding `DateTimeFormat<LocalDateTime>` for the specified pattern.
         */
        @JvmStatic
        fun DateTimePattern.findDateTimeFormat(
            dayOfWeekNames: DayOfWeekNames = DayOfWeekNames.ENGLISH_ABBREVIATED,
            monthNames: MonthNames = MonthNames.ENGLISH_ABBREVIATED
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