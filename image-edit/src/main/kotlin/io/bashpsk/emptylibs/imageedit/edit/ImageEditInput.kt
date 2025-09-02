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
import io.bashpsk.emptylibs.imageutils.shape.ImageShape

internal sealed interface ImageEditInput {

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

    data class ImageItem(
        val bitmap: ImageBitmap? = null,
        val shape: ImageShape = ImageShape.None,
        val position: Offset = Offset.Unspecified,
        val size: Size = Size.Unspecified
    ) : ImageEditInput

    data class ShapeItem(
        val shape: ImageShape = ImageShape.None,
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

    data class TextItem(
        val content: String = "Text",
        val style: TextStyle = TextStyle.Default.copy(fontSize = 28.sp),
        val position: Offset = Offset.Unspecified,
        val size: Size = Size.Unspecified
    ) : ImageEditInput
}