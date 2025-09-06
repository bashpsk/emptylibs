package io.bashpsk.emptylibs.imageutils.shape

import kotlinx.collections.immutable.persistentListOf

/**
 * A predefined list of basic image shapes that can be used as defaults or examples.
 * This list includes common geometric shapes and some specialized ones.
 *
 * Contains:
 * - [ImageShape.None]: Represents no specific shape (typically the original image bounds).
 * - [ImageShape.Circle]: A circular shape.
 * - [ImageShape.Triangle]: An equilateral triangle shape.
 * - [ImageShape.Polygon]: A regular polygon with 5 sides (pentagon).
 * - [ImageShape.Rectangle]: A rectangle with rounded corners (radius of 0.15F).
 * - [ImageShape.CutCorner]: A shape with diagonally cut corners (radius of 0.15F).
 * - [ImageShape.Star]: A 5-pointed star shape with a distance factor of 2.5F.
 */
val BasicImageShapes = persistentListOf(
    ImageShape.None,
    ImageShape.Circle,
    ImageShape.Triangle,
    ImageShape.Polygon(sides = 5),
    ImageShape.Rectangle(radius = 0.15F),
    ImageShape.CutCorner(radius = 0.15F),
    ImageShape.Star(edges = 5, distance = 2.5F)
)