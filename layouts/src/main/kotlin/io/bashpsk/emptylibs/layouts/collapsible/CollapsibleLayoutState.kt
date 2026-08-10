package io.bashpsk.emptylibs.layouts.collapsible

import androidx.compose.runtime.Stable

/**
 * Interface representing the state of a collapsible layout.
 * Provides properties and methods to control the visibility and expansion state of the layout.
 */
@Stable
interface CollapsibleLayoutState {

    /**
     * The current [CollapsibleLayoutProgress] of the layout.
     */
    val currentValue: CollapsibleLayoutProgress

    /**
     * The target [CollapsibleLayoutProgress] that the layout is currently animating towards.
     */
    val targetValue: CollapsibleLayoutProgress

    /**
     * A value between 0.0 and 1.0 representing the progress between states.
     * Usually 0.0 is expanded and 1.0 is collapsed.
     */
    val progress: Float

    /**
     * Whether the layout is currently visible (not in [CollapsibleLayoutProgress.Dismissed] state).
     */
    val isVisible: Boolean

    /**
     * Animates the layout to the [CollapsibleLayoutProgress.Expanded] state.
     */
    suspend fun expand()

    /**
     * Animates the layout to the [CollapsibleLayoutProgress.Collapsed] state.
     */
    suspend fun collapse()

    /**
     * Animates the layout to the [CollapsibleLayoutProgress.Dismissed] state.
     */
    suspend fun dismiss()
}