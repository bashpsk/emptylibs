package io.bashpsk.emptylibs.canvasslate.gesture

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.Modifier
import io.bashpsk.emptylibs.canvasslate.slate.CanvasSlateState

@OptIn(ExperimentalMaterial3Api::class)
internal fun Modifier.canvasSlateGestures(
    state: CanvasSlateState,
    pathEditSheetState: SheetState
): Modifier {

    return this then SlateGesturesElement(state = state, pathEditSheetState = pathEditSheetState)
}