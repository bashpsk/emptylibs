package io.bashpsk.emptylibs.layouts.collapsible

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.constraintlayout.compose.ExperimentalMotionApi

/**
 * Default values and constants for [SwipeCollapsibleLayout].
 */
@OptIn(ExperimentalMotionApi::class)
object SwipeCollapsibleLayoutDefault {

    /**
     * The default width ratio of the primary content when the layout is in the
     * [CollapsibleLayoutProgress.Collapsed] state.
     */
    const val PrimaryContentWidthRatio = 0.4F

    /**
     * The default height ratio (aspect ratio) of the primary content.
     */
    const val PrimaryContentHeightRatio = 9.0F / 16.0F

    /**
     * The default animation specification for state transitions in [SwipeCollapsibleLayout].
     */
    val AnimationSpec = tween<Float>(durationMillis = 500, easing = LinearOutSlowInEasing)
}