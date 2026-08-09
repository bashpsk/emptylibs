package io.bashpsk.emptylibs.jetpackui.scrollbar

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * A reusable scrollbar for any [LazyScrollBarState].
 * This composable provides the base logic for rendering a scrollbar that interacts with a lazy
 * layout. It handles animations (fade in/out), drag gestures on the thumb, and positioning
 * based on the provided [state].
 *
 * @param modifier The [Modifier] to be applied to the scrollbar container.
 * @param state The [LazyScrollBarState] providing scroll metadata and control.
 * @param orientation The [Orientation] of the scrollbar (Vertical or Horizontal).
 * @param alignment The [Alignment] of the scrollbar within its parent `Box`.
 * @param thumbColor The [Color] applied to the background of the thumb and the label.
 * @param thumbNotchWidth The width of the notch on the scrollbar thumb.
 * @param label A composable lambda that defines the content of the label, receiving the scroll
 * state metadata.
 * @param thumb A composable lambda that defines the visual content inside the scrollbar thumb.
 */
@PublishedApi
@Composable
internal inline fun BoxWithConstraintsScope.LazyScrollBar(
    modifier: Modifier = Modifier,
    state: LazyScrollBarState,
    orientation: Orientation,
    alignment: Alignment = when (orientation) {

        Orientation.Vertical -> Alignment.TopEnd
        Orientation.Horizontal -> Alignment.BottomStart
    },
    thumbColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    thumbNotchWidth: Dp = 12.dp,
    crossinline label: @Composable (
        firstVisibleItemIndex: Int,
        visibleItemsCount: Int,
        totalItemsCount: Int
    ) -> Unit = { _, _, _ -> },
    crossinline thumb: @Composable BoxScope.() -> Unit = {

        DefaultScrollBarThumb()
    }
) {

    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val barVisibleState = remember { MutableTransitionState(false) }

    var barSize by rememberSaveable { mutableFloatStateOf(0F) }
    var barPosition by rememberSaveable { mutableFloatStateOf(0F) }
    var isBarDragging by rememberSaveable { mutableStateOf(false) }

    val maximumBarPosition by remember(constraints, orientation, barSize) {
        derivedStateOf {
            when (orientation) {

                Orientation.Vertical -> constraints.maxHeight - barSize
                Orientation.Horizontal -> constraints.maxWidth - barSize
            }.coerceAtLeast(0F)
        }
    }

    val barPositionOffset by remember(orientation, alignment, barPosition, thumbNotchWidth) {
        derivedStateOf {
            val notchOffset = with(density) { thumbNotchWidth.toPx().roundToInt() }

            when (orientation) {

                Orientation.Vertical -> when (alignment) {

                    Alignment.CenterStart, Alignment.TopStart, Alignment.BottomStart -> {
                        IntOffset(x = -notchOffset, y = barPosition.roundToInt())
                    }

                    else -> IntOffset(x = notchOffset, y = barPosition.roundToInt())
                }

                Orientation.Horizontal -> when (alignment) {

                    Alignment.TopCenter, Alignment.TopStart, Alignment.TopEnd -> {
                        IntOffset(x = barPosition.roundToInt(), y = -notchOffset)
                    }

                    else -> IntOffset(x = barPosition.roundToInt(), y = notchOffset)
                }
            }
        }
    }

    val barDraggableState = rememberDraggableState { delta ->

        if (maximumBarPosition > 0) {
            barPosition = (barPosition + delta).coerceIn(0F..maximumBarPosition)
            coroutineScope.launch { state.scrollToRatio(barPosition / maximumBarPosition) }
        }
    }

    val thumbShape = retain(orientation, alignment, thumbNotchWidth) {

        ThumbNotchShape(
            orientation = orientation,
            alignment = alignment,
            thumbNotchWidth = thumbNotchWidth
        )
    }

    LaunchedEffect(state.scrollRatio, maximumBarPosition) {

        if (!isBarDragging) barPosition = (state.scrollRatio * maximumBarPosition)
            .coerceIn(0F..maximumBarPosition)
    }

    LaunchedEffect(state.isScrollInProgress, isBarDragging) {

        snapshotFlow {

            state.isScrollInProgress || isBarDragging
        }.distinctUntilChanged().collectLatest { isVisible ->

            when (isVisible) {

                true -> delay(10.milliseconds)
                else -> delay(1.4.seconds)
            }

            barVisibleState.targetState = isVisible
        }
    }

    ScrollBarView(
        modifier = modifier,
        visibleState = barVisibleState,
        orientation = orientation,
        alignment = alignment,
        barPositionOffset = barPositionOffset,
        barDraggableState = barDraggableState,
        onSizeChanged = { size ->

            barSize = when (orientation) {

                Orientation.Vertical -> size.height.toFloat()
                Orientation.Horizontal -> size.width.toFloat()
            }
        },
        onDragStarted = { isBarDragging = true },
        onDragStopped = { isBarDragging = false },
        thumbShape = thumbShape,
        thumbColor = thumbColor,
        firstVisibleItemIndex = state.firstVisibleItemIndex,
        visibleItemsCount = state.visibleItemsCount,
        totalItemsCount = state.totalItemsCount,
        label = label,
        thumb = thumb
    )
}