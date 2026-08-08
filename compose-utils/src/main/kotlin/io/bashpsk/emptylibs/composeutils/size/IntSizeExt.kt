package io.bashpsk.emptylibs.composeutils.size

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.IntSize
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

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
    fun toIntSize(): IntSize = IntSize(width = width, height = height)
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

/**
 * Constrains the width and height components of this [IntSize] to be at least the corresponding
 * components of [minimum].
 *
 * @param minimum The [IntSize] representing the lower bounds for the width and height components.
 * @return A new [IntSize] where each component is at least the value specified in [minimum].
 */
fun IntSize.coerceAtLeast(minimum: IntSize): IntSize {

    return IntSize(
        width = width.coerceAtLeast(minimum.width),
        height = height.coerceAtLeast(minimum.height)
    )
}

/**
 * Constrains the width and height components of this [IntSize] to be at most the corresponding
 * components of [maximum].
 *
 * @param maximum The [IntSize] representing the upper bounds for the width and height components.
 * @return A new [IntSize] where each component is at most the value specified in [maximum].
 */
fun IntSize.coerceAtMost(maximum: IntSize): IntSize {

    return IntSize(
        width = width.coerceAtMost(maximum.width),
        height = height.coerceAtMost(maximum.height)
    )
}

/**
 * Constrains the width and height components of this [IntSize] to be within the range defined by
 * the corresponding components of [minimum] and [maximum].
 *
 * @param minimum The [IntSize] representing the lower bounds for the width and height components.
 * @param maximum The [IntSize] representing the upper bounds for the width and height components.
 * @return A new [IntSize] with its components coerced to the specified range.
 */
fun IntSize.coerceIn(minimum: IntSize, maximum: IntSize): IntSize {

    return IntSize(
        width = width.coerceIn(minimum.width..maximum.width),
        height = height.coerceIn(minimum.height..maximum.height)
    )
}