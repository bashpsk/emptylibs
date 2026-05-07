package io.bashpsk.emptylibs.animations.wave

import androidx.annotation.FloatRange
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

/**
 * A modifier that draws a wave animation behind the content.
 *
 * @param progress The progress of the wave, from 0.0 to 1.0.
 * @param waveColor The color of the wave.
 * @param amplitude The amplitude of the wave.
 * @param animation The animation spec for the wave offset.
 */
fun Modifier.waveAnimation(
    @FloatRange(from = 0.0, to = 1.0)
    progress: Float,
    waveColor: Color = Color.Green,
    amplitude: Dp = 8.dp,
    animation: TweenSpec<Float> = tween(durationMillis = 5000, easing = LinearEasing)
): Modifier = composed {

    val density = LocalDensity.current
    val infiniteTransition = rememberInfiniteTransition(label = "Wave Animation")

    var isAnimationVisible by rememberSaveable { mutableStateOf(false) }

    val amplitudePx by remember(amplitude) {
        derivedStateOf { with(density) { amplitude.toPx() } }
    }

    val stepSize by remember { derivedStateOf { with(density) { 4.dp.toPx() } } }

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 1F,
        animationSpec = infiniteRepeatable(
            animation = animation,
            repeatMode = RepeatMode.Restart
        ),
        label = "Wave Offset"
    )

    val isAnimating by remember(progress, isAnimationVisible) {
        derivedStateOf { progress > 0F && progress < 1F && isAnimationVisible }
    }

    onVisibilityChanged { isVisible ->

        isAnimationVisible = isVisible
    }.drawWithContent {

        val progressHeight = size.height * (1F - progress)

        val currentAmplitude = amplitudePx
            .coerceAtMost(progressHeight)
            .coerceAtMost(size.height - progressHeight)

        val wavePath = Path().apply {

            reset()
            moveTo(x = 0F, y = size.height)

            if (isAnimating) {

                val stepCount = (size.width / stepSize).toInt()

                for (i in 0..stepCount) {

                    val x = (i * stepSize).coerceAtMost(size.width)
                    val relativeX = x / size.width
                    val angle = (relativeX + waveOffset) * 2 * PI
                    val y = progressHeight + (sin(angle).toFloat() * currentAmplitude)

                    lineTo(x = x, y = y)
                }

                if ((stepCount * stepSize) < size.width) {

                    val angleEnd = (1F + waveOffset) * 2 * PI
                    val y = progressHeight + (sin(angleEnd).toFloat() * currentAmplitude)

                    lineTo(x = size.width, y = y)
                }
            } else {

                lineTo(x = 0F, y = progressHeight)
                lineTo(x = size.width, y = progressHeight)
            }

            lineTo(x = size.width, y = size.height)
            close()
        }

        drawPath(path = wavePath, color = waveColor)

        drawContent()
    }
}