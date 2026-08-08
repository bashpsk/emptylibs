package io.bashpsk.emptylibs.composeutils.offset

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * A data class representing a 2D offset with x and y components, intended for use with Jetpack
 * Compose's `DpOffset`.
 * This class is designed to be immutable, parcelable, and serializable, making it suitable for
 * storing and transferring offset data.
 *
 * @property x The horizontal component of the offset, represented as a Float.
 * @property y The vertical component of the offset, represented as a Float.
 */
@Immutable
@Parcelize
@Serializable
data class DpOffsetData(val x: Float, val y: Float) : Parcelable {

    fun toDpOffset(): DpOffset = DpOffset(x.dp, y.dp)
}

/**
 * Converts a [DpOffset] to a [DpOffsetData] object.
 *
 * This function takes a [DpOffset] as input and returns a [DpOffsetData] object
 * with the same x and y values.
 *
 * @return A [DpOffsetData] object with the same x and y values as the input [DpOffset].
 */
fun DpOffset.toDpOffsetData(): DpOffsetData {

    return DpOffsetData(x.value, y.value)
}

/**
 * Constrains the x and y components of this [DpOffset] to be at least the corresponding components
 * of [minimum].
 *
 * @param minimum The [DpOffset] representing the lower bounds for the x and y components.
 * @return A new [DpOffset] where each component is at least the value specified in [minimum].
 */
fun DpOffset.coerceAtLeast(minimum: DpOffset): DpOffset {

    return DpOffset(x = x.coerceAtLeast(minimum.x), y = y.coerceAtLeast(minimum.y))
}

/**
 * Constrains the x and y components of this [DpOffset] to be at most the corresponding components
 * of [maximum].
 *
 * @param maximum The [DpOffset] representing the upper bounds for the x and y components.
 * @return A new [DpOffset] where each component is at most the value specified in [maximum].
 */
fun DpOffset.coerceAtMost(maximum: DpOffset): DpOffset {

    return DpOffset(x = x.coerceAtMost(maximum.x), y = y.coerceAtMost(maximum.y))
}

/**
 * Constrains the x and y components of this [DpOffset] to be within the range defined by the
 * corresponding components of [minimum] and [maximum].
 *
 * @param minimum The [DpOffset] representing the lower bounds for the x and y components.
 * @param maximum The [DpOffset] representing the upper bounds for the x and y components.
 * @return A new [DpOffset] with its components coerced to the specified range.
 */
fun DpOffset.coerceIn(minimum: DpOffset, maximum: DpOffset): DpOffset {

    return DpOffset(
        x = x.coerceIn(minimum.x..maximum.x),
        y = y.coerceIn(minimum.y..maximum.y)
    )
}