package io.bashpsk.emptylibs.kolorpicker.modifier

import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

internal class HueGesturesElement(
    private val thumbRadiusPx: Float,
    private val onHueChanged: (hue: Float) -> Unit
) : ModifierNodeElement<HueGesturesNode>() {

    override fun create(): HueGesturesNode {

        return HueGesturesNode(thumbRadiusPx = thumbRadiusPx, onHueChanged = onHueChanged)
    }

    override fun update(node: HueGesturesNode) {

        node.update(thumbRadiusPx = thumbRadiusPx, onHueChanged = onHueChanged)
    }

    override fun InspectorInfo.inspectableProperties() {

        name = "hueGestures"
        properties["thumbRadiusPx"] = thumbRadiusPx
        properties["onHueChanged"] = onHueChanged
    }

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other !is HueGesturesElement) return false
        if (thumbRadiusPx != other.thumbRadiusPx) return false
        if (onHueChanged != other.onHueChanged) return false

        return true
    }

    override fun hashCode(): Int {

        var result = thumbRadiusPx.hashCode()
        result = 31 * result + onHueChanged.hashCode()
        return result
    }
}