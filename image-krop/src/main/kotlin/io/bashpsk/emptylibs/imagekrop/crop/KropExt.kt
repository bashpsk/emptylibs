package io.bashpsk.emptylibs.imagekrop.crop

import io.bashpsk.emptylibs.imageutils.shape.ImageShape
import kotlinx.collections.immutable.persistentListOf

internal val BasicKropShapes = persistentListOf(
    ImageShape.None,
    ImageShape.Circle,
    ImageShape.Triangle,
    ImageShape.Polygon(sides = 5),
    ImageShape.Rectangle(radius = 0.15F),
    ImageShape.CutCorner(radius = 0.15F),
    ImageShape.Star(edges = 5, distance = 2.5F)
)