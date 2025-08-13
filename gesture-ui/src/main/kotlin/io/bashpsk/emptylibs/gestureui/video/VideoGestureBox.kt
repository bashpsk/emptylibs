package io.bashpsk.emptylibs.gestureui.video

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

/**
 * A Composable function that provides a box with gesture detection capabilities,
 * specifically designed for video player-like interactions.
 *
 * It detects various tap and drag gestures and provides callbacks for them.
 * The behavior of these gestures can be customized through the [config] parameter.
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
 * - Two-finger pinch-to-zoom and pan gestures (if enabled in [config]).
 *
 * @param modifier The modifier to be applied to the layout.
 * @param config An instance of [VideoGestureConfig] to customize the gesture behavior.
 *               Defaults to [VideoGestureConfig].
 * @param onTapChanges A lambda that is invoked when a tap gesture occurs.
 *                     It receives a [TapChanges] sealed class instance indicating the type of tap.
 * @param onDragChanges A lambda that is invoked during drag gestures.
 *                      It receives a [DragChanges] sealed class instance indicating the state and
 *                      type of drag.
 * @param content The content to be placed inside the gesture-detecting box.
 *                This is a composable lambda that receives a [BoxWithConstraintsScope].
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VideoGestureBox(
    modifier: Modifier = Modifier,
    config: VideoGestureConfig = VideoGestureConfig(),
    onTapChanges: (changes: TapChanges) -> Unit = {},
    onDragChanges: (changes: DragChanges) -> Unit = {},
    content: @Composable @UiComposable BoxWithConstraintsScope.() -> Unit
) {

    val dragGestureCoroutineScope = rememberCoroutineScope()

    var screenWidth by rememberSaveable { mutableFloatStateOf(0.0F) }
    var screenHeight by rememberSaveable { mutableFloatStateOf(0.0F) }

    val screenSize by remember(screenWidth, screenHeight) {
        derivedStateOf { Size(width = screenWidth, height = screenHeight) }
    }

    var resetDragActionJob by remember { mutableStateOf<Job?>(null) }
    var dragGestureAction by rememberSaveable { mutableStateOf<DragGestureAction?>(null) }
    var touchCount by rememberSaveable { mutableIntStateOf(0) }
    val isOneTouch by remember(touchCount) { derivedStateOf { touchCount == 1 } }
    val isTwoTouch by remember(touchCount) { derivedStateOf { touchCount == 2 } }

    var swipeAmount by remember { mutableStateOf(Offset.Zero) }

    fun resetDragGestureAction() {

        resetDragActionJob?.cancel()
        resetDragActionJob = dragGestureCoroutineScope.launch(context = Dispatchers.Default) {

            delay(duration = 1000.milliseconds)
            dragGestureAction = null
        }
    }

    val screenSizeChanged = Modifier.onSizeChanged { size ->

        screenWidth = size.width.toFloat()
        screenHeight = size.height.toFloat()
    }

    val touchPointerInput = Modifier.pointerInput(screenSize) {

        awaitEachGesture {

            do {

                val event = awaitPointerEvent()

                touchCount = event.changes.size
            } while (event.changes.any { change -> change.pressed })
        }
    }

    val tapPointerInput = Modifier.pointerInput(screenSize) {

        detectTapGestures(
            onTap = { position: Offset ->

                onTapChanges(TapChanges.SingleTap(position))
            },
            onDoubleTap = { position: Offset ->

                val isBackward = position.x in 0.0F..(screenWidth / 2)
                val isForward = position.x in (screenWidth / 2)..screenWidth

                when {

                    config.isDoubleTapEnable && isBackward -> {

                        onTapChanges(TapChanges.BackwardTap(position))
                    }

                    config.isDoubleTapEnable && isForward -> {

                        onTapChanges(TapChanges.ForwardTap(position))
                    }

                    else -> onTapChanges(TapChanges.Unknown)
                }
            }
        )
    }

    val dragPointerInput = Modifier.pointerInput(screenSize, config) {

        detectVideoGestures(
            screenSize = screenSize,
            deadZone = config.gestureMargin / 100.0F,
            onDragStart = { offset: Offset ->

                onDragChanges(DragChanges.DragStart(position = offset))
            },
            onDragCancel = {

                onDragChanges(DragChanges.DragCanceled)
                resetDragGestureAction()
            },
            onDragEnd = {

                onDragChanges(DragChanges.DragEnded)
                resetDragGestureAction()
            }
        ) { change, dragAmount, direction ->

            swipeAmount += dragAmount

            when {

                isTwoTouch -> {

                    dragGestureAction = when (dragGestureAction) {

                        DragGestureAction.Transform -> dragGestureAction
                        else -> null
                    }

                    change.changedToUp()
                    return@detectVideoGestures
                }
            }

            val isTopHorizontal = direction == PointerDirection.HorizontalTop
            val isBottomHorizontal = direction == PointerDirection.HorizontalBottom
            val isLeftVertical = direction == PointerDirection.VerticalLeft
            val isRightVertical = direction == PointerDirection.VerticalRight

            when {

                isTopHorizontal && config.isHorizontalTopEnable -> when {

                    abs(x = swipeAmount.x) > config.horizontalTopMinimumSwipe -> {

                        when (dragGestureAction) {

                            null -> {

                                dragGestureAction = DragGestureAction.HorizontalTop
                                change.consume()
                            }

                            DragGestureAction.HorizontalTop -> {

                                onDragChanges(DragChanges.HorizontalTopChanges(swipeAmount.x))
                                change.consume()
                            }

                            else -> {}
                        }

                        swipeAmount = Offset.Zero
                    }
                }

                isBottomHorizontal && config.isHorizontalBottomEnable -> when {

                    abs(x = swipeAmount.x) > config.horizontalBottomMinimumSwipe -> {

                        when (dragGestureAction) {

                            null -> {

                                dragGestureAction = DragGestureAction.HorizontalBottom
                                change.consume()
                            }

                            DragGestureAction.HorizontalBottom -> {

                                onDragChanges(DragChanges.HorizontalBottomChanges(swipeAmount.x))
                                change.consume()
                            }

                            else -> {}
                        }

                        swipeAmount = Offset.Zero
                    }
                }

                isLeftVertical && config.isVerticalLeftEnable -> when {

                    abs(x = swipeAmount.y) > config.verticalLeftMinimumSwipe -> {

                        when (dragGestureAction) {

                            null -> {

                                dragGestureAction = DragGestureAction.VerticalLeft
                                change.consume()
                            }

                            DragGestureAction.VerticalLeft -> {

                                val brightness = when (dragAmount.y > 0.0F) {

                                    true -> ValueChange.Decreased
                                    false -> ValueChange.Increased
                                }

                                onDragChanges(DragChanges.VerticalLeftChanges(brightness))
                                change.consume()
                            }

                            else -> {}
                        }

                        swipeAmount = Offset.Zero
                    }
                }

                isRightVertical && config.isVerticalRightEnable -> when {

                    abs(x = swipeAmount.y) > config.verticalRightMinimumSwipe -> {

                        when (dragGestureAction) {

                            null -> {

                                dragGestureAction = DragGestureAction.VerticalRight
                                change.consume()
                            }

                            DragGestureAction.VerticalRight -> {

                                val volume = when (dragAmount.y > 0.0F) {

                                    true -> ValueChange.Decreased
                                    false -> ValueChange.Increased
                                }

                                onDragChanges(DragChanges.VerticalRightChanges(volume))
                                change.consume()
                            }

                            else -> {}
                        }

                        swipeAmount = Offset.Zero
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

        if (config.isPanEnable.not() && config.isZoomEnable.not()) return@rememberTransformableState

        when (isTwoTouch) {

            true -> when (dragGestureAction) {

                null -> {

                    if (config.isZoomEnable || config.isPanEnable) {

                        dragGestureAction = DragGestureAction.Transform
                    }
                }

                DragGestureAction.Transform -> {

                    val finalZoomChange = zoomChange.takeIf { config.isZoomEnable } ?: 0.0F
                    val finalPanChange = panChange.takeIf { config.isPanEnable } ?: Offset.Zero

                    if (config.isZoomEnable || config.isPanEnable) {

                        onDragChanges(DragChanges.TransformChanges(finalZoomChange, finalPanChange))
                    }

                    resetDragGestureAction()
                }

                else -> return@rememberTransformableState
            }

            false -> {

                when (dragGestureAction) {

                    DragGestureAction.Transform -> null
                    else -> {}
                }

                return@rememberTransformableState
            }
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

private enum class DragGestureAction {

    Transform,
    HorizontalTop,
    HorizontalBottom,
    VerticalLeft,
    VerticalRight;
}