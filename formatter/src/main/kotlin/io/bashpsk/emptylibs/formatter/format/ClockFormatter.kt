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
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toDuration

/**
 * Default abbreviated names for the days of the week used for formatting.
 */
internal val defaultDayOfWeekNames = DayOfWeekNames(
    monday = "Mon",
    tuesday = "Tue",
    wednesday = "Wed",
    thursday = "Thu",
    friday = "Fri",
    saturday = "Sat",
    sunday = "Sun"
)

/**
 * The default abbreviated names for the months of the year used for formatting.
 */
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
 * @param pattern The formatting pattern to apply.
 * @param timeZone The time zone to use for conversion. Defaults to the system default.
 * @return The formatted date and time `String`.
 */
@Stable
fun Long.dateTime(
    pattern: DateTimePattern,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {

    return Instant.fromEpochMilliseconds(
        epochMilliseconds = this@dateTime
    ).toLocalDateTime(timeZone = timeZone).dateTime(pattern = pattern)
}

/**
 * Formats a `LocalDateTime` object into a `String` based on the specified pattern.
 *
 * @param pattern The formatting pattern to apply.
 * @param dayOfWeekNames Custom names for the days of the week.
 * @param monthNames Custom names for the months.
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
 * Parses a formatted date and time `String` into milliseconds since the epoch based on the
 * specified pattern.
 *
 * @param pattern The formatting pattern that was used for the input string.
 * @param timeZone The time zone to use for conversion. Defaults to the system default.
 * @return The date and time in milliseconds since the epoch, or `null` if parsing fails.
 */
@Stable
fun String.parseDateTimeToMilliseconds(
    pattern: DateTimePattern,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): Long? {

    return try {

        LocalDateTime.parse(
            input = this,
            format = pattern.findDateTimeFormat()
        ).toInstant(timeZone = timeZone).toEpochMilliseconds()
    } catch (exception: Exception) {

        Log.e(LOG_TAG, exception.message, exception)
        null
    }
}

/**
 * Formats a time represented by milliseconds into a `String` based on the specified pattern.
 *
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
 * Formats a [LocalTime] object into a `String` based on the specified pattern.
 *
 * @param pattern The formatting pattern to apply.
 * @return The formatted time `String`.
 */
@Stable
fun LocalTime.time(pattern: DateTimePattern): String {

    return format(format = pattern.findTimeFormat())
}

/**
 * Converts a time span, provided as a [Triple] of (hours, minutes, seconds), into its total
 * equivalent in milliseconds.
 *
 * @return The total number of milliseconds equivalent to the input time span or `null` if an
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
 * @return The total number of milliseconds equivalent to the input time span or `null` if an
 * exception occurs.
 */
@Stable
fun timeToMilliseconds(hours: Int, minutes: Int, seconds: Int): Long? {

    return Triple(first = hours, second = minutes, third = seconds).timeToMilliseconds()
}

/**
 * Converts an integer representing a time value into a two-digit string.
 *
 * @return A string representing the formatted time, always two digits long (e.g., "01", "10").
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