package io.bashpsk.emptylibs.imageedit.edit

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import kotlinx.collections.immutable.PersistentList

sealed class ImageEditItems(var uuid: String = "") {

    data class EraseItem(
        val id: String,
        val thickness: Dp,
        val strokeCap: StrokeCap,
        val strokeJoin: StrokeJoin,
        val path: PersistentList<Offset>
    ) : ImageEditItems()

    data class ImageItem(
        val bitmap: ImageBitmap,
        val position: Offset,
        val size: Size
    ) : ImageEditItems()

    data class PathItem(
        val id: String,
        val color: Color,
        val thickness: Dp,
        val strokeCap: StrokeCap,
        val strokeJoin: StrokeJoin,
        val path: PersistentList<Offset>
    ) : ImageEditItems()

    data class ShapeItem(
        val shape: Path
    ) : ImageEditItems()

    data class TextItem(
        val content: String,
        val color: Color,
        val style: TextStyle,
    ) : ImageEditItems()
}