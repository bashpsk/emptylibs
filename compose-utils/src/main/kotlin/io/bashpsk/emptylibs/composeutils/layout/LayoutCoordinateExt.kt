package io.bashpsk.emptylibs.composeutils.layout

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.unit.toSize

fun LayoutCoordinates.calculateViewport(): Rect {

    val rootCoordinates = this.findRootCoordinates()
    val rootRect = Rect(offset = Offset.Zero, size = rootCoordinates.size.toSize())
    val visibleInRoot = this.boundsInRoot().intersect(other = rootRect)

    return when {

        visibleInRoot.isEmpty -> Rect.Zero

        else -> Rect(
            topLeft = this.localPositionOf(
                sourceCoordinates = rootCoordinates,
                relativeToSource = visibleInRoot.topLeft
            ),
            bottomRight = this.localPositionOf(
                sourceCoordinates = rootCoordinates,
                relativeToSource = visibleInRoot.bottomRight
            )
        )
    }
}

fun LayoutCoordinates.calculateVisibleArea(): Rect {

    val viewport = calculateViewport()

    val layoutArea = this.boundsInParent().copy(left = viewport.left, top = viewport.top)

    val intersection = if (viewport.isEmpty) Rect.Zero else {
        viewport.intersect(other = layoutArea)
    }

    return if (intersection.width <= 0F || intersection.height <= 0F) Rect.Zero else {

        Rect(
            offset = viewport.topLeft,
            size = Size(
                width = (intersection.right - layoutArea.left),
                height = (intersection.bottom - layoutArea.top)
            )
        )
    }
}