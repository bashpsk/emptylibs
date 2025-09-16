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

@Stable
class VideoGestureBoxState(
    private val coroutineScope: CoroutineScope,
    internal val config: VideoGestureConfig
) {

    internal var screenSize by mutableStateOf(Size.Zero)

    internal var resetDragActionJob by mutableStateOf<Job?>(null)

    internal var dragGestureAction by mutableStateOf<DragGestureAction?>(null)

    internal var touchCount by mutableIntStateOf(0)

    internal var swipeAmount by mutableStateOf(Offset.Zero)

    internal fun onResetDragGestureAction() {

        resetDragActionJob?.cancel()
        resetDragActionJob = coroutineScope.launch(context = Dispatchers.Default) {

            delay(duration = 1000.milliseconds)
            onResetDragAction()
        }
    }

    internal fun onResetDragAction() {

        dragGestureAction = null
    }

    internal fun hasBackwardTap(position: Offset): Boolean {

        return config.isDoubleTapEnable && position.x in 0.0F..(screenSize.width / 2)
    }

    internal fun hasForwardTap(position: Offset): Boolean {

        return config.isDoubleTapEnable
                && position.x in (screenSize.width / 2)..screenSize.width
    }

    internal fun hasHorizontalSwipe(): Boolean {

        return abs(x = swipeAmount.x) >= config.horizontalMinimumSwipe
    }

    internal fun hasVerticalSwipe(): Boolean {

        return abs(x = swipeAmount.y) >= config.verticalMinimumSwipe
    }

    internal fun hasHorizontalTopSwipe(): Boolean {

        return config.isHorizontalTopEnable
                && (dragGestureAction.hasNull() || dragGestureAction.hasHorizontalTop())
    }

    internal fun hasHorizontalBottomSwipe(): Boolean {

        return config.isHorizontalBottomEnable
                && (dragGestureAction.hasNull() || dragGestureAction.hasHorizontalBottom())
    }

    internal fun hasVerticalLeftSwipe(): Boolean {

        return config.isVerticalLeftEnable
                && (dragGestureAction.hasNull() || dragGestureAction.hasVerticalLeft())
    }

    internal fun hasVerticalRightSwipe(): Boolean {

        return config.isVerticalRightEnable
                && (dragGestureAction.hasNull() || dragGestureAction.hasVerticalRight())
    }

    internal fun hasTransform(): Boolean {

        return config.isPanEnable || config.isZoomEnable
    }

    internal fun onDragStart() {

        onResetSwipeAmount()
    }

    internal fun onDragEnd() {

        onResetSwipeAmount()
        onResetDragGestureAction()
    }

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