package io.bashpsk.emptylibs.imageedit.edit

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import io.bashpsk.emptylibs.formatter.format.EmptyFormat

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun ImageEditUI(modifier: Modifier = Modifier, state: ImageEditState) {

    val coroutineScope = rememberCoroutineScope()

    val sizeChangedModifier = Modifier.onSizeChanged { size ->

        state.canvasSize = size.toSize()
    }

    val drawCanvasModifier = Modifier.drawWithContent {

        drawContent()

        drawIntoCanvas {

            state.imageEditItemList.forEach { items ->

                drawImageEditItem(items = items)
            }

            state.currentImageEditItem?.let { items ->

                drawImageEditItem(items = items)
            }
        }
    }

    val tapPointerInputModifier = Modifier.pointerInput(Unit) {

        detectTapGestures(
            onTap = { position ->

                state.apply {

                    /*onEditPathData(position = position)?.takeIf { isVisible -> isVisible }?.run {

                        coroutineScope.launch { pathEditSheetState.show() }
                        return@detectTapGestures
                    }*/

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

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        state.imageBitmap?.let { bitmap ->

            val aspectRatio by remember(bitmap) {
                derivedStateOf {
                    EmptyFormat.findAspectRatio(width = bitmap.width, height = bitmap.height)
                }
            }

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = aspectRatio)
                    .then(sizeChangedModifier)
                    .then(drawCanvasModifier)
                    .then(tapPointerInputModifier)
                    .then(drawPointerInputModifier)
                    .clipToBounds(),
                bitmap = bitmap,
                contentScale = ContentScale.Fit,
                contentDescription = "Edit Image"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImageEditTopBar(
    state: ImageEditState,
    onDoneClick: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val isUndoButtonEnable by remember(state) {
        derivedStateOf { state.imageEditItemList.isNotEmpty() }
    }

    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        navigationIcon = {

            Row(
                horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = onNavigateBack) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate Back"
                    )
                }

                IconButton(
                    enabled = isUndoButtonEnable,
                    onClick = onDoneClick
                ) {

                    Icon(
                        imageVector = Icons.Filled.DoneAll,
                        contentDescription = "Done"
                    )
                }
            }
        },
        title = {

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
                onClick = {

                    state.isToolBarMenuExpanded = true
                }
            ) {

                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Tool Bar"
                )

                CanvasSlateToolBar(
                    state = state,
                )
            }
        },
        windowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0)
    )
}

@Composable
internal fun ImageEditBottomBar(state: ImageEditState) {

    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        windowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0)
    ) {

    }
}

@Composable
private fun CanvasSlateToolBar(
    state: ImageEditState
) {

    DropdownMenu(
        expanded = state.isToolBarMenuExpanded,
        onDismissRequest = {

            state.isToolBarMenuExpanded = false
        }
    ) {

        HorizontalDivider()

        MenuItemView(
            icon = Icons.Filled.Draw,
            label = "Drawing Mode",
            isChecked = state.isDrawingMode,
            onClick = {

                state.apply {

                    onDrawingMode(mode = isDrawingMode.not())
                    isToolBarMenuExpanded = false
                }
            }
        )

        HorizontalDivider()

        MenuItemView(
            icon = Icons.Filled.ClearAll,
            label = "Clear Canvas",
            onClick = {

                state.apply {

                    onClearCanvas()
                    isToolBarMenuExpanded = false
                }
            }
        )
    }
}

@Composable
private fun MenuItemView(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {

    DropdownMenuItem(
        text = {

            Text(
                text = label,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        leadingIcon = {

            Icon(
                imageVector = icon,
                contentDescription = label
            )
        },
        onClick = onClick
    )
}

@Composable
private fun MenuItemView(
    icon: ImageVector,
    label: String,
    isChecked: Boolean,
    onClick: () -> Unit
) {

    val thumbIcon by remember(isChecked) {
        derivedStateOf { if (isChecked) Icons.Filled.Check else Icons.Filled.Clear }
    }

    DropdownMenuItem(
        text = {

            Text(
                text = label,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        leadingIcon = {

            Icon(
                imageVector = icon,
                contentDescription = label
            )
        },
        trailingIcon = {

            Switch(
                checked = isChecked,
                thumbContent = {

                    Icon(
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                        imageVector = thumbIcon,
                        contentDescription = "Switch"
                    )
                },
                onCheckedChange = null
            )
        },
        onClick = onClick
    )
}