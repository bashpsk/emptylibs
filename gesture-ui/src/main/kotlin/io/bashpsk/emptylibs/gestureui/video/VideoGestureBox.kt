package io.bashpsk.emptylibs.gestureui.video

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable

/**
 * A Composable function that provides a box with gesture detection capabilities,
 * specifically designed for video player-like interactions.
 *
 * It detects various tap and drag gestures and provides callbacks for them.
 * The behavior of these gestures can be customized through the [VideoGestureBoxState.config]
 * parameter, accessible via the [state] parameter.
 *
 * This Composable uses [BoxWithConstraints] to get the available screen space
 * and adapts its gesture detection logic accordingly.
 *
 * It handles:
 * - Single taps.
 * - Double taps (can be configured to trigger backward/forward actions based on tap location).
 * - Drag gestures in different regions of the screen:
 *     - Horizontal drag at the top.
 *     - Horizontal drag at the bottom.
 *     - Vertical drag on the left side (commonly used for brightness control).
 *     - Vertical drag on the right side (commonly used for volume control).
 * - Two-finger pinch-to-zoom and pan gestures (if enabled in [VideoGestureBoxState.config]).
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state An instance of [VideoGestureBoxState] that holds the configuration and current state
 * of the gestures. Defaults to a remembered [VideoGestureBoxState] instance.
 * @param onTapChanges A lambda that is invoked when a tap gesture occurs.
 * It receives a [TapChanges] sealed class instance indicating the type of tap.
 * @param onDragChanges A lambda that is invoked during drag gestures.
 * It receives a [DragChanges] sealed class instance indicating the state and type of drag.
 * @param content The content to be placed inside the gesture-detecting box.
 * This is a composable lambda that receives a [BoxWithConstraintsScope].
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VideoGestureBox(
    modifier: Modifier = Modifier,
    state: VideoGestureBoxState = rememberVideoGestureBoxState(),
    onTapChanges: (changes: TapChanges) -> Unit = {},
    onDragChanges: (changes: DragChanges) -> Unit = {},
    content: @Composable @UiComposable BoxWithConstraintsScope.() -> Unit
) {

    BoxWithConstraints(
        modifier = modifier
            .transformable(state = state.transformableState)
            .videoGestures(
                state = state,
                onTapChanges = onTapChanges,
                onDragChanges = onDragChanges
            ),
        contentAlignment = Alignment.Center
    ) {

        content()
    }
}