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