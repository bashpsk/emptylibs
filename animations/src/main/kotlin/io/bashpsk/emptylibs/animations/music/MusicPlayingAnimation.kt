package io.bashpsk.emptylibs.animations.music

import androidx.annotation.FloatRange
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
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
 * @param boxColor The color of the animated bars.
 * @param barCount The number of bars to display in the animation.
 * @param boxCount The maximum number of ticks (segments) in each bar.
 * @param boxSpacing The spacing between the bars & boxes, as a fraction of the bar width.
 * @param boxCornerRadius The corner radius of the bars & boxes, as a fraction of the bar width.
 * @param easing The easing function to use for the animation.
 * @param duration The duration of the animation for a single bar.
 * @param delay The delay before the animation starts for a single bar.
 */
@Composable
fun MusicPlayingAnimation(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    boxColor: Color = MaterialTheme.colorScheme.secondary,
    barCount: Int = 5,
    boxCount: Int = 4,
    @FloatRange(from = 0.0, to = 1.0)
    boxSpacing: Float = 0.05F,
    @FloatRange(from = 0.0, to = 1.0)
    boxCornerRadius: Float = 0.05F,
    easing: Easing = EaseInBounce,
    duration: Int = 2500,
    delay: Int = 25
) {

    val animationLabel = "Music Playing Animation"
    val infiniteTransition = rememberInfiniteTransition(label = animationLabel)

    val random = retain { Random(seed = 0) }
    var isAnimationVisible by rememberSaveable { mutableStateOf(false) }

    val isPlayAnimation by remember(isAnimationVisible, isPlaying) {
        derivedStateOf { isAnimationVisible && isPlaying }
    }

    val amplitudes = if (isPlayAnimation) List(size = barCount) {

        infiniteTransition.animateValue(
            initialValue = 0,
            targetValue = boxCount,
            typeConverter = Int.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = random.nextInt(
                        from = (duration / 4).coerceAtLeast(100),
                        until = duration
                    ),
                    easing = easing,
                    delayMillis = delay
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = animationLabel
        )
    } else emptyList()

    Box(
        modifier = modifier
            .clipToBounds()
            .onVisibilityChanged(minFractionVisible = 0.05F) { isVisible ->

                isAnimationVisible = isVisible
            }
            .musicPlayingAnimation(
                amplitudes = amplitudes,
                isPlaying = isPlayAnimation,
                barCount = barCount,
                boxCount = boxCount,
                boxSpacing = boxSpacing,
                boxCornerRadius = boxCornerRadius,
                boxColor = boxColor
            )
    )
}

/**
 * Applies a music playing visualizer animation to the [Modifier].
 *
 * @param boxColor The color of the animated bars.
 * @param barCount The number of bars to display in the animation.
 * @param boxCount The maximum number of ticks (segments) in each bar.
 * @param boxSpacing The spacing between the bars & boxes, as a fraction of the bar width.
 * @param boxCornerRadius The corner radius of the bars & boxes, as a fraction of the bar width.
 */
fun Modifier.musicPlayingAnimation(
    amplitudes: List<State<Int>>,
    isPlaying: Boolean,
    barCount: Int,
    boxCount: Int,
    boxColor: Color,
    boxSpacing: Float,
    boxCornerRadius: Float
): Modifier = this then MusicPlayingAnimationElement(
    amplitudes = amplitudes,
    isPlaying = isPlaying,
    barCount = barCount,
    boxCount = boxCount,
    boxColor = boxColor,
    boxSpacing = boxSpacing,
    boxCornerRadius = boxCornerRadius
)