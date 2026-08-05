package io.bashpsk.emptylibs.animations.music

import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode

/**
 * A UI node that represents a music playback visualization, typically consisting of
 * animated vertical bars that fluctuate in height to simulate an active audio stream.
 *
 * This component is used to provide visual feedback that music is currently playing
 * within the application.
 */
internal class MusicPlayingAnimationNode(
    var amplitudes: List<State<Int>>,
    var isPlaying: Boolean,
    var barCount: Int,
    var boxCount: Int,
    var boxColor: Color,
    var boxSpacing: Float,
    var boxCornerRadius: Float
) : Modifier.Node(), DrawModifierNode {

    override fun ContentDrawScope.draw() {

        if (isPlaying) drawMusicPlayingAnimation(
            amplitudes = amplitudes,
            barCount = barCount,
            boxCount = boxCount,
            boxSpacing = boxSpacing,
            boxCornerRadius = boxCornerRadius,
            boxColor = boxColor
        )

        drawContent()
    }
}