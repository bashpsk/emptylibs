package io.bashpsk.emptylibs.canvasslate.extension

import androidx.compose.ui.geometry.Offset

/**
 * Checks if this offset has neared another offset within a specified threshold.
 *
 * @param point The other offset to compare with.
 * @param threshold The maximum distance allowed for the offsets to be considered "neared".
 * Defaults to 24.0F.
 * @return `true` if the distance between this offset and the given point is less than or equal
 * to the threshold, `false` otherwise.
 */
internal fun Offset.hasNeared(point: Offset, threshold: Float = 24.0F): Boolean {

    return (this - point).getDistance() <= threshold
}