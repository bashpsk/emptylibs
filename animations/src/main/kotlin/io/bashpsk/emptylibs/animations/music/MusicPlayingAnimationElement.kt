package io.bashpsk.emptylibs.animations.music

import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo

/**
 * Represents a single visual component of a music playing animation, typically acting as a
 * vertical bar that fluctuates in height to simulate an equalizer or audio frequency effect.
 *
 * This element is intended to be used as a building block for complex music-related
 * visualizations or "now playing" indicators.
 */
internal data class MusicPlayingAnimationElement(
    val amplitudes: List<State<Int>>,
    val isPlaying: Boolean,
    val barCount: Int,
    val boxCount: Int,
    val boxColor: Color,
    val boxSpacing: Float,
    val boxCornerRadius: Float
) : ModifierNodeElement<MusicPlayingAnimationNode>() {

    override fun create(): MusicPlayingAnimationNode {
        return MusicPlayingAnimationNode(
            amplitudes = amplitudes,
            isPlaying=isPlaying,
            barCount = barCount,
            boxCount = boxCount,
            boxColor = boxColor,
            boxSpacing = boxSpacing,
            boxCornerRadius = boxCornerRadius
        )
    }

    override fun update(node: MusicPlayingAnimationNode) {
        node.amplitudes = amplitudes
        node.isPlaying = isPlaying
        node.barCount = barCount
        node.boxCount = boxCount
        node.boxColor = boxColor
        node.boxSpacing = boxSpacing
        node.boxCornerRadius = boxCornerRadius

        node.invalidateDraw()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "MusicPlayingAnimation"
    }
}