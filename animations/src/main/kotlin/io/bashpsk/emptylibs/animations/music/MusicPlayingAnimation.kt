package io.bashpsk.emptylibs.animations.music

import androidx.annotation.FloatRange
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onVisibilityChanged
import kotlin.random.Random

/**
 * A composable that displays a music playing animation.
 *
 * The animation consists of a series of vertical bars that animate their height,
 * creating a visual effect that resembles a music visualizer. The bars are arranged
 * in an oval shape, with the tallest bars in the center and the shortest at the edges.
 * The animation is only active when [isPlaying] is true.
 *
 * @param modifier The modifier to be applied to the animation canvas.
 * @param isPlaying A boolean that controls whether the animation is playing.
 * @param color The color of the animated bars.
 * @param barCount The number of bars to display in the animation.
 * @param initialValue The initial height of the bars as a fraction of their total height.
 * @param spacingRatio The spacing between the bars, as a fraction of the bar width.
 * @param cornerRadiusRatio The corner radius of the bars, as a fraction of the bar width.
 * @param easing The easing function to use for the animation.
 * @param duration The duration of the animation for a single bar.
 * @param delay The delay before the animation starts for a single bar.
 */
@Composable
fun MusicPlayingAnimation(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    color: Color = MaterialTheme.colorScheme.secondary,
    barCount: Int = 5,
    @FloatRange(0.0, 1.0)
    initialValue: Float = 0.15F,
    @FloatRange(0.0, 1.0)
    spacingRatio: Float = 0.10F,
    @FloatRange(0.0, 1.0)
    cornerRadiusRatio: Float = 0.05F,
    easing: Easing = EaseInBounce,
    duration: Int = 1000,
    delay: Int = 500
) {

    val animationLabel = "Music Playing Animation"

    val infiniteTransition = rememberInfiniteTransition(label = animationLabel)

    val random = remember { Random(seed = 0) }
    var isAnimationVisible by rememberSaveable { mutableStateOf(false) }

    val isPlayAnimation by remember(isAnimationVisible, isPlaying) {
        derivedStateOf { isAnimationVisible && isPlaying }
    }

    val barAnimationList = when (isPlayAnimation) {

        true -> List(size = barCount) {

            infiniteTransition.animateFloat(
                initialValue = initialValue,
                targetValue = 1.0F,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = random.nextInt(from = duration / 2, until = duration),
                        easing = easing,
                        delayMillis = random.nextInt(from = delay / 2, until = delay)
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = animationLabel
            )
        }

        false -> emptyList()
    }

    Canvas(
        modifier = modifier
            .clipToBounds()
            .onVisibilityChanged { isVisible ->

                isAnimationVisible = isVisible
            },
        contentDescription = animationLabel
    ) {

        if (isPlayAnimation) drawMusicPlayingAnimation(
            color = color,
            barAnimationList = barAnimationList,
            spacingRatio = spacingRatio,
            cornerRadiusRatio = cornerRadiusRatio
        )
    }
}