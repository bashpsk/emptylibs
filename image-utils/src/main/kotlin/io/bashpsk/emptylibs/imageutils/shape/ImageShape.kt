package io.bashpsk.emptylibs.imageutils.shape

import android.os.Parcelable
import androidx.annotation.FloatRange
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Represents the different shapes that can be used for cropping an image.
 *
 * This sealed interface defines the various geometric forms available for image cropping.
 * Each implementing object or class corresponds to a distinct shape.
 *
 * Available shapes:
 * - [None]: Represents no specific cropping shape, effectively using the original image bounds.
 * - [Circle]: Represents a circular cropping shape.
 * - [Triangle]: Represents a triangular cropping shape.
 * - [Polygon]: Represents a polygonal cropping shape with a specified number of sides.
 *     - `sides`: The number of sides for the polygon.
 * - [Rectangle]: Represents a rectangular cropping shape, which can have rounded corners.
 *     - `radius`: The corner radius for the rectangle, expressed as a fraction of the shortest
 *       side (0.0f for sharp corners, up to 1.0f).
 * - [CutCorner]: Represents a rectangular shape with diagonally cut corners.
 *     - `radius`: The extent of the corner cut, as a proportion (0.0f for no cut, up to 1.0f).
 * - [Star]: Represents a star-shaped cropping outline.
 *     - `edges`: The number of points or edges of the star.
 *     - `distance`: A parameter influencing the depth or prominence of the star's points,
 *       typically the ratio between the inner and outer radii of the star.
 */
@Parcelize
@Serializable
sealed interface ImageShape : Parcelable {

    /**
     * Represents no cropping, meaning the original image is retained.
     */
    data object None : ImageShape

    /**
     * Represents a circular crop shape.
     */
    data object Circle : ImageShape

    /**
     * Represents a triangle shape for cropping.
     */
    data object Triangle : ImageShape

    /**
     * Represents a polygon shape for cropping.
     *
     * @property sides The number of sides of the polygon.
     */
    data class Polygon(val sides: Short) : ImageShape

    /**
     * Represents a rectangle shape.
     *
     * @property radius The corner radius of the rectangle, expressed as a fraction of the shortest
     * side of the rectangle. A value of 0.0f creates a sharp-cornered rectangle. The value must
     * be between 0.0 and 1.0 (inclusive).
     */
    data class Rectangle(
        @param:FloatRange(from = 0.0, to = 1.0)
        val radius: Float
    ) : ImageShape

    /**
     * Represents a cut corner shape for cropping.
     *
     * This shape is a rectangle with its corners cut off diagonally, controlled by the `radius`
     * parameter.
     * The `radius` determines the extent of the cut from each corner.
     *
     * @property radius The extent of the corner cut. A value of 0.0 results in a regular rectangle
     * (no cut corners).
     * The value is typically a proportion, ranging from 0.0 (no cut) to 1.0 (maximum cut).
     */
    data class CutCorner(
        @param:FloatRange(from = 0.0, to = 1.0)
        val radius: Float
    ) : ImageShape

    /**
     * Represents a star shape with a specified number of edges and distance.
     *
     * @property edges The number of points or edges the star has.
     * @property distance A float value that likely influences the appearance or size of the star's
     * points relative to its center, such as the distance from the inner & outer points.
     */
    data class Star(val edges: Short, val distance: Float) : ImageShape
}