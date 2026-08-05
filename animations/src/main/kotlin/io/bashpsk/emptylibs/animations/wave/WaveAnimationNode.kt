package io.bashpsk.emptylibs.animations.wave

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

internal class WaveAnimationNode(
    var progress: Float,
    var waveOffset: Float,
    var waveColor: Color,
    var amplitude: Dp
) : Modifier.Node(), DrawModifierNode {

    override fun ContentDrawScope.draw() {

        val amplitudePx = amplitude.toPx()
        val step = 4.dp.toPx()
        val progressHeight = size.height * (1f - progress)

        val currentAmplitude = amplitudePx
            .coerceAtMost(progressHeight)
            .coerceAtMost(size.height - progressHeight)

        val path = Path().apply {

            moveTo(0f, size.height)

            if (progress in 0f..1f && progress != 0f && progress != 1f) {

                val steps = (size.width / step).toInt()

                (0..steps).forEach { index ->
                    val x = (index * step).coerceAtMost(size.width)
                    val relativeX = x / size.width
                    val angle = (relativeX + waveOffset) * (2f * PI.toFloat())
                    val y = progressHeight + sin(angle) * currentAmplitude

                    lineTo(x, y)
                }

                if ((steps * step) < size.width) {

                    val angle = (1f + waveOffset) * (2f * PI.toFloat())

                    lineTo(x = size.width, y = progressHeight + sin(angle) * currentAmplitude)
                }

            } else {

                lineTo(x = 0f, y = progressHeight)
                lineTo(x = size.width, y = progressHeight)
            }

            lineTo(x = size.width, y = size.height)
            close()
        }

        drawPath(path, waveColor)
        drawContent()
    }
}