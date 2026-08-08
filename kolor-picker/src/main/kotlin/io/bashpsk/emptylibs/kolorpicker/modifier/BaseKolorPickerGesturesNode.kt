package io.bashpsk.emptylibs.kolorpicker.modifier

import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.unit.IntSize

internal abstract class BaseKolorPickerGesturesNode : DelegatingNode(), LayoutAwareModifierNode,
    PointerInputModifierNode {

    protected var panelSize = IntSize.Zero
        private set

    protected abstract val tapNode: SuspendingPointerInputModifierNode
    protected abstract val dragNode: SuspendingPointerInputModifierNode

    override fun onRemeasured(size: IntSize) {

        panelSize = size
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