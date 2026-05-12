package io.bashpsk.emptylibs.canvasslate.slate

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.launch

/**
 * Composable function that renders the canvas for drawing and editing paths.
 *
 * This function handles user input for drawing, tapping to edit existing paths,
 * and manages the display of all paths on the canvas.
 *
 * @param modifier Modifier for styling the canvas.
 * @param state The current state of the canvas, including path data, colors, and drawing mode.
 * @param pathEditSheetState The state of the bottom sheet used for editing path properties.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CanvasSlateUI(
    modifier: Modifier = Modifier,
    state: CanvasSlateState,
    pathEditSheetState: SheetState,
) {

    val coroutineScope = rememberCoroutineScope()

    val screenSizeChanged = Modifier.onSizeChanged { size ->

        state.canvasSize = size.toSize()
    }

    val tapPointerInputModifier = Modifier.pointerInput(Unit) {

        detectTapGestures(
            onTap = { position ->

                state.apply {

                    if (onEditPathData(position = position) == true) {

                        coroutineScope.launch { pathEditSheetState.show() }
                        return@detectTapGestures
                    }

                    onPathStart()
                    onPathDraw(position = position)
                    onPathEnd()
                }
            }
        )
    }

    val drawPointerInputModifier = Modifier.pointerInput(Unit) {

        detectDragGestures(
            onDragStart = { state.onPathStart() },
            onDragEnd = state::onPathEnd,
            onDragCancel = state::onPathEnd,
            onDrag = { change, _ ->

                change.consume()
                state.onPathDraw(position = change.position)
            }
        )
    }

    Box(
        modifier = modifier.background(color = state.backgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .then(screenSizeChanged)
                .then(tapPointerInputModifier)
                .then(drawPointerInputModifier),
            contentDescription = "Canvas Slate"
        ) {

            state.allPathList.forEach { pathData ->

                drawSlatePath(slatePath = pathData)
            }

            state.currentPath?.let { pathData ->

                drawSlatePath(slatePath = pathData)
            }
        }
    }
}

/**
 * Composable function that displays the top app bar for the canvas slate.
 *
 * This top bar includes navigation icons, color selection, undo functionality, and a toolbar menu.
 *
 * @param modifier Optional [Modifier] for styling the top app bar.
 * @param state The current state of the canvas slate, holding information like selected colors and
 * drawn paths.
 * @param backgroundColorPickerDialog A [MutableTransitionState] to control the visibility of the
 * background color picker dialog.
 * @param foregroundColorPickerDialog A [MutableTransitionState] to control the visibility of the
 * foreground (brush) color picker dialog.
 * @param penStrokeDialogVisibleState A [MutableTransitionState] to control the visibility of the
 * pen stroke type selection dialog.
 * @param penThicknessDialogVisibleState A [MutableTransitionState] to control the visibility of
 * the pen thickness selection dialog.
 * @param onDoneClick Lambda function to be invoked when the "Done" button is clicked. Defaults to
 * an empty lambda.
 * @param onNavigateBack Lambda function to be invoked when the "Navigate Back" button is clicked.
 * Defaults to an empty lambda.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CanvasSlateTopBar(
    modifier: Modifier = Modifier,
    state: CanvasSlateState,
    backgroundColorPickerDialog: MutableTransitionState<Boolean>,
    foregroundColorPickerDialog: MutableTransitionState<Boolean>,
    penStrokeDialogVisibleState: MutableTransitionState<Boolean>,
    penThicknessDialogVisibleState: MutableTransitionState<Boolean>,
    onDoneClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {

    val isUndoButtonEnable by remember(state.allPathList) {
        derivedStateOf { state.allPathList.isNotEmpty() }
    }

    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {

            ColorSelectionBar(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                backgroundColorPickerDialog = backgroundColorPickerDialog,
                foregroundColorPickerDialog = foregroundColorPickerDialog
            )
        },
        navigationIcon = {

            Row(
                horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onNavigateBack
                ) {

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
                    penStrokeDialogVisibleState = penStrokeDialogVisibleState,
                    penThicknessDialogVisibleState = penThicknessDialogVisibleState
                )
            }
        },
        windowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0)
    )
}

/**
 * Composable function for the Canvas Slate Toolbar.
 * This toolbar provides options to change pen type, pen thickness, toggle drawing mode, and clear
 * the canvas.
 *
 * @param state The current state of the canvas slate, containing information like whether the
 * toolbar menu is expanded, current drawing mode, etc.
 * @param penStrokeDialogVisibleState A mutable transition state to control the visibility of the
 * pen stroke dialog.
 * @param penThicknessDialogVisibleState A mutable transition state to control the visibility of the
 * pen thickness dialog.
 */
@Composable
internal fun CanvasSlateToolBar(
    state: CanvasSlateState,
    penStrokeDialogVisibleState: MutableTransitionState<Boolean>,
    penThicknessDialogVisibleState: MutableTransitionState<Boolean>,
) {

    DropdownMenu(
        expanded = state.isToolBarMenuExpanded,
        onDismissRequest = {

            state.isToolBarMenuExpanded = false
        }
    ) {

        MenuItemView(
            icon = Icons.Filled.ModeEdit,
            label = "Pen Type",
            onClick = {

                state.isToolBarMenuExpanded = false
                penStrokeDialogVisibleState.targetState = true
            }
        )

        MenuItemView(
            icon = Icons.Filled.BorderColor,
            label = "Pen Thickness",
            onClick = {

                state.isToolBarMenuExpanded = false
                penThicknessDialogVisibleState.targetState = true
            }
        )

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

/**
 * A Composable function that displays a horizontal list of color selection boxes.
 * It allows the user to select the background color and the foreground (brush) color for the
 * canvas.
 *
 * @param modifier Optional [Modifier] for this composable.
 * @param state The current state of the canvas, containing selected colors.
 * @param backgroundColorPickerDialog A [MutableTransitionState] to control the visibility of the
 * background color picker dialog.
 * @param foregroundColorPickerDialog A [MutableTransitionState] to control the visibility of the
 * foreground color picker dialog.
 */
@Composable
private fun ColorSelectionBar(
    modifier: Modifier = Modifier,
    state: CanvasSlateState,
    backgroundColorPickerDialog: MutableTransitionState<Boolean>,
    foregroundColorPickerDialog: MutableTransitionState<Boolean>,
) {

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {

        item {

            ColorBoxView(
                color = state.backgroundColor,
                onColorClick = {

                    backgroundColorPickerDialog.targetState = true
                }
            )
        }

        item {

            ColorBoxView(
                color = state.brushColor,
                onColorClick = {

                    foregroundColorPickerDialog.targetState = true
                }
            )
        }
    }
}

/**
 * A composable function that displays a circular color box.
 *
 * @param color The color to be displayed in the box.
 * @param onColorClick A lambda function to be invoked when the color box is clicked.
 */
@Composable
private fun ColorBoxView(color: Color, onColorClick: () -> Unit) {

    Box(
        modifier = Modifier
            .size(size = 36.dp)
            .background(color = color, shape = CircleShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .clip(shape = CircleShape)
            .clickable(role = Role.Button, onClick = onColorClick)
            .shadow(elevation = 16.dp, shape = CircleShape)
    )
}

/**
 * A composable function that displays a menu item with an icon, label, and an action to perform on
 * click.
 *
 * @param icon The [ImageVector] to be displayed as the leading icon of the menu item.
 * @param label The text to be displayed as the label of the menu item.
 * @param onClick A lambda function that will be invoked when the menu item is clicked.
 */
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

/**
 * A composable function that displays a menu item with an icon, label, and a switch.
 * The switch indicates the checked state of the menu item.
 *
 * @param icon The vector graphic to be displayed as the leading icon of the menu item.
 * @param label The text to be displayed as the label of the menu item.
 * @param isChecked A boolean value indicating whether the menu item is currently checked.
 * @param onClick A lambda function to be invoked when the menu item is clicked.
 */
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