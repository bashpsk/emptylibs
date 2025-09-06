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

/**
 * Represents a single path drawn on the canvas.
 *
 * This data class is immutable, parcelable, and serializable, making it suitable for
 * state management, saving, and sharing.
 *
 * @property id A unique identifier for the path. Defaults to an empty string.
 * @property color The color of the path, represented as an ARGB integer.
 * Defaults to `Color.Unspecified`.
 * @property thickness The thickness of the path's stroke in density-independent pixels (dp).
 * Defaults to `2.dp`.
 * @property strokeCap The style of the stroke's caps.
 * Defaults to `StrokeCap.Round`. Stored as a String.
 * @property strokeJoin The style of the stroke's joins.
 * Defaults to `StrokeJoin.Round`. Stored as a String.
 * @property path A persistent list of [OffsetData] points that define the geometry of the path.
 * Defaults to an empty list.
 */
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