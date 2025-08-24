package io.bashpsk.emptylibs.imageutils.shape

/**
 * Represents the different shapes that can be used for cropping an image.
 *
 * This sealed interface defines the various geometric forms available for image cropping.
 * Each implementing object or class corresponds to a distinct shape.
 *
 * - [None]: Represents no specific cropping shape, potentially meaning the original image bounds.
 * - [Circle]: Represents a circular cropping shape.
 * - [Triangle]: Represents a triangular cropping shape.
 * - [Polygon]: Represents a polygonal cropping shape with a specified number of sides.
 *     - [sides]: The number of sides for the polygon.
 * - [Rectangle]: Represents a rectangular cropping shape, possibly with rounded corners.
 *     - [radius]: The corner radius for the rectangle. A value of 0 indicates sharp corners.
 * - [CutCorner]: Represents a rectangular shape with diagonally cut corners.
 *     - [radius]: The extent of the corner cut.
 * - [Star]: Represents a star-shaped cropping outline.
 *     - [edges]: The number of points or edges of the star.
 *     - [distance]: A parameter influencing the depth or prominence of the star's points.
 */
sealed interface ImageShape {

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
     * @property radius The corner radius of the rectangle. A value of 0.0f creates a sharp-cornered
     * rectangle.
     */
    data class Rectangle(val radius: Float) : ImageShape

    /**
     * Represents a cut corner shape for cropping.
     *
     * This shape is a rectangle with its corners cut off at a specified radius.
     *
     * @property radius The radius of the cut corners. A value of 0 results in a regular rectangle.
     */
    data class CutCorner(val radius: Float) : ImageShape

    /**
     * Represents a star shape with a specified number of edges and distance.
     *
     * @property edges The number of points or edges the star has.
     * @property distance A float value that likely influences the appearance or size of the star's
     * points relative to its center, such as the distance from the inner & outer points.
     */
    data class Star(val edges: Short, val distance: Float) : ImageShape
}