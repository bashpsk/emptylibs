package io.bashpsk.emptylibs.canvasslate.gesture

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import io.bashpsk.emptylibs.canvasslate.slate.CanvasSlateState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
internal class SlateGesturesNode(
    private var state: CanvasSlateState,
    private var pathEditSheetState: SheetState,
) : DelegatingNode(), LayoutAwareModifierNode, PointerInputModifierNode {

    private val tapNode = delegate(
        SuspendingPointerInputModifierNode {

            detectTapGestures(
                onTap = { position ->

                    state.apply {

                        if (onEditPathData(position = position) == true) {

                            coroutineScope.launch { pathEditSheetState.show() }
                            return@detectTapGestures
                        }

                        onPathStart()
                        onPathDraw(position = position)
                        onPathEnd()
                    }
                }
            )
        }
    )

    private val dragNode = delegate(
        SuspendingPointerInputModifierNode {

            detectDragGestures(
                onDragStart = { state.onPathStart() },
                onDragEnd = state::onPathEnd,
                onDragCancel = state::onPathEnd,
                onDrag = { change, _ ->

                    change.consume()
                    state.onPathDraw(position = change.position)
                }
            )
        }
    )

    fun update(state: CanvasSlateState, pathEditSheetState: SheetState) {

        val oldState = this.state
        val oldSheetState = this.pathEditSheetState

        this.state = state
        this.pathEditSheetState = pathEditSheetState

        if (oldState != state || oldSheetState != pathEditSheetState) {

            tapNode.resetPointerInputHandler()
            dragNode.resetPointerInputHandler()
        }
    }

    override fun onRemeasured(size: IntSize) {

        state.canvasSize = size.toSize()
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {

        tapNode.onPointerEvent(pointerEvent = pointerEvent, pass = pass, bounds = bounds)
        dragNode.onPointerEvent(pointerEvent = pointerEvent, pass = pass, bounds = bounds)
    }

    override fun onCancelPointerInput() {

        tapNode.onCancelPointerInput()
        dragNode.onCancelPointerInput()
    }
}