package io.bashpsk.emptylibs.canvasslate.slate

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.composeutils.offset.OffsetData
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Immutable
@Parcelize
@Serializable
data class CanvasSlatePath(
    val id: String = "",
    val color: Int = Color.Unspecified.toArgb(),
    val thickness: Float = 2.dp.value,
    val strokeCap: String = StrokeCap.Round.toString(),
    val strokeJoin: String = StrokeJoin.Round.toString(),
    val path: PersistentList<OffsetData> = persistentListOf()
) : Parcelable