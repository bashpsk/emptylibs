package io.bashpsk.emptylibs.imagekrop.offset

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.bashpsk.emptylibs.composeutils.offset.hasNeared
import io.bashpsk.emptylibs.imagekrop.crop.KropCorner

/**
 * Converts a top-left offset to a center offset based on the given size.
 *
 * This function assumes the current `Offset` represents the top-left corner of a rectangle.
 * It calculates the center of that rectangle by adding half of the `size`'s width to the
 * x-coordinate and half of the `size`'s height to the y-coordinate.
 *
 * @param size The size of the rectangle.
 * @return A new `Offset` representing the center of the rectangle.
 */
internal fun Offset.toCenter(size: Size): Offset {

    return Offset(x = x + size.width / 2, y = y + size.height / 2)
}

/**
 * Calculates the top-left offset of a rectangle given its center offset and size.
 *
 * This function assumes that the receiver `Offset` represents the center point of the rectangle.
 * It then subtracts half of the width and half of the height from the center's x and y coordinates,
 * respectively, to find the coordinates of the top-left corner.
 *
 * @param size The `Size` of the rectangle (width and height).
 * @return An `Offset` representing the coordinates of the top-left corner of the rectangle.
 */
internal fun Offset.toTopLeft(size: Size): Offset {

    return Offset(x = x - size.width / 2, y = y - size.height / 2)
}

/**
 * Calculates the top-right corner `Offset` of a rectangle based on its top-left `Offset` and
 * `Size`.
 *
 * This function assumes the current `Offset` represents the top-left corner of a rectangle.
 * It adds the width of the rectangle to the x-coordinate of the current `Offset` to find
 * the x-coordinate of the top-right corner. The y-coordinate remains the same as the top-left
 * corner.
 *
 * @receiver The `Offset` representing the top-left corner of the rectangle.
 * @param size The `Size` of the rectangle (width and height).
 * @return A new `Offset` representing the top-right corner of the rectangle.
 */
internal fun Offset.toTopRight(size: Size): Offset {

    return Offset(x = x + size.width, y = y)
}

/**
 * Calculates the bottom-left `Offset` of a rectangle relative to this `Offset`,
 * assuming this `Offset` represents the top-left corner of the rectangle.
 *
 * @param size The `Size` of the rectangle.
 * @return A new `Offset` representing the bottom-left corner of the rectangle.
 * The x-coordinate remains the same as this `Offset`, and the y-coordinate is
 * this `Offset`'s y-coordinate plus the `size.height`.
 */
internal fun Offset.toBottomLeft(size: Size): Offset {

    return Offset(x = x, y = y + size.height)
}

/**
 * Calculates the bottom-right corner `Offset` of a rectangle given its top-left `Offset` and
 * `Size`.
 *
 * This function is useful for determining the coordinates of the bottom-right point of a
 * rectangular
 * area when you know its starting point (top-left) and its dimensions.
 *
 * @receiver The `Offset` representing the top-left corner of the rectangle.
 * @param size The `Size` of the rectangle (width and height).
 * @return A new `Offset` representing the bottom-right corner of the rectangle.
 */
internal fun Offset.toBottomRight(size: Size): Offset {

    return Offset(x = x + size.width, y = y + size.height)
}

/**
 * Calculates the top-center offset based on the current offset and a given size.
 *
 * This function assumes the current offset represents the top-left corner.
 * It returns a new `Offset` where the x-coordinate is shifted to the center of the
 * provided `size`'s width, and the y-coordinate remains the same.
 *
 * @param size The `Size` object used to determine the horizontal shift.
 * @return A new `Offset` representing the top-center position.
 */
internal fun Offset.toTopCenter(size: Size): Offset {

    return Offset(x = x + size.width / 2, y = y)
}

/**
 * Calculates the bottom-center offset based on the current offset and a given size.
 *
 * This function assumes the current offset represents the top-left corner of a rectangle.
 * It then calculates the coordinates of the bottom-center point of that rectangle.
 *
 * @param size The size of the rectangle.
 * @return A new `Offset` representing the bottom-center point of the rectangle.
 */
internal fun Offset.toBottomCenter(size: Size): Offset {

    return Offset(x = x + size.width / 2, y = y + size.height)
}

/**
 * Converts this offset to the left center of a rectangle of the given size.
 *
 * @param size The size of the rectangle.
 * @return The offset of the left center of the rectangle.
 */
internal fun Offset.toLeftCenter(size: Size): Offset {

    return Offset(x = x, y = y + size.height / 2)
}

/**
 * Calculates the coordinates of the right center of a rectangle, given its top-left corner
 * and size.
 *
 * This function is useful for determining the position of the right-middle handle or anchor point
 * of a resizable or movable UI element.
 *
 * @receiver The `Offset` representing the top-left corner of the rectangle.
 * @param size The `Size` of the rectangle (width and height).
 * @return An `Offset` object representing the coordinates of the right center of the rectangle.
 * The x-coordinate is the original x plus the width, and the y-coordinate is the original y plus
 * half the height.
 */
internal fun Offset.toRightCenter(size: Size): Offset {

    return Offset(x = x + size.width, y = y + size.height / 2)
}

/**
 * Creates a `Rect` from this `Size` at the given `position`.
 *
 * The `position` is treated as the top-left corner of the `Rect`.
 *
 * @param position The `Offset` representing the top-left corner of the `Rect`.
 * @return A `Rect` with its top-left corner at `position` and dimensions matching this `Size`.
 */
internal fun Size.itemRect(position: Offset): Rect {

    return Rect(offset = position, size = this)
}

/**
 * Coerces the width and height of this Size to be at least the specified minimum width and height.
 *
 * @param width The minimum width.
 * @param height The minimum height.
 * @return A new Size with width and height coerced to be at least the specified minimums.
 */
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