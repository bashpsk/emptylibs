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
import io.bashpsk.emptylibs.formatter.resolution.ResolutionType
import io.bashpsk.emptylibs.formatter.utils.LOG_TAG
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
 * @deprecated This object is deprecated. Use the extension functions provided in
 * [ClockFormatter], [ColorFormatter], [DurationFormatter], [FileFormatter], [NumberFormatter],
 * and [ResolutionFormatter] instead.
 */
@Deprecated(
    message = "Use extension functions in specific formatters instead.",
    replaceWith = ReplaceWith("this.dateTime(pattern) or similar extension functions")
)
@Suppress("unused")
object EmptyFormat {

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
    @Deprecated(
        message = "Use Long.dateTime(pattern) instead.",
        replaceWith = ReplaceWith("millis.dateTime(pattern)")
    )
    fun dateTime(millis: Long, pattern: DateTimePattern): String {

        val instant = Instant.fromEpochMilliseconds(epochMilliseconds = millis)
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
    @Deprecated(
        message = "Use LocalDateTime.dateTime(pattern) instead.",
        replaceWith = ReplaceWith("localDateTime.dateTime(pattern)")
    )
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

            LocalDateTime.parse(
                input = dateTime,
                format = findDateTimeFormat(pattern = pattern)
            ).toInstant(timeZone = TimeZone.currentSystemDefault()).toEpochMilliseconds()
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
    @Deprecated(
        message = "Use Long.time(pattern) instead.",
        replaceWith = ReplaceWith("time.time(pattern)")
    )
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
    @Deprecated(
        message = "Use LocalTime.time(pattern) instead.",
        replaceWith = ReplaceWith("localTime.time(pattern)")
    )
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
     */
    @JvmStatic
    fun findTimeFormat(pattern: DateTimePattern): DateTimeFormat<LocalTime> {

        return when (pattern) {

            DateTimePattern.TIME_HH_MM -> LocalTime.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_24 -> LocalTime.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_SS -> LocalTime.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_SS_24 -> LocalTime.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_12 -> LocalTime.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
                char(' ')
                amPmMarker(am = "AM", pm = "PM")
            }

            DateTimePattern.TIME_24 -> LocalTime.Format {

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
    fun findDateTimeFormat(pattern: DateTimePattern): DateTimeFormat<LocalDateTime> {

        return when (pattern) {

            DateTimePattern.TIME_HH_MM -> LocalDateTime.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_24 -> LocalDateTime.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_SS -> LocalDateTime.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_HH_MM_SS_24 -> LocalDateTime.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.TIME_12 -> LocalDateTime.Format {

                amPmHour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
                char(' ')
                amPmMarker(am = "AM", pm = "PM")
            }

            DateTimePattern.TIME_24 -> LocalDateTime.Format {

                hour(padding = Padding.ZERO)
                char(value = ':')
                minute(padding = Padding.ZERO)
                char(value = ':')
                second(padding = Padding.ZERO)
            }

            DateTimePattern.SHORT_DATE -> LocalDateTime.Format {

                day(padding = Padding.ZERO)
                char(value = ':')
                monthNumber(padding = Padding.ZERO)
                char(value = ':')
                year(padding = Padding.ZERO)
            }

            DateTimePattern.LONG_DATE -> LocalDateTime.Format {

                monthName(names = monthNames)
                char(value = ' ')
                day(padding = Padding.ZERO)
                char(value = ',')
                char(value = ' ')
                year(padding = Padding.ZERO)
            }

            DateTimePattern.SHORT_DATE_TIME -> LocalDateTime.Format {

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

            DateTimePattern.SHORT_DATE_TIME_24 -> LocalDateTime.Format {

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

            DateTimePattern.LONG_DATE_TIME -> LocalDateTime.Format {

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

            DateTimePattern.LONG_DATE_TIME_24 -> LocalDateTime.Format {

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

            DateTimePattern.LONG_DATE_TIME_MILLIS -> LocalDateTime.Format {

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

            DateTimePattern.LONG_DATE_TIME_MILLIS_24 -> LocalDateTime.Format {

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

            DateTimePattern.FILE_NAME -> LocalDateTime.Format {

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

            DateTimePattern.DAY_ONLY -> LocalDateTime.Format {

                dayOfWeek(names = dayOfWeekNames)
            }

            DateTimePattern.MONTH_ONLY -> LocalDateTime.Format {

                monthName(names = monthNames)
            }

            DateTimePattern.YEAR_ONLY -> LocalDateTime.Format {

                year(padding = Padding.ZERO)
            }

            DateTimePattern.MONTH_DAY -> LocalDateTime.Format {

                monthName(names = monthNames)
                char(value = ' ')
                day(padding = Padding.ZERO)
            }

            DateTimePattern.SHORT_MONTH_YEAR -> LocalDateTime.Format {

                monthName(names = monthNames)
                char(value = ' ')
                yearTwoDigits(baseYear = 1960)
            }

            DateTimePattern.MONTH_YEAR -> LocalDateTime.Format {

                monthName(names = monthNames)
                char(value = ' ')
                year(padding = Padding.ZERO)
            }

            DateTimePattern.DAY_OF_YEAR -> LocalDateTime.Format {

                dayOfYear(padding = Padding.ZERO)
            }

            DateTimePattern.DAY_OF_MONTH -> LocalDateTime.Format {

                day(padding = Padding.ZERO)
            }

            DateTimePattern.MONTH_OF_YEAR -> LocalDateTime.Format {

                monthNumber(padding = Padding.ZERO)
            }

            DateTimePattern.TIMESTAMP_COMPACT -> LocalDateTime.Format {

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
    @OptIn(ExperimentalTime::class)
    @JvmStatic
    @Deprecated(
        message = "Use timeToMilliseconds(hours, minutes, seconds) from ClockFormatter instead.",
        replaceWith = ReplaceWith("timeToMilliseconds(hours, minutes, seconds)")
    )
    fun timeToMilliseconds(hours: Int, minutes: Int, seconds: Int): Long? {

        return try {

            val hourMillis = Duration.convert(
                value = hours.toDouble(),
                sourceUnit = DurationUnit.HOURS,
                targetUnit = DurationUnit.MILLISECONDS
            ).toLong()

            val minuteMillis = Duration.convert(
                value = minutes.toDouble(),
                sourceUnit = DurationUnit.MINUTES,
                targetUnit = DurationUnit.MILLISECONDS
            ).toLong()

            val secondMillis = Duration.convert(
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
    @Deprecated(
        message = "Use Int.toRoundTime() from ClockFormatter instead.",
        replaceWith = ReplaceWith("time.toRoundTime()")
    )
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
     * @param durationValue the numeric value of the duration
     * @param unit the unit of time for the duration value (e.g., seconds, minutes)
     * @param pattern the formatting pattern to apply
     * @return a human-readable string representation of the duration
     */
    @JvmStatic
    @Deprecated(
        message = "Use duration(durationValue, unit, pattern) from DurationFormatter instead.",
        replaceWith = ReplaceWith("duration(durationValue, unit, pattern)")
    )
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
     * @param duration the `Duration` to format
     * @param pattern the formatting pattern to apply
     * @return a human-readable string representation of the duration
     */
    @JvmStatic
    @Deprecated(
        message = "Use Duration.duration(pattern) from DurationFormatter instead.",
        replaceWith = ReplaceWith("duration.duration(pattern)")
    )
    fun duration(duration: Duration, pattern: DurationPattern): String {

        val patternMap = when (pattern) {

            is DurationPattern.TimeLabel -> findDurationPattern(
                duration = duration,
                daysLabel = pattern.days,
                hoursLabel = pattern.hours,
                minutesLabel = pattern.minutes,
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
                DurationUnit.MINUTES
            ) -> duration.toComponents { seconds, nanoseconds ->

                "%02d${secondsLabel ?: ""}" to persistentListOf(seconds)
            }

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

        val timeZone = TimeZone.currentSystemDefault()
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

        val timeZone = TimeZone.currentSystemDefault()
        val localDateTime = Clock.System.now().toLocalDateTime(timeZone = timeZone)

        val endOfDay = localDateTime.date.plus(period = DatePeriod(days = 1)).atStartOfDayIn(
            timeZone = timeZone
        ).minus(value = 1, unit = DateTimeUnit.NANOSECOND)

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

        val timeZone = TimeZone.currentSystemDefault()
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

        val timeZone = TimeZone.currentSystemDefault()

        val endOfDay = localDateTime.date.plus(period = DatePeriod(days = 1)).atStartOfDayIn(
            timeZone = timeZone
        ).minus(value = 1, unit = DateTimeUnit.NANOSECOND)

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
    @Deprecated(
        message = "Use Double.toRoundedDecimal(fraction) instead.",
        replaceWith = ReplaceWith("decimal.toRoundedDecimal(fraction)")
    )
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
    @Deprecated(
        message = "Use Float.toRoundedDecimal(fraction) instead.",
        replaceWith = ReplaceWith("decimal.toRoundedDecimal(fraction)")
    )
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
    @Deprecated(
        message = "Use Long.toFileSize(context) instead.",
        replaceWith = ReplaceWith("size.toFileSize(context)")
    )
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
    @Deprecated(
        message = "Use Long.toFileSize() instead.",
        replaceWith = ReplaceWith("size.toFileSize()")
    )
    fun toFileSize(size: Long): String {

        val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")

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
     */
    @JvmStatic
    @Deprecated(
        message = "Use Long.shortenedNumericalNotation() instead.",
        replaceWith = ReplaceWith("value.shortenedNumericalNotation()")
    )
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
     */
    @JvmStatic
    @Deprecated(
        message = "Use Double.shortenedNumericalNotation() instead.",
        replaceWith = ReplaceWith("value.shortenedNumericalNotation()")
    )
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
     */
    @JvmStatic
    @Deprecated(
        message = "Use Int.shortenedNumericalNotation() instead.",
        replaceWith = ReplaceWith("value.shortenedNumericalNotation()")
    )
    fun shortenedNumericalNotation(value: Int): String {

        return shortenedNumericalNotation(value = value.toLong())
    }

    /**
     * Converts a [Color] object to its hexadecimal string representation, including alpha.
     *
     * @param color The [Color] object to convert.
     * @return A [String] representing the color in "#AARRGGBB" hexadecimal format.
     */
    @JvmStatic
    @Deprecated(
        message = "Use Color.toHexString() instead.",
        replaceWith = ReplaceWith("color.toHexString()")
    )
    fun toColorHex(color: Color): String {

        return String.format(locale = Locale.getDefault(), format = "#%08X", color.toArgb())
    }

    /**
     * Converts a hexadecimal color string to an Android [Color] object.
     *
     * @param hex The hexadecimal color string to convert. Must start with '#'.
     * @return The corresponding [Color] object, or `null` if parsing fails.
     */
    @JvmStatic
    @Deprecated(
        message = "Use String.parseHexToColor() instead.",
        replaceWith = ReplaceWith("hex.parseHexToColor()")
    )
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
    @Deprecated(
        message = "Use findPercentage(total, obtained) instead.",
        replaceWith = ReplaceWith("findPercentage(total, obtained)")
    )
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
    @Deprecated(
        message = "Use findPercentage(total, obtained) instead.",
        replaceWith = ReplaceWith("findPercentage(total, obtained)")
    )
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
     */
    @JvmStatic
    @Deprecated(
        message = "Use findPercentage(total, obtained) instead.",
        replaceWith = ReplaceWith("findPercentage(total, obtained)")
    )
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
     */
    @JvmStatic
    @Deprecated(
        message = "Use findPercentage(total, obtained) instead.",
        replaceWith = ReplaceWith("findPercentage(total, obtained)")
    )
    fun findPercentage(total: Float, obtained: Float): Float {

        return when (total) {

            0.0F -> 0.0F
            else -> toRoundedDecimal(decimal = (obtained / total) * 100, fraction = 1)
        }
    }

    /**
     * Finds a human-readable resolution label (e.g., "1080p HD", "4K UHD") for a given width and
     * height.
     *
     * @param width The width of the resolution in pixels.
     * @param height The height of the resolution in pixels.
     * @return A `String` containing the standard resolution label (e.g., "1080p FHD") or the
     * raw dimensions (e.g., "1366x768").
     */
    @JvmStatic
    @Deprecated(
        message = "Use findResolutionLabel(width, height) instead.",
        replaceWith = ReplaceWith("findResolutionLabel(width, height)")
    )
    fun findResolutionLabel(width: Int, height: Int): String {

        return findResolutionLabelOrNull(width = width, height = height) ?: "${width}x${height}"
    }

    /**
     * Finds a human-readable resolution label (e.g., "1080p HD", "4K UHD") for a given width and
     * height, returning null if no standard match is found.
     *
     * @param width The width of the resolution in pixels.
     * @param height The height of the resolution in pixels.
     * @return A `String` containing the standard resolution label (e.g., "1080p FHD"), or `null`
     * if the resolution is not a recognized standard.
     */
    @JvmStatic
    @Deprecated(
        message = "Use findResolutionLabelOrNull(width, height) instead.",
        replaceWith = ReplaceWith("findResolutionLabelOrNull(width, height)")
    )
    fun findResolutionLabelOrNull(width: Int, height: Int): String? {

        return ResolutionType.findOrNull(width = width, height = height)?.label
    }

    /**
     * Calculates the aspect ratio of a given width and height.
     *
     * @param width The width of the dimension.
     * @param height The height of the dimension.
     * @return The aspect ratio as a Float (width / height), or 0.0F if height is 0.
     */
    @JvmStatic
    @Deprecated(
        message = "Use findAspectRatio(width, height) instead.",
        replaceWith = ReplaceWith("findAspectRatio(width, height)")
    )
    fun findAspectRatio(width: Int, height: Int): Float {

        return if (height == 0) 0.0F else width / height.toFloat()
    }

    /**
     * Calculates the aspect ratio of a given width and height.
     *
     * @param width The width of the dimension.
     * @param height The height of the dimension.
     * @return The aspect ratio as a Float (width / height), or 0.0F if height is 0.0F.
     */
    @JvmStatic
    @Deprecated(
        message = "Use findAspectRatio(width, height) instead.",
        replaceWith = ReplaceWith("findAspectRatio(width, height)")
    )
    fun findAspectRatio(width: Float, height: Float): Float {

        return if (height == 0.0F) 0.0F else width / height
    }

    /**
     * Calculates the simplified aspect ratio of a given width and height.
     *
     * @param width The horizontal resolution in pixels.
     * @param height The vertical resolution in pixels.
     * @return A string representing the simplified aspect ratio (e.g., "16:9").
     */
    @JvmStatic
    @Deprecated(
        message = "Use aspectRatioLabel(width, height) instead.",
        replaceWith = ReplaceWith("aspectRatioLabel(width, height)")
    )
    fun aspectRatioLabel(width: Int, height: Int): String {

        fun gcd(a: Int, b: Int): Int {

            return if (b == 0) a else gcd(b, a % b)
        }

        val divisor = gcd(width, height)

        return "${width / divisor}:${height / divisor}"
    }
}