package io.bashpsk.emptylibs.layouts.collapsible

import androidx.compose.runtime.Stable

/**
 * Interface representing the state of a collapsible layout.
 * Provides properties and methods to control the visibility and expansion state of the layout.
 */
@Stable
interface CollapsibleLayoutState {

    /**
     * The current [CollapsibleLayoutValue] of the layout.
     */
    val currentValue: CollapsibleLayoutValue

    /**
     * The target [CollapsibleLayoutValue] that the layout is currently animating towards.
     */
    val targetValue: CollapsibleLayoutValue

    /**
     * A value between 0.0 and 1.0 representing the progress between states.
     * Usually 0.0 is expanded and 1.0 is collapsed.
     */
    val progress: Float

    /**
     * Whether the layout is currently visible (not in [CollapsibleLayoutValue.Dismissed] state).
     */
    val isVisible: Boolean

    /**
     * Animates the layout to the [CollapsibleLayoutValue.Expanded] state.
     */
    suspend fun expand()

    /**
     * Animates the layout to the [CollapsibleLayoutValue.Collapsed] state.
     */
    suspend fun collapse()

    /**
     * Animates the layout to the [CollapsibleLayoutValue.Dismissed] state.
     */
    suspend fun dismiss()
}