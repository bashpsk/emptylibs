package io.bashpsk.emptylibs.animations.shimmer

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Properties for configuring a shimmer effect.
 *
 * @property colors The colors used for the shimmer gradient. Typically, includes transparent colors
 * at the edges.
 * @property angle The angle of the shimmer effect in degrees. 0 degrees is horizontal from left to
 * right.
 * @property widthRatio The ratio of the shimmer width relative to the width of the drawing area.
 * @property animationSpec The infinite animation specification for the shimmer progress.
 */
@Immutable
data class ShimmerEffectProperties(
    val colors: List<Color>,
    val angle: Float = 0F,
    val widthRatio: Float = 0.35F,
    val animationSpec: InfiniteRepeatableSpec<Float>
)