package io.bashpsk.emptylibs.kolorpicker.modifier

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.unit.IntSize

internal class ImageKolorPickerGesturesNode(
    private var onColorSelection: (position: Offset) -> Unit
) : DelegatingNode(), PointerInputModifierNode {

    private val tapNode = delegate(
        SuspendingPointerInputModifierNode {

            detectTapGestures(
                onPress = { offset ->

                    onColorSelection(offset)
                }
            )
        }
    )

    private val dragNode = delegate(
        SuspendingPointerInputModifierNode {

            detectDragGestures { change, _ ->

                onColorSelection(change.position)
                change.consume()
            }
        }
    )

    fun update(onColorSelection: (position: Offset) -> Unit) {

        this.onColorSelection = onColorSelection
        tapNode.resetPointerInputHandler()
        dragNode.resetPointerInputHandler()
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