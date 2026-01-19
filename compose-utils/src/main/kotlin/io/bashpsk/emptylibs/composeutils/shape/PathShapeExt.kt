package io.bashpsk.emptylibs.composeutils.shape

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Converts this [PathShape] into a Compose [Path] based on the provided [canvasSize].
 *
 * @param canvasSize The size of the canvas or area where the path will be drawn.
 * @return A [Path] representing the geometric shape.
 */
fun PathShape.toPath(canvasSize: Size): Path {

    val rect = Rect(left = 0.0F, top = 0.0F, right = canvasSize.width, bottom = canvasSize.height)

    return Path().apply {

        when (this@toPath) {

            is PathShape.None -> addRect(rect = rect)

            is PathShape.Circle -> {

                val center = Offset(x = canvasSize.width / 2, y = canvasSize.height / 2)
                val radius = minOf(canvasSize.width, canvasSize.height) / 2

                addOval(oval = Rect(center = center, radius = radius))
            }

            is PathShape.Triangle -> addPath(path = createTrianglePath(rect = rect))

            is PathShape.Rectangle -> {

                val rectRadius = minOf(canvasSize.width, canvasSize.height) * radius
                val cornerRadius = CornerRadius(x = rectRadius, y = rectRadius)

                addRoundRect(RoundRect(rect = rect, cornerRadius = cornerRadius))
            }

            is PathShape.Polygon -> {

                addPath(path = createPolygonPath(rect = rect, sides = sides))
            }

            is PathShape.CutCorner -> {

                val cutRadius = minOf(canvasSize.width, canvasSize.height) * radius

                addPath(path = createCutCornerPath(rect = rect, radius = cutRadius))
            }

            is PathShape.Star -> {

                addPath(path = createStarPath(rect = rect, edges = edges, distance = distance))
            }
        }
    }
}

/**
 * Creates a triangular path within the given rectangle.
 *
 * This function creates an isosceles triangle that fills the provided [rect].
 * The triangle points upwards, with its top vertex at the top-center of the rectangle and its base
 * corners at the bottom-left and bottom-right.
 *
 * @param rect The bounding rectangle for the triangle.
 * @return A [Path] object representing the triangle.
 */
private fun createTrianglePath(rect: Rect): Path {

    return Path().apply {

        moveTo(rect.center.x, rect.top)
        lineTo(rect.right, rect.bottom)
        lineTo(rect.left, rect.bottom)
        close()
    }
}

/**
 * Creates a rectangular path with cut corners within the given rectangle.
 *
 * This function calculates the coordinates for a rectangle where each corner is "clipped" or
 * "cut" diagonally based on the provided [radius].
 *
 * @param rect The bounding rectangle for the path.
 * @param radius The absolute distance from each corner to the start of the cut along the edges.
 * @return A [Path] object representing the cut-corner rectangle.
 */
private fun createCutCornerPath(rect: Rect, radius: Float): Path {

    return Path().apply {

        moveTo(rect.left + radius, rect.top)
        lineTo(rect.right - radius, rect.top)
        lineTo(rect.right, rect.top + radius)
        lineTo(rect.right, rect.bottom - radius)
        lineTo(rect.right - radius, rect.bottom)
        lineTo(rect.left + radius, rect.bottom)
        lineTo(rect.left, rect.bottom - radius)
        lineTo(rect.left, rect.top + radius)
        close()
    }
}

/**
 * Creates a regular polygon path within the given rectangle.
 *
 * @param rect The bounding rectangle for the polygon.
 * @param sides The number of sides of the polygon. Must be 3 or greater.
 * @return A [Path] object representing the polygon.
 *
 * The polygon is centered within the rectangle.
 * The size of the polygon is determined by the smaller dimension (width or height) of the
 * rectangle.
 * For polygons with an odd number of sides, one vertex points upwards.
 * For polygons with an even number of sides, two vertices form a horizontal top edge.
 */
private fun createPolygonPath(rect: Rect, sides: Short): Path {

    return Path().apply {

        val radius = min(rect.width, rect.height) / 2
        val centerX = rect.center.x
        val centerY = rect.center.y
        val angle = (2 * PI / sides)
        val startAngle = if (sides % 2 != 0) -PI / 2 else 0.0

        (0 until sides).forEach { side ->

            val theta = startAngle + side * angle
            val x = centerX + radius * cos(theta).toFloat()
            val y = centerY + radius * sin(theta).toFloat()

            if (side == 0) moveTo(x, y) else lineTo(x, y)
        }

        close()
    }
}

/**
 * Creates a star-shaped path within the given rectangle.
 *
 * This function calculates the points of a star with the specified number of [edges]
 * that fits within the bounds of the provided [rect]. The [distance] factor determines
 * the ratio between the outer and inner radii.
 *
 * @param rect The [Rect] defining the bounds within which the star path will be created.
 * @param edges The number of points (outer vertices) of the star.
 * @param distance The ratio of the outer radius to the inner radius.
 * @return A [Path] object representing the star shape.
 */
private fun createStarPath(rect: Rect, edges: Int, distance: Float): Path {

    return Path().apply {

        val centerX = rect.center.x
        val centerY = rect.center.y
        val outerRadius = min(rect.width, rect.height) / 2
        val innerRadius = outerRadius / distance
        val points = edges * 2

        (0 until points).forEach { point ->

            val radius = if (point % 2 == 0) outerRadius else innerRadius
            val angle = Math.toRadians((point * 360.0 / points) - 90)
            val x = centerX + radius * cos(angle).toFloat()
            val y = centerY + radius * sin(angle).toFloat()

            if (point == 0) moveTo(x, y) else lineTo(x, y)
        }

        close()
    }
}

/**
 * Converts an [PathShape] to a human-readable label string.
 *
 * This function is useful for displaying the type of shape in UI elements or logs.
 *
 * @return A string representation of the [PathShape].
 */
fun PathShape.toLabel(): String {

    return when (this) {

        is PathShape.None -> "None"
        is PathShape.Circle -> "Circle"
        is PathShape.Triangle -> "Triangle"
        is PathShape.Polygon -> "Polygon"
        is PathShape.Rectangle -> "Rectangle"
        is PathShape.CutCorner -> "Cut Corner"
        is PathShape.Star -> "Star"
    }
}

/**
 * Calculates a "minimum radius" or representative scale for a given [PathShape].
 *
 * This function returns a value representing the inner bounds or a characteristic radius
 * for various shapes, which can be useful for alignment or sizing other elements relative to the
 * shape.
 *
 * @param boundingRadius The radius of the bounding circle for the shape.
 * @return The calculated radius as a [Float].
 */
fun PathShape.getMinimumRadius(boundingRadius: Float): Float {

    return when (this) {

        is PathShape.None -> boundingRadius
        is PathShape.Circle -> boundingRadius
        is PathShape.Triangle -> boundingRadius * cos(PI / 3).toFloat()
        is PathShape.Polygon -> boundingRadius * cos(PI / sides).toFloat()
        is PathShape.Rectangle -> boundingRadius
        is PathShape.CutCorner -> boundingRadius
        is PathShape.Star -> boundingRadius / distance
    }
}