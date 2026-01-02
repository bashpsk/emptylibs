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
internal fun Instant.getSystemDateTime(): LocalDateTime {

    return toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
}