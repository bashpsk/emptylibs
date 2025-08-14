package io.bashpsk.emptylibs.canvasslate.slate

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PathData(
    val id: String = "",
    val color: Color = Color.Unspecified,
    val thickness: Dp = 2.dp,
    val path: PersistentList<Offset> = persistentListOf()
)