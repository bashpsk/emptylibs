package io.bashpsk.emptylibs.gestureui.video

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import io.bashpsk.emptylibs.gestureui.video.DragGestureAction.Companion.hasHorizontalBottom
import io.bashpsk.emptylibs.gestureui.video.DragGestureAction.Companion.hasHorizontalTop
import io.bashpsk.emptylibs.gestureui.video.DragGestureAction.Companion.hasNull
import io.bashpsk.emptylibs.gestureui.video.DragGestureAction.Companion.hasVerticalLeft
import io.bashpsk.emptylibs.gestureui.video.DragGestureAction.Companion.hasVerticalRight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

/**
 * Creates and remembers a [VideoGestureBoxState] instance.
 *
 * This state object holds the configuration and current state for [VideoGestureBox].
 * It is designed to be remembered across recompositions and saved during configuration changes.
 *
 * @param config The [VideoGestureConfig] to configure the gesture behavior.
 * Defaults to a default [VideoGestureConfig] instance.
 * @return A remembered [VideoGestureBoxState] instance.
 */
@Composable
fun rememberVideoGestureBoxState(
    config: VideoGestureConfig = VideoGestureConfig()
): VideoGestureBoxState {

    val coroutineScope = rememberCoroutineScope()

    return retain(coroutineScope, config) {
        VideoGestureBoxState(coroutineScope = coroutineScope, config = config)
    }
}

/**
 * Holds the state and configuration for the [VideoGestureBox] composable.
 *
 * This class manages the current state of detected gestures, such as screen size,
 * active drag action, touch count, and swipe amount. It also provides helper functions
 * to determine gesture eligibility based on the provided [config].
 *
 * Instances of this class are typically created and remembered
 * using [rememberVideoGestureBoxState].
 *
 * @property coroutineScope The [CoroutineScope] used for managing timed operations like resetting
 * drag actions.
 * @property config The [VideoGestureConfig] that defines the behavior of the gestures.
 */
@Stable
class VideoGestureBoxState(
    private val coroutineScope: CoroutineScope,
    internal val config: VideoGestureConfig
) {

    /**
     * The size of the screen or the area where gestures are detected.
     * This is updated when the [VideoGestureBox] is laid out.
     */
    internal var screenSize by mutableStateOf(IntSize.Zero)

    /**
     * A [Job] that handles the delayed reset of the current drag action.
     * This is used to clear the [dragGestureAction] after a certain period of inactivity
     * during a transform gesture.
     */
    internal var resetDragActionJob by mutableStateOf<Job?>(null)

    /**
     * A [Job] that handles the delayed emission of a single tap gesture.
     * This allows the system to wait and see if a second tap occurs (forming a double tap)
     * before confirming the single tap.
     */
    private var tapJob by mutableStateOf<Job?>(null)

    /**
     * The currently active [DragGestureAction], if any.
     * This indicates the type of drag gesture being performed (e.g., horizontal top).
     */
    internal var dragGestureAction by mutableStateOf<DragGestureAction?>(null)

    /**
     * The number of touch pointers currently active on the screen.
     * Used to differentiate between single-finger and multi-finger (e.g., pinch-to-zoom) gestures.
     */
    internal var touchCount by mutableIntStateOf(0)

    /**
     * The timestamp of the last detected tap gesture.
     * Used to detect multi-tap gestures (e.g., double tap for seeking).
     */
    private var lastTapTime by mutableLongStateOf(0L)

    /**
     * The number of consecutive taps detected within the configured multi-tap timeout.
     */
    private var tapCount by mutableIntStateOf(0)

    /**
     * The accumulated swipe amount during a drag gesture.
     * This is reset when a drag gesture starts or ends.
     */
    internal var swipeAmount by mutableStateOf(Offset.Zero)

    /**
     * A lambda that is invoked during drag gestures.
     * It receives a [DragChanges] sealed class instance indicating the state and type of drag.
     */
    internal var onDragChanges: (changes: DragChanges) -> Unit = {}

    /**
     * A [TransformableState] that handles zoom and pan gestures.
     */
    val transformableState = TransformableState { _, zoomChange, panChange, _ ->

        if (hasTransform().not()) return@TransformableState

        when (touchCount) {

            2 -> when (dragGestureAction) {

                null -> {

                    dragGestureAction = DragGestureAction.Transform
                }

                DragGestureAction.Transform -> {

                    val newZoomChange = if (config.isZoomEnable) zoomChange else 1.0F
                    val newPanChange = if (config.isPanEnable) panChange else Offset.Zero

                    onDragChanges(DragChanges.TransformChanges(newZoomChange, newPanChange))
                    onResetDragGestureAction()
                }

                else -> return@TransformableState
            }

            else -> {

                when (dragGestureAction) {

                    DragGestureAction.Transform -> dragGestureAction = null
                    else -> {}
                }

                return@TransformableState
            }
        }
    }

    /**
     * Initiates a delayed reset of the [dragGestureAction].
     * If a new drag action occurs before the delay completes, the previous reset is canceled.
     * The delay is 750 milliseconds.
     */
    internal fun onResetDragGestureAction() {

        resetDragActionJob?.cancel()
        resetDragActionJob = coroutineScope.launch(context = Dispatchers.Default) {

            delay(duration = 750.milliseconds)
            onResetDragAction()
        }
    }

    /**
     * Resets the [dragGestureAction] to `null` immediately.
     */
    internal fun onResetDragAction() {

        dragGestureAction = null
    }

    /**
     * Handles a tap gesture and determines if it's a single tap or part of a multi-tap sequence
     * for seeking.
     *
     * @param position The [Offset] where the tap occurred.
     * @param onTapChanges Callback to notify about the detected tap type.
     */
    internal fun onTap(position: Offset, onTapChanges: (TapChanges) -> Unit) {

        val currentTime = System.currentTimeMillis()
        val tapInterval = currentTime - lastTapTime

        if (tapInterval <= config.doubleTapTimeoutMillis) {

            tapCount++
            tapJob?.cancel()
        } else tapCount = 1

        lastTapTime = currentTime

        if (tapCount == 1) tapJob = coroutineScope.launch {

            delay(duration = config.doubleTapTimeoutMillis.milliseconds)
            onTapChanges(TapChanges.SingleTap(position = position))
            tapCount = 0
        } else {

            when {

                hasBackwardTap(position) -> onTapChanges(TapChanges.BackwardTap(position))
                hasForwardTap(position) -> onTapChanges(TapChanges.ForwardTap(position))
                else -> onTapChanges(TapChanges.SingleTap(position))
            }

            tapCount = 0
        }
    }

    /**
     * Checks if a double tap at the given [position] should trigger a "backward" action.
     * This is true if double tap is enabled in the [config] and the tap occurs on the left half of
     * the screen.
     * @param position The [Offset] of the tap.
     * @return `true` if the tap is a backward tap, `false` otherwise.
     */
    internal fun hasBackwardTap(position: Offset): Boolean {

        return config.isDoubleTapEnable && position.x in 0.0F..(screenSize.width.toFloat() / 2)
    }

    /**
     * Checks if a double tap at the given [position] should trigger a "forward" action.
     * This is true if double tap is enabled in the [config] and the tap occurs on the right half of
     * the screen.
     * @param position The [Offset] of the tap.
     * @return `true` if the tap is a forward tap, `false` otherwise.
     */
    internal fun hasForwardTap(position: Offset): Boolean {

        return config.isDoubleTapEnable
                && position.x in (screenSize.width.toFloat() / 2)..screenSize.width.toFloat()
    }

    /**
     * Checks if the horizontal component of [swipeAmount] has exceeded the minimum threshold
     * defined in [config].
     * @return `true` if a horizontal swipe is detected, `false` otherwise.
     */
    internal fun hasHorizontalSwipe(): Boolean {

        return abs(x = swipeAmount.x) >= config.horizontalMinimumSwipe
    }

    /**
     * Checks if the vertical component of [swipeAmount] has exceeded the minimum threshold defined
     * in [config].
     * @return `true` if a vertical swipe is detected, `false` otherwise.
     */
    internal fun hasVerticalSwipe(): Boolean {

        return abs(x = swipeAmount.y) >= config.verticalMinimumSwipe
    }

    /**
     * Checks if a horizontal swipe at the top of the screen is enabled and currently allowed.
     * A horizontal top swipe is allowed if it's enabled in the [config] and either no drag action
     * is active or the active drag action is already [DragGestureAction.HorizontalTop].
     * @return `true` if a horizontal top swipe is allowed, `false` otherwise.
     */
    internal fun hasHorizontalTopSwipe(): Boolean {

        return config.isHorizontalTopEnable
                && (dragGestureAction.hasNull() || dragGestureAction.hasHorizontalTop())
    }

    /**
     * Checks if a horizontal swipe at the bottom of the screen is enabled and currently allowed.
     * A horizontal bottom swipe is allowed if it's enabled in the [config] and either no drag
     * action is active or the active drag action is already [DragGestureAction.HorizontalBottom].
     * @return `true` if a horizontal bottom swipe is allowed, `false` otherwise.
     */
    internal fun hasHorizontalBottomSwipe(): Boolean {

        return config.isHorizontalBottomEnable
                && (dragGestureAction.hasNull() || dragGestureAction.hasHorizontalBottom())
    }

    /**
     * Checks if a vertical swipe on the left side of the screen is enabled and currently allowed.
     * A vertical left swipe is allowed if it's enabled in the [config] and either no drag action
     * is active or the active drag action is already [DragGestureAction.VerticalLeft].
     * @return `true` if a vertical left swipe is allowed, `false` otherwise.
     */
    internal fun hasVerticalLeftSwipe(): Boolean {

        return config.isVerticalLeftEnable
                && (dragGestureAction.hasNull() || dragGestureAction.hasVerticalLeft())
    }

    /**
     * Checks if a vertical swipe on the right side of the screen is enabled and currently allowed.
     * A vertical right swipe is allowed if it's enabled in the [config] and either no drag action
     * is active or the active drag action is already [DragGestureAction.VerticalRight].
     * @return `true` if a vertical right swipe is allowed, `false` otherwise.
     */
    internal fun hasVerticalRightSwipe(): Boolean {

        return config.isVerticalRightEnable
                && (dragGestureAction.hasNull() || dragGestureAction.hasVerticalRight())
    }

    /**
     * Checks if pan or zoom gestures are enabled in the [config].
     * @return `true` if either Pan or Zoom is enabled, `false` otherwise.
     */
    internal fun hasTransform(): Boolean {

        return config.isPanEnable || config.isZoomEnable
    }

    /**
     * Called when a drag gesture starts. Resets the [swipeAmount].
     */
    internal fun onDragStart() {

        tapJob?.cancel()
        tapCount = 0
        onResetSwipeAmount()
    }

    /**
     * Called when a drag gesture ends. Resets the [swipeAmount] and initiates the reset of
     * [dragGestureAction].
     */
    internal fun onDragEnd() {

        tapJob?.cancel()
        tapCount = 0
        onResetSwipeAmount()
        onResetDragGestureAction()
    }

    /**
     * Resets the [swipeAmount] to [Offset.Zero].
     */
    internal fun onResetSwipeAmount() {

        swipeAmount = Offset.Zero
    }
}