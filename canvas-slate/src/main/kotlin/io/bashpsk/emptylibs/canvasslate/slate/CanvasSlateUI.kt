package io.bashpsk.emptylibs.canvasslate.slate

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.canvasslate.extension.findContentColorFor

@Composable
fun CanvasSlateUI(
    modifier: Modifier = Modifier,
    state: CanvasSlateState
) {

    val tapPointerInputModifier = Modifier.pointerInput(Unit) {

        detectTapGestures(
            onTap = { position ->

                state.apply {

                    Log.d("PSK", "Tap Position: $position")
                    onNewPathStart()
                    onPathDraw(position = position)
                    onPathEnd()
                }
            },
            onDoubleTap = { position ->

                state.apply {

                    Log.d("PSK", "D Tap Position: $position")
                    onNewPathStart()
                    onPathDraw(position = position)
                    onPathEnd()
                }
            },
            onLongPress = { position ->

                state.apply {

                    Log.d("PSK", "L Tap Position: $position")
                    onNewPathStart()
                    onPathDraw(position = position)
                    onPathEnd()
                }
            }
        )
    }

    val drawPointerInputModifier = Modifier.pointerInput(Unit) {

        detectDragGestures(
            onDragStart = { state.onNewPathStart() },
            onDragEnd = state::onPathEnd,
            onDragCancel = state::onPathEnd,
            onDrag = { change, dragAmount ->

                change.consume()
                state.onPathDraw(position = change.position)
            }
        )
    }

    Canvas(
        modifier = modifier
            .background(color = state.selectedBackgroundColor)
            .clipToBounds()
            .then(tapPointerInputModifier)
            .then(drawPointerInputModifier),
        contentDescription = "Canvas Slate"
    ) {

        state.allPathList.forEach { pathData ->

            drawPathData(pathData = pathData)
        }

        state.currentPath?.let { pathData ->

            drawPathData(pathData = pathData)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasSlateTopBar(
    modifier: Modifier = Modifier,
    state: CanvasSlateState,
    onNavigateBack: () -> Unit = {}
) {

    val isUndoButtonEnable by remember(state.allPathList) {
        derivedStateOf { state.allPathList.isNotEmpty() }
    }

    TopAppBar(
        title = {

            ColorSelectionBar(
                modifier = modifier,
                state = state
            )
        },
        navigationIcon = {

            IconButton(
                onClick = onNavigateBack
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate Back"
                )
            }
        },
        actions = {

            IconButton(
                enabled = isUndoButtonEnable,
                onClick = state::onUndoCanvas
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo"
                )
            }

            IconButton(
                enabled = isUndoButtonEnable,
                onClick = state::onClearCanvas
            ) {

                Icon(
                    imageVector = Icons.Filled.ClearAll,
                    contentDescription = "Clear Canvas"
                )
            }
        },
        windowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0)
    )
}

@Composable
fun CanvasSlateToolBar(
    modifier: Modifier = Modifier,
    state: CanvasSlateState
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

    }
}

@Composable
private fun ColorSelectionBar(
    modifier: Modifier = Modifier,
    state: CanvasSlateState
) {

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        items(
            items = state.colorList,
            key = { colorItem -> colorItem.hashCode() }
        ) { colorItem ->

            val isSelected by remember(state, colorItem) {
                derivedStateOf { state.selectedPenColor == colorItem }
            }

            ColorBoxView(
                color = colorItem,
                onColorClick = state::updatePenColor,
                isSelected = isSelected
            )
        }
    }
}

@Composable
private fun ColorBoxView(color: Color, isSelected: Boolean, onColorClick: (Color) -> Unit) {

    val elevatedCardElevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)

    val elevatedCardColors = CardDefaults.elevatedCardColors(
        containerColor = color,
        contentColor = findContentColorFor(color)
    )

    ElevatedCard(
        modifier = Modifier.size(size = 40.dp),
        shape = CircleShape,
        elevation = elevatedCardElevation,
        colors = elevatedCardColors,
        onClick = {

            onColorClick(color)
        }
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {

                Icon(
                    imageVector = Icons.Filled.Check,
                    tint = elevatedCardColors.contentColor,
                    contentDescription = "Color Selected"
                )
            }
        }
    }
}