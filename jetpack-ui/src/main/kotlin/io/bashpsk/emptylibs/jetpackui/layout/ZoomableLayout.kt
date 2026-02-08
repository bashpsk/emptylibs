package io.bashpsk.emptylibs.jetpackui.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import kotlin.math.roundToInt

/**
 * A layout that zooms its content.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param zoomScale The scale factor to apply to the content.
 * @param content The content to be zoomed.
 */
@Composable
inline fun ZoomableLayout(
    modifier: Modifier = Modifier,
    zoomScale: Float = 1.0F,
    crossinline content: @Composable () -> Unit
) {

    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        val placeables = measurables.map { measurable ->

            measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }

        val width = placeables.maxOfOrNull { placeable -> placeable.width } ?: 0
        val height = placeables.maxOfOrNull { placeable -> placeable.height } ?: 0

        val layoutWidth = (width * zoomScale).roundToInt()
        val layoutHeight = (height * zoomScale).roundToInt()

        layout(width = layoutWidth, height = layoutHeight) {

            placeables.forEach { placeable ->

                val positionX = ((layoutWidth - placeable.width) / 2F).roundToInt()
                val positionY = ((layoutHeight - placeable.height) / 2F).roundToInt()

                placeable.placeRelativeWithLayer(x = positionX, y = positionY) {

                    scaleX = zoomScale
                    scaleY = zoomScale
                }
            }
        }
    }
}