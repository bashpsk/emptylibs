package io.bashpsk.emptylibs.gestureui.transform

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize

internal class TransformableGesturesNode(
    private var state: TransformableGesturesState,
    private var onClick: (position: Offset) -> Unit,
    private var onLongClick: (position: Offset) -> Unit
) : DelegatingNode(), LayoutAwareModifierNode, PointerInputModifierNode {

    private val touchPointerInputNode = delegate(
        SuspendingPointerInputModifierNode {

            awaitEachGesture {

                do {

                    val event = awaitPointerEvent()

                    state.touchCount = event.changes.size
                } while (event.changes.any { change -> change.pressed })
            }
        }
    )

    private val tapPointerInputNode = delegate(
        SuspendingPointerInputModifierNode {

            detectTapGestures(
                onDoubleTap = state::onDoubleTap,
                onTap = onClick,
                onLongPress = onLongClick
            )
        }
    )

    fun update(
        state: TransformableGesturesState,
        onClick: (position: Offset) -> Unit,
        onLongClick: (position: Offset) -> Unit
    ) {

        val oldState = this.state

        this.state = state
        this.onClick = onClick
        this.onLongClick = onLongClick

        if (oldState.enableDoubleTapZoom != state.enableDoubleTapZoom ||
            oldState.boundSize != state.boundSize
        ) tapPointerInputNode.resetPointerInputHandler()
    }

    override fun onRemeasured(size: IntSize) {

        state.boundSize = size.toSize()
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {

        touchPointerInputNode.onPointerEvent(
            pointerEvent = pointerEvent,
            pass = pass,
            bounds = bounds
        )

        tapPointerInputNode.onPointerEvent(
            pointerEvent = pointerEvent,
            pass = pass,
            bounds = bounds
        )
    }

    override fun onCancelPointerInput() {

        touchPointerInputNode.onCancelPointerInput()
        tapPointerInputNode.onCancelPointerInput()
    }
}