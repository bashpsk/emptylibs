package io.bashpsk.emptylibs.jetpackui.joystick

import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

internal data class JoyStickElement(
    private val state: JoyStickState,
    private val onUp: () -> Unit,
) : ModifierNodeElement<JoyStickNode>() {

    override fun create(): JoyStickNode {

        return JoyStickNode(state = state, onUp = onUp)
    }

    override fun update(node: JoyStickNode) {

        node.update(state = state, onUp = onUp)
    }

    override fun InspectorInfo.inspectableProperties() {

        name = "joyStickGestures"
        properties["state"] = state
        properties["onUp"] = onUp
    }
}