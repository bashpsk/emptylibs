package io.bashpsk.emptylibs.gestureui.video

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize

/**
 * A Composable function that provides a box with gesture detection capabilities,
 * specifically designed for video player-like interactions.
 *
 * It detects various tap and drag gestures and provides callbacks for them.
 * The behavior of these gestures can be customized through the [VideoGestureBoxState.config]
 * parameter, accessible via the [state] parameter.
 *
 * This Composable uses [BoxWithConstraints] to get the available screen space
 * and adapts its gesture detection logic accordingly.
 *
 * It handles:
 * - Single taps.
 * - Double taps (can be configured to trigger backward/forward actions based on tap location).
 * - Drag gestures in different regions of the screen:
 *     - Horizontal drag at the top.
 *     - Horizontal drag at the bottom.
 *     - Vertical drag on the left side (commonly used for brightness control).
 *     - Vertical drag on the right side (commonly used for volume control).
 * - Two-finger pinch-to-zoom and pan gestures (if enabled in [VideoGestureBoxState.config]).
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state An instance of [VideoGestureBoxState] that holds the configuration and current state
 * of the gestures. Defaults to a remembered [VideoGestureBoxState] instance.
 * @param onTapChanges A lambda that is invoked when a tap gesture occurs.
 * It receives a [TapChanges] sealed class instance indicating the type of tap.
 * @param onDragChanges A lambda that is invoked during drag gestures.
 * It receives a [DragChanges] sealed class instance indicating the state and type of drag.
 * @param content The content to be placed inside the gesture-detecting box.
 * This is a composable lambda that receives a [BoxWithConstraintsScope].
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VideoGestureBox(
    modifier: Modifier = Modifier,
    state: VideoGestureBoxState = rememberVideoGestureBoxState(),
    onTapChanges: (changes: TapChanges) -> Unit = {},
    onDragChanges: (changes: DragChanges) -> Unit = {},
    content: @Composable @UiComposable BoxWithConstraintsScope.() -> Unit
) {

    val screenSizeChanged = Modifier.onSizeChanged { size ->

        state.screenSize = size.toSize()
    }

    val touchPointerInput = Modifier.pointerInput(state.screenSize) {

        awaitEachGesture {

            do {

                val event = awaitPointerEvent()

                state.touchCount = event.changes.size
            } while (event.changes.any { change -> change.pressed })
        }
    }

    val tapPointerInput = Modifier.pointerInput(state.screenSize) {

        detectTapGestures(
            onTap = { position: Offset ->

                onTapChanges(TapChanges.SingleTap(position))
            },
            onDoubleTap = { position: Offset ->

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

    val dragPointerInput = Modifier.pointerInput(state.screenSize, state.config) {

        detectVideoGestures(
            screenSize = state.screenSize,
            deadZone = state.config.gestureMargin / 100.0F,
            onDragStart = { offset: Offset ->

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

            state.touchCount.takeIf { count -> count == 2 }?.run {

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

                        state.isDragStartSend.takeIf { hasSend -> hasSend.not() }?.run {

                            onDragChanges(DragChanges.HorizontalBottomStart)
                            state.isDragStartSend = true
                        }

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

                        state.isDragStartSend.takeIf { hasSend -> hasSend.not() }?.run {

                            onDragChanges(DragChanges.HorizontalBottomStart)
                            state.isDragStartSend = true
                        }

                        state.dragGestureAction = DragGestureAction.HorizontalBottom
                        change.consume()
                        onDragChanges(
                            DragChanges.HorizontalBottomChanges(state.swipeAmount.x, dragAmount.x)
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

    val transformableState = rememberTransformableState { zoomChange, panChange, rotationChange ->

        state.hasTransform().takeIf { isTransform -> isTransform.not() }?.run {

            return@rememberTransformableState
        }

        state.touchCount.takeIf { count -> count == 2 }?.run {

            when (state.dragGestureAction) {

                null -> {

                    state.dragGestureAction = DragGestureAction.Transform
                }

                DragGestureAction.Transform -> {

                    val newZoomChange = zoomChange.takeIf { state.config.isZoomEnable } ?: 1.0F
                    val newPanChange = panChange.takeIf { state.config.isPanEnable } ?: Offset.Zero

                    onDragChanges(DragChanges.TransformChanges(newZoomChange, newPanChange))
                    state.onResetDragGestureAction()
                }

                else -> return@rememberTransformableState
            }
        } ?: run {

            when (state.dragGestureAction) {

                DragGestureAction.Transform ->  state.dragGestureAction = null
                else -> {}
            }

            return@rememberTransformableState
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .then(touchPointerInput)
            .transformable(state = transformableState)
            .then(tapPointerInput)
            .then(dragPointerInput)
            .then(screenSizeChanged),
        contentAlignment = Alignment.Center
    ) {

        content()
    }
}