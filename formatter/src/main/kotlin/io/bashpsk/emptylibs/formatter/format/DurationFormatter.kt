package io.bashpsk.emptylibs.formatter.format

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
@Stable
fun duration(durationValue: Long, unit: DurationUnit, pattern: DurationPattern): String {

    return durationValue.toDuration(unit).duration(pattern = pattern)
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
@Stable
fun Duration.duration(pattern: DurationPattern): String {

    val patternMap = when (pattern) {

        is DurationPattern.TimeLabel -> findDurationPattern(
            daysLabel = pattern.days,
            hoursLabel = pattern.hours,
            minutesLabel = pattern.minutes,
            secondsLabel = pattern.seconds
        )

        is DurationPattern.Separator -> findDurationPattern(
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

private fun Duration.findDurationPattern(
    daysLabel: String,
    hoursLabel: String,
    minutesLabel: String,
    secondsLabel: String?
): Pair<String, PersistentList<Any>> {

    return when {

        this == Duration.ZERO -> "%01d${secondsLabel ?: ""}" to persistentListOf(0)

        this < 1.toDuration(
            DurationUnit.MINUTES
        ) -> this.toComponents { seconds, nanoseconds ->

            "%02d${secondsLabel ?: ""}" to persistentListOf(seconds)
        }

        this < 1.toDuration(
            DurationUnit.HOURS
        ) -> this.toComponents { minutes, seconds, nanoseconds ->

            "%02d${minutesLabel}%02d${secondsLabel ?: ""}" to persistentListOf(minutes, seconds)
        }

        this < 1.toDuration(
            DurationUnit.DAYS
        ) -> this.toComponents { hours, minutes, seconds, nanoseconds ->

            "%02d${hoursLabel}%02d${minutesLabel}%02d${secondsLabel ?: ""}" to persistentListOf(
                hours,
                minutes,
                seconds
            )
        }

        else -> this.toComponents { days, hours, minutes, seconds, nanoseconds ->

            "%01d${daysLabel}%02d${hoursLabel}%02d${minutesLabel}%02d${
                secondsLabel ?: ""
            }" to persistentListOf(days, hours, minutes, seconds)
        }
    }
}