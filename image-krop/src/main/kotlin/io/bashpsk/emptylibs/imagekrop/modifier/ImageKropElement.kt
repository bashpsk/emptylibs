package io.bashpsk.emptylibs.imagekrop.modifier

import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import io.bashpsk.emptylibs.imagekrop.crop.ImageKropState

internal class ImageKropElement(
    private val state: ImageKropState
) : ModifierNodeElement<ImageKropNode>() {

    override fun create(): ImageKropNode {

        return ImageKropNode(state = state)
    }

    override fun update(node: ImageKropNode) {

        node.update(state = state)
    }

    override fun InspectorInfo.inspectableProperties() {

        name = "imageKropModifier"
        properties["state"] = state
    }

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other !is ImageKropElement) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {

        return state.hashCode()
    }
}