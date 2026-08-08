package io.bashpsk.emptylibs.composeutils.offset

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * A data class that represents an offset with x and y coordinates.
 * This class is immutable, parcelable, and serializable.
 * It can be used to store and transfer offset data.
 *
 * @property x The x-coordinate of the offset.
 * @property y The y-coordinate of the offset.
 */
@Immutable
@Parcelize
@Serializable
data class OffsetData(val x: Float, val y: Float) : Parcelable {

    /**
     * Converts this [OffsetData] to a [Offset].
     *
     * @return The [Offset] representation of this [OffsetData].
     */
    fun toOffset(): Offset = Offset(x = x, y = y)
}

/**
 * Converts an [Offset] to an [OffsetData].
 *
 * This function is useful when you need to serialize or store an [Offset] object.
 * [OffsetData] is a data class that can be easily serialized and deserialized.
 *
 * @return An [OffsetData] object with the same x and y values as the original [Offset].
 */
fun Offset.toOffsetData(): OffsetData {

    return OffsetData(x = x, y = y)
}

/**
 * Constrains the x and y components of this [Offset] to be at least the corresponding components
 * of [minimum].
 *
 * @param minimum The [Offset] representing the lower bounds for the x and y components.
 * @return A new [Offset] where each component is at least the value specified in [minimum].
 */
fun Offset.coerceAtLeast(minimum: Offset): Offset {

    return Offset(x = x.coerceAtLeast(minimum.x), y = y.coerceAtLeast(minimum.y))
}

/**
 * Constrains the x and y components of this [Offset] to be at most the corresponding components
 * of [maximum].
 *
 * @param maximum The [Offset] representing the upper bounds for the x and y components.
 * @return A new [Offset] where each component is at most the value specified in [maximum].
 */
fun Offset.coerceAtMost(maximum: Offset): Offset {

    return Offset(x = x.coerceAtMost(maximum.x), y = y.coerceAtMost(maximum.y))
}

/**
 * Constrains the x and y components of this [Offset] to be within the range defined by the
 * corresponding components of [minimum] and [maximum].
 *
 * This function returns a new [Offset] where the x-coordinate is clamped between `minimum.x` and
 * `maximum.x`, and the y-coordinate is clamped between `minimum.y` and `maximum.y`.
 *
 * @param minimum The [Offset] representing the lower bounds for the x and y components.
 * @param maximum The [Offset] representing the upper bounds for the x and y components.
 * @return A new [Offset] with its components coerced to the specified range.
 */
fun Offset.coerceIn(minimum: Offset, maximum: Offset): Offset {

    return Offset(
        x = x.coerceIn(minimum.x..maximum.x),
        y = y.coerceIn(minimum.y..maximum.y)
    )
}

/**
 * Checks if this offset has neared another offset within a specified threshold.
 *
 * @param point The other offset to compare with.
 * @param threshold The maximum distance allowed for the offsets to be considered "neared".
 * Defaults to 24.0F.
 * @return `true` if the distance between this offset and the given point is less than or equal
 * to the threshold, `false` otherwise.
 */
fun Offset.hasNeared(point: Offset, threshold: Float = 24.0F): Boolean {

    return (this - point).getDistance() <= threshold
}