package io.bashpsk.emptylibs.gestureui.video

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.positionChangedIgnoreConsumed
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import kotlin.math.abs
import kotlin.math.sign

internal sealed interface PointerSlopResult {

    data class Tap(val change: PointerInputChange) : PointerSlopResult

    data class SlopReached(val change: PointerInputChange, val slop: Offset) : PointerSlopResult

    data object Cancelled : PointerSlopResult
}

internal suspend inline fun AwaitPointerEventScope.awaitPointerSlopOrTap(
    pointerId: PointerId,
    pointerType: PointerType,
    pointerDirectionConfig: PointerDirectionConfig,
    onPointerSlopReached: (PointerInputChange, Offset) -> Unit = { _, _ -> },
): PointerSlopResult {
    if (currentEvent.isPointerUp(pointerId)) {
        return PointerSlopResult.Cancelled
    }
    val touchSlop = viewConfiguration.pointerSlop(pointerType)
    var pointer: PointerId = pointerId
    var totalPositionChange = Offset.Zero

    while (true) {
        val event = awaitPointerEvent()
        val dragEvent = event.changes.fastFirstOrNull { it.id == pointer } ?: return PointerSlopResult.Cancelled
        if (dragEvent.isConsumed) {
            return PointerSlopResult.Cancelled
        } else if (dragEvent.changedToUpIgnoreConsumed()) {
            val otherDown = event.changes.fastFirstOrNull { it.pressed }
            if (otherDown == null) {
                // This is the last "up" - it's a tap!
                return PointerSlopResult.Tap(dragEvent)
            } else {
                pointer = otherDown.id
            }
        } else {
            val currentPosition = dragEvent.position
            val previousPosition = dragEvent.previousPosition

            val positionChange = currentPosition - previousPosition

            totalPositionChange += positionChange

            val inDirection = pointerDirectionConfig.calculateDeltaChange(
                totalPositionChange
            )

            if (inDirection < touchSlop) {
                // verify that nothing else consumed the drag event
                awaitPointerEvent(PointerEventPass.Final)
                if (dragEvent.isConsumed) {
                    return PointerSlopResult.Cancelled
                }
            } else {
                val postSlopOffset = pointerDirectionConfig.calculatePostSlopOffset(
                    totalPositionChange,
                    touchSlop
                )

                onPointerSlopReached(
                    dragEvent,
                    postSlopOffset
                )
                if (dragEvent.isConsumed) {
                    return PointerSlopResult.SlopReached(dragEvent, postSlopOffset)
                } else {
                    totalPositionChange = Offset.Zero
                }
            }
        }
    }
}

internal suspend fun AwaitPointerEventScope.drag(
    pointerId: PointerId,
    onDrag: (PointerInputChange) -> Unit
): Boolean {
    var pointer = pointerId
    while (true) {
        val change = awaitDragOrCancellation(pointer) ?: return false

        if (change.changedToUpIgnoreConsumed()) {
            return true
        }

        onDrag(change)
        pointer = change.id
    }
}

internal suspend fun AwaitPointerEventScope.awaitDragOrCancellation(
    pointerId: PointerId,
): PointerInputChange? {
    if (currentEvent.isPointerUp(pointerId)) {
        return null // The pointer has already been lifted, so the gesture is canceled
    }
    val change = awaitDragOrUp(pointerId) { it.positionChangedIgnoreConsumed() }
    return if (change?.isConsumed == false) change else null
}

private suspend inline fun AwaitPointerEventScope.awaitDragOrUp(
    pointerId: PointerId,
    hasDragged: (PointerInputChange) -> Boolean
): PointerInputChange? {
    var pointer = pointerId
    while (true) {
        val event = awaitPointerEvent()
        val dragEvent = event.changes.fastFirstOrNull { it.id == pointer } ?: return null
        if (dragEvent.changedToUpIgnoreConsumed()) {
            val otherDown = event.changes.fastFirstOrNull { it.pressed }
            if (otherDown == null) {
                // This is the last "up"
                return dragEvent
            } else {
                pointer = otherDown.id
            }
        } else if (hasDragged(dragEvent)) {
            return dragEvent
        }
    }
}

internal suspend inline fun AwaitPointerEventScope.awaitPointerSlopOrCancellation(
    pointerId: PointerId,
    pointerType: PointerType,
    pointerDirectionConfig: PointerDirectionConfig,
    onPointerSlopReached: (PointerInputChange, Offset) -> Unit,
): PointerInputChange? {
    if (currentEvent.isPointerUp(pointerId)) {
        return null // The pointer has already been lifted, so the gesture is canceled
    }
    val touchSlop = viewConfiguration.pointerSlop(pointerType)
    var pointer: PointerId = pointerId
    var totalPositionChange = Offset.Zero

    while (true) {
        val event = awaitPointerEvent()
        val dragEvent = event.changes.fastFirstOrNull { it.id == pointer } ?: return null
        if (dragEvent.isConsumed) {
            return null
        } else if (dragEvent.changedToUpIgnoreConsumed()) {
            val otherDown = event.changes.fastFirstOrNull { it.pressed }
            if (otherDown == null) {
                // This is the last "up"
                return null
            } else {
                pointer = otherDown.id
            }
        } else {
            val currentPosition = dragEvent.position
            val previousPosition = dragEvent.previousPosition

            val positionChange = currentPosition - previousPosition

            totalPositionChange += positionChange

            val inDirection = pointerDirectionConfig.calculateDeltaChange(
                totalPositionChange
            )

            if (inDirection < touchSlop) {
                // verify that nothing else consumed the drag event
                awaitPointerEvent(PointerEventPass.Final)
                if (dragEvent.isConsumed) {
                    return null
                }
            } else {
                val postSlopOffset = pointerDirectionConfig.calculatePostSlopOffset(
                    totalPositionChange,
                    touchSlop
                )

                onPointerSlopReached(
                    dragEvent,
                    postSlopOffset
                )
                if (dragEvent.isConsumed) {
                    return dragEvent
                } else {
                    totalPositionChange = Offset.Zero
                }
            }
        }
    }
}

internal interface PointerDirectionConfig {
    fun calculateDeltaChange(offset: Offset): Float
    fun calculatePostSlopOffset(
        totalPositionChange: Offset,
        touchSlop: Float
    ): Offset
}

internal val HorizontalPointerDirectionConfig = object : PointerDirectionConfig {
    override fun calculateDeltaChange(offset: Offset): Float = abs(offset.x)

    override fun calculatePostSlopOffset(
        totalPositionChange: Offset,
        touchSlop: Float
    ): Offset {
        val finalMainPositionChange = totalPositionChange.x -
                (sign(totalPositionChange.x) * touchSlop)
        return Offset(finalMainPositionChange, totalPositionChange.y)
    }
}

internal val VerticalPointerDirectionConfig = object : PointerDirectionConfig {
    override fun calculateDeltaChange(offset: Offset): Float = abs(offset.y)

    override fun calculatePostSlopOffset(
        totalPositionChange: Offset,
        touchSlop: Float
    ): Offset {
        val finalMainPositionChange = totalPositionChange.y -
                (sign(totalPositionChange.y) * touchSlop)
        return Offset(totalPositionChange.x, finalMainPositionChange)
    }
}

internal val BidirectionalPointerDirectionConfig = object : PointerDirectionConfig {
    override fun calculateDeltaChange(offset: Offset): Float = offset.getDistance()

    override fun calculatePostSlopOffset(
        totalPositionChange: Offset,
        touchSlop: Float
    ): Offset {
        val touchSlopOffset =
            totalPositionChange / calculateDeltaChange(totalPositionChange) * touchSlop
        return totalPositionChange - touchSlopOffset
    }
}

internal fun Orientation.toPointerDirectionConfig(): PointerDirectionConfig =
    if (this == Orientation.Vertical) VerticalPointerDirectionConfig
    else HorizontalPointerDirectionConfig

private fun PointerEvent.isPointerUp(pointerId: PointerId): Boolean =
    changes.fastFirstOrNull { it.id == pointerId }?.pressed != true

private val mouseSlop = 0.125.dp
private val defaultTouchSlop = 18.dp // The default touch slop on Android devices
private val mouseToTouchSlopRatio = mouseSlop / defaultTouchSlop

internal fun ViewConfiguration.pointerSlop(pointerType: PointerType): Float {
    return when (pointerType) {
        PointerType.Mouse -> touchSlop * mouseToTouchSlopRatio
        else -> touchSlop
    }
}