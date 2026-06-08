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

internal class AlphaGesturesNode(
    private var thumbRadiusPx: Float,
    private var onAlphaChanged: (alpha: Float) -> Unit
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
                        val minAlpha = (0F..1F).start
                        val maxAlpha = (0F..1F).endInclusive
                        val sliderWidth = panelWidth - (2 * thumbRadiusPx)
                        val normalizedPosition = if (sliderWidth > 0) {
                            (newX - thumbRadiusPx) / sliderWidth
                        } else 0F
                        val newValue = minAlpha + (normalizedPosition * (maxAlpha - minAlpha))

                        onAlphaChanged(newValue.coerceIn(range = 0F..1F))
                    }
                }
            )
        }
    )

    private val dragNode = delegate(
        SuspendingPointerInputModifierNode {

            detectDragGestures { change, _ ->

                val panelWidth = panelSize.width.toFloat()

                if (panelWidth > 0) {

                    val newX = change.position.x.coerceIn(
                        range = thumbRadiusPx..panelWidth - thumbRadiusPx
                    )
                    val minAlpha = (0F..1F).start
                    val maxAlpha = (0F..1F).endInclusive
                    val sliderWidth = panelWidth - (2 * thumbRadiusPx)
                    val normalizedPosition = if (sliderWidth > 0) {
                        (newX - thumbRadiusPx) / sliderWidth
                    } else 0F
                    val newValue = minAlpha + (normalizedPosition * (maxAlpha - minAlpha))

                    onAlphaChanged(newValue.coerceIn(range = 0F..1F))
                    change.consume()
                }
            }
        }
    )

    fun update(thumbRadiusPx: Float, onAlphaChanged: (alpha: Float) -> Unit) {

        this.thumbRadiusPx = thumbRadiusPx
        this.onAlphaChanged = onAlphaChanged
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