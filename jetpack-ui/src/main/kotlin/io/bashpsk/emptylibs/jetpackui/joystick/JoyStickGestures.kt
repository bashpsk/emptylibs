package io.bashpsk.emptylibs.jetpackui.joystick

import androidx.compose.ui.Modifier

internal fun Modifier.joyStickGestures(state: JoyStickState, onUp: () -> Unit): Modifier {

    return this then JoyStickElement(state = state, onUp = onUp)
}