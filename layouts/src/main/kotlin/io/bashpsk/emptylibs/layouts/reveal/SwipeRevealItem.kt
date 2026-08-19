package io.bashpsk.emptylibs.layouts.reveal

import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import kotlin.math.roundToInt

/**
 * A Composable that provides a swipe-to-reveal interaction.
 *
 * It allows users to swipe a main content area to reveal actions on the left or right sides.
 * The reveal content is placed behind the main content.
 *
 * @param modifier The [Modifier] to be applied to the item.
 * @param state The state object that manages the swipe reveal logic.
 * See [rememberSwipeRevealState].
 * @param leftContent An optional Composable to be shown when swiping right to reveal the left side.
 * @param rightContent An optional Composable to be shown when swiping left to reveal the right
 * side.
 * @param content The main content Composable that will be swipable.
 */
@Composable
fun SwipeRevealItem(
    modifier: Modifier = Modifier,
    state: SwipeRevealState = rememberSwipeRevealState(),
    leftContent: (@Composable () -> Unit)? = null,
    rightContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {

    Layout(
        modifier = modifier.anchoredDraggable(
            state = state.anchoredDraggableState,
            orientation = Orientation.Horizontal,
            flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                state = state.anchoredDraggableState,
                animationSpec = state.animationSpec
            )
        ),
        content = {

            if (leftContent != null) Box(Modifier.layoutId(LeftContentId)) { leftContent() }

            if (rightContent != null) Box(Modifier.layoutId(RightContentId)) { rightContent() }

            Box(Modifier.layoutId(MainContentId)) { content() }
        }
    ) { measurables, constraints ->

        val leftMeasurable = measurables.find { measurable ->

            measurable.layoutId == LeftContentId
        }

        val rightMeasurable = measurables.find { measurable ->

            measurable.layoutId == RightContentId
        }

        val mainMeasurable = measurables.find { measurable ->

            measurable.layoutId == MainContentId
        }

        val mainPlaceable = mainMeasurable?.measure(constraints = constraints)

        val layoutWidth = mainPlaceable?.width ?: constraints.minWidth
        val layoutHeight = mainPlaceable?.height ?: constraints.minHeight

        val leftPlaceable = leftMeasurable?.measure(
            constraints = constraints.copy(
                minWidth = 0,
                maxWidth = mainPlaceable?.width ?: constraints.minWidth
            )
        )

        val rightPlaceable = rightMeasurable?.measure(
            constraints = constraints.copy(
                minWidth = 0,
                maxWidth = mainPlaceable?.width ?: constraints.minWidth
            )
        )

        val leftWidth = leftPlaceable?.width?.toFloat() ?: 0F
        val rightWidth = rightPlaceable?.width?.toFloat() ?: 0F

        val newAnchors = DraggableAnchors {

            SwipeRevealProgress.Hidden at 0F
            if (leftWidth > 0) SwipeRevealProgress.LeftRevealed at leftWidth
            if (rightWidth > 0) SwipeRevealProgress.RightRevealed at -rightWidth
        }

        state.anchoredDraggableState.updateAnchors(newAnchors = newAnchors)

        layout(width = layoutWidth, height = layoutHeight) {

            val offset = state.requireOffset().roundToInt()

            if (offset > 0) leftPlaceable?.placeRelative(
                x = 0,
                y = (layoutHeight - leftPlaceable.height) / 2
            ) else if (offset < 0) rightPlaceable?.placeRelative(
                x = layoutWidth - rightPlaceable.width,
                y = (layoutHeight - rightPlaceable.height) / 2
            )

            mainPlaceable?.placeRelative(x = offset, y = 0)
        }
    }
}