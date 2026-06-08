package io.bashpsk.emptylibs.canvasslate.gesture

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import io.bashpsk.emptylibs.canvasslate.slate.CanvasSlateState

@OptIn(ExperimentalMaterial3Api::class)
internal class SlateGesturesElement(
    private val state: CanvasSlateState,
    private val pathEditSheetState: SheetState,
) : ModifierNodeElement<SlateGesturesNode>() {

    override fun create(): SlateGesturesNode {

        return SlateGesturesNode(state = state, pathEditSheetState = pathEditSheetState)
    }

    override fun update(node: SlateGesturesNode) {

        node.update(state = state, pathEditSheetState = pathEditSheetState)
    }

    override fun InspectorInfo.inspectableProperties() {

        name = "slateGestures"
        properties["state"] = state
        properties["pathEditSheetState"] = pathEditSheetState
    }

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other !is SlateGesturesElement) return false
        if (state != other.state) return false
        if (pathEditSheetState != other.pathEditSheetState) return false

        return true
    }

    override fun hashCode(): Int {

        var result = state.hashCode()
        result = 31 * result + pathEditSheetState.hashCode()
        return result
    }
}