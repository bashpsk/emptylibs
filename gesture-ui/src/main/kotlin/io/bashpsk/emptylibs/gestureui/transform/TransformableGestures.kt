package io.bashpsk.emptylibs.gestureui.transform

import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

/**
 * A modifier that enables transform gestures, such as zoom, pan, and rotation, on a composable.
 *
 * This modifier should be applied to the composable that you want to make transformable.
 *
 * @param state The state object that will be updated when a transform gesture is performed.
 * @param onClick A lambda that will be called when the composable is clicked.
 * @param onLongClick A lambda that will be called when the composable is long-clicked.
 */
fun Modifier.transformableGestures(
    state: TransformableGesturesState,
    onClick: (offset: Offset) -> Unit = {},
    onLongClick: (offset: Offset) -> Unit = {},
): Modifier {

    return this
        .transformable(state = state.transformableState)
        .then(
            TransformableGesturesElement(
                state = state,
                onClick = onClick,
                onLongClick = onLongClick
            )
        )
}