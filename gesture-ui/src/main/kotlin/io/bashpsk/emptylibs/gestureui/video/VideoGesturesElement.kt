package io.bashpsk.emptylibs.gestureui.video

import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

internal class VideoGesturesElement(
    private val state: VideoGestureBoxState,
    private val onTapChanges: (changes: TapChanges) -> Unit,
    private val onDragChanges: (changes: DragChanges) -> Unit,
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

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other !is VideoGesturesElement) return false
        if (state != other.state) return false
        if (onTapChanges != other.onTapChanges) return false
        if (onDragChanges != other.onDragChanges) return false

        return true
    }

    override fun hashCode(): Int {

        var result = state.hashCode()
        result = 31 * result + onTapChanges.hashCode()
        result = 31 * result + onDragChanges.hashCode()
        return result
    }
}