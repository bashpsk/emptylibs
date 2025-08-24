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
 * This function takes an [ImageBitmap] and a [ImageShape] and returns a new [ImageBitmap]
 * where the original image is clipped to the specified shape.
 *
 * @param imageBitmap The input [ImageBitmap] to be masked.
 * @param kropShape The [ImageShape] to use as the mask.
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
 * Creates a [Path] object representing the specified [ImageShape] within the given dimensions.
 *
 * This function is used internally to generate the clipping mask for shaping the cropped image.
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
private fun createStarPath(rect: Rect, edges: Short, distance: Float): Path {

    val path = Path()
    val centerX = rect.center.x
    val centerY = rect.center.y
    val outerRadius = min(rect.width, rect.height) / 2
    val innerRadius = outerRadius / distance
    val points = edges * 2

    for (i in 0 until points) {

        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = Math.toRadians((i * 360.0 / points) - 90)
        val x = centerX + radius * cos(angle).toFloat()
        val y = centerY + radius * sin(angle).toFloat()

        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }

    path.close()

    return path
}

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