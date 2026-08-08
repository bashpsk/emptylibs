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