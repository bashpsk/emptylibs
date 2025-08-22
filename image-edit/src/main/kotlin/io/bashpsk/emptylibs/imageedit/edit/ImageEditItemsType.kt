package io.bashpsk.emptylibs.imageedit.edit

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ImageEditBitmap(
    val bitmap: ImageBitmap? = null,
)

@Immutable
data class ImageEditPath(
    val id: String = "",
    val color: Color = Color.Unspecified,
    val thickness: Dp = 2.dp,
    val strokeCap: StrokeCap = StrokeCap.Round,
    val strokeJoin: StrokeJoin = StrokeJoin.Round,
    val path: PersistentList<Offset> = persistentListOf()
)

@Immutable
data class ImageEditShape(
    val shape: Path = Path(),
)

@Immutable
data class ImageEditText(
    val content: String = "",
    val color: Color = Color.Unspecified,
    val style: TextStyle = TextStyle.Default
)