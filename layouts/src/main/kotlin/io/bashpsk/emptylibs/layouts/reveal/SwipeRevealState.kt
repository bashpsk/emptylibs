package io.bashpsk.emptylibs.layouts.reveal

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.retain.retain

/**
 * Creates and remembers a [SwipeRevealState] instance.
 *
 * @param initialValue The initial progress state. Defaults to [SwipeRevealProgress.Hidden].
 * @param animationSpec The animation specification for state transitions.
 * @return A remembered [SwipeRevealState] instance.
 */
@Composable
fun rememberSwipeRevealState(
    initialValue: SwipeRevealProgress = SwipeRevealProgress.Hidden,
    animationSpec: AnimationSpec<Float> = tween()
): SwipeRevealState {

    return retain(initialValue, animationSpec) {
        SwipeRevealState(initialValue = initialValue, animationSpec = animationSpec)
    }
}

/**
 * State object for [SwipeRevealItem] that manages the anchored draggable state.
 *
 * @param initialValue The initial [SwipeRevealProgress] state.
 * @param animationSpec The [AnimationSpec] used for transitions between states.
 */
@Stable
class SwipeRevealState(
    private val initialValue: SwipeRevealProgress,
    internal val animationSpec: AnimationSpec<Float>
) {

    internal val anchoredDraggableState = AnchoredDraggableState(initialValue = initialValue)

    /**
     * The current progress value of the swipe interaction.
     */
    val currentValue: SwipeRevealProgress
        get() = anchoredDraggableState.currentValue

    /**
     * The target progress value the interaction is moving towards.
     */
    val targetValue: SwipeRevealProgress
        get() = anchoredDraggableState.targetValue

    /**
     * The current pixel offset of the swipe interaction.
     */
    val offset: Float
        get() = anchoredDraggableState.offset

    /**
     * Returns the current offset or throws if not initialized.
     */
    fun requireOffset(): Float = anchoredDraggableState.requireOffset()

    /**
     * Animates to the [SwipeRevealProgress.LeftRevealed] state.
     */
    suspend fun revealLeft() {

        anchoredDraggableState.animateTo(
            targetValue = SwipeRevealProgress.LeftRevealed,
            animationSpec = animationSpec
        )
    }

    /**
     * Animates to the [SwipeRevealProgress.RightRevealed] state.
     */
    suspend fun revealRight() {

        anchoredDraggableState.animateTo(
            targetValue = SwipeRevealProgress.RightRevealed,
            animationSpec = animationSpec
        )
    }

    /**
     * Animates to the [SwipeRevealProgress.Hidden] state.
     */
    suspend fun hide() {

        anchoredDraggableState.animateTo(
            targetValue = SwipeRevealProgress.Hidden,
            animationSpec = animationSpec
        )
    }

    /**
     * Snaps to the specified [targetValue] without animation.
     *
     * @param targetValue The target progress state to snap to.
     */
    suspend fun snapTo(targetValue: SwipeRevealProgress) {

        anchoredDraggableState.snapTo(targetValue = targetValue)
    }
}