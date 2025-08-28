package io.bashpsk.emptylibs.imageedit.extension

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.bashpsk.emptylibs.imageedit.edit.EditItemCorner
import io.bashpsk.emptylibs.imageedit.edit.ImageEditItems

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

internal fun ImageEditItems.toRect(): Rect? {

    return when (this) {

        is ImageEditItems.BrushItem -> null
        is ImageEditItems.EraseItem -> null
        is ImageEditItems.ImageItem -> size.itemRect(position = position)
        is ImageEditItems.ShapeItem -> size.itemRect(position = position)
        is ImageEditItems.TextItem -> size.itemRect(position = position)
    }
}

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