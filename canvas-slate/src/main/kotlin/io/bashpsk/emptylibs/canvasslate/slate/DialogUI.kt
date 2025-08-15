package io.bashpsk.emptylibs.canvasslate.slate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.bashpsk.emptylibs.formatter.format.EmptyFormat
import io.bashpsk.emptylibs.kolorpicker.color.ColorPickerDialog
import io.bashpsk.emptylibs.kolorpicker.color.rememberColorPickerState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PathEditBottomSheet(
    pathEditSheetState: SheetState,
    state: CanvasSlateState
) {

    val coroutineScope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = pathEditSheetState.isVisible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
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

                PenThicknessSelectionView(
                    penThickness = state.penThickness,
                    onThicknessChange = state::updatePenThickness
                )
            },
            confirmButton = {

                DialogConfirmButton(dialogVisibleState = dialogVisibleState)
            }
        )
    }
}

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

@Composable
private fun PathEditView(state: CanvasSlateState, onDismiss: () -> Unit) {

    val isUndoButtonEnable by remember(state.allPathList, state.previewPathList) {
        derivedStateOf { state.previewPathList.containsAll(elements = state.allPathList).not() }
    }

    val isDeleteButtonEnable by remember(state.editPathData) {
        derivedStateOf { state.editPathData != null }
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

            state.editPathData?.let { pathData ->

                PenThicknessSelectionView(
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

            state.editPathData?.let { pathData ->

                PenStrokeSelectionView(
                    selectedStrokeCap = pathData.strokeCap,
                    selectedStrokeJoin = pathData.strokeJoin,
                    onStrokeCapChange = { strokeCap ->

                        state.apply {

                            val newPath = pathData.copy(strokeCap = strokeCap)

                            onUpdateEditPath(path = newPath)
                            addPathInPreview(path = newPath)
                        }
                    },
                    onStrokeJoinChange = { strokeJoin ->

                        state.apply {

                            val newPath = pathData.copy(strokeJoin = strokeJoin)

                            onUpdateEditPath(path = newPath)
                            addPathInPreview(path = newPath)
                        }
                    }
                )
            }
        }

        item {

            state.editPathData?.let { pathData ->

                PenColorSelectionView(
                    modifier = Modifier.fillParentMaxWidth(),
                    color = pathData.color,
                    onSelectedColor = { color ->

                        state.apply {

                            val newPath = pathData.copy(color = color)

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

@Composable
private fun PenColorSelectionView(
    modifier: Modifier = Modifier,
    color: Color,
    onSelectedColor: (color: Color) -> Unit
) {

    val colorPickerState = rememberColorPickerState(
        initialColor = color,
        enableAlphaPanel = true
    )

    val colorPickerDialog = remember { MutableTransitionState(false) }

    val colorBoxShape = MaterialTheme.shapes.extraSmall

    ColorPickerDialog(
        dialogVisibleState = colorPickerDialog,
        state = colorPickerState,
        onSelectedColor = onSelectedColor
    )

    Row(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.extraSmall)
            .clickable(role = Role.Button, onClick = { colorPickerDialog.targetState = true })
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

@Composable
private fun PenThicknessSelectionView(
    penThickness: Dp,
    onThicknessChange: (thickness: Dp) -> Unit,
) {

    val selectedThickness by remember(penThickness) {
        derivedStateOf {
            "Selected Thickness - ${
                EmptyFormat.toRoundedDecimal(decimal = penThickness.value, fraction = 1)
            } Dp"
        }
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
            value = penThickness.value,
            valueRange = 0.3.dp.value..40.dp.value,
            onValueChange = { newValue ->

                onThicknessChange(newValue.dp)
            }
        )
    }
}

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

@Composable
private fun CanvasPathPreview(modifier: Modifier = Modifier, state: CanvasSlateState) {

    val canvasAspectRatio by remember(state.canvasSize) {
        derivedStateOf {
            EmptyFormat.findAspectRatio(
                width = state.canvasSize.width.toInt(),
                height = state.canvasSize.height.toInt()
            )
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

            drawPathData(pathData = pathData)
        }
    }
}

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