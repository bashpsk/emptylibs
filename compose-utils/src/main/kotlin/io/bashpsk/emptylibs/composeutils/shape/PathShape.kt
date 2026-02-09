package io.bashpsk.emptylibs.composeutils.shape

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Represents various geometric shapes that can be converted into a
 * [androidx.compose.ui.graphics.Path].
 *
 * This sealed class provides a structured way to define common shapes like circles, polygons,
 * and stars, which can then be rendered on a canvas.
 */
@Stable
@Parcelize
@Serializable
sealed class PathShape : Parcelable {

    /**
     * Represents no specific shape, typically rendered as the full rectangular bounds.
     */
    data object None : PathShape()

    /**
     * Represents a circular shape that fits within the bounding rectangle.
     */
    data object Circle : PathShape()

    /**
     * Represents an isosceles triangle pointing upwards.
     */
    data object Triangle : PathShape()

    /**
     * Represents a regular polygon.
     *
     * @property sides The number of sides for the polygon. Must be at least 3.
     */
    data class Polygon(val sides: Short) : PathShape()

    /**
     * Represents a rectangle with rounded corners.
     *
     * @property radius The radius of the corners, expressed as a fraction (0.0 to 1.0)
     * of the smaller dimension of the shape's bounds.
     */
    data class Rectangle(val radius: Float) : PathShape()

    /**
     * Represents a rectangle with diagonally cut corners.
     *
     * @property radius The size of the cut, expressed as a fraction (0.0 to 1.0)
     * of the smaller dimension of the shape's bounds.
     */
    data class CutCorner(val radius: Float) : PathShape()

    /**
     * Represents a star shape.
     *
     * @property edges The number of outer points of the star.
     * @property distance The ratio between the outer radius and the inner radius
     * (outerRadius / innerRadius).
     */
    data class Star(val edges: Int, val distance: Float) : PathShape()
}