package io.bashpsk.emptylibs.composeutils.size

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * A data class that represents a size in density-independent pixels (dp).
 * This class is designed to be immutable, parcelable, and serializable, making it suitable
 * for use in Compose UI and for passing data between components or across processes.
 *
 * @property width The width of the size in dp.
 * @property height The height of the size in dp.
 */
@Immutable
@Parcelize
@Serializable
data class DpSizeData(val width: Float, val height: Float) : Parcelable {

    /**
     * Converts this DpSizeData to a DpSize.
     *
     * @return The DpSize equivalent of this DpSizeData.
     */
    fun toDpSize(): DpSize = DpSize(width.dp, height.dp)
}

/**
 * Converts a [DpSize] to a [DpSizeData].
 *
 * @return The [DpSizeData] representation of this [DpSize].
 */
fun DpSize.toDpSizeData(): DpSizeData {

    return DpSizeData(width.value, height.value)
}

/**
 * A data class representing size with width and height as Float values.
 * It is parcelable and serializable.
 *
 * @property width The width of the size.
 * @property height The height of the size.
 */
@Immutable
@Parcelize
@Serializable
data class SizeData(val width: Float, val height: Float) : Parcelable {

    /**
     * Converts this [SizeData] object to a [Size] object.
     *
     * @return A [Size] object with the same width and height as this [SizeData].
     */
    fun toSize(): Size = Size(width, height)
}

/**
 * Converts a [Size] object to a [SizeData] object.
 *
 * This function is useful when you need to serialize or store size information
 * in a format that is independent of the Compose UI framework.
 *
 * @return A [SizeData] object with the same width and height as the original [Size].
 */
fun Size.toSizeData(): SizeData {

    return SizeData(width, height)
}

/**
 * A data class representing a size with integer dimensions (width and height).
 *
 * This class serves as a plain data holder, facilitating the serialization and parcelization of
 * size information.
 * It provides a convenient way to convert between [IntSize] and this serializable/parcelable
 * representation.
 *
 * @property width The width component of the size, as an [Int].
 * @property height The height component of the size, as an [Int].
 */
@Immutable
@Parcelize
@Serializable
data class IntSizeData(val width: Int, val height: Int) : Parcelable {

    /**
     * Converts this [IntSizeData] to an [IntSize].
     *
     * @return The corresponding [IntSize] with the same width and height.
     */
    fun toIntSize(): IntSize = IntSize(width, height)
}

/**
 * Converts an [IntSize] to an [IntSizeData].
 *
 * This function facilitates the serialization or transmission of [IntSize] objects
 * by converting them to [IntSizeData], which is both [Parcelable] and [Serializable].
 *
 * @receiver The [IntSize] to be converted.
 * @return The [IntSizeData] representation of this [IntSize].
 */
fun IntSize.toIntSizeData(): IntSizeData {

    return IntSizeData(width, height)
}