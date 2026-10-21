package io.bashpsk.emptylibs.layouts.collapsible

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Default values and constants for [SwipeCollapsibleLayout].
 */
object SwipeCollapsibleLayoutDefault {

    /**
     * The default width ratio of the primary content when the layout is in the
     * [CollapsibleLayoutValue.Collapsed] state.
     */
    const val PrimaryContentWidthRatio = 0.4F

    /**
     * The default height ratio (aspect ratio) of the primary content.
     */
    const val PrimaryContentHeightRatio = 9.0F / 16.0F

    /**
     * The default animation specification for state transitions in [SwipeCollapsibleLayout].
     */
    val AnimationSpec = tween<Float>(durationMillis = 300, easing = LinearEasing)

    @Stable
    @Composable
    fun shapes(
        collapsedShape: Shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        expandedShape: Shape = RectangleShape
    ): CollapsibleLayoutShape {

        return CollapsibleLayoutShape(
            collapsedShape = collapsedShape,
            expandedShape = expandedShape
        )
    }
}