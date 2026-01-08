package io.bashpsk.emptylibs.jetpackui.scrollbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.AndroidPath
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

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
    thumbNotchWidth: Dp = 8.dp,
    crossinline label: @Composable (index: Int) -> Unit = {},
    crossinline thumb: @Composable () -> Unit = {

        Icon(
            modifier = Modifier.size(size = 24.dp),
            imageVector = Icons.Filled.DragIndicator,
            contentDescription = "Drag Indicator"
        )
    }
) {

    val density = LocalDensity.current
    val listCoroutineScope = rememberCoroutineScope()
    val scrollBarVisibleState = remember { MutableTransitionState(false) }

    val firstVisibleItemIndex by remember { derivedStateOf { state.firstVisibleItemIndex } }
    val totalItemsCount by remember { derivedStateOf { state.layoutInfo.totalItemsCount } }
    val visibleItemsInfo by remember { derivedStateOf { state.layoutInfo.visibleItemsInfo } }
    val viewportStartOffset by remember { derivedStateOf { state.layoutInfo.viewportStartOffset } }
    val viewportEndOffset by remember { derivedStateOf { state.layoutInfo.viewportEndOffset } }
    val firstItemScrollOffset by remember { derivedStateOf { state.firstVisibleItemScrollOffset } }

    val scrollRatio by remember(
        totalItemsCount,
        viewportStartOffset,
        viewportEndOffset,
        visibleItemsInfo
    ) {
        derivedStateOf {
            when {

                visibleItemsInfo.isEmpty() || totalItemsCount == 0 -> 0F

                else -> {

                    val firstVisibleItem = visibleItemsInfo.first()
                    val totalItemsSize = totalItemsCount * (firstVisibleItem.size.toFloat() / 1)
                    val viewportSize = viewportEndOffset - viewportStartOffset

                    (totalItemsSize - viewportSize).coerceAtLeast(0F).takeIf { scrollableDistance ->

                        scrollableDistance > 0
                    }?.let { distance ->

                        (((firstVisibleItem.index * firstVisibleItem.size) + firstItemScrollOffset)
                                / distance).coerceIn(0F, 1F)
                    } ?: 0F
                }
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
            }
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

            barPosition = (barPosition + delta).coerceIn(0F, maximumBarPosition)

            listCoroutineScope.launch {

                val totalItemsSize = totalItemsCount * visibleItemsInfo.first().size
                val viewportSize = viewportEndOffset - viewportStartOffset
                val scrollableDistance = (totalItemsSize - viewportSize).coerceAtLeast(0)
                val scrollPosition = scrollableDistance * (barPosition / maximumBarPosition)
                val itemSize = visibleItemsInfo.firstOrNull()?.size ?: 1
                val newItem = (scrollPosition / itemSize).toInt().coerceIn(0..<totalItemsCount)
                val targetOffset = (scrollPosition % itemSize).toInt()

                state.scrollToItem(index = newItem, scrollOffset = targetOffset)
            }
        }
    }

    val thumbShape = remember(orientation, alignment, thumbNotchWidth) {
        object : Shape {

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
    }

    LaunchedEffect(scrollRatio, maximumBarPosition) {

        if (isBarDragging) return@LaunchedEffect

        barPosition = (scrollRatio * maximumBarPosition).coerceIn(0F, maximumBarPosition)
    }

    LaunchedEffect(state, isBarDragging) {

        snapshotFlow {
            state.isScrollInProgress || isBarDragging
        }.distinctUntilChanged().collectLatest { isVisible ->

            when (isVisible) {

                true -> delay(duration = 10.milliseconds)
                else -> delay(duration = 750.milliseconds)
            }

            scrollBarVisibleState.targetState = isVisible
        }
    }

    AnimatedVisibility(
        modifier = Modifier.align(alignment = alignment),
        visibleState = scrollBarVisibleState,
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
            .onSizeChanged { size ->

                barSize = when (orientation) {

                    Orientation.Vertical -> size.height.toFloat()
                    Orientation.Horizontal -> size.width.toFloat()
                }
            }

        val thumbContent = @Composable {

            Box(
                modifier = Modifier
                    .clip(shape = thumbShape)
                    .background(color = thumbColor, shape = thumbShape)
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .draggable(
                        state = barDraggableState,
                        orientation = orientation,
                        onDragStarted = { isBarDragging = true },
                        onDragStopped = { isBarDragging = false }
                    ),
                contentAlignment = Alignment.Center
            ) {

                thumb()
            }
        }

        val labelContent = @Composable {

            Box(
                modifier = Modifier
                    .background(color = thumbColor, shape = CircleShape)
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {

                label(firstVisibleItemIndex)
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