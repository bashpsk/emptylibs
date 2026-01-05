package io.bashpsk.emptylibs.gestureui.transform

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

/**
 * A range representing a zoomed-out state, where the zoom level is between 0% and 95%.
 */
internal val ZoomedOut = 0.0F..0.95F

/**
 * A range representing a neutral zoom state (100%), with a small tolerance.
 */
internal val ZoomZero = 0.95F..1.05F

/**
 * A range representing a zoomed-in state, where the zoom level is greater than 105%.
 */
internal val ZoomedIn = 1.05F..Float.MAX_VALUE

/**
 * A range representing the full circle of rotation, from 0 to 360 degrees.
 */
internal val RotationIn = 0.0F..360.0F

/**
 * Calculates the allowable panning range for a given zoom level and content size.
 *
 * @param zoom The current zoom level.
 * @param boundSize The size of the view bounds (e.g., width or height).
 * @param contentSize The size of the content (e.g., width or height).
 * @return A [ClosedFloatingPointRange] representing the minimum and maximum allowed pan values.
 */
internal fun getPanRange(
    zoom: Float,
    boundSize: Float,
    contentSize: Float
): ClosedFloatingPointRange<Float> {

    val contentWidth = contentSize * zoom
    val horizontalOverflow = (contentWidth - boundSize).coerceAtLeast(0F)

    return when {

        horizontalOverflow > 0F -> {

            val maximumPan = horizontalOverflow / 2F
            val minimumPan = -maximumPan

            minimumPan..maximumPan
        }

        else -> 0F..0F
    }
}

/**
 * Coerces the given position to stay within the valid panning range.
 *
 * @param position The current pan position.
 * @param zoom The current zoom level.
 * @param boundSize The size of the view bounds.
 * @return An [Offset] representing the coerced position.
 */
internal fun getCoercedPosition(
    position: Offset,
    zoom: Float,
    boundSize: Size
): Offset {

    return Offset(
        x = position.x.coerceIn(
            getPanRange(zoom = zoom, boundSize = boundSize.width, contentSize = boundSize.width)
        ),
        y = position.y.coerceIn(
            getPanRange(zoom = zoom, boundSize = boundSize.height, contentSize = boundSize.height)
        )
    )
}