package io.bashpsk.emptylibs.composeutils.offset

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Immutable
@Parcelize
@Serializable
data class DpOffsetData(val x: Float, val y: Float) : Parcelable {

    fun toDpOffset(): DpOffset = DpOffset(x.dp, y.dp)
}

fun DpOffset.toDpOffsetData(): DpOffsetData {

    return DpOffsetData(x.value, y.value)
}

@Immutable
@Parcelize
@Serializable
data class OffsetData(val x: Float, val y: Float) : Parcelable {

    fun toOffset(): Offset = Offset(x, y)
}

fun Offset.toOffsetData(): OffsetData {

    return OffsetData(x, y)
}

@Immutable
@Parcelize
@Serializable
data class IntOffsetData(val x: Int, val y: Int) : Parcelable {

    fun toIntOffset(): IntOffset = IntOffset(x, y)
}

fun IntOffset.toIntOffsetData(): IntOffsetData {

    return IntOffsetData(x, y)
}