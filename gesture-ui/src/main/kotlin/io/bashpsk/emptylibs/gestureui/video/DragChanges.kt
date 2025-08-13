package io.bashpsk.emptylibs.gestureui.video

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
 * - [VerticalLeftChanges]: Represents vertical drag changes specifically for the left side.
 * - [VerticalRightChanges]: Represents vertical drag changes specifically for the right side.
 * - [TransformChanges]: Represents changes in zoom and pan during a transform gesture
 * (e.g., pinch-to-zoom).
 * - [Unknown]: Represents an unknown or unhandled drag change.
 */
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
     * @param changes The amount of change in the horizontal direction.
     * A positive value indicates a drag towards the right, and a negative value indicates a drag
     * towards the left.
     */
    data class HorizontalTopChanges(val changes: Float) : DragChanges

    /**
     * Represents horizontal drag changes originating from the bottom side.
     *
     * @param changes The amount of change in the horizontal direction.
     * A positive value indicates a drag towards the right, and a negative value indicates a drag
     * towards the left.
     */
    data class HorizontalBottomChanges(val changes: Float) : DragChanges

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