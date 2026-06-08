package io.bashpsk.emptylibs.gestureui.transform

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

internal class TransformableGesturesElement(
    private val state: TransformableGesturesState,
    private val onClick: (position: Offset) -> Unit,
    private val onLongClick: (position: Offset) -> Unit,
) : ModifierNodeElement<TransformableGesturesNode>() {

    override fun create(): TransformableGesturesNode {

        return TransformableGesturesNode(
            state = state,
            onClick = onClick,
            onLongClick = onLongClick
        )
    }

    override fun update(node: TransformableGesturesNode) {

        node.update(state = state, onClick = onClick, onLongClick = onLongClick)
    }

    override fun InspectorInfo.inspectableProperties() {

        name = "transformableGestures"
        properties["state"] = state
        properties["onClick"] = onClick
        properties["onLongClick"] = onLongClick
    }

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other !is TransformableGesturesElement) return false
        if (state != other.state) return false
        if (onClick != other.onClick) return false
        if (onLongClick != other.onLongClick) return false

        return true
    }

    override fun hashCode(): Int {

        var result = state.hashCode()
        result = 31 * result + onClick.hashCode()
        result = 31 * result + onLongClick.hashCode()
        return result
    }
}