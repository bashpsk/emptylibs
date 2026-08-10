package io.bashpsk.emptylibs.layouts.collapsible

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize

/**
 * Creates and remembers a [SwipeCollapsibleLayoutState] for [SwipeCollapsibleLayout].
 *
 * @param initialValue The initial [CollapsibleLayoutProgress] of the layout.
 * @param animationSpec The [AnimationSpec] used for state transitions.
 */
@Composable
fun rememberSwipeCollapsibleLayoutState(
    initialValue: CollapsibleLayoutProgress = CollapsibleLayoutProgress.Dismissed,
    animationSpec: AnimationSpec<Float> = SwipeCollapsibleLayoutDefault.AnimationSpec
): SwipeCollapsibleLayoutState {

    return retain(initialValue, animationSpec) {
        SwipeCollapsibleLayoutState(initialValue = initialValue, animationSpec = animationSpec)
    }
}

/**
 * The state implementation for [SwipeCollapsibleLayout].
 * Manages the anchored draggable state and provides methods to transition between
 * [CollapsibleLayoutProgress] states.
 *
 * @param initialValue The initial [CollapsibleLayoutProgress] state.
 * @param animationSpec The [AnimationSpec] to use for animations.
 */
@OptIn(ExperimentalFoundationApi::class)
class SwipeCollapsibleLayoutState(
    private val initialValue: CollapsibleLayoutProgress,
    internal val animationSpec: AnimationSpec<Float>
) : CollapsibleLayoutState {

    internal val anchoredDraggableState = AnchoredDraggableState(initialValue = initialValue)

    override val currentValue: CollapsibleLayoutProgress
        get() = anchoredDraggableState.currentValue

    override val targetValue: CollapsibleLayoutProgress
        get() = anchoredDraggableState.targetValue

    override val progress: Float
        get() = anchoredDraggableState.progress(
            from = CollapsibleLayoutProgress.Expanded,
            to = CollapsibleLayoutProgress.Collapsed
        )

    override val isVisible: Boolean
        get() = currentValue != CollapsibleLayoutProgress.Dismissed

    internal var layoutSize by mutableStateOf(IntSize.Zero)

    override suspend fun expand() {

        anchoredDraggableState.animateTo(
            targetValue = CollapsibleLayoutProgress.Expanded,
            animationSpec = animationSpec
        )
    }

    override suspend fun collapse() {

        anchoredDraggableState.animateTo(
            targetValue = CollapsibleLayoutProgress.Collapsed,
            animationSpec = animationSpec
        )
    }

    override suspend fun dismiss() {

        anchoredDraggableState.animateTo(
            targetValue = CollapsibleLayoutProgress.Dismissed,
            animationSpec = animationSpec
        )
    }
}