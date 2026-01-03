package io.bashpsk.emptylibs.animations.music

import androidx.annotation.FloatRange
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.sin

/**
 * Draws the music playing animation on the canvas.
 *
 * This function is an internal implementation detail of the [MusicPlayingAnimation] composable.
 * It draws the animated bars based on the provided animation values.
 *
 * @param color The color of the bars.
 * @param barAnimationList A list of states that represent the height of each bar.
 * @param spacingRatio The spacing between the bars, as a fraction of the bar width.
 * @param cornerRadiusRatio The corner radius of the bars, as a fraction of the bar width.
 */
internal fun DrawScope.drawMusicPlayingAnimation(
    color: Color = Color.Unspecified,
    barAnimationList: List<State<Float>>,
    @FloatRange(0.0, 1.0)
    spacingRatio: Float = 0.10F,
    @FloatRange(0.0, 1.0)
    cornerRadiusRatio: Float = 0.05F,
) {

    val barCount = barAnimationList.size

    if (barCount == 0) return

    val horizontalSpace = barCount + spacingRatio * (barCount - 1).coerceAtLeast(0)
    val barWidth = size.width / horizontalSpace
    val spacing = barWidth * spacingRatio
    val canvasCenterY = size.height / 2F
    val cornerRadius = (barWidth / 2F) * cornerRadiusRatio

    barAnimationList.forEachIndexed { index, heightAnimation ->

        val animatedBarHeight = heightAnimation.value.coerceAtLeast(0F)
        val barPositionAngle = (index + 1) * PI / (barCount + 1)
        val barHeight = animatedBarHeight * canvasCenterY * sin(barPositionAngle).toFloat()

        drawRoundRect(
            color = color,
            topLeft = Offset(x = index * (barWidth + spacing), y = canvasCenterY - barHeight),
            size = Size(width = barWidth, height = barHeight * 2),
            cornerRadius = CornerRadius(x = cornerRadius, y = cornerRadius)
        )
    }
}