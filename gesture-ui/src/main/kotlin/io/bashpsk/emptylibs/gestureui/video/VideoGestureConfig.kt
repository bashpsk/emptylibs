package io.bashpsk.emptylibs.gestureui.video

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Configuration class for video gesture controls.
 *
 * This class defines the behavior of various gestures that can be performed on a video player.
 *
 * @property isDoubleTapEnable Whether double-tap gestures are enabled. Defaults to `true`.
 * @property isHorizontalTopEnable Whether horizontal swipe gestures in the top area are enabled.
 * Defaults to `true`.
 * @property isHorizontalBottomEnable Whether horizontal swipe gestures in the bottom area are
 * enabled. Defaults to `true`.
 * @property isVerticalLeftEnable Whether vertical swipe gestures in the left area are enabled.
 * Defaults to `true`.
 * @property isVerticalRightEnable Whether vertical swipe gestures in the right area are enabled.
 * Defaults to `true`.
 * @property isZoomEnable Whether pinch-to-zoom gestures are enabled. Defaults to `true`.
 * @property isPanEnable Whether pan gestures are enabled. Defaults to `true`.
 * @property horizontalTopMinimumSwipe The minimum swipe distance (in pixels) required to trigger a
 * horizontal top gesture. Defaults to `25`.
 * @property horizontalBottomMinimumSwipe The minimum swipe distance (in pixels) required to trigger
 * a horizontal bottom gesture. Defaults to `25`.
 * @property verticalLeftMinimumSwipe The minimum swipe distance (in pixels) required to trigger a
 * vertical left gesture. Defaults to `25`.
 * @property verticalRightMinimumSwipe The minimum swipe distance (in pixels) required to trigger a
 * vertical right gesture. Defaults to `25`.
 * @property gestureMargin The margin (in pixels) from the edges of the video player where gestures
 * are still recognized. Defaults to `5`.
 */
@Immutable
@Parcelize
@Serializable
data class VideoGestureConfig(
    val isDoubleTapEnable: Boolean = true,
    val isHorizontalTopEnable: Boolean = true,
    val isHorizontalBottomEnable: Boolean = true,
    val isVerticalLeftEnable: Boolean = true,
    val isVerticalRightEnable: Boolean = true,
    val isZoomEnable: Boolean = true,
    val isPanEnable: Boolean = true,
    val horizontalTopMinimumSwipe: Int = 25,
    val horizontalBottomMinimumSwipe: Int = 25,
    val verticalLeftMinimumSwipe: Int = 25,
    val verticalRightMinimumSwipe: Int = 25,
    val gestureMargin: Int = 5
) : Parcelable