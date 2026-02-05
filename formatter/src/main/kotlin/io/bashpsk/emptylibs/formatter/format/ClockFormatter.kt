package io.bashpsk.emptylibs.formatter.format

import android.util.Log
import androidx.compose.runtime.Stable
import io.bashpsk.emptylibs.formatter.format.DateTimePattern.Companion.findDateTimeFormat
import io.bashpsk.emptylibs.formatter.format.DateTimePattern.Companion.findTimeFormat
import io.bashpsk.emptylibs.formatter.utils.LOG_TAG
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toDuration

internal val defaultDayOfWeekNames = DayOfWeekNames(
    monday = "Mon",
    tuesday = "Tue",
    wednesday = "Wed",
    thursday = "Thu",
    friday = "Fri",
    saturday = "Sat",
    sunday = "Sun"
)

internal val defaultMonthNames = MonthNames(
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
@Stable
fun Long.dateTime(
    pattern: DateTimePattern,
    timeZone: TimeZone /*= TimeZone.currentSystemDefault()*/
): String {

    return Instant.fromEpochMilliseconds(
        epochMilliseconds = this@dateTime
    ).toLocalDateTime(timeZone = timeZone).dateTime(pattern = pattern)
}

/**
 * Formats a `LocalDateTime` object into a `String` based on the specified pattern.
 *
 * @param localDateTime The `LocalDateTime` object to format.
 * @param pattern The formatting pattern to apply.
 * @return The formatted date and time `String`.
 */
@Stable
fun LocalDateTime.dateTime(
    pattern: DateTimePattern,
    dayOfWeekNames: DayOfWeekNames = defaultDayOfWeekNames,
    monthNames: MonthNames = defaultMonthNames
): String {

    return format(
        format = pattern.findDateTimeFormat(
            dayOfWeekNames = dayOfWeekNames,
            monthNames = monthNames
        )
    )
}

/**
 * Formats a time represented by milliseconds into a `String` based on the specified pattern.
 *
 * @param time The time in milliseconds.
 * @param pattern The formatting pattern to apply.
 * @return The formatted time `String`.
 */
@Stable
fun Long.time(pattern: DateTimePattern): String {

    val duration = this.toDuration(unit = DurationUnit.MILLISECONDS)
    val hours = duration.inWholeHours
    val minutes = duration.inWholeMinutes % 60
    val seconds = duration.inWholeSeconds % 60
    val nanoseconds = duration.inWholeNanoseconds % 1_000_000_000

    return LocalTime(
        hour = hours.toInt().coerceIn(0..23),
        minute = minutes.toInt().coerceIn(0..59),
        second = seconds.toInt().coerceIn(0..59),
        nanosecond = nanoseconds.toInt().coerceIn(0..999_999_999)
    ).time(pattern = pattern)
}

/**
 * Formats a time represented by milliseconds into a `String` based on the specified pattern.
 *
 * @param localTime The `LocalTime` object to format.
 * @param pattern The formatting pattern to apply.
 * @return The formatted time `String`.
 */
@Stable
fun LocalTime.time(pattern: DateTimePattern): String {

    return format(format = pattern.findTimeFormat())
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
@Stable
fun Triple<Int, Int, Int>.timeToMilliseconds(): Long? {

    return try {

        val hourMillis = Duration.convert(
            value = first.toDouble(),
            sourceUnit = DurationUnit.HOURS,
            targetUnit = DurationUnit.MILLISECONDS
        ).toLong()

        val minuteMillis = Duration.convert(
            value = second.toDouble(),
            sourceUnit = DurationUnit.MINUTES,
            targetUnit = DurationUnit.MILLISECONDS
        ).toLong()

        val secondMillis = Duration.convert(
            value = third.toDouble(),
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
 * Converts a time span, provided in hours, minutes, and seconds, into its total
 * equivalent in milliseconds.
 *
 * @param hours The number of hours in the time span.
 * @param minutes The number of minutes in the time span.
 * @param seconds The number of seconds in the time span.
 * @return The total number of milliseconds equivalent to the input time span or 0L if an
 * exception occurs.
 */
@Stable
fun timeToMilliseconds(hours: Int, minutes: Int, seconds: Int): Long? {

    return Triple(first = hours, second = minutes, third = seconds).timeToMilliseconds()
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
@Stable
fun Int.toRoundTime(): String {

    return try {

        "%02d".format(locale = Locale.getDefault(), this)
    } catch (exception: Exception) {

        Log.e(LOG_TAG, exception.message, exception)
        "00"
    }
}