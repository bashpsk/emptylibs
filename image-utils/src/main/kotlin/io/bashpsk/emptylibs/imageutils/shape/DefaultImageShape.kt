package io.bashpsk.emptylibs.imageutils.shape

import kotlinx.collections.immutable.persistentListOf

val BasicImageShapes = persistentListOf(
    ImageShape.None,
    ImageShape.Circle,
    ImageShape.Triangle,
    ImageShape.Polygon(sides = 5),
    ImageShape.Rectangle(radius = 0.15F),
    ImageShape.CutCorner(radius = 0.15F),
    ImageShape.Star(edges = 5, distance = 2.5F)
)