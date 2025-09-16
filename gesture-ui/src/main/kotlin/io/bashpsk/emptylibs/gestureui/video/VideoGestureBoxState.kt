package io.bashpsk.emptylibs.gestureui.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.bashpsk.emptylibs.composeutils.offset.OffsetData
import io.bashpsk.emptylibs.composeutils.offset.toOffsetData
import io.bashpsk.emptylibs.composeutils.size.SizeData
import io.bashpsk.emptylibs.composeutils.size.toSizeData
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
    config: VideoGestureConfig = VideoGestureConfig(),
): VideoGestureBoxState {

    val coroutineScope = rememberCoroutineScope()

    return rememberSaveable(
        config,
        coroutineScope,
        saver = VideoGestureBoxState.StateSaver(coroutineScope = coroutineScope, config = config)
    ) {
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
    internal var screenSize by mutableStateOf(Size.Zero)

    /**
     * A [Job] that handles the delayed reset of the current drag action.
     * This is used to clear the [dragGestureAction] after a certain period of inactivity
     * during a transform gesture.
     */
    internal var resetDragActionJob by mutableStateOf<Job?>(null)

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
     * The accumulated swipe amount during a drag gesture.
     * This is reset when a drag gesture starts or ends.
     */
    internal var swipeAmount by mutableStateOf(Offset.Zero)

    /**
     * Initiates a delayed reset of the [dragGestureAction].
     * If a new drag action occurs before the delay completes, the previous reset is cancelled.
     * The delay is 1000 milliseconds.
     */
    internal fun onResetDragGestureAction() {

        resetDragActionJob?.cancel()
        resetDragActionJob = coroutineScope.launch(context = Dispatchers.Default) {

            delay(duration = 1000.milliseconds)
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
     * Checks if a double tap at the given [position] should trigger a "backward" action.
     * This is true if double tap is enabled in the [config] and the tap occurs on the left half of
     * the screen.
     * @param position The [Offset] of the tap.
     * @return `true` if the tap is a backward tap, `false` otherwise.
     */
    internal fun hasBackwardTap(position: Offset): Boolean {

        return config.isDoubleTapEnable && position.x in 0.0F..(screenSize.width / 2)
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
                && position.x in (screenSize.width / 2)..screenSize.width
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
     * @return `true` if either pan or zoom is enabled, `false` otherwise.
     */
    internal fun hasTransform(): Boolean {

        return config.isPanEnable || config.isZoomEnable
    }

    /**
     * Called when a drag gesture starts. Resets the [swipeAmount].
     */
    internal fun onDragStart() {

        onResetSwipeAmount()
    }

    /**
     * Called when a drag gesture ends. Resets the [swipeAmount] and initiates the reset of
     * [dragGestureAction].
     */
    internal fun onDragEnd() {

        onResetSwipeAmount()
        onResetDragGestureAction()
    }

    /**
     * Resets the [swipeAmount] to [Offset.Zero].
     */
    internal fun onResetSwipeAmount() {

        swipeAmount = Offset.Zero
    }

    companion object {

        private const val KEY_SCREEN_SIZE = "VIDEO-GESTURE-BOX-SCREEN-SIZE"
        private const val KEY_GESTURE_ACTION = "VIDEO-GESTURE-BOX-GESTURE-ACTION"
        private const val KEY_TOUCH_COUNT = "VIDEO-GESTURE-BOX-TOUCH-COUNT"
        private const val KEY_SWIPE_AMOUNT = "VIDEO-GESTURE-BOX-SWIPE-AMOUNT"

        fun StateSaver(
            coroutineScope: CoroutineScope,
            config: VideoGestureConfig
        ): Saver<VideoGestureBoxState, Any> = mapSaver(
            save = { state ->

                mapOf(
                    KEY_SCREEN_SIZE to state.screenSize.toSizeData(),
                    KEY_GESTURE_ACTION to state.dragGestureAction,
                    KEY_TOUCH_COUNT to state.touchCount,
                    KEY_SWIPE_AMOUNT to state.swipeAmount.toOffsetData()
                )
            },
            restore = { elements ->

                VideoGestureBoxState(coroutineScope = coroutineScope, config = config).apply {

                    screenSize = (elements[KEY_SCREEN_SIZE] as SizeData).toSize()
                    dragGestureAction = elements[KEY_GESTURE_ACTION] as DragGestureAction?
                    touchCount = elements[KEY_TOUCH_COUNT] as Int
                    swipeAmount = (elements[KEY_SWIPE_AMOUNT] as OffsetData).toOffset()
                }
            }
        )
    }
}