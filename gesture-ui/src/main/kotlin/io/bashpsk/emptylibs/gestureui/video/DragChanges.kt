package io.bashpsk.emptylibs.gestureui.video

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset

/**
 * Represents the different types of drag changes that can occur during a drag gesture.
 * This sealed interface is used to communicate drag events and their associated data.
 *
 * - [DragStart]: Indicates the start of a drag gesture, providing the initial position.
 * - [DragEnded]: Signals the end of a successful drag gesture.
 * - [DragCanceled]: Signals that the drag gesture was canceled.
 * - [HorizontalTopChanges]: Represents horizontal drag changes specifically for the top side.
 * - [HorizontalBottomChanges]: Represents horizontal drag changes specifically for the bottom side.
 * - [HorizontalTopEnd]: Signals the end of a horizontal drag gesture from the top side.
 * - [HorizontalBottomEnd]: Signals the end of a horizontal drag gesture from the bottom side.
 * - [VerticalLeftChanges]: Represents vertical drag changes specifically for the left side.
 * - [VerticalRightChanges]: Represents vertical drag changes specifically for the right side.
 * - [TransformChanges]: Represents changes in zoom and pan during a transform gesture
 * (e.g., pinch-to-zoom).
 * - [Unknown]: Represents an unknown or unhandled drag change.
 */
@Stable
sealed interface DragChanges {

    /**
     * Represents the start of a drag gesture.
     *
     * @property position The initial position where the drag started.
     */
    data class DragStart(val position: Offset) : DragChanges

    /**
     * Represents the state when a drag gesture has ended.
     */
    data object DragEnded : DragChanges

    /**
     * Represents the cancellation of a drag gesture.
     * This is typically triggered when the drag is interrupted.
     */
    data object DragCanceled : DragChanges

    /**
     * Represents horizontal drag changes originating from the top side.
     *
     * @param amount The amount of change in the horizontal direction.
     * A positive value indicates a drag towards the right, and a negative value indicates a drag
     * towards the left.
     * @property changes The amount of change in the horizontal direction since the last event.
     */
    data class HorizontalTopChanges(val amount: Float, val changes: Float) : DragChanges

    /**
     * Represents horizontal drag changes originating from the bottom side.
     *
     * @param amount The amount of change in the horizontal direction.
     * A positive value indicates a drag towards the right, and a negative value indicates a drag
     * towards the left.
     * @property changes The amount of change in the horizontal direction since the last event.
     */
    data class HorizontalBottomChanges(val amount: Float, val changes: Float) : DragChanges

    /**
     * Represents the end of a horizontal drag gesture originating from the top side.
     * This event is typically dispatched when the drag gesture is completed.
     *
     * @property amount The total horizontal distance dragged from the top side.
     * A positive value indicates a drag towards the right, and a negative value indicates a drag
     * towards the left.
     */
    data class HorizontalTopEnd(val amount: Float) : DragChanges

    /**
     * Represents the end of a horizontal drag gesture originating from the bottom side.
     * This indicates the total amount of horizontal movement when the drag is released.
     *
     * @param amount The total horizontal distance dragged from the bottom edge.
     * A positive value typically indicates a drag towards the right, and a negative value
     * indicates a drag towards the left, relative to the starting point of the drag
     * on the bottom edge.
     */
    data class HorizontalBottomEnd(val amount: Float) : DragChanges

    /**
     * Represents vertical drag changes originating from the left side.
     *
     * @param changes The amount of change in the vertical direction.
     * A positive value indicates a drag towards the right, and a negative value indicates a drag
     * towards the left.
     */
    data class VerticalLeftChanges(val changes: ValueChange) : DragChanges

    /**
     * Represents vertical drag changes originating from the bottom side.
     *
     * @param changes The amount of change in the vertical direction.
     * A positive value indicates a drag towards the right, and a negative value indicates a drag
     * towards the left.
     */
    data class VerticalRightChanges(val changes: ValueChange) : DragChanges

    /**
     * Represents changes related to transformations like zoom and pan.
     *
     * @property zoom The amount of zoom change. A value greater than 1.0f indicates zooming in,
     * a value between 0.0f and 1.0f indicates zooming out.
     * @property pan The amount of panning change as an [Offset].
     */
    data class TransformChanges(val zoom: Float, val pan: Offset) : DragChanges

    /**
     * Represents an unknown or unhandled drag change.
     * This can be used as a default or fallback state when the specific type of drag change
     * cannot be determined or is not relevant.
     */
    data object Unknown : DragChanges
}