package io.bashpsk.emptylibs.gestureui.video

import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

internal data class VideoGesturesElement(
    val state: VideoGestureBoxState,
    val onTapChanges: (changes: TapChanges) -> Unit,
    val onDragChanges: (changes: DragChanges) -> Unit
) : ModifierNodeElement<VideoGesturesNode>() {

    override fun create(): VideoGesturesNode {

        return VideoGesturesNode(
            state = state,
            onTapChanges = onTapChanges,
            onDragChanges = onDragChanges
        )
    }

    override fun update(node: VideoGesturesNode) {

        node.update(state = state, onTapChanges = onTapChanges, onDragChanges = onDragChanges)
    }

    override fun InspectorInfo.inspectableProperties() {

        name = "videoGestures"
        properties["state"] = state
        properties["onTapChanges"] = onTapChanges
        properties["onDragChanges"] = onDragChanges
    }
}