package io.bashpsk.emptylibs.formatter.extension

import kotlinx.datetime.LocalDateTime

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