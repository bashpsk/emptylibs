package io.bashpsk.emptylibs.imageedit.extension

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.bashpsk.emptylibs.composeutils.offset.hasNeared
import io.bashpsk.emptylibs.imageedit.edit.EditItemCorner
import io.bashpsk.emptylibs.imageedit.edit.ImageEditItems

/**
 * Converts a top-left offset to a center offset based on the given size.
 *
 * This function assumes the current `Offset` represents the top-left corner of a rectangle.
 * It calculates the center of that rectangle by adding half of the `size.width` to the x-coordinate
 * and half of the `size.height` to the y-coordinate.
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
 * respectively, to determine the coordinates of the top-left corner.
 *
 * @param size The size (width and height) of the rectangle.
 * @return An `Offset` representing the top-left corner of the rectangle.
 */
internal fun Offset.toTopLeft(size: Size): Offset {

    return Offset(x = x - size.width / 2, y = y - size.height / 2)
}

/**
 * Calculates the top-right corner of a rectangle, given its top-left corner (this offset) and its
 * size.
 *
 * @param size The size of the rectangle.
 * @return The offset representing the top-right corner of the rectangle.
 */
internal fun Offset.toTopRight(size: Size): Offset {

    return Offset(x = x + size.width, y = y)
}

/**
 * Calculates the bottom-left corner offset of a rectangle given its top-left corner and size.
 *
 * This function assumes the current `Offset` represents the top-left corner of a rectangle.
 * It then calculates the coordinates of the bottom-left corner of that rectangle.
 *
 * @param size The `Size` of the rectangle (width and height).
 * @return An `Offset` representing the bottom-left corner of the rectangle.
 * The x-coordinate remains the same as the original offset.
 * The y-coordinate is the original y-coordinate plus the height of the rectangle.
 */
internal fun Offset.toBottomLeft(size: Size): Offset {

    return Offset(x = x, y = y + size.height)
}

/**
 * Calculates the bottom-right corner of a rectangle, given its top-left corner (this offset)
 * and its size.
 *
 * @param size The size (width and height) of the rectangle.
 * @return An [Offset] representing the coordinates of the bottom-right corner.
 */
internal fun Offset.toBottomRight(size: Size): Offset {

    return Offset(x = x + size.width, y = y + size.height)
}

/**
 * Converts this offset to represent the top-center point of a rectangle
 * with the given size, assuming this offset is the top-left corner of that rectangle.
 *
 * @param size The size of the rectangle.
 * @return A new `Offset` representing the top-center point.
 */
internal fun Offset.toTopCenter(size: Size): Offset {

    return Offset(x = x + size.width / 2, y = y)
}

/**
 * Calculates the bottom center coordinates of a rectangle given its top-left corner and size.
 *
 * This function assumes the current `Offset` represents the top-left corner of the rectangle.
 *
 * @param size The `Size` of the rectangle (width and height).
 * @return An `Offset` representing the coordinates of the bottom center of the rectangle.
 */
internal fun Offset.toBottomCenter(size: Size): Offset {

    return Offset(x = x + size.width / 2, y = y + size.height)
}

/**
 * Adjusts this offset to the left-center position relative to a given size.
 *
 * This function is typically used to position an item such that its left edge aligns with this
 * offset's x-coordinate, and its vertical center aligns with this offset's y-coordinate plus half
 * of the item's height.
 *
 * @param size The size of the item for which the left-center position is being calculated.
 * @return A new `Offset` representing the left-center position. The x-coordinate remains the same
 * as this offset, and the y-coordinate is adjusted by adding half of the `size.height`.
 */
internal fun Offset.toLeftCenter(size: Size): Offset {

    return Offset(x = x, y = y + size.height / 2)
}

/**
 * Calculates the offset of the right-center point of a rectangle,
 * given its top-left corner offset and its size.
 *
 * @param size The size of the rectangle.
 * @return The offset of the right-center point.
 */
internal fun Offset.toRightCenter(size: Size): Offset {

    return Offset(x = x + size.width, y = y + size.height / 2)
}

/**
 * Creates a `Rect` from this `Size` and a given `position`.
 *
 * The `position` is used as the top-left corner of the `Rect`.
 *
 * @param position The `Offset` representing the top-left corner of the `Rect`.
 * @return A `Rect` with its top-left corner at the given `position` and dimensions matching
 * this `Size`.
 */
internal fun Size.itemRect(position: Offset): Rect {

    return Rect(offset = position, size = this)
}

/**
 * Converts an [ImageEditItems] instance to a [Rect] if applicable.
 *
 * This function checks the type of the [ImageEditItems]:
 * - If it's an [ImageEditItems.ImageItem], [ImageEditItems.ShapeItem], or [ImageEditItems.TextItem],
 *   it returns a [Rect] defined by the item's `size` and `position`.
 * - If it's an [ImageEditItems.BrushItem] or [ImageEditItems.EraseItem], it returns `null`
 *   as these items do not have a rectangular boundary in the same way.
 *
 * @receiver The [ImageEditItems] to convert.
 * @return A [Rect] representing the bounds of the item, or `null` if the item type
 * does not have a rectangular representation (e.g., BrushItem, EraseItem).
 */
internal fun ImageEditItems.toRect(): Rect? {

    return when (this) {

        is ImageEditItems.BrushItem -> null
        is ImageEditItems.EraseItem -> null
        is ImageEditItems.ImageItem -> size.itemRect(position = position)
        is ImageEditItems.ShapeItem -> size.itemRect(position = position)
        is ImageEditItems.TextItem -> size.itemRect(position = position)
    }
}

/**
 * Checks if an [ImageEditItems] has been clicked based on the click position.
 *
 * For [ImageEditItems.ImageItem], [ImageEditItems.ShapeItem], and [ImageEditItems.TextItem],
 * this function determines if the `clickPosition` falls within the bounding box
 * (`itemRect`) of the item.
 *
 * For [ImageEditItems.BrushItem] and [ImageEditItems.EraseItem], this function
 * currently always returns `false` as these items typically don't have a defined
 * clickable area in the same way as other items.
 *
 * @receiver The [ImageEditItems] to check.
 * @param clickPosition The [Offset] representing the coordinates of the click.
 * @return `true` if the `clickPosition` is within the item's bounds (for applicable item types),
 * `false` otherwise.
 */
internal fun ImageEditItems.hasEditItemClicked(clickPosition: Offset): Boolean {
    
    return when (this) {

        is ImageEditItems.BrushItem -> false
        is ImageEditItems.EraseItem -> false
        is ImageEditItems.ImageItem -> size.itemRect(position = position).contains(clickPosition)
        is ImageEditItems.ShapeItem -> size.itemRect(position = position).contains(clickPosition)
        is ImageEditItems.TextItem -> size.itemRect(position = position).contains(clickPosition)
    }
}

/**
 * Determines which EditItemCorner is tapped based on the click position and the `Rect`'s bounds.
 *
 * This function checks if the `clickPosition` is within a certain `threshold` of any of the
 * `Rect`'s corners or the midpoints of its edges.
 *
 * @receiver The `Rect` representing the bounds of the item being checked.
 * @param clickPosition The coordinates of the click.
 * @param threshold The maximum distance from a corner or edge midpoint for a tap to be considered
 * on that corner/edge. Defaults to 24.0F. A smaller value requires more precise tapping.
 * @return The `EditItemCorner` that was tapped, or `null` if the tap is not near any corner or
 * edge midpoint within the specified `threshold`.
 */
internal fun Rect.getEditItemCorner(
    clickPosition: Offset,
    threshold: Float = 24.0F
): EditItemCorner? {
    
    val topCenter = Offset(x = center.x, y = top)
    val bottomCenter = Offset(x = center.x, y = bottom)
    val leftCenter = Offset(x = left, y = center.y)
    val rightCenter = Offset(x = right, y = center.y)

    return when {

        clickPosition.hasNeared(topLeft, threshold) -> EditItemCorner.TOP_LEFT
        clickPosition.hasNeared(topRight, threshold) -> EditItemCorner.TOP_RIGHT
        clickPosition.hasNeared(bottomLeft, threshold) -> EditItemCorner.BOTTOM_LEFT
        clickPosition.hasNeared(bottomRight, threshold) -> EditItemCorner.BOTTOM_RIGHT
        clickPosition.hasNeared(topCenter, threshold) -> EditItemCorner.TOP_CENTRE
        clickPosition.hasNeared(bottomCenter, threshold) -> EditItemCorner.BOTTOM_CENTRE
        clickPosition.hasNeared(leftCenter, threshold) -> EditItemCorner.LEFT_CENTRE
        clickPosition.hasNeared(rightCenter, threshold) -> EditItemCorner.RIGHT_CENTRE
        else -> null
    }
}