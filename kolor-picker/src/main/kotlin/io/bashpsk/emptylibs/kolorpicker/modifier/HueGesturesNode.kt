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

internal class HueGesturesNode(
    private var thumbRadiusPx: Float,
    private var onHueChanged: (hue: Float) -> Unit
) : DelegatingNode(), LayoutAwareModifierNode, PointerInputModifierNode {

    private var panelSize = IntSize.Zero

    private val tapNode = delegate(
        SuspendingPointerInputModifierNode {

            detectTapGestures(
                onPress = { position ->

                    val panelWidth = panelSize.width.toFloat()

                    if (panelWidth > 0) {

                        val newX = position.x.coerceIn(
                            range = thumbRadiusPx..panelWidth - thumbRadiusPx
                        )
                        val minHue = (0F..360F).start
                        val maxHue = (0F..360F).endInclusive
                        val sliderWidth = panelWidth - (2 * thumbRadiusPx)
                        val normalizedPosition = if (sliderWidth > 0) {
                            (newX - thumbRadiusPx) / sliderWidth
                        } else 0F
                        val newValue = minHue + (normalizedPosition * (maxHue - minHue))

                        onHueChanged(newValue.coerceIn(range = 0F..360F))
                    }
                }
            )
        })

    private val dragNode = delegate(
        SuspendingPointerInputModifierNode {

            detectDragGestures { change, _ ->

                val panelWidth = panelSize.width.toFloat()

                if (panelWidth > 0) {

                    val newX = change.position.x.coerceIn(
                        range = thumbRadiusPx..panelWidth - thumbRadiusPx
                    )
                    val minHue = (0F..360F).start
                    val maxHue = (0F..360F).endInclusive
                    val sliderWidth = panelWidth - (2 * thumbRadiusPx)
                    val normalizedPosition = if (sliderWidth > 0) {
                        (newX - thumbRadiusPx) / sliderWidth
                    } else 0F
                    val newValue = minHue + (normalizedPosition * (maxHue - minHue))

                    onHueChanged(newValue.coerceIn(range = 0F..360F))
                    change.consume()
                }
            }
        }
    )

    fun update(thumbRadiusPx: Float, onHueChanged: (hue: Float) -> Unit) {

        this.thumbRadiusPx = thumbRadiusPx
        this.onHueChanged = onHueChanged
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