package io.bashpsk.emptylibs.composewidgets.clock.analog

import io.bashpsk.emptylibs.composeutils.shape.PathShape

internal typealias ClockShape = PathShape

/**
 * A collection of predefined shapes that can be used for the [AnalogClock]'s border.
 *
 * This object provides a convenient way to apply common geometric shapes to the clock, such as
 * circles, triangles, and stars. It also includes functions for creating more complex shapes like
 * polygons and rectangles with rounded corners.
 */
object AnalogClockShape {

    /** A perfect circle, the classic shape for an analog clock. */
    val Circle: ClockShape = PathShape.Circle

    /** A triangular shape for a unique clock design. */
    val Triangle: ClockShape = PathShape.Polygon(sides = 3)

    /**
     * Creates a polygon shape with a specified number of sides.
     *
     * @param sides The number of sides for the polygon.
     * @return A [ClockShape] in the form of a polygon.
     */
    fun Polygon(sides: Short = 5): ClockShape = PathShape.Polygon(sides = sides)

    /**
     * Creates a rectangular shape with optional corner radius.
     *
     * @param radius The corner radius as a fraction of the clock's size.
     * @return A [ClockShape] in the form of a rectangle.
     */
    fun Rectangle(radius: Float = 0.05F): ClockShape = PathShape.Rectangle(radius = radius)

    /**
     * Creates a shape with cut corners.
     *
     * @param radius The radius of the cut corners as a fraction of the clock's size.
     * @return A [ClockShape] with cut corners.
     */
    fun CutCorner(radius: Float = 0.05F): ClockShape = PathShape.CutCorner(radius = radius)

    /**
     * Creates a star shape.
     *
     * @param edges The number of points on the star.
     * @param distance The distance of the inner points from the center, affecting the star's
     * appearance.
     * @return A [ClockShape] in the form of a star.
     */
    fun Star(edges: Int = 5, distance: Float = 2.5F): ClockShape = PathShape.Star(
        edges = edges,
        distance = distance
    )
}