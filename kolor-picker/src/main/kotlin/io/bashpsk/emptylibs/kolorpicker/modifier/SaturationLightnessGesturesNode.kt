package io.bashpsk.emptylibs.kolorpicker.modifier

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.unit.IntSize

internal class SaturationLightnessGesturesNode(
    private var onSelectionChanged: (saturation: Float, lightness: Float) -> Unit
) : DelegatingNode(), LayoutAwareModifierNode, PointerInputModifierNode {

    private var panelSize = IntSize.Zero

    private val tapNode = delegate(
        SuspendingPointerInputModifierNode {

            detectTapGestures(
                onPress = { offset ->

                    val panelWidth = panelSize.width.toFloat()
                    val panelHeight = panelSize.height.toFloat()

                    if (panelWidth > 0 && panelHeight > 0) {

                        val newSaturation = (offset.x / panelWidth).coerceIn(range = 0F..1F)
                        val newLightness = (1F - (offset.y / panelHeight)).coerceIn(0F..1F)

                        onSelectionChanged(newSaturation, newLightness)
                    }
                }
            )
        }
    )

    private val dragNode = delegate(
        SuspendingPointerInputModifierNode {

            detectDragGestures { change, _ ->

                val panelWidth = panelSize.width.toFloat()
                val panelHeight = panelSize.height.toFloat()

                if (panelWidth > 0 && panelHeight > 0) {

                    val newX = (change.position.x).coerceIn(0F..panelWidth)
                    val newY = (change.position.y).coerceIn(0F..panelHeight)
                    val newSaturation = (newX / panelWidth).coerceIn(range = 0F..1F)
                    val newLightness = (1F - (newY / panelHeight)).coerceIn(range = 0F..1F)

                    onSelectionChanged(newSaturation, newLightness)
                    change.consume()
                }
            }
        }
    )

    fun update(onSelectionChanged: (saturation: Float, lightness: Float) -> Unit) {

        this.onSelectionChanged = onSelectionChanged
        tapNode.resetPointerInputHandler()
        dragNode.resetPointerInputHandler()
    }

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