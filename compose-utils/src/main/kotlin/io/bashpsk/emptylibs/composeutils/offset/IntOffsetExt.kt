package io.bashpsk.emptylibs.composeutils.offset

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.IntOffset
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * A data class representing a 2D offset using integer coordinates.
 *
 * This class is designed to be immutable, parcelable, and serializable, making it suitable for
 * various use cases, including state management and data persistence.
 *
 * @property x The horizontal component of the offset.
 * @property y The vertical component of the offset.
 */
@Immutable
@Parcelize
@Serializable
data class IntOffsetData(val x: Int, val y: Int) : Parcelable {

    /**
     * Converts this [IntOffsetData] to an [IntOffset].
     *
     * This function creates a new [IntOffset] instance using the `x` and `y` values
     * from this [IntOffsetData].
     *
     * @return An [IntOffset] with the same x and y coordinates as this [IntOffsetData].
     */
    fun toIntOffset(): IntOffset = IntOffset(x = x, y = y)
}

/**
 * Converts an [IntOffset] to an [IntOffsetData] object.
 *
 * This function takes an [IntOffset] and creates a new [IntOffsetData]
 * instance with the same x and y coordinates. [IntOffsetData] is often
 * used for serialization or when a parcelable representation of an offset is needed.
 *
 * @return An [IntOffsetData] object with the x and y values from this [IntOffset].
 */
fun IntOffset.toIntOffsetData(): IntOffsetData {

    return IntOffsetData(x = x, y = y)
}

/**
 * Constrains the x and y components of this [IntOffset] to be at least the corresponding components
 * of [minimum].
 *
 * @param minimum The [IntOffset] representing the lower bounds for the x and y components.
 * @return A new [IntOffset] where each component is at least the value specified in [minimum].
 */
fun IntOffset.coerceAtLeast(minimum: IntOffset): IntOffset {

    return IntOffset(x = x.coerceAtLeast(minimum.x), y = y.coerceAtLeast(minimum.y))
}

/**
 * Constrains the x and y components of this [IntOffset] to be at most the corresponding components
 * of [maximum].
 *
 * @param maximum The [IntOffset] representing the upper bounds for the x and y components.
 * @return A new [IntOffset] where each component is at most the value specified in [maximum].
 */
fun IntOffset.coerceAtMost(maximum: IntOffset): IntOffset {

    return IntOffset(x = x.coerceAtMost(maximum.x), y = y.coerceAtMost(maximum.y))
}

/**
 * Constrains the x and y components of this [IntOffset] to be within the range defined by the
 * corresponding components of [minimum] and [maximum].
 *
 * @param minimum The [IntOffset] representing the lower bounds for the x and y components.
 * @param maximum The [IntOffset] representing the upper bounds for the x and y components.
 * @return A new [IntOffset] with its components coerced to the specified range.
 */
fun IntOffset.coerceIn(minimum: IntOffset, maximum: IntOffset): IntOffset {

    return IntOffset(
        x = x.coerceIn(minimum.x..maximum.x),
        y = y.coerceIn(minimum.y..maximum.y)
    )
}