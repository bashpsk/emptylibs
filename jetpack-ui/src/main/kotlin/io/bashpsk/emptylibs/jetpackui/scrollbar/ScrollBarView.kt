package io.bashpsk.emptylibs.jetpackui.scrollbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * Internal composable that renders the visual representation of a scrollbar, including the thumb
 * and an optional label. It handles animations, positioning, and drag interactions for both
 * vertical and horizontal orientations.
 *
 * @param modifier The [Modifier] to be applied to the scrollbar container.
 * @param visibleState A [MutableTransitionState] controlling the entry and exit animations of the
 * scrollbar.
 * @param orientation The [Orientation] of the scrollbar (Vertical or Horizontal).
 * @param alignment The [Alignment] of the scrollbar within its parent [Box].
 * @param barPositionOffset The [IntOffset] representing the current visual position of the
 * scrollbar thumb.
 * @param barDraggableState The [DraggableState] used to handle drag gestures on the thumb.
 * @param onSizeChanged Callback invoked when the size of the scrollbar layout changes.
 * @param onDragStarted Callback invoked when the user starts dragging the scrollbar thumb.
 * @param onDragStopped Callback invoked when the user stops dragging the scrollbar thumb.
 * @param thumbShape The [Shape] of the scrollbar thumb.
 * @param thumbColor The [Color] applied to the background of the thumb and the label.
 * @param firstVisibleItemIndex The index of the first currently visible item.
 * @param visibleItemsCount The number of items currently visible in the viewport.
 * @param totalItemsCount The total number of items in the list.
 * @param label A composable lambda that defines the content of the label, receiving the scroll
 * state metadata.
 * @param thumb A composable lambda that defines the content inside the scrollbar thumb.
 */
@PublishedApi
@Composable
internal inline fun BoxWithConstraintsScope.ScrollBarView(
    modifier: Modifier = Modifier,
    visibleState: MutableTransitionState<Boolean>,
    orientation: Orientation,
    alignment: Alignment,
    barPositionOffset: IntOffset,
    barDraggableState: DraggableState,
    noinline onSizeChanged: (IntSize) -> Unit,
    crossinline onDragStarted: () -> Unit,
    crossinline onDragStopped: () -> Unit,
    thumbShape: Shape,
    thumbColor: Color,
    firstVisibleItemIndex: Int,
    visibleItemsCount: Int,
    totalItemsCount: Int,
    crossinline label: @Composable (
        firstVisibleItemIndex: Int,
        visibleItemsCount: Int,
        totalItemsCount: Int
    ) -> Unit,
    crossinline thumb:@Composable BoxScope.() -> Unit
) {

    AnimatedVisibility(
        modifier = Modifier.align(alignment = alignment),
        visibleState = visibleState,
        enter = when (orientation) {

            Orientation.Vertical -> when (alignment) {

                Alignment.CenterStart, Alignment.TopStart, Alignment.BottomStart -> {
                    slideInHorizontally { -it }
                }

                else -> slideInHorizontally { it }
            }

            Orientation.Horizontal -> when (alignment) {

                Alignment.TopCenter, Alignment.TopStart, Alignment.TopEnd -> {
                    slideInVertically { -it }
                }

                else -> slideInVertically { it }
            }
        },
        exit = when (orientation) {

            Orientation.Vertical -> when (alignment) {

                Alignment.CenterStart, Alignment.TopStart, Alignment.BottomStart -> {
                    slideOutHorizontally { -it }
                }

                else -> slideOutHorizontally { it }
            }

            Orientation.Horizontal -> when (alignment) {

                Alignment.TopCenter, Alignment.TopStart, Alignment.TopEnd -> {
                    slideOutVertically { -it }
                }

                else -> slideOutVertically { it }
            }
        }
    ) {

        val barModifier = modifier
            .offset { barPositionOffset }
            .onSizeChanged(onSizeChanged)

        val thumbContent = @Composable {

            Box(
                modifier = Modifier
                    .clip(shape = thumbShape)
                    .background(color = thumbColor, shape = thumbShape)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .draggable(
                        state = barDraggableState,
                        orientation = orientation,
                        onDragStarted = { onDragStarted() },
                        onDragStopped = { onDragStopped() }
                    ),
                contentAlignment = Alignment.Center,
                content = thumb
            )
        }

        val labelContent = @Composable {

            Box(
                modifier = Modifier
                    .background(color = thumbColor, shape = CircleShape)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {

                label(firstVisibleItemIndex, visibleItemsCount, totalItemsCount)
            }
        }

        when (orientation) {

            Orientation.Vertical -> Row(
                modifier = barModifier,
                horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                when (alignment) {

                    Alignment.CenterStart, Alignment.TopStart, Alignment.BottomStart -> {

                        thumbContent()
                        labelContent()
                    }

                    else -> {

                        labelContent()
                        thumbContent()
                    }
                }
            }

            Orientation.Horizontal -> Column(
                modifier = barModifier,
                verticalArrangement = Arrangement.spacedBy(space = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                when (alignment) {

                    Alignment.TopCenter, Alignment.TopStart, Alignment.TopEnd -> {

                        thumbContent()
                        labelContent()
                    }

                    else -> {

                        labelContent()
                        thumbContent()
                    }
                }
            }
        }
    }
}