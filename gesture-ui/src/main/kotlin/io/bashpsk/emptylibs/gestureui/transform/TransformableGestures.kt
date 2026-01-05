package io.bashpsk.emptylibs.gestureui.transform

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize

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
): Modifier = composed {

    val isTransforming by remember(state.touchCount, state.zoom, state.rotation) {
        derivedStateOf { state.hasTransform() }
    }

    val transformableState = rememberTransformableState { zoomChange, panChange, rotationChange ->

        if (isTransforming.not()) return@rememberTransformableState

        state.onTransformation(
            zoomChange = zoomChange,
            panChange = panChange,
            rotationChange = rotationChange
        )
    }

    val touchPointerInput = Modifier.pointerInput(Unit) {

        awaitEachGesture {

            do {

                val event = awaitPointerEvent()

                state.touchCount = event.changes.size
            } while (event.changes.any { change -> change.pressed })
        }
    }

    val tapPointerInput = Modifier.pointerInput(state.enableDoubleTapZoom, state.boundSize) {

        detectTapGestures(
            onDoubleTap = state::onDoubleTap,
            onTap = onClick,
            onLongPress = onLongClick
        )
    }

    this
        .onSizeChanged { size -> state.boundSize = size.toSize() }
        .then(touchPointerInput)
        .then(tapPointerInput)
        .transformable(state = transformableState)
}