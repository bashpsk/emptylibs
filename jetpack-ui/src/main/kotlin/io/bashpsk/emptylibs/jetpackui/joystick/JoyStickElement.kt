package io.bashpsk.emptylibs.jetpackui.joystick

import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

internal class JoyStickElement(
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

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other !is JoyStickElement) return false
        if (state != other.state) return false
        if (onUp != other.onUp) return false

        return true
    }

    override fun hashCode(): Int {

        var result = state.hashCode()
        result = 31 * result + onUp.hashCode()
        return result
    }
}