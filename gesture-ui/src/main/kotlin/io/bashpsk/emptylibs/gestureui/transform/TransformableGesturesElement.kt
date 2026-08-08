package io.bashpsk.emptylibs.gestureui.transform

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

internal data class TransformableGesturesElement(
    val state: TransformableGesturesState,
    val onClick: (position: Offset) -> Unit,
    val onLongClick: (position: Offset) -> Unit
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
}