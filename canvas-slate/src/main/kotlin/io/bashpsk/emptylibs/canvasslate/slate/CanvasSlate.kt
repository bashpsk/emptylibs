package io.bashpsk.emptylibs.canvasslate.slate

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.kolorpicker.color.ColorPickerDialog
import io.bashpsk.emptylibs.kolorpicker.color.rememberColorPickerState

@Composable
fun CanvasSlate(
    modifier: Modifier = Modifier,
    state: CanvasSlateState = rememberCanvasSlateState(),
    onDoneClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {

    val colorPickerState = rememberColorPickerState(enableAlphaPanel = true)
    val backgroundColorPickerDialog = remember { MutableTransitionState(false) }
    val foregroundColorPickerDialog = remember { MutableTransitionState(false) }

    ColorPickerDialog(
        dialogVisibleState = backgroundColorPickerDialog,
        state = colorPickerState,
        onSelectedColor = { color ->

            state.updateBackgroundColor(color = color)
        }
    )

    ColorPickerDialog(
        dialogVisibleState = foregroundColorPickerDialog,
        state = colorPickerState,
        onSelectedColor = { color ->

            state.updatePenColor(color = color)
        }
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {

        CanvasSlateTopBar(
            modifier = modifier,
            state = state,
            backgroundColorPickerDialog = backgroundColorPickerDialog,
            foregroundColorPickerDialog = foregroundColorPickerDialog,
            onDoneClick = onDoneClick,
            onNavigateBack = onNavigateBack
        )

        CanvasSlateUI(
            modifier = modifier.weight(weight = 1.0F),
            state = state
        )
    }
}