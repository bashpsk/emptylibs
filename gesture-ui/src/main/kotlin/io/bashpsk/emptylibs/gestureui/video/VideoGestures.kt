package io.bashpsk.emptylibs.gestureui.video

import androidx.compose.ui.Modifier

fun Modifier.videoGestures(
    state: VideoGestureBoxState,
    onTapChanges: (changes: TapChanges) -> Unit,
    onDragChanges: (changes: DragChanges) -> Unit
): Modifier {

    return this then VideoGesturesElement(
        state = state,
        onTapChanges = onTapChanges,
        onDragChanges = onDragChanges
    )
}