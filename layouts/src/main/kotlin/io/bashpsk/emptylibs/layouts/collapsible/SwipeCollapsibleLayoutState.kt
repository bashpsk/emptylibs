package io.bashpsk.emptylibs.layouts.collapsible

import androidx.compose.animation.core.AnimationSpec
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
 * @param initialValue The initial [CollapsibleLayoutValue] of the layout.
 * @param animationSpec The [AnimationSpec] used for state transitions.
 */
@Composable
fun rememberSwipeCollapsibleLayoutState(
    initialValue: CollapsibleLayoutValue = CollapsibleLayoutValue.Dismissed,
    animationSpec: AnimationSpec<Float> = SwipeCollapsibleLayoutDefault.AnimationSpec
): SwipeCollapsibleLayoutState {

    return retain(initialValue, animationSpec) {
        SwipeCollapsibleLayoutState(initialValue = initialValue, animationSpec = animationSpec)
    }
}

/**
 * The state implementation for [SwipeCollapsibleLayout].
 * Manages the anchored draggable state and provides methods to transition between
 * [CollapsibleLayoutValue] states.
 *
 * @param initialValue The initial [CollapsibleLayoutValue] state.
 * @param animationSpec The [AnimationSpec] to use for animations.
 */
class SwipeCollapsibleLayoutState(
    private val initialValue: CollapsibleLayoutValue,
    internal val animationSpec: AnimationSpec<Float>
) : CollapsibleLayoutState {


    internal val anchoredDraggableState = AnchoredDraggableState(initialValue = initialValue)

    override val currentValue: CollapsibleLayoutValue
        get() = anchoredDraggableState.currentValue

    override val targetValue: CollapsibleLayoutValue
        get() = anchoredDraggableState.targetValue

    override val progress: Float
        get() = anchoredDraggableState.progress(
            from = CollapsibleLayoutValue.Expanded,
            to = CollapsibleLayoutValue.Collapsed
        ).coerceIn(0.0F..1.0F)

    override val isVisible: Boolean
        get() = targetValue != CollapsibleLayoutValue.Dismissed
                || currentValue != CollapsibleLayoutValue.Dismissed

    internal var layoutSize by mutableStateOf(IntSize.Zero)

    override suspend fun expand() {

        anchoredDraggableState.animateTo(
            targetValue = CollapsibleLayoutValue.Expanded,
            animationSpec = animationSpec
        )
    }

    override suspend fun collapse() {

        anchoredDraggableState.animateTo(
            targetValue = CollapsibleLayoutValue.Collapsed,
            animationSpec = animationSpec
        )
    }

    override suspend fun dismiss() {

        anchoredDraggableState.animateTo(
            targetValue = CollapsibleLayoutValue.Dismissed,
            animationSpec = animationSpec
        )
    }
}