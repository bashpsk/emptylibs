package io.bashpsk.emptylibs.gestureui.video

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.unit.IntSize

internal class VideoGesturesNode(
    private var state: VideoGestureBoxState,
    private var onTapChanges: (changes: TapChanges) -> Unit,
    private var onDragChanges: (changes: DragChanges) -> Unit
) : DelegatingNode(), LayoutAwareModifierNode, PointerInputModifierNode {

    init {

        state.onDragChanges = onDragChanges
    }

    private val touchNode = delegate(
        SuspendingPointerInputModifierNode {

            awaitEachGesture {

                do {

                    val event = awaitPointerEvent()

                    state.touchCount = event.changes.size
                } while (event.changes.any { change -> change.pressed })
            }
        }
    )

    private val tapNode = delegate(
        SuspendingPointerInputModifierNode {

            detectTapGestures(
                onTap = { position ->

                    onTapChanges(TapChanges.SingleTap(position))
                },
                onDoubleTap = { position ->

                    when {

                        state.hasBackwardTap(position = position) -> {

                            onTapChanges(TapChanges.BackwardTap(position))
                        }

                        state.hasForwardTap(position = position) -> {

                            onTapChanges(TapChanges.ForwardTap(position))
                        }

                        else -> onTapChanges(TapChanges.Unknown)
                    }
                }
            )
        }
    )

    private val dragNode = delegate(
        SuspendingPointerInputModifierNode {

            detectVideoGestures(
                screenSize = state.screenSize,
                deadZone = state.config.gestureMargin / 100.0F,
                onDragStart = { offset ->

                    state.onDragStart()
                    onDragChanges(DragChanges.DragStart(position = offset))
                },
                onDragCancel = {

                    when (state.dragGestureAction) {

                        DragGestureAction.HorizontalTop -> {

                            if (state.config.isHorizontalTopEnable) onDragChanges(
                                DragChanges.HorizontalTopEnd(state.swipeAmount.x)
                            )
                        }

                        DragGestureAction.HorizontalBottom -> {

                            if (state.config.isHorizontalBottomEnable) onDragChanges(
                                DragChanges.HorizontalBottomEnd(state.swipeAmount.x)
                            )
                        }

                        else -> {}
                    }

                    state.onDragEnd()
                    onDragChanges(DragChanges.DragCanceled)
                },
                onDragEnd = {

                    when (state.dragGestureAction) {

                        DragGestureAction.HorizontalTop -> {

                            if (state.config.isHorizontalTopEnable) onDragChanges(
                                DragChanges.HorizontalTopEnd(state.swipeAmount.x)
                            )
                        }

                        DragGestureAction.HorizontalBottom -> {

                            if (state.config.isHorizontalBottomEnable) onDragChanges(
                                DragChanges.HorizontalBottomEnd(state.swipeAmount.x)
                            )
                        }

                        else -> {}
                    }

                    state.onDragEnd()
                    onDragChanges(DragChanges.DragEnded)
                }
            ) { change, dragAmount, direction ->

                state.swipeAmount += dragAmount

                if (state.touchCount == 2) {

                    state.dragGestureAction = when (state.dragGestureAction) {

                        DragGestureAction.Transform -> state.dragGestureAction
                        else -> null
                    }

                    change.changedToUp()
                    return@detectVideoGestures
                }

                when {

                    direction.hasHorizontalTop() && state.hasHorizontalSwipe() -> when {

                        state.hasHorizontalTopSwipe() -> {

                            state.dragGestureAction = DragGestureAction.HorizontalTop
                            change.consume()
                            onDragChanges(
                                DragChanges.HorizontalTopChanges(state.swipeAmount.x, dragAmount.x)
                            )
                        }

                        else -> {}
                    }

                    direction.hasHorizontalBottom() && state.hasHorizontalSwipe() -> when {

                        state.hasHorizontalBottomSwipe() -> {

                            state.dragGestureAction = DragGestureAction.HorizontalBottom
                            change.consume()
                            onDragChanges(
                                DragChanges.HorizontalBottomChanges(
                                    state.swipeAmount.x,
                                    dragAmount.x
                                )
                            )
                        }

                        else -> {}
                    }

                    direction.hasVerticalLeft() && state.hasVerticalSwipe() -> when {

                        state.hasVerticalLeftSwipe() -> when (state.dragGestureAction) {

                            null -> {

                                state.dragGestureAction = DragGestureAction.VerticalLeft
                                change.consume()
                            }

                            DragGestureAction.VerticalLeft -> {

                                val brightness = when (dragAmount.y > 0.0F) {

                                    true -> ValueChange.Decreased
                                    false -> ValueChange.Increased
                                }

                                change.consume()
                                onDragChanges(DragChanges.VerticalLeftChanges(brightness))
                                state.onResetSwipeAmount()
                            }

                            else -> {}
                        }
                    }

                    direction.hasVerticalRight() && state.hasVerticalSwipe() -> when {

                        state.hasVerticalRightSwipe() -> when (state.dragGestureAction) {

                            null -> {

                                state.dragGestureAction = DragGestureAction.VerticalRight
                                change.consume()
                            }

                            DragGestureAction.VerticalRight -> {

                                val volume = when (dragAmount.y > 0.0F) {

                                    true -> ValueChange.Decreased
                                    false -> ValueChange.Increased
                                }

                                change.consume()
                                onDragChanges(DragChanges.VerticalRightChanges(volume))
                                state.onResetSwipeAmount()
                            }

                            else -> {}
                        }
                    }

                    else -> {

                        change.changedToUp()
                        onDragChanges(DragChanges.Unknown)
                    }
                }
            }
        }
    )

    fun update(
        state: VideoGestureBoxState,
        onTapChanges: (changes: TapChanges) -> Unit,
        onDragChanges: (changes: DragChanges) -> Unit
    ) {

        val oldState = this.state
        val oldOnTapChanges = this.onTapChanges
        val oldOnDragChanges = this.onDragChanges

        this.state = state
        this.onTapChanges = onTapChanges
        this.onDragChanges = onDragChanges
        state.onDragChanges = onDragChanges

        if (oldState.screenSize != state.screenSize || oldState.config != state.config
            || oldOnDragChanges != onDragChanges
        ) dragNode.resetPointerInputHandler()

        if (oldOnTapChanges != onTapChanges || oldState.config.isDoubleTapEnable
            != state.config.isDoubleTapEnable
        ) tapNode.resetPointerInputHandler()
    }

    override fun onRemeasured(size: IntSize) {

        val oldSize = state.screenSize

        state.screenSize = size
        if (oldSize != size) dragNode.resetPointerInputHandler()
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {

        touchNode.onPointerEvent(pointerEvent = pointerEvent, pass = pass, bounds = bounds)
        tapNode.onPointerEvent(pointerEvent = pointerEvent, pass = pass, bounds = bounds)
        dragNode.onPointerEvent(pointerEvent = pointerEvent, pass = pass, bounds = bounds)
    }

    override fun onCancelPointerInput() {

        touchNode.onCancelPointerInput()
        tapNode.onCancelPointerInput()
        dragNode.onCancelPointerInput()
    }
}