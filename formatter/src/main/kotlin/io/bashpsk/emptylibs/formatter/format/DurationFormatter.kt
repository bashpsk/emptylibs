package io.bashpsk.emptylibs.formatter.format

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Formats a duration value into a human-readable string based on the specified [DurationPattern].
 *
 * This function automatically selects the most appropriate format depending on the length of
 * the duration and the type of [DurationPattern] provided.
 *
 * @param durationValue The numeric value of the duration.
 * @param unit The unit of time for the duration value
 * (e.g., [DurationUnit.SECONDS], [DurationUnit.MINUTES]).
 * @param pattern The formatting pattern to apply.
 * @return A human-readable string representation of the duration.
 */
@Stable
fun formattedDuration(durationValue: Long, unit: DurationUnit, pattern: DurationPattern): String {

    return durationValue.toDuration(unit).formattedDuration(pattern = pattern)
}

/**
 * Formats a [Duration] object into a human-readable string based on the specified [DurationPattern].
 *
 * This function automatically selects the most appropriate format depending on the length of
 * the [Duration] and the type of [DurationPattern] provided.
 *
 * Supported pattern types:
 *
 * 1. [DurationPattern.Separator]: Uses a character (e.g., `:`) to separate time components.
 *    - If the duration is less than 1 hour: formatted as `MM:SS`
 *    - If the duration is less than 1 day: formatted as `HH:MM:SS`
 *    - If the duration is 1 day or more: formatted as `D:HH:MM:SS`
 *
 * 2. [DurationPattern.TimeLabel]: Uses localized labels (e.g., "d", "h", "m", "s") to separate
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
 * ```kotlin
 * // returns "01:02:03"
 * 3723.toDuration(DurationUnit.SECONDS).formattedDuration(DurationPattern.Separator(':'))
 * ```
 *
 * @param pattern The formatting pattern to apply.
 * @return A human-readable string representation of the duration.
 */
@Stable
fun Duration.formattedDuration(pattern: DurationPattern): String {

    val patternMap = when (pattern) {

        is DurationPattern.TimeLabel -> findDurationPattern(
            daysLabel = pattern.days,
            hoursLabel = pattern.hours,
            minutesLabel = pattern.minutes,
            secondsLabel = pattern.seconds,
            isSecondsOnly = true
        )

        is DurationPattern.Separator -> findDurationPattern(
            daysLabel = pattern.char,
            hoursLabel = pattern.char,
            minutesLabel = pattern.char,
            secondsLabel = null,
            isSecondsOnly = false
        )
    }

    return patternMap.first.format(locale = Locale.getDefault(), *patternMap.second.toTypedArray())
}

/**
 * Determines the appropriate format string and its corresponding arguments based on the duration's
 * magnitude.
 *
 * This private helper function selects a pattern template (e.g., `%02d:%02d`) and gathers the
 */
private fun Duration.findDurationPattern(
    daysLabel: String,
    hoursLabel: String,
    minutesLabel: String,
    secondsLabel: String?,
    isSecondsOnly: Boolean
): Pair<String, PersistentList<Any>> {

    return when {

        this == Duration.ZERO -> "%01d${secondsLabel ?: ""}" to persistentListOf(0)

        this < 1.toDuration(
            DurationUnit.MINUTES
        ) && isSecondsOnly -> this.toComponents { seconds, _ ->

            "%02d${secondsLabel ?: ""}" to persistentListOf(seconds)
        }

        this < 1.toDuration(
            DurationUnit.HOURS
        ) -> this.toComponents { minutes, seconds, _ ->

            "%02d${minutesLabel}%02d${secondsLabel ?: ""}" to persistentListOf(minutes, seconds)
        }

        this < 1.toDuration(
            DurationUnit.DAYS
        ) -> this.toComponents { hours, minutes, seconds, _ ->

            "%02d${hoursLabel}%02d${minutesLabel}%02d${secondsLabel ?: ""}" to persistentListOf(
                hours,
                minutes,
                seconds
            )
        }

        else -> this.toComponents { days, hours, minutes, seconds, _ ->

            "%01d${daysLabel}%02d${hoursLabel}%02d${minutesLabel}%02d${
                secondsLabel ?: ""
            }" to persistentListOf(days, hours, minutes, seconds)
        }
    }
}