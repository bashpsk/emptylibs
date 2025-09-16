package io.bashpsk.emptylibs.gestureui.video

/**
 * Enum class representing different drag gesture actions in the video player.
 * These actions define how the player responds to various drag gestures.
 */
internal enum class DragGestureAction {

    Transform,
    HorizontalTop,
    HorizontalBottom,
    VerticalLeft,
    VerticalRight;

    companion object {

        /**
         * Checks if the [DragGestureAction] is null.
         *
         * @return `true` if the [DragGestureAction] is null, `false` otherwise.
         */
        fun DragGestureAction?.hasNull() = this == null

        /**
         * Checks if the current drag gesture action is a transform action.
         *
         * @return `true` if the drag gesture action is [Transform], `false` otherwise.
         */
        fun DragGestureAction?.hasTransform() = this == Transform

        /**
         * Checks if the drag gesture action is [HorizontalTop].
         *
         * @return `true` if the drag gesture action is [HorizontalTop], `false` otherwise.
         */
        fun DragGestureAction?.hasHorizontalTop() = this == HorizontalTop

        /**
         * Checks if the current [DragGestureAction] is [HorizontalBottom].
         *
         * @return `true` if the action is [HorizontalBottom], `false` otherwise.
         */
        fun DragGestureAction?.hasHorizontalBottom() = this == HorizontalBottom

        /**
         * Checks if the current drag gesture action is [VerticalLeft].
         *
         * @return `true` if the drag gesture action is [VerticalLeft], `false` otherwise.
         */
        fun DragGestureAction?.hasVerticalLeft() = this == VerticalLeft

        /**
         * Checks if the [DragGestureAction] is [VerticalRight].
         *
         * @return `true` if the [DragGestureAction] is [VerticalRight], `false` otherwise.
         */
        fun DragGestureAction?.hasVerticalRight() = this == VerticalRight
    }
}