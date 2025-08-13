package io.bashpsk.emptylibs.gestureui.video

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.positionChange
import kotlin.math.abs

/**
 * Percentage of the screen to be considered a dead zone.
 * Gestures starting within this zone might be ignored or handled differently.
 * This is used to prevent accidental gesture detection at the screen edges or center.
 */
private const val DEAD_ZONE_PERCENTAGE = 0.05F // 5%

/**
 * Detects drag gestures in a video player-like interface.
 *
 * This function analyzes pointer input to determine the direction and amount of drag,
 * considering dead zones around the edges and center of the screen. It aims to provide
 * a more intuitive gesture experience for video controls like seeking, volume adjustment,
 * or brightness control.
 *
 * The gesture detection logic is as follows:
 * 1. **Dead Zones:** Defines dead zones at the top, bottom, left, right edges, and the center
 *    of the screen. If a drag gesture starts within these zones, its direction is initially
 *    marked as [PointerDirection.Unknown].
 * 2. **Initial Direction (Outside Dead Zones):** If the drag starts outside the dead zones,
 *    the initial direction is determined based on the pointer's position relative to the
 *    screen's center and which axis (horizontal or vertical) has a larger displacement
 *    from the center.
 *    - If horizontally dominant and in the top half: [PointerDirection.HorizontalTop]
 *    - If horizontally dominant and in the bottom half: [PointerDirection.HorizontalBottom]
 *    - If vertically dominant and in the left half: [PointerDirection.VerticalLeft]
 *    - If vertically dominant and in the right half: [PointerDirection.VerticalRight]
 * 3. **Drag Locking:** Once a drag starts with a determined direction
 *      (not [PointerDirection.Unknown]),
 *    the gesture can "lock" to a more specific direction if the drag movement significantly
 *    changes axis. For example, if the initial drag is [PointerDirection.HorizontalTop]
 *    (suggesting seeking in the top part of the screen) and the user then drags predominantly
 *    vertically, the direction might change to [PointerDirection.VerticalLeft] or
 *    [PointerDirection.VerticalRight] based on the initial touch's horizontal position.
 *    This helps prevent accidental diagonal drags from triggering unintended actions.
 * 4. **Callbacks:**
 *    - `onDragStart`: Called when a drag gesture is initiated after the initial touch slop.
 *    - `onDragAmount`: Called repeatedly as the pointer moves during a drag. It provides the
 *      [PointerInputChange], the drag amount [Offset], and the currently determined
 *      [PointerDirection].
 */
@OptIn(ExperimentalComposeUiApi::class)
internal suspend fun PointerInputScope.detectVideoGestures(
    screenSize: Size = Size.Zero,
    deadZone: Float? = DEAD_ZONE_PERCENTAGE,
    onDragStart: (position: Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {},
    onDragAmount: (
        change: PointerInputChange,
        dragAmount: Offset,
        direction: PointerDirection
    ) -> Unit
) {

    if (screenSize == Size.Zero) return

    val effectiveDeadZone = deadZone ?: DEAD_ZONE_PERCENTAGE

    awaitEachGesture {

        val downEvent = awaitFirstDown(requireUnconsumed = false)
        val pointerId = downEvent.id
        var currentSlop = Offset.Zero

        val viewWidth = screenSize.width
        val viewHeight = screenSize.height

        val deadZoneWidth = viewWidth * effectiveDeadZone
        val deadZoneHeight = viewHeight * effectiveDeadZone

        val topDeadZoneEnd = deadZoneHeight
        val bottomDeadZoneStart = viewHeight - deadZoneHeight
        val leftDeadZoneEnd = deadZoneWidth
        val rightDeadZoneStart = viewWidth - deadZoneWidth

        val centerHorizontalMargin = viewHeight * effectiveDeadZone
        val centerHorizontalStart = (viewHeight - centerHorizontalMargin) / 2F
        val centerHorizontalEnd = centerHorizontalStart + centerHorizontalMargin

        val centerVerticalMargin = viewWidth * effectiveDeadZone
        val centerVerticalStart = (viewWidth - centerVerticalMargin) / 2F
        val centerVerticalEnd = centerVerticalStart + centerVerticalMargin

        val initialPointerChange = awaitPointerSlopOrCancellation(
            pointerId = pointerId,
            pointerType = downEvent.type,
            pointerDirectionConfig = BidirectionalPointerDirectionConfig
        ) { change, slopAmount ->

            change.consume()
            currentSlop = slopAmount
        }

        initialPointerChange?.let { inputChange ->

            val touchX = inputChange.position.x
            val touchY = inputChange.position.y

            val isInTopDeadZone = touchY < topDeadZoneEnd
            val isInBottomDeadZone = touchY >= bottomDeadZoneStart
            val isInLeftDeadZone = touchX < leftDeadZoneEnd
            val isInRightDeadZone = touchX >= rightDeadZoneStart

            val isInCenterHorizontalDeadZone = touchY >= centerHorizontalStart
                    && touchY < centerHorizontalEnd

            val isInCenterVerticalDeadZone = touchX >= centerVerticalStart
                    && touchX < centerVerticalEnd

            val isStartedInDeadZone = isInTopDeadZone || isInBottomDeadZone || isInLeftDeadZone
                    || isInRightDeadZone || isInCenterHorizontalDeadZone
                    || isInCenterVerticalDeadZone

            val initialDirection = when (isStartedInDeadZone) {

                true -> PointerDirection.Unknown

                else -> {

                    val isTopHalf = touchY < viewHeight / 2F
                    val isLeftHalf = touchX < viewWidth / 2F
                    val deltaXFromCenter = touchX - (viewWidth / 2F)
                    val deltaYFromCenter = touchY - (viewHeight / 2F)
                    val isHorizontalDominant = abs(deltaXFromCenter) > abs(deltaYFromCenter)

                    when {

                        isHorizontalDominant && isTopHalf -> PointerDirection.HorizontalTop
                        isHorizontalDominant -> PointerDirection.HorizontalBottom
                        isLeftHalf -> PointerDirection.VerticalLeft
                        else -> PointerDirection.VerticalRight
                    }
                }
            }

            onDragStart(inputChange.position)
            onDragAmount(inputChange, currentSlop, initialDirection)

            when (initialDirection) {

                PointerDirection.Unknown -> {

                    val wasDragSuccessful = drag(pointerId) { change ->

                        onDragAmount(change, change.positionChange(), PointerDirection.Unknown)
                        change.consume()
                    }

                    if (wasDragSuccessful) onDragEnd() else onDragCancel()
                }

                else -> {

                    var currentDirection = initialDirection

                    val wasDragSuccessful = drag(pointerId) { change ->

                        val dragPosition = change.positionChange()
                        val isDragValid = dragPosition.x != 0F || dragPosition.y != 0F

                        (isDragValid).takeIf { isValid -> isValid }?.let { _ ->

                            val isDragHorizontal = abs(dragPosition.x) > abs(dragPosition.y)
                            val isDragVertical = abs(dragPosition.y) > abs(dragPosition.x)

                            val isHorizontal = currentDirection.hasHorizontal()
                            val isVertical = currentDirection.hasVertical()

                            val isLockVerticalDrag = isHorizontal && isDragVertical
                            val isLockHorizontalDrag = isVertical && isDragHorizontal
                            val isDragLocked = isLockVerticalDrag || isLockHorizontalDrag

                            (isDragLocked).takeIf { isLocked -> isLocked }?.let { _ ->

                                val initialTouchIsInLeftHalf = touchX < viewWidth / 2F
                                val initialTouchIsInTopHalf = touchY < viewHeight / 2F

                                currentDirection = when {

                                    isDragVertical -> when {

                                        initialTouchIsInLeftHalf -> PointerDirection.VerticalLeft
                                        else -> PointerDirection.VerticalRight
                                    }

                                    isDragHorizontal -> when {

                                        initialTouchIsInTopHalf -> PointerDirection.HorizontalTop
                                        else -> PointerDirection.HorizontalBottom
                                    }

                                    else -> currentDirection
                                }
                            }
                        }

                        onDragAmount(change, change.positionChange(), currentDirection)
                        change.consume()
                    }

                    if (wasDragSuccessful) onDragEnd() else onDragCancel()
                }
            }
        }
    }
}