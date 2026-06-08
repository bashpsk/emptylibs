package io.bashpsk.emptylibs.jetpackui.joystick

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A customizable joystick component for touch-based user interfaces.
 *
 * @param modifier The [Modifier] to be applied to the joystick's layout and behavior.
 * @param state The [JoyStickState] object that manages the joystick's internal logic, radius, and
 * movement.
 * @param colors The [JoyStickColors] defining the appearance of the thumb and the background track.
 * @param onUp Callback invoked when the user releases the joystick
 * (on tap release, drag end, or drag cancel).
 */
@Composable
fun JoyStick(
    modifier: Modifier = Modifier,
    state: JoyStickState = rememberJoyStickState(),
    colors: JoyStickColors = JoyStickDefaults.colors(),
    onUp: () -> Unit = { state.onUp() }
) {

    Canvas(
        modifier = modifier
            .aspectRatio(ratio = 1F)
            .joyStickGestures(state = state, onUp = onUp),
        contentDescription = "Joy Stick"
    ) {

        drawJoyStick(
            colors = colors,
            properties = state.properties,
            thumbPosition = state.thumbPosition,
            isPressed = state.isPressed
        )
    }
}