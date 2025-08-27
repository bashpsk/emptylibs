package io.bashpsk.emptylibs.imagekrop.offset

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.bashpsk.emptylibs.imagekrop.crop.KropCorner

internal fun Offset.toCenter(size: Size): Offset {

    return Offset(x = x + size.width / 2, y = y + size.height / 2)
}

internal fun Offset.toTopLeft(size: Size): Offset {

    return Offset(x = x - size.width / 2, y = y - size.height / 2)
}

internal fun Offset.toTopRight(size: Size): Offset {

    return Offset(x = x + size.width, y = y)
}

internal fun Offset.toBottomLeft(size: Size): Offset {

    return Offset(x = x, y = y + size.height)
}

internal fun Offset.toBottomRight(size: Size): Offset {

    return Offset(x = x + size.width, y = y + size.height)
}

internal fun Offset.toTopCenter(size: Size): Offset {

    return Offset(x = x + size.width / 2, y = y)
}

internal fun Offset.toBottomCenter(size: Size): Offset {

    return Offset(x = x + size.width / 2, y = y + size.height)
}

internal fun Offset.toLeftCenter(size: Size): Offset {

    return Offset(x = x, y = y + size.height / 2)
}

internal fun Offset.toRightCenter(size: Size): Offset {

    return Offset(x = x + size.width, y = y + size.height / 2)
}

/**
 * Checks if this offset has neared another offset within a specified threshold.
 *
 * @param point The other offset to compare with.
 * @param threshold The maximum distance allowed for the offsets to be considered "neared".
 * Defaults to 24.0F.
 * @return `true` if the distance between this offset and the given point is less than or equal
 * to the threshold, `false` otherwise.
 */
internal fun Offset.hasNeared(point: Offset, threshold: Float = 24.0F): Boolean {

    return (this - point).getDistance() <= threshold
}

internal fun Size.itemRect(position: Offset): Rect {

    return Rect(offset = position, size = this)
}

fun Size.coerceAtLeast(width: Float, height: Float): Size {

    return Size(this.width.coerceAtLeast(width), this.height.coerceAtLeast(height))
}

/**
 * Determines which KropCorner is tapped based on the click position and the `Rect`'s bounds.
 *
 * This function checks if the `clickPosition` is within a certain `threshold` of any of the
 * `Rect`'s corners or the midpoints of its edges.
 *
 * @receiver The `Rect` representing the bounds of the item being checked.
 * @param clickPosition The coordinates of the click.
 * @param threshold The maximum distance from a corner or edge midpoint for a tap to be considered
 * on that corner/edge. Defaults to 24.0F. A smaller value requires more precise tapping.
 * @return The `KropCorner` that was tapped, or `null` if the tap is not near any corner or
 * edge midpoint within the specified `threshold`.
 */
internal fun Rect.getKropCorner(
    clickPosition: Offset,
    threshold: Float = 24.0F
): KropCorner? {

    val topCenter = Offset(x = center.x, y = top)
    val bottomCenter = Offset(x = center.x, y = bottom)
    val leftCenter = Offset(x = left, y = center.y)
    val rightCenter = Offset(x = right, y = center.y)

    return when {

        clickPosition.hasNeared(topLeft, threshold) -> KropCorner.TOP_LEFT
        clickPosition.hasNeared(topRight, threshold) -> KropCorner.TOP_RIGHT
        clickPosition.hasNeared(bottomLeft, threshold) -> KropCorner.BOTTOM_LEFT
        clickPosition.hasNeared(bottomRight, threshold) -> KropCorner.BOTTOM_RIGHT
        clickPosition.hasNeared(topCenter, threshold) -> KropCorner.TOP_CENTRE
        clickPosition.hasNeared(bottomCenter, threshold) -> KropCorner.BOTTOM_CENTRE
        clickPosition.hasNeared(leftCenter, threshold) -> KropCorner.LEFT_CENTRE
        clickPosition.hasNeared(rightCenter, threshold) -> KropCorner.RIGHT_CENTRE
        else -> null
    }
}