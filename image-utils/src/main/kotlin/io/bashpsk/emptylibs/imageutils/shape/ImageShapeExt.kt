package io.bashpsk.emptylibs.imageutils.shape

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Applies a shape mask to an [ImageBitmap].
 *
 * This function takes an [ImageBitmap] and an [ImageShape] and returns a new [ImageBitmap]
 * where the original image is clipped to the specified shape.
 *
 * @param imageBitmap The input [ImageBitmap] to be masked.
 * @return A new [ImageBitmap] with the shape mask applied.
 */
fun ImageShape.bitmapMask(imageBitmap: ImageBitmap): ImageBitmap {

    val width = imageBitmap.width
    val height = imageBitmap.height
    val imageSize = Size(width = width.toFloat(), height = height.toFloat())

    val outputImageBitmap = ImageBitmap(
        width = width,
        height = height,
        config = ImageBitmapConfig.Argb8888
    )

    Canvas(image = outputImageBitmap).apply {

        val shapePath = this@bitmapMask.toPath(canvasSize = imageSize)

        val paint = Paint().apply {

            isAntiAlias = true
            shader = ImageShader(imageBitmap)
        }

        save()
        clipPath(path = shapePath)
        drawImageRect(image = imageBitmap, paint = paint)
        restore()
    }

    return outputImageBitmap
}

/**
 * Converts an [ImageShape] to a [Path] object.
 *
 * This function takes an [ImageShape] and the size of the canvas as input and returns a [Path]
 * object that represents the shape. The path can then be used for drawing or clipping.
 *
 * The behavior of this function depends on the type of [ImageShape]:
 * - For [ImageShape.None], it creates a rectangular path that covers the entire canvas.
 * - For [ImageShape.Circle], it creates a circular path centered within the canvas.
 * - For [ImageShape.Triangle], it creates an isosceles triangle path pointing upwards,
 *   with its base at the bottom of the canvas.
 * - For [ImageShape.Rectangle], it creates a rounded rectangle path. The corner radius is
 *   proportional to the smaller dimension of the canvas.
 * - For [ImageShape.Polygon], it creates a regular polygon path centered within the canvas.
 *   The number of sides is determined by the `sides` property of the [ImageShape.Polygon].
 * - For [ImageShape.CutCorner], it creates a rectangular path with its corners cut. The size
 *   of the cut is proportional to the smaller dimension of the canvas.
 * - For [ImageShape.Star], it creates a star-shaped path centered within the canvas.
 *   The number of points and the inner radius of the star are determined by the `edges` and
 *   `distance` properties of the [ImageShape.Star].
 *
 * @param canvasSize The size of the canvas on which the shape will be drawn.
 * @return A [Path] object representing the specified [ImageShape].
 */
fun ImageShape.toPath(canvasSize: Size): Path {

    val rect = Rect(left = 0.0F, top = 0.0F, right = canvasSize.width, bottom = canvasSize.height)

    return Path().apply {

        when (this@toPath) {

            is ImageShape.None -> addRect(rect = rect)

            is ImageShape.Circle -> {

                val center = Offset(x = canvasSize.width / 2, y = canvasSize.height / 2)
                val radius = minOf(canvasSize.width, canvasSize.height) / 2

                addOval(oval = Rect(center = center, radius = radius))
            }

            is ImageShape.Triangle -> {

                moveTo(rect.center.x, rect.top)
                lineTo(rect.right, rect.bottom)
                lineTo(rect.left, rect.bottom)
                close()
            }

            is ImageShape.Rectangle -> {

                val rectRadius = minOf(canvasSize.width, canvasSize.height) * radius
                val cornerRadius = CornerRadius(x = rectRadius, y = rectRadius)

                addRoundRect(RoundRect(rect = rect, cornerRadius = cornerRadius))
            }

            is ImageShape.Polygon -> {

                addPath(path = createPolygonPath(rect = rect, sides = sides))
            }

            is ImageShape.CutCorner -> {

                val cutRadius = minOf(canvasSize.width, canvasSize.height) * radius

                moveTo(rect.left + cutRadius, rect.top)
                lineTo(rect.right - cutRadius, rect.top)
                lineTo(rect.right, rect.top + cutRadius)
                lineTo(rect.right, rect.bottom - cutRadius)
                lineTo(rect.right - cutRadius, rect.bottom)
                lineTo(rect.left + cutRadius, rect.bottom)
                lineTo(rect.left, rect.bottom - cutRadius)
                lineTo(rect.left, rect.top + cutRadius)
                close()
            }

            is ImageShape.Star -> {

                addPath(path = createStarPath(rect = rect, edges = edges, distance = distance))
            }
        }
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

    val path = Path()
    val radius = min(rect.width, rect.height) / 2
    val centerX = rect.center.x
    val centerY = rect.center.y
    val angle = (2 * PI / sides)
    val startAngle = if (sides % 2 != 0) -PI / 2 else 0.0

    for (i in 0 until sides) {

        val theta = startAngle + i * angle
        val x = centerX + radius * cos(theta).toFloat()
        val y = centerY + radius * sin(theta).toFloat()

        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }

    path.close()

    return path
}

/**
 * Creates a star-shaped path within the given rectangle.
 *
 * This function calculates the points of a 5-pointed star (as it uses 10 points, alternating outer
 * and inner) that fits within the bounds of the provided [rect].
 *
 * @param rect The [Rect] defining the bounds within which the star path will be created.
 * @return A [Path] object representing the star shape.
 */
private fun createStarPath(rect: Rect, edges: Int, distance: Float): Path {

    val path = Path()
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

        if (point == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }

    path.close()

    return path
}

/**
 * Converts an [ImageShape] to a human-readable label string.
 *
 * This function is useful for displaying the type of shape in UI elements or logs.
 *
 * @return A string representation of the [ImageShape].
 */
fun ImageShape.toLabel(): String {

    return when (this) {

        is ImageShape.None -> "None"
        is ImageShape.Circle -> "Circle"
        is ImageShape.Triangle -> "Triangle"
        is ImageShape.Polygon -> "Polygon"
        is ImageShape.Rectangle -> "Rectangle"
        is ImageShape.CutCorner -> "Cut Corner"
        is ImageShape.Star -> "Star"
    }
}

/**
 * Gets the minimum radius for a given [ImageShape].
 *
 * This function returns the corner radius or equivalent proportional value for shapes
 * that have rounded or cut corners, such as [ImageShape.Rectangle] and [ImageShape.CutCorner].
 * For other shapes, it returns `0.0F`.
 *
 * The radius is calculated as a proportion of the smaller dimension of the canvas the shape is
 * drawn on.
 *
 * @return The radius as a [Float]. This value represents a proportion (e.g., 0.2F for 20%)
 * of the smaller dimension of the canvas the shape is drawn on.
 */
fun ImageShape.getMinimumRadius(boundingRadius: Float): Float {

    return when (this) {

        is ImageShape.None -> boundingRadius
        is ImageShape.Circle -> boundingRadius
        is ImageShape.Triangle -> boundingRadius * cos(PI / 3).toFloat()
        is ImageShape.Polygon -> boundingRadius * cos(PI / sides).toFloat()
        is ImageShape.Rectangle -> boundingRadius
        is ImageShape.CutCorner -> boundingRadius
        is ImageShape.Star -> boundingRadius / distance
    }
}