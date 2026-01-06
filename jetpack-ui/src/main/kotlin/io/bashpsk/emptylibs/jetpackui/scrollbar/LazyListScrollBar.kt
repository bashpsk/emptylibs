package io.bashpsk.emptylibs.jetpackui.scrollbar

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
inline fun BoxWithConstraintsScope.LazyListScrollBar(
    modifier: Modifier = Modifier,
    state: LazyListState,
    thumbColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    crossinline label: @Composable (index: Int) -> Unit = {},
    crossinline thumb: @Composable () -> Unit = {

        Icon(
            modifier = Modifier.size(size = 24.dp),
            imageVector = Icons.Filled.DragIndicator,
            contentDescription = "Drag Indicator"
        )
    }
) {

    val listCoroutineScope = rememberCoroutineScope()

    val firstVisibleItemIndex by remember { derivedStateOf { state.firstVisibleItemIndex } }
    val totalItemsCount by remember { derivedStateOf { state.layoutInfo.totalItemsCount } }

    val scrollableItemsCount by remember(totalItemsCount) {
        derivedStateOf {
            (totalItemsCount - state.layoutInfo.visibleItemsInfo.size).coerceAtLeast(0)
        }
    }

    val scrollRatio by remember(scrollableItemsCount) {
        derivedStateOf { state.firstVisibleItemIndex.toFloat() / scrollableItemsCount }
    }

    var barHeight by rememberSaveable { mutableStateOf(0F) }
    var barPositionY by rememberSaveable { mutableFloatStateOf(0F) }
    var isBarDragging by rememberSaveable { mutableStateOf(false) }

    val maximumBarY by remember(constraints.maxHeight, barHeight) {
        derivedStateOf { constraints.maxHeight - barHeight }
    }

    val barDraggableState = rememberDraggableState { delta ->

        if (maximumBarY > 0) {

            barPositionY = (barPositionY + delta).coerceIn(0F, maximumBarY)

            listCoroutineScope.launch {

                val newItem = (scrollableItemsCount * (barPositionY / maximumBarY)).roundToInt()

                state.scrollToItem(index = newItem)
            }
        }
    }

    LaunchedEffect(state.firstVisibleItemIndex, totalItemsCount) {

        if (isBarDragging) return@LaunchedEffect

        barPositionY = when {

            scrollableItemsCount > 0 -> (scrollRatio * maximumBarY).coerceIn(0F, maximumBarY)
            else -> 0F
        }
    }

    Row(
        modifier = modifier
            .offset { IntOffset(x = 8.dp.toPx().roundToInt(), y = barPositionY.roundToInt()) }
            .onSizeChanged { size ->

                barHeight = size.height.toFloat()
            },
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .background(color = thumbColor, shape = CircleShape)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {

            label(firstVisibleItemIndex)
        }

        Box(
            modifier = Modifier
                .background(color = thumbColor, shape = CircleShape)
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .draggable(
                    state = barDraggableState,
                    orientation = Orientation.Vertical,
                    onDragStarted = { isBarDragging = true },
                    onDragStopped = { isBarDragging = false }
                ),
            contentAlignment = Alignment.Center
        ) {

            thumb()
        }
    }
}