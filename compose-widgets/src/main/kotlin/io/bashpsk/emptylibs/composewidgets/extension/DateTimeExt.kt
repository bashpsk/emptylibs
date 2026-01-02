package io.bashpsk.emptylibs.composewidgets.extension

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Converts an [Instant] to a [LocalDateTime] using the system's default time zone.
 *
 * This extension function simplifies the process of converting a specific point in universal time
 * to the local date and time representation based on the user's current system settings.
 *
 * @return A [LocalDateTime] object representing the date and time in the system's default time
 * zone.
 */
fun Instant.getSystemDateTime(): LocalDateTime {

    return toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
}

/**
 * Checks if the time represented by the [LocalDateTime] is in the AM (ante meridiem) period.
 *
 * The AM period is defined as the time from midnight (00:00) to just before noon (11:59).
 *
 * @return `true` if the hour is between 0 and 11 (inclusive), `false` otherwise.
 */
fun LocalDateTime.hasAM(): Boolean {

    return hour in 0..11
}

/**
 * Checks if the time represented by the [LocalDateTime] is in the PM (post meridiem) period.
 *
 * This function determines if the hour component of the [LocalDateTime] is 12 or greater,
 * which corresponds to the hours from 12:00 PM to 11:59 PM.
 *
 * @return `true` if the hour is 12 or more (PM), `false` otherwise (AM).
 */
fun LocalDateTime.hasPM(): Boolean {

    return hour in 12..23
}