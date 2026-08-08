package io.bashpsk.emptylibs.composeutils.size

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

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
    fun toSize(): Size = Size(width = width, height = height)
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

    return SizeData(width = width, height = height)
}

/**
 * Constrains the width and height components of this [Size] to be at least the corresponding
 * components of [minimum].
 *
 * @param minimum The [Size] representing the lower bounds for the width and height components.
 * @return A new [Size] where each component is at least the value specified in [minimum].
 */
fun Size.coerceAtLeast(minimum: Size): Size {

    return Size(
        width = width.coerceAtLeast(minimum.width),
        height = height.coerceAtLeast(minimum.height)
    )
}

/**
 * Constrains the width and height components of this [Size] to be at most the corresponding
 * components of [maximum].
 *
 * @param maximum The [Size] representing the upper bounds for the width and height components.
 * @return A new [Size] where each component is at most the value specified in [maximum].
 */
fun Size.coerceAtMost(maximum: Size): Size {

    return Size(
        width = width.coerceAtMost(maximum.width),
        height = height.coerceAtMost(maximum.height)
    )
}

/**
 * Constrains the width and height components of this [Size] to be within the range defined by the
 * corresponding components of [minimum] and [maximum].
 *
 * @param minimum The [Size] representing the lower bounds for the width and height components.
 * @param maximum The [Size] representing the upper bounds for the width and height components.
 * @return A new [Size] with its components coerced to the specified range.
 */
fun Size.coerceIn(minimum: Size, maximum: Size): Size {

    return Size(
        width = width.coerceIn(minimum.width..maximum.width),
        height = height.coerceIn(minimum.height..maximum.height)
    )
}