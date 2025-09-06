package io.bashpsk.emptylibs.composeutils.offset

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
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
    fun toOffset(): Offset = Offset(x, y)
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

    return OffsetData(x, y)
}

/**
 * A data class representing a 2D offset using integer coordinates.
 *
 * This class is designed to be immutable, parcelable, and serializable, making it suitable
 * for various use cases, including state management and data persistence.
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
    fun toIntOffset(): IntOffset = IntOffset(x, y)
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

    return IntOffsetData(x, y)
}