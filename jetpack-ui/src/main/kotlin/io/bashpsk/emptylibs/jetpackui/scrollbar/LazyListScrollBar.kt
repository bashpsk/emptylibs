package io.bashpsk.emptylibs.jetpackui.scrollbar

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * A scrollbar for [LazyListState] that appears when scrolling and fades out after a short delay.
 *
 * @param modifier The modifier to be applied to the scrollbar.
 * @param state The [LazyListState] to attach the scrollbar to.
 * @param orientation The orientation of the scrollbar.
 * Defaults to the orientation of the [LazyListState].
 * @param alignment The alignment of the scrollbar.
 * Defaults to [Alignment.TopEnd] for vertical lists and [Alignment.BottomStart] for horizontal
 * lists.
 * @param thumbColor The color of the scrollbar thumb.
 * @param thumbNotchWidth The width of the notch on the scrollbar thumb.
 * @param label A composable that displays the current item index.
 * @param thumb The composable to be used as the scrollbar thumb.
 */
@OptIn(FlowPreview::class)
@Composable
inline fun BoxWithConstraintsScope.LazyListScrollBar(
    modifier: Modifier = Modifier,
    state: LazyListState,
    orientation: Orientation = state.layoutInfo.orientation,
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

        Icon(
            modifier = Modifier.size(size = 28.dp),
            imageVector = Icons.Filled.DragIndicator,
            contentDescription = "Drag Indicator"
        )
    }
) {

    val density = LocalDensity.current
    val listCoroutineScope = rememberCoroutineScope()
    val barVisibleState = remember { MutableTransitionState(false) }

    val firstVisibleItemIndex by remember { derivedStateOf { state.firstVisibleItemIndex } }
    val totalItemsCount by remember { derivedStateOf { state.layoutInfo.totalItemsCount } }
    val visibleItemsInfo by remember { derivedStateOf { state.layoutInfo.visibleItemsInfo } }
    val viewportStartOffset by remember { derivedStateOf { state.layoutInfo.viewportStartOffset } }
    val viewportEndOffset by remember { derivedStateOf { state.layoutInfo.viewportEndOffset } }
    val firstItemScrollOffset by remember { derivedStateOf { state.firstVisibleItemScrollOffset } }
    val visibleItemsCount by remember(visibleItemsInfo) { derivedStateOf { visibleItemsInfo.size } }

    val scrollRatio by remember(
        totalItemsCount,
        viewportStartOffset,
        viewportEndOffset,
        visibleItemsInfo
    ) {
        derivedStateOf {
            when {

                visibleItemsInfo.isEmpty() || totalItemsCount == 0 -> 0F

                else -> visibleItemsInfo.firstOrNull()?.let { firstVisibleItem ->

                    val totalItemsSize = totalItemsCount * firstVisibleItem.size
                    val viewportSize = viewportEndOffset - viewportStartOffset

                    (totalItemsSize - viewportSize).coerceAtLeast(0).takeIf { distance ->

                        distance > 0F
                    }?.let { distance ->

                        (((firstVisibleItem.index * firstVisibleItem.size)
                                + firstItemScrollOffset).toFloat() / distance).coerceIn(0F..1F)
                    } ?: 0F
                } ?: 0F
            }
        }
    }

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

            listCoroutineScope.launch {

                val itemSize = visibleItemsInfo.firstOrNull()?.size ?: 1
                val totalItemsSize = totalItemsCount * itemSize
                val viewportSize = viewportEndOffset - viewportStartOffset
                val scrollableDistance = (totalItemsSize - viewportSize).coerceAtLeast(0)
                val scrollPosition = scrollableDistance * (barPosition / maximumBarPosition)
                val targetItem = (scrollPosition / itemSize).toInt().coerceIn(
                    0 until totalItemsCount.coerceAtLeast(1)
                )
                val targetOffset = (scrollPosition % itemSize).toInt()

                state.scrollToItem(index = targetItem, scrollOffset = targetOffset)
            }
        }
    }

    val thumbShape = remember(orientation, alignment, thumbNotchWidth) {
        ThumbNotchShape(
            orientation = orientation,
            alignment = alignment,
            thumbNotchWidth = thumbNotchWidth
        )
    }

    LaunchedEffect(scrollRatio, maximumBarPosition) {

        if (isBarDragging) return@LaunchedEffect

        barPosition = (scrollRatio * maximumBarPosition).coerceIn(0F..maximumBarPosition)
    }

    LaunchedEffect(state, isBarDragging) {

        snapshotFlow {
            state.isScrollInProgress || isBarDragging
        }.distinctUntilChanged().collectLatest { isVisible ->

            when (isVisible) {

                true -> delay(duration = 10.milliseconds)
                else -> delay(duration = 1.4.seconds)
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
        firstVisibleItemIndex = firstVisibleItemIndex,
        visibleItemsCount = visibleItemsCount,
        totalItemsCount = totalItemsCount,
        label = label,
        thumb = thumb
    )
}