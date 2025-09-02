package io.bashpsk.emptylibs.composeutils.size

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Immutable
@Parcelize
@Serializable
data class DpSizeData(val width: Float, val height: Float) : Parcelable {

    fun toDpSize(): DpSize = DpSize(width.dp, height.dp)
}

fun DpSize.toDpSizeData(): DpSizeData {

    return DpSizeData(width.value, height.value)
}

@Immutable
@Parcelize
@Serializable
data class SizeData(val width: Float, val height: Float) : Parcelable {

    fun toSize(): Size = Size(width, height)
}

fun Size.toSizeData(): SizeData {

    return SizeData(width, height)
}

@Immutable
@Parcelize
@Serializable
data class IntSizeData(val width: Int, val height: Int) : Parcelable {

    fun toIntSize(): IntSize = IntSize(width, height)
}

fun IntSize.toIntSizeData(): IntSizeData {

    return IntSizeData(width, height)
}