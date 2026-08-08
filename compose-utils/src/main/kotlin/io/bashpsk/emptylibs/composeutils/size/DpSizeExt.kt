package io.bashpsk.emptylibs.composeutils.size

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.DpSize
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
    fun toDpSize(): DpSize = DpSize(width = width.dp, height = height.dp)
}

/**
 * Converts a [DpSize] to a [DpSizeData].
 *
 * @return The [DpSizeData] representation of this [DpSize].
 */
fun DpSize.toDpSizeData(): DpSizeData {

    return DpSizeData(width = width.value, height = height.value)
}

/**
 * Constrains the width and height components of this [DpSize] to be at least the corresponding
 * components of [minimum].
 *
 * @param minimum The [DpSize] representing the lower bounds for the width and height components.
 * @return A new [DpSize] where each component is at least the value specified in [minimum].
 */
fun DpSize.coerceAtLeast(minimum: DpSize): DpSize {

    return DpSize(
        width = width.coerceAtLeast(minimum.width),
        height = height.coerceAtLeast(minimum.height)
    )
}

/**
 * Constrains the width and height components of this [DpSize] to be at most the corresponding
 * components of [maximum].
 *
 * @param maximum The [DpSize] representing the upper bounds for the width and height components.
 * @return A new [DpSize] where each component is at most the value specified in [maximum].
 */
fun DpSize.coerceAtMost(maximum: DpSize): DpSize {

    return DpSize(
        width = width.coerceAtMost(maximum.width),
        height = height.coerceAtMost(maximum.height)
    )
}

/**
 * Constrains the width and height components of this [DpSize] to be within the range defined by the
 * corresponding components of [minimum] and [maximum].
 *
 * @param minimum The [DpSize] representing the lower bounds for the width and height components.
 * @param maximum The [DpSize] representing the upper bounds for the width and height components.
 * @return A new [DpSize] with its components coerced to the specified range.
 */
fun DpSize.coerceIn(minimum: DpSize, maximum: DpSize): DpSize {

    return DpSize(
        width = width.coerceIn(minimum.width..maximum.width),
        height = height.coerceIn(minimum.height..maximum.height)
    )
}