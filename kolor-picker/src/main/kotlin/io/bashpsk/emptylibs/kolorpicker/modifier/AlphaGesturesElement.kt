package io.bashpsk.emptylibs.kolorpicker.modifier

import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

internal class AlphaGesturesElement(
    private val thumbRadiusPx: Float,
    private val onAlphaChanged: (alpha: Float) -> Unit
) : ModifierNodeElement<AlphaGesturesNode>() {

    override fun create(): AlphaGesturesNode {

        return AlphaGesturesNode(thumbRadiusPx = thumbRadiusPx, onAlphaChanged = onAlphaChanged)
    }

    override fun update(node: AlphaGesturesNode) {

        node.update(thumbRadiusPx = thumbRadiusPx, onAlphaChanged = onAlphaChanged)
    }

    override fun InspectorInfo.inspectableProperties() {

        name = "alphaGestures"
        properties["thumbRadiusPx"] = thumbRadiusPx
        properties["onAlphaChanged"] = onAlphaChanged
    }

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other !is AlphaGesturesElement) return false
        if (thumbRadiusPx != other.thumbRadiusPx) return false
        if (onAlphaChanged != other.onAlphaChanged) return false

        return true
    }

    override fun hashCode(): Int {

        var result = thumbRadiusPx.hashCode()
        result = 31 * result + onAlphaChanged.hashCode()
        return result
    }
}