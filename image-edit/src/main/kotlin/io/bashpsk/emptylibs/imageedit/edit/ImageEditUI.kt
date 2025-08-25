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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.TextIncrease
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
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
internal fun ImageEditUI(
    modifier: Modifier = Modifier,
    state: ImageEditState,
    config: ImageEditConfig
) {

    val coroutineScope = rememberCoroutineScope()

    val sizeChangedModifier = Modifier.onSizeChanged { size ->

        state.canvasSize = size.toSize()
    }

    val drawCanvasModifier = Modifier.drawWithContent {

        drawContent()

        drawIntoCanvas {

            state.imageEditItemList.forEach { items ->

                drawImageEditItem(items = items, textMeasurer = state.textMeasurer)
            }

            state.currentImageEditItem?.let { items ->

                drawImageEditItem(items = items, textMeasurer = state.textMeasurer)
                drawImageEditItemHandle(items = items, config = config)
            }
        }
    }

    val tapPointerInputModifier = Modifier.pointerInput(Unit) {

        detectTapGestures(
            onTap = { position ->

                state.apply {

                    onEditItemStart()
                    onEditItemChanges(position = position, size = null)
                    onEditItemEnd()
                }
            }
        )
    }

    val drawPointerInputModifier = Modifier.pointerInput(Unit) {

        detectDragGestures(
            onDragStart = { state.onEditItemStart() },
            onDragEnd = state::onEditItemEnd,
            onDragCancel = state::onEditItemEnd,
            onDrag = { change, dragAmount ->

                change.consume()
                state.onEditItemChanges(position = change.position, null)
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
internal fun ImageEditBottomBar(
    state: ImageEditState,
    onBitmapSelect: () -> Unit
) {

    val isEraseItemSelected by remember(state) {
        derivedStateOf { state.currentImageEditItem is ImageEditItems.EraseItem }
    }

    val isImageItemSelected by remember(state) {
        derivedStateOf { state.currentImageEditItem is ImageEditItems.ImageItem }
    }

    val isPathItemSelected by remember(state) {
        derivedStateOf { state.currentImageEditItem is ImageEditItems.PathItem }
    }

    val isShapeItemSelected by remember(state) {
        derivedStateOf { state.currentImageEditItem is ImageEditItems.ShapeItem }
    }

    val isTextItemSelected by remember(state) {
        derivedStateOf { state.currentImageEditItem is ImageEditItems.TextItem }
    }

    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        windowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0)
    ) {

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            item {

                FilledIconToggleButton(
                    checked = isEraseItemSelected,
                    onCheckedChange = { checked ->

                        if (checked) state.onEraseItem() else state.onResetEditItem()
                    }
                ) {

                    Icon(
                        imageVector = when (isEraseItemSelected) {

                            true -> Icons.Filled.BorderColor
                            false -> Icons.Outlined.BorderColor
                        },
                        contentDescription = "Erase"
                    )
                }
            }

            item {

                FilledIconToggleButton(
                    checked = isImageItemSelected,
                    onCheckedChange = { checked ->

                        when (checked) {

                            true -> {

                                when (state.selectedBitmap) {

                                    null -> onBitmapSelect()
                                    else -> state.onImageItem()
                                }
                            }

                            false -> state.onResetEditItem()
                        }
                    }
                ) {

                    Icon(
                        imageVector = when (isImageItemSelected) {

                            true -> Icons.Filled.Image
                            false -> Icons.Outlined.Image
                        },
                        contentDescription = "Image"
                    )
                }
            }

            item {

                FilledIconToggleButton(
                    checked = isPathItemSelected,
                    onCheckedChange = { checked ->

                        if (checked) state.onPathItem() else state.onResetEditItem()
                    }
                ) {

                    Icon(
                        imageVector = when (isPathItemSelected) {

                            true -> Icons.Filled.Draw
                            false -> Icons.Outlined.Draw
                        },
                        contentDescription = "Draw"
                    )
                }
            }

            item {

                FilledIconToggleButton(
                    checked = isShapeItemSelected,
                    onCheckedChange = { checked ->

                        if (checked) state.onShapeItem() else state.onResetEditItem()
                    }
                ) {

                    Icon(
                        imageVector = when (isShapeItemSelected) {

                            true -> Icons.Filled.Star
                            false -> Icons.Outlined.StarBorder
                        },
                        contentDescription = "Shape"
                    )
                }
            }

            item {

                FilledIconToggleButton(
                    checked = isTextItemSelected,
                    onCheckedChange = { checked ->

                        if (checked) state.onTextItem() else state.onResetEditItem()
                    }
                ) {

                    Icon(
                        imageVector = when (isTextItemSelected) {

                            true -> Icons.Filled.TextIncrease
                            false -> Icons.Outlined.TextIncrease
                        },
                        contentDescription = "Text"
                    )
                }
            }

            item {

                IconButton(
                    onClick = {}
                ) {

                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Tool Settings"
                    )
                }
            }
        }
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