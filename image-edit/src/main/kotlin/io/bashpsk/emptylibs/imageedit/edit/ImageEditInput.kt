package io.bashpsk.emptylibs.imageedit.edit

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import io.bashpsk.emptylibs.composeutils.shape.PathShape

/**
 * Sealed interface representing different types of inputs for image editing.
 * This interface is used to define the various elements that can be added or manipulated
 * on an image, such as brush strokes, erased areas, images, shapes, and text.
 *
 * Each implementing data class represents a specific type of edit operation or element.
 */
internal sealed interface ImageEditInput {

    /**
     * Represents a brush stroke to be drawn on the image.
     *
     * @property color The color of the brush stroke. Defaults to [Color.White].
     * @property thickness The thickness of the brush stroke in pixels.
     * Defaults to `12.0F`.
     * @property miter The miter limit for the stroke. Defaults to `4.0F`.
     * Used when [strokeJoin] is [StrokeJoin.Miter].
     * @property strokeCap The cap style for the start and end of the brush stroke.
     * Defaults to [StrokeCap.Round].
     * @property strokeJoin The join style for the segments of the brush stroke.
     * Defaults to [StrokeJoin.Round].
     * @property dashIntervalOff The length of the space in a dashed line.
     * Defaults to `0.0F` (solid line).
     * @property dashIntervalOn The length of the dash in a dashed line.
     * Defaults to `0.0F` (solid line).
     * @property dashPhase The offset into the dash pattern. Defaults to `0.0F`.
     * @property smoothness The level of smoothness to apply to the brush stroke.
     * Higher values result in smoother curves. Defaults to `2`.
     */
    data class BrushItem(
        val color: Color = Color.White,
        val thickness: Float = 12.0F,
        val miter: Float = 4.0F,
        val strokeCap: StrokeCap = StrokeCap.Round,
        val strokeJoin: StrokeJoin = StrokeJoin.Round,
        val dashIntervalOff: Float = 0.0F,
        val dashIntervalOn: Float = 0.0F,
        val dashPhase: Float = 0.0F,
        val smoothness: Int = 2
    ) : ImageEditInput

    /**
     * Represents an erase operation in image editing.
     *
     * @property thickness The thickness of the eraser stroke. Default is 12.0F.
     * @property strokeCap The style of the caps at the start and end of stroked lines.
     * Default is [StrokeCap.Round].
     * @property strokeJoin The join style for connections between segments of a stroked path.
     * Default is [StrokeJoin.Round].
     * @property miter The limit for miters to be drawn on segments of a stroked path.
     * Default is 4.0F.
     * @property dashIntervalOff The length of the transparent section of a dashed line.
     * Default is 0.0F (solid line).
     * @property dashIntervalOn The length of the opaque section of a dashed line.
     * Default is 0.0F (solid line).
     * @property dashPhase The offset into the dash pattern at which to start drawing.
     * Default is 0.0F.
     * @property smoothness The degree of smoothing applied to the erased path. Default is 2.
     */
    data class EraseItem(
        val thickness: Float = 12.0F,
        val strokeCap: StrokeCap = StrokeCap.Round,
        val strokeJoin: StrokeJoin = StrokeJoin.Round,
        val miter: Float = 4.0F,
        val dashIntervalOff: Float = 0.0F,
        val dashIntervalOn: Float = 0.0F,
        val dashPhase: Float = 0.0F,
        val smoothness: Int = 2
    ) : ImageEditInput

    /**
     * Represents an image to be drawn on the canvas.
     *
     * @param bitmap The [ImageBitmap] to draw. If null, no image will be drawn.
     * @param shape The [PathShape] to apply to the image. Defaults to [PathShape.None].
     * @param position The [Offset] where the top-left corner of the image will be drawn.
     * Defaults to [Offset.Unspecified].
     * @param size The [Size] of the image to be drawn. Defaults to [Size.Unspecified],
     * which means the original size of the bitmap will be used.
     */
    data class ImageItem(
        val bitmap: ImageBitmap? = null,
        val shape: PathShape = PathShape.None,
        val position: Offset = Offset.Unspecified,
        val size: Size = Size.Unspecified
    ) : ImageEditInput

    /**
     * Represents a shape to be drawn on the image.
     *
     * @param shape The type of shape to draw (e.g., Rectangle, Oval).
     * Defaults to [PathShape.None].
     * @param color The color of the shape. Defaults to [Color.Green].
     * @param style The drawing style for the shape (e.g., Fill, Stroke). Defaults to [Fill].
     * @param thickness The stroke thickness if the style is Stroke. Defaults to `2.0F`.
     * @param strokeCap The cap style for the stroke. Defaults to [StrokeCap.Round].
     * @param strokeJoin The join style for the stroke. Defaults to [StrokeJoin.Round].
     * @param miter The miter limit for the stroke. Defaults to `4.0F`.
     * @param dashIntervalOff The length of the off interval for a dashed stroke.
     * Defaults to `0.0F`.
     * @param dashIntervalOn The length of the on interval for a dashed stroke. Defaults to `0.0F`.
     * @param dashPhase The phase of the dash pattern. Defaults to `0.0F`.
     * @param position The top-left offset of the shape. Defaults to [Offset.Unspecified].
     * @param size The size of the shape. Defaults to [Size.Unspecified].
     */
    data class ShapeItem(
        val shape: PathShape = PathShape.None,
        val color: Color = Color.Green,
        val style: DrawStyle = Fill,
        val thickness: Float = 2.0F,
        val strokeCap: StrokeCap = StrokeCap.Round,
        val strokeJoin: StrokeJoin = StrokeJoin.Round,
        val miter: Float = 4.0F,
        val dashIntervalOff: Float = 0.0F,
        val dashIntervalOn: Float = 0.0F,
        val dashPhase: Float = 0.0F,
        val position: Offset = Offset.Unspecified,
        val size: Size = Size.Unspecified
    ) : ImageEditInput

    /**
     * Represents a text item to be drawn on an image.
     *
     * @property content The text content to be displayed. Defaults to "Text".
     * @property style The [TextStyle] to be applied to the text.
     * Defaults to a copy of [TextStyle.Default] with a font size of 28.sp.
     * @property position The [Offset] of the text item on the image.
     * Defaults to [Offset.Unspecified].
     * @property size The [Size] of the text item. Defaults to [Size.Unspecified].
     */
    data class TextItem(
        val content: String = "Text",
        val style: TextStyle = TextStyle.Default.copy(fontSize = 28.sp),
        val position: Offset = Offset.Unspecified,
        val size: Size = Size.Unspecified
    ) : ImageEditInput
}