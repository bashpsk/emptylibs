package io.bashpsk.emptylibs.imageedit.edit

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.text.TextStyle
import io.bashpsk.emptylibs.imageutils.shape.ImageShape
import kotlinx.collections.immutable.PersistentList

sealed class ImageEditItems(var uuid: String = "") {

    data class BrushItem(
        val color: Color,
        val style: DrawStyle,
        val path: PersistentList<Offset>
    ) : ImageEditItems()

    data class EraseItem(
        val style: DrawStyle,
        val path: PersistentList<Offset>
    ) : ImageEditItems()

    data class ImageItem(
        val bitmap: ImageBitmap,
        val position: Offset,
        val size: Size
    ) : ImageEditItems()

    data class ShapeItem(
        val shape: ImageShape,
        val color: Color,
        val style: DrawStyle,
        val position: Offset,
        val size: Size
    ) : ImageEditItems()

    data class TextItem(
        val content: String,
        val style: TextStyle,
        val position: Offset,
        val size: Size
    ) : ImageEditItems()
}