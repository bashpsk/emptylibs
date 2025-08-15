package io.bashpsk.emptylibs.canvasslate.slate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.bashpsk.emptylibs.formatter.format.EmptyFormat
import kotlinx.collections.immutable.persistentListOf

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

                PenThicknessSelectionView(state = state)
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

                PenStrokeSelectionView(state = state)
            },
            confirmButton = {

                DialogConfirmButton(dialogVisibleState = dialogVisibleState)
            }
        )
    }
}

@Composable
private fun PenThicknessSelectionView(state: CanvasSlateState) {

    val selectedThickness by remember(state.penThickness) {
        derivedStateOf {
            "Selected Thickness - ${
                EmptyFormat.toRoundedDecimal(
                    decimal = state.penThickness.value,
                    fraction = 1
                )
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
            value = state.penThickness.value,
            valueRange = 0.3.dp.value..40.dp.value,
            onValueChange = { newValue ->

                state.updatePenThickness(newValue.dp)
            }
        )
    }
}

@Composable
private fun PenStrokeSelectionView(state: CanvasSlateState) {

    val strokeCapList = persistentListOf(StrokeCap.Butt, StrokeCap.Round, StrokeCap.Square)
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

            val isSelected by remember(state, strokeCap) {
                derivedStateOf { state.selectedStrokeCap == strokeCap }
            }

            PenStrokeView(
                stroke = strokeCap,
                isSelected = isSelected,
                onClick = state::updateStrokeCap
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

            val isSelected by remember(state, strokeJoin) {
                derivedStateOf { state.selectedStrokeJoin == strokeJoin }
            }

            PenStrokeView(
                stroke = strokeJoin,
                isSelected = isSelected,
                onClick = state::updateStrokeJoin
            )
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