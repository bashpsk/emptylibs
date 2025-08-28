package io.bashpsk.emptylibs.imageedit.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.formatter.format.EmptyFormat
import io.bashpsk.emptylibs.kolorpicker.color.ColorPickerDialog
import io.bashpsk.emptylibs.kolorpicker.color.ColorPreview
import io.bashpsk.emptylibs.kolorpicker.color.rememberColorPickerState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImageEditInputSheet(
    editToolInputSheetState: SheetState,
    state: ImageEditState
) {

    val sheetCoroutineScope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = editToolInputSheetState.isVisible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {

        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            sheetState = editToolInputSheetState,
            onDismissRequest = {

                sheetCoroutineScope.launch { editToolInputSheetState.hide() }
            },
            shape = MaterialTheme.shapes.small
        ) {

            when (state.currentImageEditItem) {

                is ImageEditItems.BrushItem -> InputEditBrush(state = state)
                is ImageEditItems.EraseItem -> InputEditErase(state = state)
                is ImageEditItems.ImageItem -> InputEditImage(state = state)
                is ImageEditItems.ShapeItem -> InputEditShape(state = state)
                is ImageEditItems.TextItem -> InputEditText(state = state)
                null -> {}
            }
        }
    }
}

@Composable
private fun InputEditBrush(state: ImageEditState) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        item {

            ColorSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                label = "Color:",
                color = state.brushEditInput.color,
                onColorChange = { newColor ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(color = newColor)
                    }
                }
            )
        }

        item { HorizontalDivider() }

        item {

            SmoothnessSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                smoothness = state.brushEditInput.smoothness,
                onSmoothnessChange = { newSmoothness ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(smoothness = newSmoothness)
                    }
                }
            )
        }

        item {

            ThicknessSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                thickness = state.brushEditInput.thickness,
                onThicknessChange = { newThickness ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(thickness = newThickness)
                    }
                }
            )
        }

        item {

            MiterSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                miter = state.brushEditInput.miter,
                onMiterChange = { newMiter ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(miter = newMiter)
                    }
                }
            )
        }

        item { HorizontalDivider() }

        item {

            DashPathSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                intervalOff = state.brushEditInput.dashIntervalOff,
                intervalOn = state.brushEditInput.dashIntervalOn,
                intervalPhase = state.brushEditInput.dashPhase,
                onIntervalOffChange = { newInterval ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(dashIntervalOff = newInterval)
                    }
                },
                onIntervalOnChange = { newInterval ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(dashIntervalOn = newInterval)
                    }
                },
                onIntervalPhaseChange = { newInterval ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(dashPhase = newInterval)
                    }
                }
            )
        }

        item { HorizontalDivider() }

        item {

            StrokeSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                selectedStrokeCap = state.brushEditInput.strokeCap,
                selectedStrokeJoin = state.brushEditInput.strokeJoin,
                onStrokeCapChange = { newStrokeCap ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(strokeCap = newStrokeCap)
                    }
                },
                onStrokeJoinChange = { newStrokeJoin ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(strokeJoin = newStrokeJoin)
                    }
                }
            )
        }
    }
}

@Composable
private fun InputEditErase(state: ImageEditState) {

}

@Composable
private fun InputEditImage(state: ImageEditState) {

}

@Composable
private fun InputEditShape(state: ImageEditState) {

}

@Composable
private fun InputEditText(state: ImageEditState) {

}

@Composable
private fun PositionSelectionView(
    modifier: Modifier = Modifier,
    position: Offset,
    onPositionChange: (newPosition: Offset) -> Unit
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Position:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier.weight(weight = 1.0F),
                text = "X",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                modifier = Modifier.weight(weight = 1.0F),
                text = "Y",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            NumberInputField(
                modifier = Modifier.weight(weight = 1.0F),
                label = "X",
                value = position.x.toString(),
                onValueChange = { newValue ->

                    onPositionChange(position.copy(x = newValue.toFloatOrNull() ?: 0.0F))
                }
            )

            NumberInputField(
                modifier = Modifier.weight(weight = 1.0F),
                label = "Y",
                value = position.y.toString(),
                onValueChange = { newValue ->

                    onPositionChange(position.copy(y = newValue.toFloatOrNull() ?: 0.0F))
                }
            )
        }
    }
}

@Composable
private fun SizeSelectionView(
    modifier: Modifier = Modifier,
    size: Size,
    onSizeChange: (newSize: Size) -> Unit
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Size:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier.weight(weight = 1.0F),
                text = "Width",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                modifier = Modifier.weight(weight = 1.0F),
                text = "Height",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            NumberInputField(
                modifier = Modifier.weight(weight = 1.0F),
                label = "Width",
                value = size.width.toString(),
                onValueChange = { newValue ->

                    onSizeChange(size.copy(width = newValue.toFloatOrNull() ?: 0.0F))
                }
            )

            NumberInputField(
                modifier = Modifier.weight(weight = 1.0F),
                label = "Height",
                value = size.height.toString(),
                onValueChange = { newValue ->

                    onSizeChange(size.copy(height = newValue.toFloatOrNull() ?: 0.0F))
                }
            )
        }
    }
}

@Composable
private fun ColorSelectionView(
    modifier: Modifier = Modifier,
    label: String,
    color: Color,
    onColorChange: (color: Color) -> Unit
) {

    val colorPickerState = rememberColorPickerState(initialColor = color)
    val colorPickerDialogState = remember { MutableTransitionState(false) }

    ColorPickerDialog(
        dialogVisibleState = colorPickerDialogState,
        state = colorPickerState,
        enableCopyButtons = true,
        onSelectedColor = onColorChange
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = label,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        ColorPreview(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.extraSmall)
                .clickable(
                    role = Role.Button,
                    onClick = { colorPickerDialogState.targetState = true }
                ),
            color = color
        )
    }
}

@Composable
private fun SmoothnessSelectionView(
    modifier: Modifier = Modifier,
    smoothness: Int,
    onSmoothnessChange: (smoothness: Int) -> Unit
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Smoothness - $smoothness",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Slider(
            value = smoothness.toFloat(),
            valueRange = 0.0F..10.0F,
            steps = 9,
            onValueChange = { newValue ->

                onSmoothnessChange(newValue.toInt())
            }
        )
    }
}

@Composable
private fun ThicknessSelectionView(
    modifier: Modifier = Modifier,
    thickness: Float,
    onThicknessChange: (thickness: Float) -> Unit
) {

    val selectedThickness by remember(thickness) {
        derivedStateOf {
            "Thickness - ${EmptyFormat.toRoundedDecimal(decimal = thickness, fraction = 1)} Px"
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = selectedThickness,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Slider(
            value = thickness,
            valueRange = 0.1F..64.0F,
            onValueChange = onThicknessChange
        )
    }
}

@Composable
private fun MiterSelectionView(
    modifier: Modifier = Modifier,
    miter: Float,
    onMiterChange: (miter: Float) -> Unit
) {

    val selectedMiter by remember(miter) {
        derivedStateOf { "Miter - ${EmptyFormat.toRoundedDecimal(decimal = miter, fraction = 1)}" }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = selectedMiter,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Slider(
            value = miter,
            valueRange = 0.3F..20.0F,
            onValueChange = onMiterChange
        )
    }
}

@Composable
private fun StrokeSelectionView(
    modifier: Modifier = Modifier,
    selectedStrokeCap: StrokeCap,
    selectedStrokeJoin: StrokeJoin,
    onStrokeCapChange: (stroke: StrokeCap) -> Unit,
    onStrokeJoinChange: (stroke: StrokeJoin) -> Unit
) {

    val strokeCapList = persistentListOf(StrokeCap.Round, StrokeCap.Butt, StrokeCap.Square)
    val strokeJoinList = persistentListOf(StrokeJoin.Round, StrokeJoin.Bevel, StrokeJoin.Miter)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Stroke Cap:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        strokeCapList.forEach { strokeCap ->

            val isSelected by remember(selectedStrokeCap, strokeCap) {
                derivedStateOf { selectedStrokeCap == strokeCap }
            }

            StrokeView(
                stroke = strokeCap,
                isSelected = isSelected,
                onClick = onStrokeCapChange
            )
        }

        HorizontalDivider()

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Stroke Join:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        strokeJoinList.forEach { strokeJoin ->

            val isSelected by remember(selectedStrokeJoin, strokeJoin) {
                derivedStateOf { selectedStrokeJoin == strokeJoin }
            }

            StrokeView(
                stroke = strokeJoin,
                isSelected = isSelected,
                onClick = onStrokeJoinChange
            )
        }
    }
}

@Composable
private fun DashPathSelectionView(
    modifier: Modifier = Modifier,
    intervalOff: Float,
    intervalOn: Float,
    intervalPhase: Float,
    onIntervalOffChange: (newInterval: Float) -> Unit,
    onIntervalOnChange: (newInterval: Float) -> Unit,
    onIntervalPhaseChange: (newInterval: Float) -> Unit
) {

    val intervalOffLabel by remember(intervalOff) {
        derivedStateOf {
            "Interval OFF Size - ${
                EmptyFormat.toRoundedDecimal(decimal = intervalOff, fraction = 1)
            } Px"
        }
    }

    val intervalOnLabel by remember(intervalOn) {
        derivedStateOf {
            "Interval ON Size - ${
                EmptyFormat.toRoundedDecimal(decimal = intervalOn, fraction = 1)
            } Px"
        }
    }

    val intervalPhaseLabel by remember(intervalPhase) {
        derivedStateOf {
            "Interval Phase Size - ${
                EmptyFormat.toRoundedDecimal(decimal = intervalPhase, fraction = 1)
            } Px"
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Dash Effect:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = intervalOffLabel,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium,
        )

        Slider(
            value = intervalOff,
            valueRange = 0.0F..100.0F,
            onValueChange = onIntervalOffChange
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = intervalOnLabel,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium,
        )

        Slider(
            value = intervalOn,
            valueRange = 0.0F..100.0F,
            onValueChange = onIntervalOnChange
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = intervalPhaseLabel,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium,
        )

        Slider(
            value = intervalPhase,
            valueRange = 0.0F..100.0F,
            onValueChange = onIntervalPhaseChange
        )
    }
}

@Composable
private fun StrokeView(
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
private fun StrokeView(
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
private fun NumberInputField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (newValue: String) -> Unit
) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {

            Text(text = label)
        },
        placeholder = {

            Text(text = label)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        keyboardActions = KeyboardActions(
            onDone = {

                focusManager.clearFocus()
            }
        )
    )
}