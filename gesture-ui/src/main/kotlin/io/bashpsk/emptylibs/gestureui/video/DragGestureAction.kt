package io.bashpsk.emptylibs.gestureui.video

internal enum class DragGestureAction {

    Transform,
    HorizontalTop,
    HorizontalBottom,
    VerticalLeft,
    VerticalRight;

    companion object {

        fun DragGestureAction?.hasNull() = this == null

        fun DragGestureAction?.hasTransform() = this == Transform

        fun DragGestureAction?.hasHorizontalTop() = this == HorizontalTop

        fun DragGestureAction?.hasHorizontalBottom() = this == HorizontalBottom

        fun DragGestureAction?.hasVerticalLeft() = this == VerticalLeft

        fun DragGestureAction?.hasVerticalRight() = this == VerticalRight
    }
}