package io.bashpsk.emptylibs.animations.wave

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp

internal data class WaveAnimationElement(
    val progress: Float,
    val waveOffset: Float,
    val waveColor: Color,
    val amplitude: Dp
) : ModifierNodeElement<WaveAnimationNode>() {

    override fun create(): WaveAnimationNode {
        return WaveAnimationNode(
            progress = progress,
            waveOffset = waveOffset,
            waveColor = waveColor,
            amplitude = amplitude
        )
    }

    override fun update(node: WaveAnimationNode) {
        node.progress = progress
        node.waveOffset = waveOffset
        node.waveColor = waveColor
        node.amplitude = amplitude
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "WaveAnimation"
    }
}