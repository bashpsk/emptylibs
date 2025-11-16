package io.bashpsk.emptylibs.canvasslate.slate

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.kolorpicker.color.KolorPickerDialog
import io.bashpsk.emptylibs.kolorpicker.color.rememberKolorPickerState

/**
 * A composable function that provides a canvas slate for drawing and editing paths.
 *
 * This function integrates various components like color pickers, pen stroke/thickness dialogs,
 * and a path edit bottom sheet to offer a comprehensive drawing experience.
 *
 * @param modifier The modifier to be applied to the CanvasSlate.
 * @param state The state object that manages the canvas, drawing tools, and path data.
 * Defaults to a new `CanvasSlateState` instance.
 * @param onDoneClick A lambda function to be invoked when the "Done" action is triggered,
 * typically from the top bar.
 * @param onNavigateBack A lambda function to be invoked when the "Navigate Back" action is
 * triggered, typically from the top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasSlate(
    modifier: Modifier = Modifier,
    state: CanvasSlateState = rememberCanvasSlateState(),
    onDoneClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {

    val pathEditSheetState = rememberModalBottomSheetState()
    val colorPickerState = rememberKolorPickerState()
    val backgroundColorPickerDialog = remember { MutableTransitionState(false) }
    val foregroundColorPickerDialog = remember { MutableTransitionState(false) }
    val penStrokeDialogVisibleState = remember { MutableTransitionState(false) }
    val penThicknessDialogVisibleState = remember { MutableTransitionState(false) }

    DisposableEffect(Unit) {

        onDispose { state.clearState() }
    }

    KolorPickerDialog(
        dialogVisibleState = backgroundColorPickerDialog,
        state = colorPickerState,
        enableAlphaPanel = true,
        onSelectedColor = { color ->

            state.updateBackgroundColor(color = color)
        }
    )

    KolorPickerDialog(
        dialogVisibleState = foregroundColorPickerDialog,
        state = colorPickerState,
        enableAlphaPanel = true,
        onSelectedColor = { color ->

            state.updateBrushColor(color = color)
        }
    )

    PenStrokeDialog(dialogVisibleState = penStrokeDialogVisibleState, state = state)

    PenThicknessDialog(dialogVisibleState = penThicknessDialogVisibleState, state = state)

    PathEditBottomSheet(pathEditSheetState = pathEditSheetState, state = state)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {

        CanvasSlateTopBar(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            backgroundColorPickerDialog = backgroundColorPickerDialog,
            foregroundColorPickerDialog = foregroundColorPickerDialog,
            penStrokeDialogVisibleState = penStrokeDialogVisibleState,
            penThicknessDialogVisibleState = penThicknessDialogVisibleState,
            onDoneClick = onDoneClick,
            onNavigateBack = onNavigateBack
        )

        CanvasSlateUI(
            modifier = Modifier.weight(weight = 1.0F),
            state = state,
            pathEditSheetState = pathEditSheetState
        )
    }
}