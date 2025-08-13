package io.bashpsk.emptylibs.imagekrop.crop

import kotlinx.collections.immutable.persistentListOf

internal val BasicKropShapes = persistentListOf(
    KropShape.None,
    KropShape.Circle,
    KropShape.Triangle,
    KropShape.Polygon(sides = 5),
    KropShape.Rectangle(radius = 0.15F),
    KropShape.CutCorner(radius = 0.15F),
    KropShape.Star(edges = 5, distance = 2.5F)
)

fun KropShape.toLabel(): String{

    return when (this) {

        is KropShape.None -> "None"
        is KropShape.Circle -> "Circle"
        is KropShape.Triangle -> "Triangle"
        is KropShape.Polygon -> "Polygon"
        is KropShape.Rectangle -> "Rectangle"
        is KropShape.CutCorner -> "Cut Corner"
        is KropShape.Star -> "Star"
    }
}