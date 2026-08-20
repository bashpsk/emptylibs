package io.bashpsk.emptylibs.animations.shimmer

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color

/**
 * Default values and helpers for [ShimmerEffectProperties].
 */
object ShimmerEffectDefault {

    /**
     * The default colors for the shimmer effect, consisting of a transparent-white-transparent
     * gradient.
     */
    val EffectColors = listOf(
        Color.Transparent,
        Color.White.copy(alpha = 0.2F),
        Color.White.copy(alpha = 0.5F),
        Color.White.copy(alpha = 0.2F),
        Color.Transparent
    )

    /**
     * The default animation specification for the shimmer effect, a 2000ms tween that repeats
     * infinitely.
     */
    val AnimationSpec = infiniteRepeatable<Float>(
        animation = tween(durationMillis = 2000),
        repeatMode = RepeatMode.Restart
    )

    /**
     * Creates a new instance of [ShimmerEffectProperties] with default values.
     *
     * @param colors The colors used for the shimmer gradient.
     * @param angle The angle of the shimmer effect in degrees.
     * @param widthRatio The ratio of the shimmer width relative to the width of the drawing area.
     * @param animationSpec The infinite animation specification for the shimmer progress.
     */
    fun properties(
        colors: List<Color> = EffectColors,
        angle: Float = 0F,
        widthRatio: Float = 0.35F,
        animationSpec: InfiniteRepeatableSpec<Float> = AnimationSpec
    ): ShimmerEffectProperties {

        return ShimmerEffectProperties(
            colors = colors,
            angle = angle,
            widthRatio = widthRatio,
            animationSpec = animationSpec
        )
    }
}