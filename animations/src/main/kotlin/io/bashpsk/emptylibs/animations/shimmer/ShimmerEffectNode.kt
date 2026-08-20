package io.bashpsk.emptylibs.animations.shimmer

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw

/**
 * A [Modifier.Node] that implements [DrawModifierNode] to draw a shimmer effect.
 *
 * @property properties The [ShimmerEffectProperties] to be applied.
 */
internal class ShimmerEffectNode(
    var properties: ShimmerEffectProperties
) : Modifier.Node(), DrawModifierNode {

    /**
     * A delegate responsible for managing the shimmer animation state and generating the brush used
     * for drawing the effect.
     */
    private var delegate: ShimmerEffectNodeDelegate? = null

    override fun onAttach() {
        super.onAttach()

        delegate = ShimmerEffectNodeDelegate(
            properties = properties,
            coroutineScope = coroutineScope,
            onInvalidate = { invalidateDraw() }
        )
    }

    override fun ContentDrawScope.draw() {

        drawContent()

        val brush = delegate?.getBrush(size = size) ?: return

        drawRect(brush = brush)
    }

    /**
     * Updates the properties of the shimmer effect.
     *
     * @param properties The new [ShimmerEffectProperties] to be applied.
     */
    fun update(properties: ShimmerEffectProperties) {

        this.properties = properties
        delegate?.update(properties = properties)
    }
}