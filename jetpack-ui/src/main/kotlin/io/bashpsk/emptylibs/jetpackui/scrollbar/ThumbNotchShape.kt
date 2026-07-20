package io.bashpsk.emptylibs.jetpackui.scrollbar

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.AndroidPath
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.min

@Stable
@PublishedApi
internal class ThumbNotchShape(
    private val orientation: Orientation,
    private val alignment: Alignment,
    private val thumbNotchWidth: Dp
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val diameter = min(size.width, size.height)
        val notchPx = with(density) { thumbNotchWidth.toPx() }

        val circlePath = AndroidPath().apply {

            addOval(Rect(0F, 0F, diameter, diameter))
        }

        val notchRect = when (orientation) {

            Orientation.Vertical -> when (alignment) {

                Alignment.CenterStart, Alignment.TopStart, Alignment.BottomStart -> {
                    Rect(0F, 0F, notchPx, diameter)
                }

                else -> Rect(diameter - notchPx, 0F, diameter, diameter)
            }

            Orientation.Horizontal -> when (alignment) {

                Alignment.TopCenter, Alignment.TopStart, Alignment.TopEnd -> {
                    Rect(0F, 0F, diameter, notchPx)
                }

                else -> Rect(0F, diameter - notchPx, diameter, diameter)
            }
        }

        val notchPath = AndroidPath().apply {

            addRect(notchRect)
        }

        val notchedCirclePath = Path().apply {

            op(circlePath, notchPath, PathOperation.Difference)
        }

        return Outline.Generic(notchedCirclePath)
    }
}