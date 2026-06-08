package io.bashpsk.emptylibs.jetpackui.joystick

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.unit.IntSize

internal class JoyStickNode(
    private var state: JoyStickState,
    private var onUp: () -> Unit,
) : DelegatingNode(), LayoutAwareModifierNode, PointerInputModifierNode {

    private val tapNode = delegate(
        SuspendingPointerInputModifierNode {
            detectTapGestures(
                onPress = { position ->

                    state.onDown(
                        newPosition = position - Offset(
                            x = state.boundRadius,
                            y = state.boundRadius
                        )
                    )

                    awaitRelease()
                    onUp()
                }
            )
        }
    )

    private val dragNode = delegate(
        SuspendingPointerInputModifierNode {

            detectDragGestures(
                onDragStart = { position ->

                    state.onDown(
                        newPosition = position - Offset(
                            x = state.boundRadius,
                            y = state.boundRadius
                        )
                    )
                },
                onDrag = { change, dragAmount ->

                    change.consume()
                    state.onDrag(amount = dragAmount)
                },
                onDragEnd = onUp,
                onDragCancel = onUp
            )
        }
    )

    fun update(state: JoyStickState, onUp: () -> Unit) {

        this.state = state
        this.onUp = onUp
        tapNode.resetPointerInputHandler()
        dragNode.resetPointerInputHandler()
    }

    override fun onRemeasured(size: IntSize) {

        state.boundRadius = size.width / 2F
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