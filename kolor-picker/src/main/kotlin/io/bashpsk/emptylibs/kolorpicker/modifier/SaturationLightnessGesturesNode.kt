package io.bashpsk.emptylibs.kolorpicker.modifier

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode

internal class SaturationLightnessGesturesNode(
    private var onSelectionChanged: (saturation: Float, lightness: Float) -> Unit
) : BaseKolorPickerGesturesNode() {

    override val tapNode = delegate(
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

    override val dragNode = delegate(
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
    }
}