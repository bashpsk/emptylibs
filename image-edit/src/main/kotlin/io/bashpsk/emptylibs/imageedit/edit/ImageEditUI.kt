package io.bashpsk.emptylibs.imageedit.edit

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Brush
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
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.launch

/**
 * Displays the image editing UI.
 *
 * This composable function is responsible for rendering the image and handling user interactions
 * such as tapping and dragging to edit the image. It uses a [BoxWithConstraints] to determine
 * the available space and then displays the image within that space, maintaining its aspect ratio.
 *
 * The image editing functionality is achieved by drawing [ImageEditItems] onto a canvas.
 * User gestures are detected and translated into editing actions, which are then applied
 * to the [ImageEditState].
 *
 * @param modifier The modifier to be applied to the UI.
 * @param state The current state of the image editing process. This includes the image bitmap,
 * the list of edit items, and other configuration options.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun ImageEditUI(
    modifier: Modifier = Modifier,
    state: ImageEditState
) {

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

                drawImageEditItemHandle(items = items, config = state.config)
                drawImageEditItem(items = items, textMeasurer = state.textMeasurer)
            }
        }
    }

    val tapPointerInputModifier = Modifier.pointerInput(Unit) {

        detectTapGestures(
            onTap = { position ->

                state.apply {

                    onEditItemStart(position = position)
                    onEditItemChanges(position = position, amount = Offset.Zero)
                    onEditItemEnd()
                    onEditItemsClick(position = position)
                }
            }
        )
    }

    val drawPointerInputModifier = Modifier.pointerInput(Unit) {

        detectDragGestures(
            onDragStart = state::onEditItemStart,
            onDragEnd = state::onEditItemEnd,
            onDragCancel = state::onEditItemEnd,
            onDrag = { change, dragAmount ->

                change.consume()
                state.onEditItemChanges(position = change.position, amount = dragAmount)
            }
        )
    }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        state.imageBitmap?.let { bitmap ->

            Image(
                modifier = Modifier
                    .fillMaxSize()
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

/**
 * Composable function that displays the top bar for the image editing screen.
 * It includes navigation controls, undo functionality, and access to tool settings and a toolbar
 * menu.
 *
 * @param state The current state of the image editing process, containing information about drawn
 * items and selected tools.
 * @param editToolInputSheetState The state of the bottom sheet used for tool-specific input
 * (e.g., text input).
 * @param onDoneClick Callback function invoked when the "Done" button is clicked, typically to
 * finalize edits.
 * @param onNavigateBack Callback function invoked when the back navigation button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImageEditTopBar(
    state: ImageEditState,
    editToolInputSheetState: SheetState,
    onDoneClick: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val sheetCoroutineScope = rememberCoroutineScope()

    val isUndoButtonEnable by remember(state.imageEditItemList) {
        derivedStateOf { state.imageEditItemList.isNotEmpty() }
    }

    val isToolSettingsVisible by remember(state.currentImageEditItem) {
        derivedStateOf { state.currentImageEditItem != null }
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

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                item {

                    AnimatedVisibility(
                        visible = isToolSettingsVisible,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {

                        IconButton(
                            onClick = {

                                sheetCoroutineScope.launch { editToolInputSheetState.expand() }
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Tool Settings"
                            )
                        }
                    }
                }
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

/**
 * Composable function for the bottom app bar in the image editor.
 * This bar contains toggle buttons for selecting different editing tools like brush, erase, image,
 * shape, and text.
 *
 * @param state The current state of the image editor, containing information about the selected
 * tool and callbacks for tool selection.
 */
@Composable
internal fun ImageEditBottomBar(state: ImageEditState) {

    val isBrushItemSelected by remember(state.currentImageEditItem) {
        derivedStateOf { state.currentImageEditItem is ImageEditItems.BrushItem }
    }

    val isEraseItemSelected by remember(state.currentImageEditItem) {
        derivedStateOf { state.currentImageEditItem is ImageEditItems.EraseItem }
    }

    val isImageItemSelected by remember(state.currentImageEditItem) {
        derivedStateOf { state.currentImageEditItem is ImageEditItems.ImageItem }
    }

    val isShapeItemSelected by remember(state.currentImageEditItem) {
        derivedStateOf { state.currentImageEditItem is ImageEditItems.ShapeItem }
    }

    val isTextItemSelected by remember(state.currentImageEditItem) {
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
                    checked = isBrushItemSelected,
                    onCheckedChange = { checked ->

                        if (checked) state.onBrushItem() else state.onResetEditItem()
                    }
                ) {

                    Icon(
                        imageVector = when (isBrushItemSelected) {

                            true -> Icons.Filled.Brush
                            false -> Icons.Outlined.Brush
                        },
                        contentDescription = "Brush"
                    )
                }
            }

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

                        if (checked) state.onImageItem() else state.onResetEditItem()
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
        }
    }
}

/**
 * Composable function for the canvas slate toolbar.
 * This toolbar provides options to delete the current item or clear the entire canvas.
 *
 * @param state The current state of the image editor, containing information about the
 * selected item and canvas content.
 */
@Composable
private fun CanvasSlateToolBar(state: ImageEditState) {

    val isDeleteEnabled by remember(state.currentImageEditItem) {
        derivedStateOf { state.currentImageEditItem != null }
    }

    DropdownMenu(
        expanded = state.isToolBarMenuExpanded,
        onDismissRequest = {

            state.isToolBarMenuExpanded = false
        }
    ) {

        MenuItemView(
            icon = Icons.Filled.Delete,
            label = "Delete Item",
            enabled = isDeleteEnabled,
            onClick = {

                state.apply {

                    onCurrentImageEdit(items = null)
                    isToolBarMenuExpanded = false
                }
            }
        )

        MenuItemView(
            icon = Icons.Filled.ClearAll,
            label = "Clear All",
            onClick = {

                state.apply {

                    onClearCanvas()
                    isToolBarMenuExpanded = false
                }
            }
        )
    }
}

/**
 * A composable function that displays a menu item with an icon, label, and an onClick action.
 *
 * @param icon The [ImageVector] to be displayed as the leading icon.
 * @param label The text to be displayed as the label for the menu item.
 * @param enabled A boolean indicating whether the menu item is enabled or disabled.
 * Defaults to true.
 * @param onClick A lambda function to be executed when the menu item is clicked.
 */
@Composable
private fun MenuItemView(
    icon: ImageVector,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {

    DropdownMenuItem(
        enabled = enabled,
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

/**
 * A composable function that displays a menu item with an icon, label, and a switch.
 *
 * @param icon The icon to display for the menu item.
 * @param label The text label for the menu item.
 * @param isChecked Whether the switch is checked or not.
 * @param enabled Whether the menu item is enabled or not. Defaults to true.
 * @param onClick The callback to be invoked when the menu item is clicked.
 */
@Composable
private fun MenuItemView(
    icon: ImageVector,
    label: String,
    isChecked: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {

    val thumbIcon by remember(isChecked) {
        derivedStateOf { if (isChecked) Icons.Filled.Check else Icons.Filled.Clear }
    }

    DropdownMenuItem(
        enabled = enabled,
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