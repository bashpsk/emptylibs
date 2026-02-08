package io.bashpsk.emptylibs.canvasslate.slate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.bashpsk.emptylibs.canvasslate.extension.toStrokeCap
import io.bashpsk.emptylibs.canvasslate.extension.toStrokeJoin
import io.bashpsk.emptylibs.formatter.format.findAspectRatio
import io.bashpsk.emptylibs.formatter.format.toRoundedDecimal
import io.bashpsk.emptylibs.kolorpicker.color.KolorPickerDialog
import io.bashpsk.emptylibs.kolorpicker.color.rememberKolorPickerState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

/**
 * A composable function that displays a modal bottom sheet for editing a path on the canvas.
 *
 * This bottom sheet allows the user to modify various properties of a selected path,
 * such as its thickness, stroke cap, stroke join, and color. It also provides options
 * to undo changes, delete the path, or apply the modifications.
 *
 * @param pathEditSheetState The [SheetState] that controls the visibility and behavior of the
 * bottom sheet.
 * @param state The [CanvasSlateState] that holds the current state of the canvas and the path being
 * edited.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PathEditBottomSheet(
    pathEditSheetState: SheetState,
    state: CanvasSlateState
) {

    val coroutineScope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = pathEditSheetState.isVisible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {

        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            sheetState = pathEditSheetState,
            onDismissRequest = {

                coroutineScope.launch { pathEditSheetState.hide() }
            },
            shape = MaterialTheme.shapes.small
        ) {

            PathEditView(
                state = state,
                onDismiss = {

                    coroutineScope.launch { pathEditSheetState.hide() }
                }
            )
        }
    }
}

/**
 * Composable function that displays a dialog for selecting pen thickness.
 *
 * @param dialogVisibleState The mutable transition state to control the visibility of the dialog.
 * @param state The current state of the canvas slate, used to get and update the pen thickness.
 */
@Composable
internal fun PenThicknessDialog(
    dialogVisibleState: MutableTransitionState<Boolean>,
    state: CanvasSlateState,
) {

    AnimatedVisibility(
        visibleState = dialogVisibleState,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {

        AlertDialog(
            modifier = Modifier.fillMaxWidth(fraction = 0.95F),
            onDismissRequest = {

                dialogVisibleState.targetState = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            ),
            title = {

                DialogTitleView(
                    title = "Pen Thickness",
                    onDismiss = {

                        dialogVisibleState.targetState = false
                    }
                )
            },
            text = {

                BrushThicknessSelectionView(
                    penThickness = state.brushThickness,
                    onThicknessChange = state::updateBrushThickness
                )
            },
            confirmButton = {

                DialogConfirmButton(dialogVisibleState = dialogVisibleState)
            }
        )
    }
}

/**
 * Composable function that displays an alert dialog for selecting pen stroke cap and join.
 *
 * @param dialogVisibleState The mutable transition state to control the visibility of the dialog.
 * @param state The current state of the canvas slate, used to access and update stroke properties.
 */
@Composable
internal fun PenStrokeDialog(
    dialogVisibleState: MutableTransitionState<Boolean>,
    state: CanvasSlateState,
) {

    AnimatedVisibility(
        visibleState = dialogVisibleState,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {

        AlertDialog(
            modifier = Modifier.fillMaxWidth(fraction = 0.95F),
            onDismissRequest = {

                dialogVisibleState.targetState = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            ),
            title = {

                DialogTitleView(
                    title = "Pen Stroke",
                    onDismiss = {

                        dialogVisibleState.targetState = false
                    }
                )
            },
            text = {

                PenStrokeSelectionView(
                    selectedStrokeCap = state.selectedStrokeCap,
                    selectedStrokeJoin = state.selectedStrokeJoin,
                    onStrokeCapChange = state::updateStrokeCap,
                    onStrokeJoinChange = state::updateStrokeJoin
                )
            },
            confirmButton = {

                DialogConfirmButton(dialogVisibleState = dialogVisibleState)
            }
        )
    }
}

/**
 * A composable function that displays a view for editing a path in the canvas.
 *
 * This view includes options to undo changes, delete the path, apply changes,
 * and modify the brush thickness, stroke cap, stroke join, and color of the path.
 *
 * @param state The current state of the canvas slate, containing information about paths and
 * editing.
 * @param onDismiss A lambda function to be invoked when the edit view is dismissed.
 */
@Composable
private fun PathEditView(state: CanvasSlateState, onDismiss: () -> Unit) {

    val isUndoButtonEnable by remember(state.allPathList, state.previewPathList) {
        derivedStateOf { state.previewPathList.containsAll(elements = state.allPathList).not() }
    }

    val isDeleteButtonEnable by remember(state.editCanvasSlatePath) {
        derivedStateOf { state.editCanvasSlatePath != null }
    }

    val iconButtonColors = IconButtonDefaults.iconButtonColors(
        containerColor = ButtonDefaults.filledTonalButtonColors().containerColor,
        contentColor = ButtonDefaults.filledTonalButtonColors().contentColor,
        disabledContainerColor = ButtonDefaults.filledTonalButtonColors().disabledContainerColor,
        disabledContentColor = ButtonDefaults.filledTonalButtonColors().disabledContentColor
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        stickyHeader {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    colors = iconButtonColors,
                    enabled = isUndoButtonEnable,
                    onClick = state::onUndoPreview
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo"
                    )
                }

                IconButton(
                    colors = iconButtonColors,
                    enabled = isDeleteButtonEnable,
                    onClick = {

                        state.onDeleteEditPath()
                        onDismiss()
                    }
                ) {

                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = "Delete Path"
                    )
                }

                Button(
                    onClick = {

                        state.onApplyPreview()
                        onDismiss()
                    }
                ) {

                    Icon(
                        modifier = Modifier.size(size = 18.dp),
                        imageVector = Icons.Filled.DoneAll,
                        contentDescription = "Apply Changes"
                    )

                    Spacer(modifier = Modifier.width(width = 2.dp))

                    Text(text = "Apply Changes")
                }
            }
        }

        item {

            state.editCanvasSlatePath?.let { pathData ->

                BrushThicknessSelectionView(
                    penThickness = pathData.thickness,
                    onThicknessChange = { thickness ->

                        state.apply {

                            val newPath = pathData.copy(thickness = thickness)

                            onUpdateEditPath(path = newPath)
                            addPathInPreview(path = newPath)
                        }
                    }
                )
            }
        }

        item {

            state.editCanvasSlatePath?.let { pathData ->

                PenStrokeSelectionView(
                    selectedStrokeCap = pathData.strokeCap.toStrokeCap(),
                    selectedStrokeJoin = pathData.strokeJoin.toStrokeJoin(),
                    onStrokeCapChange = { strokeCap ->

                        state.apply {

                            val newPath = pathData.copy(strokeCap = strokeCap.toString())

                            onUpdateEditPath(path = newPath)
                            addPathInPreview(path = newPath)
                        }
                    },
                    onStrokeJoinChange = { strokeJoin ->

                        state.apply {

                            val newPath = pathData.copy(strokeJoin = strokeJoin.toString())

                            onUpdateEditPath(path = newPath)
                            addPathInPreview(path = newPath)
                        }
                    }
                )
            }
        }

        item {

            state.editCanvasSlatePath?.let { pathData ->

                PenColorSelectionView(
                    modifier = Modifier.fillParentMaxWidth(),
                    color = Color(pathData.color),
                    onSelectedColor = { color ->

                        state.apply {

                            val newPath = pathData.copy(color = color.toArgb())

                            onUpdateEditPath(path = newPath)
                            addPathInPreview(path = newPath)
                        }
                    }
                )
            }
        }

        item {

            HorizontalDivider(modifier = Modifier.fillParentMaxWidth())
        }

        item {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Drawing Preview:",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleSmall
            )
        }

        item {

            CanvasPathPreview(
                modifier = Modifier.fillMaxWidth(),
                state = state
            )
        }
    }
}

/**
 * Composable function for selecting a pen color.
 *
 * This function displays a color picker dialog when clicked, allowing the user to choose a color.
 * The selected color is then passed to the `onSelectedColor` callback.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param color The initial color to be displayed and selected.
 * @param onSelectedColor A callback function that is invoked when a new color is selected.
 * It receives the selected [Color] as a parameter.
 */
@Composable
private fun PenColorSelectionView(
    modifier: Modifier = Modifier,
    color: Color,
    onSelectedColor: (color: Color) -> Unit
) {

    val colorPickerState = rememberKolorPickerState(initialColor = color)

    val colorBoxShape = MaterialTheme.shapes.extraSmall

    KolorPickerDialog(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        state = colorPickerState,
        enableAlphaPanel = true,
        onSelectedColor = onSelectedColor
    )

    Row(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.extraSmall)
            .clickable(
                role = Role.Button,
                onClick = { colorPickerState.dialogVisible.targetState = true }
            )
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(size = 40.dp)
                .background(color = color, shape = colorBoxShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = colorBoxShape
                )
                .shadow(elevation = 16.dp, shape = colorBoxShape)
        )

        Text(
            modifier = Modifier.weight(weight = 1.0F),
            text = "Update Color",
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Composable function for displaying a view to select brush thickness.
 * It includes a text indicating the current thickness and a slider to adjust it.
 *
 * @param penThickness The current thickness of the pen.
 * @param onThicknessChange Callback function invoked when the thickness is changed via the slider.
 * It provides the new thickness value.
 */
@Composable
private fun BrushThicknessSelectionView(
    penThickness: Float,
    onThicknessChange: (thickness: Float) -> Unit,
) {

    val selectedThickness by remember(penThickness) {
        derivedStateOf { "Brush Thickness - ${penThickness.toRoundedDecimal(fraction = 1)} Px" }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = selectedThickness,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall
        )

        Slider(
            value = penThickness,
            valueRange = 0.3.dp.value..40.dp.value,
            onValueChange = { newValue ->

                onThicknessChange(newValue)
            }
        )
    }
}

/**
 * A composable function that displays options for selecting pen stroke cap and join types.
 *
 * @param selectedStrokeCap The currently selected stroke cap.
 * @param selectedStrokeJoin The currently selected stroke join.
 * @param onStrokeCapChange A callback function to be invoked when the stroke cap selection changes.
 * @param onStrokeJoinChange A callback function to be invoked when the stroke join selection
 * changes.
 */
@Composable
private fun PenStrokeSelectionView(
    selectedStrokeCap: StrokeCap,
    selectedStrokeJoin: StrokeJoin,
    onStrokeCapChange: (stroke: StrokeCap) -> Unit,
    onStrokeJoinChange: (stroke: StrokeJoin) -> Unit
) {

    val strokeCapList = persistentListOf(StrokeCap.Round, StrokeCap.Butt, StrokeCap.Square)
    val strokeJoinList = persistentListOf(StrokeJoin.Round, StrokeJoin.Bevel, StrokeJoin.Miter)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Pen Stroke Cap:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall
        )

        strokeCapList.forEach { strokeCap ->

            val isSelected by remember(selectedStrokeCap, strokeCap) {
                derivedStateOf { selectedStrokeCap == strokeCap }
            }

            PenStrokeView(
                stroke = strokeCap,
                isSelected = isSelected,
                onClick = onStrokeCapChange
            )
        }

        HorizontalDivider()

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Pen Stroke Join:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall
        )

        strokeJoinList.forEach { strokeJoin ->

            val isSelected by remember(selectedStrokeJoin, strokeJoin) {
                derivedStateOf { selectedStrokeJoin == strokeJoin }
            }

            PenStrokeView(
                stroke = strokeJoin,
                isSelected = isSelected,
                onClick = onStrokeJoinChange
            )
        }
    }
}

/**
 * A composable function that displays a preview of the canvas paths.
 *
 * This function takes a [CanvasSlateState] object as input, which contains the current state of the
 * canvas.
 * It then renders a preview of the paths on the canvas, allowing the user to see how their changes
 * will look before applying them.
 *
 * @param modifier The modifier to be applied to the canvas.
 * @param state The current state of the canvas.
 */
@Composable
private fun CanvasPathPreview(modifier: Modifier = Modifier, state: CanvasSlateState) {

    val canvasAspectRatio by remember(state.canvasSize) {
        derivedStateOf {
            findAspectRatio(width = state.canvasSize.width, height = state.canvasSize.height)
        }
    }

    Canvas(
        modifier = modifier
            .background(color = state.selectedBackgroundColor)
            .aspectRatio(ratio = canvasAspectRatio)
            .clipToBounds(),
        contentDescription = "Canvas Slate"
    ) {

        state.previewPathList.forEach { pathData ->

            drawSlatePath(slatePath = pathData)
        }
    }
}

/**
 * A composable function that displays a selectable row for a pen stroke cap.
 *
 * @param modifier The modifier to be applied to the row.
 * @param stroke The [StrokeCap] to display.
 * @param isSelected Whether the stroke is currently selected.
 * @param onClick The callback to be invoked when the stroke is clicked.
 */
@Composable
private fun PenStrokeView(
    modifier: Modifier = Modifier,
    stroke: StrokeCap,
    isSelected: Boolean,
    onClick: (stroke: StrokeCap) -> Unit
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.extraSmall)
            .selectable(
                selected = isSelected,
                role = Role.RadioButton,
                onClick = {

                    onClick(stroke)
                }
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(selected = isSelected, onClick = null)

        Text(
            modifier = Modifier.weight(weight = 1.0F),
            text = stroke.toString(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * A composable function that displays a selectable row for a pen stroke join option.
 *
 * @param modifier The modifier to be applied to the row.
 * @param stroke The [StrokeJoin] to display.
 * @param isSelected Whether the stroke join is currently selected.
 * @param onClick The callback to be invoked when the stroke join is clicked.
 */
@Composable
private fun PenStrokeView(
    modifier: Modifier = Modifier,
    stroke: StrokeJoin,
    isSelected: Boolean,
    onClick: (stroke: StrokeJoin) -> Unit
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.extraSmall)
            .selectable(
                selected = isSelected,
                role = Role.RadioButton,
                onClick = {

                    onClick(stroke)
                }
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(selected = isSelected, onClick = null)

        Text(
            modifier = Modifier.weight(weight = 1.0F),
            text = stroke.toString(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Composable function that displays the title of a dialog along with a close button.
 *
 * @param title The title text to be displayed.
 * @param onDismiss A callback function to be invoked when the close button is clicked.
 */
@Composable
private fun DialogTitleView(title: String, onDismiss: () -> Unit) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = Modifier.weight(weight = 1.0F),
            text = title,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        IconButton(
            onClick = onDismiss
        ) {

            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Dialog Close"
            )
        }
    }
}

/**
 * Composable function to display a confirmation button for a dialog.
 * This button, when clicked, sets the target state of the provided [dialogVisibleState] to false,
 * effectively dismissing the dialog.
 * It displays a "Done" icon and text.
 *
 * @param dialogVisibleState The [MutableTransitionState] controlling the visibility of the dialog.
 */
@Composable
private fun DialogConfirmButton(dialogVisibleState: MutableTransitionState<Boolean>) {

    Button(
        onClick = {

            dialogVisibleState.targetState = false
        }
    ) {

        Icon(
            modifier = Modifier.size(size = 18.dp),
            imageVector = Icons.Filled.Done,
            contentDescription = "Done"
        )

        Spacer(modifier = Modifier.width(width = 2.dp))

        Text(text = "Done")
    }
}