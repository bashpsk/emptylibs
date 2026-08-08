package io.bashpsk.emptylibs.kolorpicker.modifier

import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

internal data class SliderGesturesElement(
    private val thumbRadiusPx: Float,
    private val range: ClosedFloatingPointRange<Float>,
    private val onValueChanged: (Float) -> Unit
) : ModifierNodeElement<SliderGesturesNode>() {

    override fun create(): SliderGesturesNode {

        return SliderGesturesNode(
            thumbRadiusPx = thumbRadiusPx,
            range = range,
            onValueChanged = onValueChanged
        )
    }

    override fun update(node: SliderGesturesNode) {

        node.update(
            thumbRadiusPx = thumbRadiusPx,
            range = range,
            onValueChanged = onValueChanged
        )
    }

    override fun InspectorInfo.inspectableProperties() {

        name = "sliderGestures"
        properties["thumbRadiusPx"] = thumbRadiusPx
        properties["range"] = range
        properties["onValueChanged"] = onValueChanged
    }
}
