package io.bashpsk.emptylibs.imagekrop.modifier

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import io.bashpsk.emptylibs.imagekrop.crop.ImageKropState
import io.bashpsk.emptylibs.imagekrop.crop.drawKropHandle

internal class ImageKropNode(
    private var state: ImageKropState,
) : DelegatingNode(), LayoutAwareModifierNode, DrawModifierNode, PointerInputModifierNode {

    private val dragNode = delegate(
        SuspendingPointerInputModifierNode {

            detectDragGestures(
                onDragStart = state::onKropStart,
                onDragEnd = state::onKropEnd,
                onDragCancel = state::onKropEnd,
                onDrag = { change, dragAmount ->

                    change.consume()
                    state.onKropChanges(position = change.position, amount = dragAmount)
                }
            )
        }
    )

    fun update(state: ImageKropState) {

        this.state = state
        dragNode.resetPointerInputHandler()
    }

    override fun onPlaced(coordinates: LayoutCoordinates) {

        val imageWidth = coordinates.size.width.toFloat()
        val imageHeight = coordinates.size.height.toFloat()

        state.apply {

            kropRectPosition = Offset(x = imageWidth * 0.05F, y = imageHeight * 0.05F)
            kropRectSize = Size(width = imageWidth * 0.90F, height = imageHeight * 0.90F)
            canvasSize = coordinates.size.toSize()
        }
    }

    override fun ContentDrawScope.draw() {

        drawContent()

        drawKropHandle(
            kropShape = state.kropShape,
            topLeft = state.kropRectPosition,
            rectSize = state.kropRectSize,
            config = state.config
        )
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {

        dragNode.onPointerEvent(pointerEvent = pointerEvent, pass = pass, bounds = bounds)
    }

    override fun onCancelPointerInput() {

        dragNode.onCancelPointerInput()
    }
}