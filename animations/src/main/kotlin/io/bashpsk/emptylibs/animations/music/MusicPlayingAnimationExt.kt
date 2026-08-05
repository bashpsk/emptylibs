package io.bashpsk.emptylibs.animations.music

import androidx.annotation.FloatRange
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Draws the music playing animation on the canvas.
 *
 * This function is an internal implementation detail of the [MusicPlayingAnimation] composable.
 * It draws the animated bars based on the provided animation values.
 *
 * @param amplitudes A list of states that represent the height of each bar.
 * @param barCount The number of bars to display in the animation.
 * @param boxCount The maximum number of ticks (segments) in each bar.
 * @param boxSpacing The spacing between the bars & boxes, as a fraction of the bar width.
 * @param boxCornerRadius The corner radius of the bars & boxes, as a fraction of the bar width.
 * @param boxColor The color of the animated bars.
 */
internal fun DrawScope.drawMusicPlayingAnimation(
    amplitudes: List<State<Int>>,
    barCount: Int,
    boxCount: Int,
    @FloatRange(0.0, 1.0)
    boxSpacing: Float,
    @FloatRange(0.0, 1.0)
    boxCornerRadius: Float,
    boxColor: Color
) {

    if (amplitudes.isEmpty()) return

    val totalRelativeWidth = barCount + boxSpacing * (barCount - 1).coerceAtLeast(0)
    val totalRelativeHeight = boxCount + boxSpacing * (boxCount - 1).coerceAtLeast(0)
    val barWidth = size.width / totalRelativeWidth
    val barHeight = (size.height / totalRelativeHeight)
    val horizontalSpacing = barWidth * boxSpacing
    val verticalSpacing = barWidth * boxSpacing
    val barRadius = (barHeight / 2.0F) * boxCornerRadius

    val boxSize = Size(width = barWidth, height = barHeight)
    val cornerRadius = CornerRadius(x = barRadius, y = barRadius)

    amplitudes.forEachIndexed { barIndex, amplitudeState ->

        (0..amplitudeState.value).forEach { boxIndex ->

            drawRoundRect(
                topLeft = Offset(
                    x = barIndex * (barWidth + horizontalSpacing),
                    y = (size.height - barHeight) - (boxIndex * (barHeight + verticalSpacing))
                ),
                size = boxSize,
                cornerRadius = cornerRadius,
                color = boxColor
            )
        }
    }
}