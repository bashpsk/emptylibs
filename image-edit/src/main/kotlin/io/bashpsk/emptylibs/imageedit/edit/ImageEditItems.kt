package io.bashpsk.emptylibs.imageedit.edit

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.text.TextStyle
import io.bashpsk.emptylibs.imageutils.shape.ImageShape
import kotlinx.collections.immutable.PersistentList

/**
 * Represents different types of items that can be added or manipulated in an image editor.
 * Each item has a unique identifier [uuid].
 *
 * @property uuid A unique identifier for the image edit item.
 */
sealed class ImageEditItems(var uuid: String = "") {

    /**
     * Represents a brush stroke item in the image editor.
     *
     * @property color The color of the brush stroke.
     * @property style The drawing style of the brush stroke (e.g., fill, stroke).
     * @property smoothness The smoothness level of the brush stroke. Higher values result in
     * smoother curves.
     * @property path A persistent list of [Offset] points representing the path of the brush
     * stroke.
     */
    data class BrushItem(
        val color: Color,
        val style: DrawStyle,
        val smoothness: Int,
        val path: PersistentList<Offset>
    ) : ImageEditItems()

    /**
     * Represents an erase action on the image.
     *
     * @property style The style of the erase stroke (e.g., width, cap).
     * @property smoothness The smoothness level of the erase path.
     * @property path A list of [Offset] points defining the erase path.
     */
    data class EraseItem(
        val style: DrawStyle,
        val smoothness: Int,
        val path: PersistentList<Offset>
    ) : ImageEditItems()

    /**
     * Represents an image item to be drawn on the canvas.
     *
     * @property bitmap The [ImageBitmap] to be drawn. Can be null if no image is selected.
     * @property shape The [ImageShape] defining the clipping mask for the image.
     * @property position The [Offset] representing the top-left corner of the image on the canvas.
     * @property size The [Size] of the image on the canvas.
     */
    data class ImageItem(
        val bitmap: ImageBitmap?,
        val shape: ImageShape,
        val position: Offset,
        val size: Size
    ) : ImageEditItems()

    /**
     * Represents a shape item to be drawn on the image.
     *
     * @property shape The shape of the item (e.g., rectangle, circle).
     * @property color The color of the shape.
     * @property style The drawing style for the shape (e.g., fill, stroke).
     * @property position The offset (x, y) coordinates of the top-left corner of the shape.
     * @property size The size (width, height) of the shape.
     */
    data class ShapeItem(
        val shape: ImageShape,
        val color: Color,
        val style: DrawStyle,
        val position: Offset,
        val size: Size
    ) : ImageEditItems()

    /**
     * Represents a text item that can be added to an image.
     *
     * @property content The text content of the item.
     * @property style The [TextStyle] to be applied to the text.
     * @property position The [Offset] representing the top-left position of the text item.
     * @property size The [Size] of the text item.
     */
    data class TextItem(
        val content: String,
        val style: TextStyle,
        val position: Offset,
        val size: Size
    ) : ImageEditItems()
}