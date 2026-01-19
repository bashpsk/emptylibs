package io.bashpsk.emptylibs.composeutils.shape

import kotlinx.collections.immutable.persistentListOf

/**
 * A predefined list of basic shapes that can be used as defaults or examples.
 *
 * This collection includes common geometric shapes like circles and triangles,
 * along with parameterized shapes like polygons and stars.
 *
 * The list contains:
 * - [PathShape.None]: Represents the full rectangular bounds.
 * - [PathShape.Circle]: A circular shape fitting the bounds.
 * - [PathShape.Triangle]: An isosceles triangle pointing upwards.
 * - [PathShape.Polygon]: A regular pentagon(5 sides).
 * - [PathShape.Polygon]: A regular hexagon(6 sides).
 * - [PathShape.Rectangle]: A rounded rectangle with a 15% corner radius.
 * - [PathShape.CutCorner]: A cut-corner rectangle with a 15% corner radius.
 * - [PathShape.Star]: A 5-pointed star with a 2.5 distance ratio.
 */
val BasicPathShapes = persistentListOf(
    PathShape.None,
    PathShape.Circle,
    PathShape.Triangle,
    PathShape.Polygon(sides = 5),
    PathShape.Polygon(sides = 6),
    PathShape.Rectangle(radius = 0.15F),
    PathShape.CutCorner(radius = 0.15F),
    PathShape.Star(edges = 5, distance = 2.5F)
)