package io.bashpsk.emptylibs.animations.shimmer

import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

/**
 * A [ModifierNodeElement] that creates and updates [ShimmerEffectNode].
 *
 * @property properties The [ShimmerEffectProperties] to be applied.
 */
internal data class ShimmerEffectElement(
    val properties: ShimmerEffectProperties
) : ModifierNodeElement<ShimmerEffectNode>() {

    override fun create(): ShimmerEffectNode {

        return ShimmerEffectNode(properties = properties)
    }

    override fun update(node: ShimmerEffectNode) {

        node.update(properties = properties)
    }

    override fun InspectorInfo.inspectableProperties() {

        name = "shimmerEffect"
        properties["properties"] = properties
    }
}