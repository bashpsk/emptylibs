package io.bashpsk.emptylibs.formatter.format

import android.content.Context
import android.text.format.Formatter
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import androidx.core.graphics.toColorLong
import io.bashpsk.emptylibs.formatter.format.EmptyFormat.duration
import io.bashpsk.emptylibs.formatter.format.EmptyFormat.time
import io.bashpsk.emptylibs.formatter.format.EmptyFormat.toRoundedDecimal
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toDuration

/**
 * `EmptyFormat` is a utility object that provides a comprehensive suite of functions for
 * formatting, parsing, and converting various data types. It focuses on date/time manipulation,
 * numerical formatting, file size representation, color conversions, and resolution calculations.
 *
 * This object aims to simplify common formatting tasks by offering a consistent and easy-to-use
 * API. It leverages the `kotlinx-datetime` library for robust date and time operations and
 * provides custom solutions for other formatting needs.
 *
 * Key features include:
 * - **Date and Time Formatting:**
 *     - Format `LocalDateTime` and epoch milliseconds into various string representations using
 *       predefined patterns.
 *     - Parse formatted date/time strings back into epoch milliseconds.
 *     - Convert time values (hours, minutes, seconds) into milliseconds and vice-versa.
 *     - Retrieve start and end of day timestamps.
 * - **Numerical Formatting:**
 *     - Round `Double` and `Float` values to a specified number of decimal places.
 *     - Format large numbers into human-readable strings with scaling suffixes (K, M, B, etc.).
 *     - Calculate percentages.
 * - **File Size Formatting:**
 *     - Convert byte counts into human-readable file size strings (e.g., "1.5 MB").
 * - **Color Formatting:**
 *     - Convert `Color` objects to hexadecimal string representations.
 *     - Convert hexadecimal color strings to `Color` objects and Android color integers.
 * - **Resolution Formatting:**
 *     - Generate human-readable labels for common screen resolutions (e.g., "1080p HD", "4K UHD").
 *     - Calculate simplified aspect ratios (e.g., "16:9").
 *
 * The `EmptyFormat` object is designed to be a versatile tool for developers needing to handle
 * diverse formatting requirements within their applications.
 */
@OptIn(ExperimentalTime::class)
@Suppress("unused")
object EmptyFormat {

    private const val LOG_TAG = "EmptyFormat"

    private val dayOfWeekNames = DayOfWeekNames(
        monday = "Mon",
        tuesday = "Tue",
        wednesday = "Wed",
        thursday = "Thu",
        friday = "Fri",
        saturday = "Sat",
        sunday = "Sun"
    )

    private val monthNames = MonthNames(
        january = "Jan",
        february = "Feb",
        march = "Mar",
        april = "Apr",
        may = "May",
        june = "Jun",
        july = "Jul",
        august = "Aug",
        september = "Sep",
        october = "Oct",
        november = "Nov",
        december = "Dec"
    )

    /**
     * Formats a date and time represented by milliseconds since the epoch into a `String` based on
     * the specified pattern.
     *
     * @param millis The date and time in milliseconds since the epoch.
     * @param pattern The formatting pattern to apply.
     * @return The formatted date and time `String`.
     */
    @JvmStatic
    fun dateTime(millis: Long, pattern: DateTimePattern): String {

        val instant = Instant.Companion.fromEpochMilliseconds(epochMilliseconds = millis)
        val localDateTime = instant.toLocalDateTime(timeZone = TimeZone.currentSystemDefault())

        return dateTime(localDateTime = localDateTime, pattern = pattern)
    }

    /**
     * Formats a `LocalDateTime` object into a `String` based on the specified pattern.
     *
     * @param localDateTime The `LocalDateTime` object to format.
     * @param pattern The formatting pattern to apply.
     * @return The formatted date and time `String`.
     */
    @JvmStatic
    fun dateTime(localDateTime: LocalDateTime, pattern: DateTimePattern): String {

        return localDateTime.format(format = findDateTimeFormat(pattern = pattern))
    }

    /**
     * Parses a formatted date and time `String` into milliseconds since the epoch based on the
     * specified pattern.
     *
     * @param dateTime The formatted date and time `String`.
     * @param pattern
     * @return The date and time in milliseconds since the epoch.
     */
    @JvmStatic
    fun dateTimeToMilliseconds(dateTime: String, pattern: DateTimePattern): Long? {

        return try {

            LocalDateTime.Companion.parse(
                input = dateTime,
                format = findDateTimeFormat(pattern = pattern)
            ).toInstant(timeZone = TimeZone.Companion.currentSystemDefault()).toEpochMilliseconds()
        } catch (exception: Exception) {

            Log.e(LOG_TAG, exception.message, exception)
            null
        }
    }

    /**
     * Formats a time represented by milliseconds into a `String` based on the specified pattern.
     *
     * @param time The time in milliseconds.
     * @param pattern The formatting pattern to apply.
     * @return The formatted time `String`.
     */
    @JvmStatic
    fun time(time: Long, pattern: DateTimePattern): String {

        val duration = time.toDuration(unit = DurationUnit.MILLISECONDS)
        val hours = duration.inWholeHours
        val minutes = duration.inWholeMinutes % 60
        val seconds = duration.inWholeSeconds % 60
        val nanoseconds = duration.inWholeNanoseconds % 1_000_000_000

        val localTime = LocalTime(
            hour = hours.toInt().coerceIn(0, 23),
            minute = minutes.toInt().coerceIn(0, 59),
            second = seconds.toInt().coerceIn(0, 59),
            nanosecond = nanoseconds.toInt().coerceIn(0, 999_999_999)
        )

        return time(localTime = localTime, pattern = pattern)
    }

    /**
     * Formats a time represented by milliseconds into a `String` based on the specified pattern.
     *
     * @param localTime The `LocalTime` object to format.
     * @param pattern The formatting pattern to apply.
     * @return The formatted time `String`.
     */
    @JvmStatic
    fun time(localTime: LocalTime, pattern: DateTimePattern): String {

        return localTime.format(format = findTimeFormat(pattern = pattern))
    }

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
     * - `Pattern.TIME_HH_MM` → Time in 12-hour format. Example: 07:30.
     * - `Pattern.TIME_HH_MM_24` → Time in 24-hour format. Example: 19:30.
     * - `Pattern.TIME_HH_MM_SS` → Time in 12-hour format, including seconds. Example: 07:30:33.
     * - `Pattern.TIME_HH_MM_SS_24` → Time in 24-hour format, including seconds. Example: 19:30:33.
     * - `Pattern.TIME_12` → Time in 12-hour format with AM/PM indicator. Example: 07:30:33 PM.
     * - `Pattern.TIME_24` → Time in 24-hour format. Example: 19:30:33.
     * - `else` → `Pattern.TIME_12`.
     */
    @JvmStatic
    fun findTimeFormat(pattern: DateTimePattern): DateTimeFormat<LocalTime> {

        return when (pattern) {

            DateTimePattern.TIME_HH_MM -> LocalTime.Companion.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_24 -> LocalTime.Companion.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_SS -> LocalTime.Companion.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_SS_24 -> LocalTime.Companion.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_12 -> LocalTime.Companion.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
                char(' ')
                amPmMarker(am = "AM", pm = "PM")
            }

            DateTimePattern.TIME_24 -> LocalTime.Companion.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            else -> LocalTime.Companion.Format {

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
    fun findDateTimeFormat(pattern: DateTimePattern): DateTimeFormat<LocalDateTime> {

        return when (pattern) {

            DateTimePattern.TIME_HH_MM -> LocalDateTime.Companion.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_24 -> LocalDateTime.Companion.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_SS -> LocalDateTime.Companion.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_SS_24 -> LocalDateTime.Companion.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_12 -> LocalDateTime.Companion.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
                char(' ')
                amPmMarker(am = "AM", pm = "PM")
            }

            DateTimePattern.TIME_24 -> LocalDateTime.Companion.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.SHORT_DATE -> LocalDateTime.Companion.Format {

                day(padding = Padding.ZERO)
                char(value = ':')
                monthNumber(padding = Padding.ZERO)
                char(value = ':')
                year(padding = Padding.ZERO)
            }

            DateTimePattern.LONG_DATE -> LocalDateTime.Companion.Format {

                monthName(names = monthNames)
                char(value = ' ')
                day(padding = Padding.ZERO)
                char(value = ',')
                char(value = ' ')
                year(padding = Padding.ZERO)
            }

            DateTimePattern.SHORT_DATE_TIME -> LocalDateTime.Companion.Format {

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

            DateTimePattern.SHORT_DATE_TIME_24 -> LocalDateTime.Companion.Format {

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

            DateTimePattern.LONG_DATE_TIME -> LocalDateTime.Companion.Format {

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

            DateTimePattern.LONG_DATE_TIME_24 -> LocalDateTime.Companion.Format {

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

            DateTimePattern.LONG_DATE_TIME_MILLIS -> LocalDateTime.Companion.Format {

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

            DateTimePattern.LONG_DATE_TIME_MILLIS_24 -> LocalDateTime.Companion.Format {

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

            DateTimePattern.FILE_NAME -> LocalDateTime.Companion.Format {

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

            DateTimePattern.DAY_ONLY -> LocalDateTime.Companion.Format {

                dayOfWeek(names = dayOfWeekNames)
            }

            DateTimePattern.MONTH_ONLY -> LocalDateTime.Companion.Format {

                monthName(names = monthNames)
            }

            DateTimePattern.YEAR_ONLY -> LocalDateTime.Companion.Format {

                year(padding = Padding.ZERO)
            }

            DateTimePattern.MONTH_DAY -> LocalDateTime.Companion.Format {

                monthName(names = monthNames)
                char(value = ' ')
                day(padding = Padding.ZERO)
            }

            DateTimePattern.SHORT_MONTH_YEAR -> LocalDateTime.Companion.Format {

                monthName(names = monthNames)
                char(value = ' ')
                yearTwoDigits(baseYear = 1960)
            }

            DateTimePattern.MONTH_YEAR -> LocalDateTime.Companion.Format {

                monthName(names = monthNames)
                char(value = ' ')
                year(padding = Padding.ZERO)
            }

            DateTimePattern.DAY_OF_YEAR -> LocalDateTime.Companion.Format {

                dayOfYear(padding = Padding.ZERO)
            }

            DateTimePattern.DAY_OF_MONTH -> LocalDateTime.Companion.Format {

                day(padding = Padding.ZERO)
            }

            DateTimePattern.MONTH_OF_YEAR -> LocalDateTime.Companion.Format {

                monthNumber(padding = Padding.ZERO)
            }

            DateTimePattern.TIMESTAMP_COMPACT -> LocalDateTime.Companion.Format {

                year(padding = Padding.ZERO)
                monthNumber(padding = Padding.ZERO)
                day(padding = Padding.ZERO)
                hour(padding = Padding.ZERO)
                minute(padding = Padding.ZERO)
                second(padding = Padding.ZERO)
            }
        }
    }

    /**
     * Converts a time span, provided in hours, minutes, and seconds, into its total
     * equivalent in milliseconds.
     *
     * @param hours The number of hours in the time span.
     * @param minutes The number of minutes in the time span.
     * @param seconds The number of seconds in the time span.
     * @return The total number of milliseconds equivalent to the input time span or 0L if an
     * exception occurs.
     */
    @JvmStatic
    fun timeToMilliseconds(hours: Int, minutes: Int, seconds: Int): Long? {

        return try {

            val hourMillis = Duration.Companion.convert(
                value = hours.toDouble(),
                sourceUnit = DurationUnit.HOURS,
                targetUnit = DurationUnit.MILLISECONDS
            ).toLong()

            val minuteMillis = Duration.Companion.convert(
                value = minutes.toDouble(),
                sourceUnit = DurationUnit.MINUTES,
                targetUnit = DurationUnit.MILLISECONDS
            ).toLong()

            val secondMillis = Duration.Companion.convert(
                value = seconds.toDouble(),
                sourceUnit = DurationUnit.SECONDS,
                targetUnit = DurationUnit.MILLISECONDS
            ).toLong()

            hourMillis + minuteMillis + secondMillis
        } catch (exception: Exception) {

            Log.e(LOG_TAG, exception.message, exception)
            null
        }
    }

    /**
     * Converts an integer representing a time value into a two-digit string.
     *
     * This function uses the default locale to format the time value as a string
     * with leading zeros if the value is less than 10, ensuring a consistent
     * two-digit representation (e.g., "01", "09", "10", "25").
     *
     * If any exception occurs during the formatting process, it logs the error
     * and returns "00" as a default fallback value.
     *
     * @param time The integer representing the time value to format.
     * @return A string representing the formatted time, always two digits long.
     * Returns "00" in case of an exception.
     */
    @JvmStatic
    fun toRoundTime(time: Int): String {

        return try {

            String.format(locale = Locale.getDefault(), format = "%02d", time)
        } catch (exception: Exception) {

            Log.e(LOG_TAG, exception.message, exception)
            "00"
        }
    }

    /**
     * Formats a `Duration` object into a human-readable string based on the specified
     * `DurationPattern`.
     *
     * This function automatically selects the most appropriate format depending on the length of
     * the `Duration` and the type of `DurationPattern` provided.
     *
     * Supported pattern types:
     *
     * 1. `DurationPattern.Separator`: Uses a character (e.g., `:`) to separate time components.
     *    - If the duration is less than 1 hour: formatted as `MM:SS`
     *    - If the duration is less than 1 day: formatted as `HH:MM:SS`
     *    - If the duration is 1 day or more: formatted as `D:HH:MM:SS`
     *
     * 2. `DurationPattern.TimeLabel`: Uses localized labels (e.g., "d", "h", "m", "s") to separate
     * components.
     *    - If the duration is less than 1 hour: formatted as `MMm SSs`
     *    - If the duration is less than 1 day: formatted as `HHh MMm SSs`
     *    - If the duration is 1 day or more: formatted as `Dd HHh MMm SSs`
     *
     * Formatting rules:
     * - All numerical components are zero-padded to two digits where applicable
     * (e.g., `05` instead of `5`), except for days.
     * - For a zero duration, the output is `"0"` followed by the seconds label if provided
     * (e.g., `"0s"`), or just `"0"` otherwise.
     *
     * Example usage:
     * ```
     * duration(3723, DurationUnit.SECONDS, DurationPattern.Separator(':')) // returns "01:02:03"
     * ```
     *
     * @param durationValue the numeric value of the duration
     * @param unit the unit of time for the duration value (e.g., seconds, minutes)
     * @param pattern the formatting pattern to apply
     * @return a human-readable string representation of the duration
     */
    @JvmStatic
    fun duration(durationValue: Long, unit: DurationUnit, pattern: DurationPattern): String {

        return duration(duration = durationValue.toDuration(unit), pattern = pattern)
    }

    /**
     * Formats a `Duration` object into a human-readable string based on the specified
     * `DurationPattern`.
     *
     * This function automatically selects the most appropriate format depending on the length of
     * the `Duration` and the type of `DurationPattern` provided.
     *
     * Supported pattern types:
     *
     * 1. `DurationPattern.Separator`: Uses a character (e.g., `:`) to separate time components.
     *    - If the duration is less than 1 hour: formatted as `MM:SS`
     *    - If the duration is less than 1 day: formatted as `HH:MM:SS`
     *    - If the duration is 1 day or more: formatted as `D:HH:MM:SS`
     *
     * 2. `DurationPattern.TimeLabel`: Uses localized labels (e.g., "d", "h", "m", "s") to separate
     * components.
     *    - If the duration is less than 1 hour: formatted as `MMm SSs`
     *    - If the duration is less than 1 day: formatted as `HHh MMm SSs`
     *    - If the duration is 1 day or more: formatted as `Dd HHh MMm SSs`
     *
     * Formatting rules:
     * - All numerical components are zero-padded to two digits where applicable
     * (e.g., `05` instead of `5`), except for days.
     * - For a zero duration, the output is `"0"` followed by the seconds label if provided
     * (e.g., `"0s"`), or just `"0"` otherwise.
     *
     * Example usage:
     * ```
     * duration(3723, DurationUnit.SECONDS, DurationPattern.Separator(':')) // returns "01:02:03"
     * ```
     *
     * @param duration the `Duration` to format
     * @param pattern the formatting pattern to apply
     * @return a human-readable string representation of the duration
     */
    @JvmStatic
    fun duration(duration: Duration, pattern: DurationPattern): String {

        val patternMap = when (pattern) {

            is DurationPattern.TimeLabel -> findDurationPattern(
                duration = duration,
                daysLabel = "${pattern.days} ",
                hoursLabel = "${pattern.hours} ",
                minutesLabel = "${pattern.minutes} ",
                secondsLabel = pattern.seconds
            )

            is DurationPattern.Separator -> findDurationPattern(
                duration = duration,
                daysLabel = pattern.char,
                hoursLabel = pattern.char,
                minutesLabel = pattern.char,
                secondsLabel = null
            )
        }

        return patternMap.first.format(
            locale = Locale.getDefault(),
            *patternMap.second.toTypedArray()
        )
    }

    private fun findDurationPattern(
        duration: Duration,
        daysLabel: String,
        hoursLabel: String,
        minutesLabel: String,
        secondsLabel: String?
    ): Pair<String, PersistentList<Any>> {

        return when {

            duration == Duration.ZERO -> "%01d${secondsLabel ?: ""}" to persistentListOf(0)

            duration < 1.toDuration(
                DurationUnit.HOURS
            ) -> duration.toComponents { minutes, seconds, nanoseconds ->

                "%02d${minutesLabel}%02d${secondsLabel ?: ""}" to persistentListOf(minutes, seconds)
            }

            duration < 1.toDuration(
                DurationUnit.DAYS
            ) -> duration.toComponents { hours, minutes, seconds, nanoseconds ->

                "%02d${hoursLabel}%02d${minutesLabel}%02d${secondsLabel ?: ""}" to persistentListOf(
                    hours,
                    minutes,
                    seconds
                )
            }

            else -> duration.toComponents { days, hours, minutes, seconds, nanoseconds ->

                "%01d${daysLabel}%02d${hoursLabel}%02d${minutesLabel}%02d${
                    secondsLabel ?: ""
                }" to persistentListOf(days, hours, minutes, seconds)
            }
        }
    }

    /**
     * Retrieves the start of the current day in milliseconds since the epoch.
     *
     * This function utilizes the `kotlinx-datetime` library to determine the current system's
     * default timezone, obtain the current date and time, and calculate the start of the current
     * day.
     *
     * The returned value represents the number of milliseconds elapsed between the Unix epoch
     * (January 1, 1970, 00:00:00 UTC) and the start of the current day in the system's default
     * timezone.
     *
     * @return A [Long] representing the start of the current day in milliseconds.
     */
    @JvmStatic
    fun getTodayStartMillis(): Long {

        val timeZone = TimeZone.Companion.currentSystemDefault()
        val localDateTime = Clock.System.now().toLocalDateTime(timeZone = timeZone)
        val startOfDay = localDateTime.date.atStartOfDayIn(timeZone = timeZone)

        return startOfDay.toEpochMilliseconds()
    }

    /**
     * Calculates the end-of-day timestamp in milliseconds for the current day.
     *
     * This function utilizes the `kotlinx-datetime` library to determine the current system's
     * default timezone, obtain the current date and time, and calculate the end of the current
     * day.
     *
     * @return [Long] The timestamp representing the last millisecond of the current day.
     */
    @JvmStatic
    fun getTodayEndMillis(): Long {

        val timeZone = TimeZone.Companion.currentSystemDefault()
        val localDateTime = Clock.System.now().toLocalDateTime(timeZone = timeZone)

        val endOfDay = localDateTime.date.plus(period = DatePeriod(days = 1)).atStartOfDayIn(
            timeZone = timeZone
        ).minus(value = 1, unit = DateTimeUnit.Companion.NANOSECOND)

        return endOfDay.toEpochMilliseconds()
    }

    /**
     * Calculates the start of the day in milliseconds for a given [LocalDateTime].
     *
     * This function takes a [LocalDateTime] object and returns the corresponding
     * time in milliseconds that represents the start of that day in the current system's
     * default timezone.
     *
     * @param localDateTime The [LocalDateTime] for which to calculate the start of the day.
     * @return A [Long] representing the start of the day in milliseconds since the epoch.
     */
    @JvmStatic
    fun getDayStartMillis(localDateTime: LocalDateTime): Long {

        val timeZone = TimeZone.Companion.currentSystemDefault()
        val startOfDay = localDateTime.date.atStartOfDayIn(timeZone = timeZone)

        return startOfDay.toEpochMilliseconds()
    }

    /**
     * Calculates the end of the day in epoch milliseconds for a given [LocalDateTime].
     *
     * This function takes a [LocalDateTime] object and returns the corresponding
     * time in milliseconds that represents the end of that day in the current system's
     * default timezone.
     *
     * @param localDateTime The [LocalDateTime] for which to find the end of the day.
     * @return A [Long] representing the end of the day in milliseconds since the epoch.
     */
    @JvmStatic
    fun getDayEndMillis(localDateTime: LocalDateTime): Long {

        val timeZone = TimeZone.Companion.currentSystemDefault()

        val endOfDay = localDateTime.date.plus(period = DatePeriod(days = 1)).atStartOfDayIn(
            timeZone = timeZone
        ).minus(value = 1, unit = DateTimeUnit.Companion.NANOSECOND)

        return endOfDay.toEpochMilliseconds()
    }

    /**
     * Rounds a `Double` to a specified number of decimal places.
     *
     * This function takes a `Double` value and rounds it to the nearest decimal
     * place specified by the `fraction` parameter. It uses the default locale
     * for formatting. If an exception occurs during the rounding process, it
     * will log the error using `Log.e()` and return 0.0.
     *
     * @param decimal The `Double` value to be rounded.
     * @param fraction The number of decimal places to round to. Defaults to 0.0.
     * @return The rounded `Double` value, or 0.0 if an error occurs.
     */
    @JvmStatic
    fun toRoundedDecimal(decimal: Double, fraction: Int = 0): Double {

        return try {

            String.format(
                locale = Locale.getDefault(),
                format = "%.${fraction}f",
                decimal
            ).toDouble()
        } catch (exception: Exception) {

            Log.e(LOG_TAG, exception.message, exception)
            0.0
        }
    }

    /**
     * Rounds a `Float` to a specified number of decimal places.
     *
     * Similar to `toRoundedDecimal(Double, Int)`, this function rounds a `Float`
     * value to the nearest decimal place specified by the `fraction` parameter.
     * It also uses the default locale for formatting. If an error occurs during
     * rounding, it will log the error using `Log.e()` and return 0F.
     *
     * @param decimal The `Float` value to be rounded.
     * @param fraction The number of decimal places to round to. Defaults to 0F.
     * @return The rounded `Float` value, or 0F if an error occurs.
     */
    @JvmStatic
    fun toRoundedDecimal(decimal: Float, fraction: Int = 0): Float {

        return try {

            String.format(
                locale = Locale.getDefault(),
                format = "%.${fraction}f",
                decimal
            ).toFloat()
        } catch (exception: Exception) {

            Log.e(LOG_TAG, exception.message, exception)
            0F
        }
    }

    /**
     * Formats a file size represented as a Long into a human-readable string using the
     * system's default formatting.
     *
     * This method utilizes the Android framework's `Formatter` class to format the provided
     * `size` into a file size string appropriate for the user's locale.
     *
     * @param context The application's context, used by the underlying `Formatter`.
     * @param size The file size in bytes.
     * @return A string representing the formatted file size (e.g., "1.5 MB", "1024 KB").
     *
     * @see android.text.format.Formatter.formatFileSize
     */
    @JvmStatic
    fun toFileSize(context: Context, size: Long): String {

        return Formatter.formatFileSize(context, size).uppercase()
    }

    /**
     * Formats a file size represented as a Long into a human-readable string.
     *
     * This method converts the provided `size` (in bytes) into a string with the appropriate
     * magnitude suffix (B, KB, MB, GB, TB, PB, EB, ZB, YB). The output string is formatted
     * to two decimal places and uses the current locale's formatting.
     *
     * @param size The file size in bytes.
     * @return A string representing the formatted file size (e.g., "1.50 MB", "1024.00 KB").
     */
    @JvmStatic
    fun toFileSize(size: Long): String {

        val units = persistentListOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")

        var length = size.toDouble()
        var order = 0

        while (length >= 1024 && order < units.size - 1) {

            order++
            length /= 1024
        }

        return String.format(
            locale = Locale.getDefault(),
            format = "%.2f %s",
            length,
            units.getOrElse(index = order) { 0 }
        ).uppercase()
    }

    /**
     * Formats a `Long` value into a human-readable string with scaling suffixes.
     *
     * @param value The `Long` value to format.
     * @return A formatted `String` representation of the input value.
     * Example:
     * - 1234 -> "1.2K"
     * - 1234567 -> "1.2M"
     * - 123 -> "123"
     */
    @JvmStatic
    fun shortenedNumericalNotation(value: Long): String {

        return when {

            value >= 1_000_000_000_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fQ",
                value / 1_000_000_000_000_000.0
            )

            value >= 1_000_000_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fT",
                value / 1_000_000_000_000.0
            )

            value >= 1_000_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fB",
                value / 1_000_000_000.0
            )

            value >= 1_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fM",
                value / 1_000_000.0
            )

            value >= 1_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fK",
                value / 1_000.0
            )

            else -> value.toString()
        }
    }

    /**
     * Formats a `Double` value into a human-readable string with scaling suffixes.
     *
     * @param value The `Double` value to format.
     * @return A formatted `String` representation of the input value.
     * Example:
     * - 1234 -> "1.2K"
     * - 1234567 -> "1.2M"
     * - 123 -> "123"
     */
    @JvmStatic
    fun shortenedNumericalNotation(value: Double): String {

        return when {

            value >= 1_000_000_000_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fQ",
                value / 1_000_000_000_000_000.0
            )

            value >= 1_000_000_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fT",
                value / 1_000_000_000_000.0
            )

            value >= 1_000_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fB",
                value / 1_000_000_000.0
            )

            value >= 1_000_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fM",
                value / 1_000_000.0
            )

            value >= 1_000 -> String.format(
                locale = Locale.getDefault(),
                format = "%.1fK",
                value / 1_000.0
            )

            else -> String.format(
                locale = Locale.getDefault(),
                format = "%.1f",
                value
            )
        }
    }

    /**
     * Formats an `Int` value into a human-readable string with scaling suffixes.
     *
     * @param value The `Int` value to format.
     * @return A formatted `String` representation of the input value.
     * Example:
     * - 1234 -> "1.2K"
     * - 1234567 -> "1.2M"
     * - 123 -> "123"
     */
    @JvmStatic
    fun shortenedNumericalNotation(value: Int): String {

        return shortenedNumericalNotation(value = value.toLong())
    }

    /**
     * Converts a [Color] object to its hexadecimal string representation, including alpha.
     *
     * The resulting string will be in the format "AARRGGBB", where AA is the alpha component,
     * RR is red, GG is green, and BB is blue, all in hexadecimal.
     *
     * @param color The [Color] object to convert.
     * @return A [String] representing the color in "#AARRGGBB" hexadecimal format.
     * For example, `Color.Red` would be returned as "#FFFF0000".
     */
    @JvmStatic
    fun toColorHex(color: Color): String {

        return String.format(locale = Locale.getDefault(), format = "#%08X", color.toArgb())
    }

    /**
     * Converts a hexadecimal color string to an Android [Color] object.
     *
     * This function supports hexadecimal color strings in the following formats:
     * - `#RRGGBB` (e.g., "#FF0000" for red)
     * - `#AARRGGBB` (e.g., "#80FF0000" for semi-transparent red)
     * - `#RGB` (e.g., "#F00" for red, shorthand notation)
     * - `#ARGB` (e.g., "#8F00" for semi-transparent red, shorthand notation)
     *
     * The hexadecimal values are case-insensitive.
     *
     * If the input string is not in a valid hexadecimal format, or if it does not represent
     * a valid color, the function will return `null`.
     *
     * @param hex The hexadecimal color string to convert. Must start with '#'.
     * @return The corresponding [Color] object, or `null` if parsing fails.
     */
    @JvmStatic
    fun hexToColor(hex: String): Color? {

        return try {

            Color.fromColorLong(colorLong = hex.toColorInt().toColorLong())
        } catch (exception: Exception) {

            Log.e(LOG_TAG, "Failed to parse hex to Color : $hex", exception)
            null
        }
    }

    /**
     * Converts a hexadecimal color string to an Android color integer.
     *
     * This function takes a hexadecimal color string as input and returns the corresponding
     * Android color integer. The input string can be in the following formats:
     * - "#RRGGBBAA" (e.g., "#FF0000FF" for red with full opacity)
     *
     * If the input string is not in a valid hexadecimal format, or if it does not represent
     * a valid color, the function will return an [Color.Companion.Unspecified].
     *
     * @param hex The hexadecimal color string.
     * @return The Android color integer.
     */
    @JvmStatic
    fun hexToArgb(hex: String): Int? {

        val cleanHex = hex.removePrefix("#")

        return try {

            cleanHex.hexToInt(format = HexFormat.Default)
        } catch (exception: Exception) {

            Log.e(LOG_TAG, "Failed to parse hex to Android Color Int : $hex", exception)
            null
        }
    }

    /**
     * Calculates the percentage of obtained value relative to the total.
     * @param total The total possible value.
     * @param obtained The obtained value.
     * @return The percentage value as an integer, or 0 if total is zero.
     */
    @JvmStatic
    fun findPercentage(total: Long, obtained: Long): Int {

        return when (total) {

            0L -> 0
            else -> ((obtained.toDouble() / total) * 100).toInt()
        }
    }

    /**
     * Calculates the percentage of obtained value relative to the total.
     * @param total The total possible value.
     * @param obtained The obtained value.
     * @return The percentage value as an integer, or 0 if total is zero.
     */
    @JvmStatic
    fun findPercentage(total: Int, obtained: Int): Int {

        return when (total) {

            0 -> 0
            else -> ((obtained.toDouble() / total) * 100).toInt()
        }
    }

    /**
     * Calculates the percentage of obtained value relative to the total.
     * @param total The total possible value.
     * @param obtained The obtained value.
     * @return The percentage value rounded to one decimal place, or 0.0 if total is zero.
     * @see toRoundedDecimal for rounding the result.
     */
    @JvmStatic
    fun findPercentage(total: Double, obtained: Double): Double {

        return when (total) {

            0.0 -> 0.0
            else -> toRoundedDecimal(decimal = (obtained / total) * 100, fraction = 1)
        }
    }

    /**
     * Calculates the percentage of obtained value relative to the total.
     * @param total The total possible value.
     * @param obtained The obtained value.
     * @return The percentage value rounded to one decimal place, or 0.0F if total is zero.
     * @see toRoundedDecimal for rounding the result.
     */
    @JvmStatic
    fun findPercentage(total: Float, obtained: Float): Float {

        return when (total) {

            0.0F -> 0.0F
            else -> toRoundedDecimal(decimal = (obtained / total) * 100, fraction = 1)
        }
    }

    /**
     * Returns a human-readable resolution label (e.g., "1080p", "4K UHD", "16K") based on screen
     * width and height.
     *
     * This function identifies common video and display resolutions including SD, HD, Full HD,
     * Ultra HD,
     * cinema-grade resolutions, and even ultra-high formats like 12K and 16K. If the resolution
     * doesn't match
     * a known label, it falls back to raw pixel dimensions (e.g., "1234x567").
     *
     * @param width  The width of the resolution in pixels.
     * @param height The height of the resolution in pixels.
     * @return A string describing the resolution in standard format.
     *
     * Example:
     * ```
     * formatResolutionLabel(1920, 1080) // returns "1080p HD"
     * formatResolutionLabel(15360, 8640) // returns "16K UHD"
     * ```
     */
    @JvmStatic
    fun findResolutionLabel(width: Int, height: Int): String {

        val resolution = maxOf(width, height)

        return when {

            resolution >= 15360 -> "16K UHD"
            resolution >= 11520 -> "12K UHD"
            resolution >= 8192 -> "8K UHD"
            resolution >= 7680 -> "8K"
            resolution >= 5120 -> "5K"
            resolution >= 4096 -> "4K DCI"
            resolution >= 3840 -> "4K UHD"
            resolution >= 3200 -> "3K"
            resolution >= 2880 -> "WQHD+"
            resolution >= 2560 -> "2.5K"
            resolution >= 2048 -> "2K DCI"
            resolution >= 1920 -> "1080p HD"
            resolution >= 1600 -> "UXGA"
            resolution >= 1440 -> "HD+"
            resolution >= 1366 -> "HD"
            resolution >= 1280 -> "720p"
            resolution >= 1024 -> "XGA"
            resolution >= 960 -> "FWVGA"
            resolution >= 854 -> "480p"
            resolution >= 640 -> "360p"
            resolution >= 426 -> "240p"
            resolution >= 256 -> "144p"
            else -> "${width}x$height"
        }
    }

    /**
     * Calculates the aspect ratio of a given width and height.
     *
     * This function divides the width by the height to determine the aspect ratio.
     * If the height is zero, it returns 0.0F to prevent division by zero errors.
     *
     * @param width The width of the dimension.
     * @param height The height of the dimension.
     * @return The aspect ratio as a Float (width / height), or 0.0F if height is 0.
     */
    @JvmStatic
    fun findAspectRatio(width: Int, height: Int): Float {

        return if (height == 0) 0.0F else width / height.toFloat()
    }

    /**
     * Calculates the aspect ratio of a given width and height.
     *
     * This function divides the width by the height to determine the aspect ratio.
     * If the height is zero, it returns 0.0F to prevent division by zero errors.
     *
     * @param width The width of the dimension.
     * @param height The height of the dimension.
     * @return The aspect ratio as a Float (width / height), or 0.0F if height is 0.0F.
     */
    @JvmStatic
    fun findAspectRatio(width: Float, height: Float): Float {

        return if (height == 0.0F) 0.0F else width / height
    }

    /**
     * Calculates the simplified aspect ratio of a given width and height.
     *
     * This function uses the greatest common divisor (GCD) to reduce the given dimensions
     * to their simplest integer ratio form, commonly used in video and display resolutions.
     *
     * @param width The horizontal resolution in pixels.
     * @param height The vertical resolution in pixels.
     * @return A string representing the simplified aspect ratio (e.g., "16:9").
     *
     * Example:
     * ```
     * aspectRatioLabel(1920, 1080) // returns "16:9"
     * aspectRatioLabel(1080, 1920) // returns "9:16"
     * ```
     */
    @JvmStatic
    fun aspectRatioLabel(width: Int, height: Int): String {

        fun gcd(a: Int, b: Int): Int {

            return if (b == 0) a else gcd(b, a % b)
        }

        val divisor = gcd(width, height)
        val ratioWidth = width / divisor
        val ratioHeight = height / divisor

        return "$ratioWidth:$ratioHeight"
    }
}