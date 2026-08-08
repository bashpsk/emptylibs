package io.bashpsk.emptylibs.kolorpicker.modifier

import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

internal data class SaturationLightnessGesturesElement(
    private val onSelectionChanged: (saturation: Float, lightness: Float) -> Unit
) : ModifierNodeElement<SaturationLightnessGesturesNode>() {

    override fun create(): SaturationLightnessGesturesNode {

        return SaturationLightnessGesturesNode(onSelectionChanged = onSelectionChanged)
    }

    override fun update(node: SaturationLightnessGesturesNode) {

        node.update(onSelectionChanged = onSelectionChanged)
    }

    override fun InspectorInfo.inspectableProperties() {

        name = "saturationLightnessGestures"
        properties["onSelectionChanged"] = onSelectionChanged
    }
}