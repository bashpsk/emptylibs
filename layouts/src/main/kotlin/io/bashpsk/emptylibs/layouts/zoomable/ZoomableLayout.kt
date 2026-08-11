package io.bashpsk.emptylibs.layouts.zoomable

import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toSize
import io.bashpsk.emptylibs.gestureui.transform.TransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.rememberTransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.transformableGestures
import kotlin.math.roundToInt

@Composable
inline fun ZoomableLayout(
    modifier: Modifier = Modifier,
    state: TransformableGesturesState = rememberTransformableGesturesState(),
    crossinline content: @Composable ZoomableLayoutScope.() -> Unit
) {

    val layoutPosition by remember(state.position) { derivedStateOf { state.position.round() } }

    ZoomableLayout(
        modifier = modifier
            .transformableGestures(state = state)
            .offset { layoutPosition },
        zoomScale = state.zoom,
        content = content
    )
}

/**
 * A layout that zooms its content.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param zoomScale The scale factor to apply to the content.
 * @param content The content to be zoomed, with [ZoomableLayoutScope].
 */
@Composable
inline fun ZoomableLayout(
    modifier: Modifier = Modifier,
    zoomScale: Float = 1.0F,
    crossinline content: @Composable ZoomableLayoutScope.() -> Unit
) {

    val scope = retain { ZoomableLayoutScopeImpl() }

    Layout(
        modifier = modifier.onGloballyPositioned { coordinates ->

            val rootCoordinates = coordinates.findRootCoordinates()
            val rootRect = Rect(offset = Offset.Zero, size = rootCoordinates.size.toSize())
            val visibleInRoot = coordinates.boundsInRoot().intersect(rootRect)

            val localViewport = when (visibleInRoot.isEmpty) {

                true -> Rect.Zero

                false -> Rect(
                    topLeft = coordinates.localPositionOf(
                        sourceCoordinates = rootCoordinates,
                        relativeToSource = visibleInRoot.topLeft
                    ),
                    bottomRight = coordinates.localPositionOf(
                        sourceCoordinates = rootCoordinates,
                        relativeToSource = visibleInRoot.bottomRight
                    )
                )
            }

            scope.viewport = Rect(
                left = localViewport.left / zoomScale,
                top = localViewport.top / zoomScale,
                right = localViewport.right / zoomScale,
                bottom = localViewport.bottom / zoomScale
            )
        },
        content = { scope.content() }
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