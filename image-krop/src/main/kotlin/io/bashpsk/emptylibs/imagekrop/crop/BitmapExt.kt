package io.bashpsk.emptylibs.imagekrop.crop

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntSize
import io.bashpsk.emptylibs.imagekrop.utils.LOG_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Constant value used for flipping an image horizontally.
 * This scale factor (-1.0F) is applied to the x-axis to mirror the image.
 */
private const val FLIP_HORIZONTAL_SCALE = -1.0F

/**
 * Constant value used for flipping an image vertically.
 * This scale factor (-1.0F) inverts the Y-axis of the image,
 * effectively creating a mirror image across the horizontal axis.
 */
private const val FLIP_VERTICAL_SCALE = -1.0F

/**
 * Represents the identity scale factor, used for no scaling.
 */
private const val IDENTITY_SCALE = 1.0F

/**
 * Minimum dimension for the cropped image in pixels.
 * This is used to prevent creating a bitmap with zero or negative dimensions, which would
 * cause a crash.
 */
private const val MIN_CROP_DIMENSION_PX = 1

/**
 * Crops the [ImageBitmap] based on the provided [cropRect] and applies optional flipping and
 * shaping.
 *
 * This function takes an [ImageBitmap] and crops it according to the [cropRect] specified in canvas
 * coordinates. It also handles optional image flipping ([imageFlip]) and applies a shape mask
 * ([kropShape]) to the cropped image.
 *
 * The process involves:
 * 1. Validating the input parameters (canvas size, source image size, crop rectangle dimensions).
 * 2. Calculating the scaling factor and offsets to transform canvas coordinates to bitmap
 * coordinates.
 * 3. Transforming the crop rectangle coordinates from canvas space to bitmap space.
 * 4. Validating and adjusting the crop dimensions to ensure they are at least
 * `MIN_CROP_DIMENSION_PX`.
 * 5. If `imageFlip` is specified, the source bitmap is flipped accordingly (horizontally or
 * vertically).
 * 6. The bitmap is then cropped using the calculated bitmap coordinates and dimensions.
 * 7. Finally, the `kropShape` is applied to the cropped bitmap, masking it to the desired shape.
 *
 * @param cropRect The rectangle defining the crop area in canvas coordinates.
 * @param canvasSize The size of the canvas on which the image is displayed and the `cropRect` is
 * defined.
 * @param imageFlip An optional [KropImageFlip] value to flip the image horizontally or vertically
 * before cropping. Defaults to `null` (no flip).
 * @param kropShape The [KropShape] to apply to the cropped image. Defaults to
 * [KropShape.SharpeCorner].
 * @return A [KropResult] which is either:
 *   - [KropResult.Success] containing the cropped and shaped [ImageBitmap] and the original
 *   [ImageBitmap].
 *   - [KropResult.Failed] containing an error message and the original [ImageBitmap] if any error
 *   occurs during the process (e.g., invalid dimensions, out of memory).
 */
internal suspend fun ImageBitmap.getCroppedImageBitmap(
    cropRect: Rect,
    canvasSize: IntSize,
    imageFlip: KropImageFlip? = null,
    kropShape: KropShape = KropShape.None
): KropResult = withContext(context = Dispatchers.IO) {

    val sourceImageBitmap = this@getCroppedImageBitmap

    if (canvasSize.width <= 0 || canvasSize.height <= 0) {

        val message = "Canvas size must be positive : ${canvasSize.width}x${canvasSize.height}"

        Log.e(LOG_TAG, message)
        return@withContext KropResult.Failed(message = message)
    }

    if (sourceImageBitmap.width <= 0 || sourceImageBitmap.height <= 0) {

        val message = "Source image bitmap size must be positive : ${
            sourceImageBitmap.width
        }x${
            sourceImageBitmap.height
        }"

        Log.e(LOG_TAG, message)
        return@withContext KropResult.Failed(message = message)
    }

    if (cropRect.width < 0 || cropRect.height < 0) {

        val message = "Crop rectangle dimensions cannot be negative : ${
            cropRect.width
        }x${
            cropRect.height
        }"

        Log.e(LOG_TAG, message)
        return@withContext KropResult.Failed(message = message)
    }

    val canvasWidth = canvasSize.width.toFloat()
    val canvasHeight = canvasSize.height.toFloat()
    val actualBitmapWidth = sourceImageBitmap.width.toFloat()
    val actualBitmapHeight = sourceImageBitmap.height.toFloat()

    val widthRatio = canvasWidth / actualBitmapWidth
    val heightRatio = canvasHeight / actualBitmapHeight
    val scaleFactor = min(widthRatio, heightRatio)

    val displayedImageWidth = actualBitmapWidth * scaleFactor
    val displayedImageHeight = actualBitmapHeight * scaleFactor

    val offsetX = (canvasWidth - displayedImageWidth) / 2f
    val offsetY = (canvasHeight - displayedImageHeight) / 2f

    val cropLeft = transformToBitmapCoordinate(
        canvasCoordinate = cropRect.left,
        canvasOffset = offsetX,
        scaleFactor = scaleFactor,
        maxBitmapDimension = actualBitmapWidth.toInt()
    )

    val cropTop = transformToBitmapCoordinate(
        canvasCoordinate = cropRect.top,
        canvasOffset = offsetY,
        scaleFactor = scaleFactor,
        maxBitmapDimension = actualBitmapHeight.toInt()
    )

    val cropRight = transformToBitmapCoordinate(
        canvasCoordinate = cropRect.right,
        canvasOffset = offsetX,
        scaleFactor = scaleFactor,
        maxBitmapDimension = actualBitmapWidth.toInt()
    )

    val cropBottom = transformToBitmapCoordinate(
        canvasCoordinate = cropRect.bottom,
        canvasOffset = offsetY,
        scaleFactor = scaleFactor,
        maxBitmapDimension = actualBitmapHeight.toInt()
    )

    val validatedCropLeft = min(cropLeft, cropRight)
    val validatedCropTop = min(cropTop, cropBottom)
    val validatedCropRight = max(cropLeft, cropRight)
    val validatedCropBottom = max(cropTop, cropBottom)

    val cropWidth = (validatedCropRight - validatedCropLeft).coerceAtLeast(MIN_CROP_DIMENSION_PX)
    val cropHeight = (validatedCropBottom - validatedCropTop).coerceAtLeast(MIN_CROP_DIMENSION_PX)

    if (cropWidth <= MIN_CROP_DIMENSION_PX && cropHeight <= MIN_CROP_DIMENSION_PX) {

        val message = "Calculated crop dimensions too small : ${
            cropWidth
        }x${cropHeight}. Minimum is $MIN_CROP_DIMENSION_PX."

        Log.w(LOG_TAG, message)
    }

    try {

        val androidBitmap = sourceImageBitmap.asAndroidBitmap()
        var bitmapToProcess = androidBitmap

        if (imageFlip != null) {

            val matrix = Matrix()

            when (imageFlip) {

                KropImageFlip.Horizontal -> matrix.preScale(FLIP_HORIZONTAL_SCALE, IDENTITY_SCALE)
                KropImageFlip.Vertical -> matrix.preScale(IDENTITY_SCALE, FLIP_VERTICAL_SCALE)
            }

            if (matrix.isIdentity.not()) {

                bitmapToProcess = Bitmap.createBitmap(
                    androidBitmap,
                    0,
                    0,
                    androidBitmap.width,
                    androidBitmap.height,
                    matrix,
                    true
                )
            }
        }

        val croppedBitmap = Bitmap.createBitmap(
            bitmapToProcess,
            validatedCropLeft,
            validatedCropTop,
            cropWidth,
            cropHeight
        ).asImageBitmap()

        val shapedBitmap = bitmapShapeMask(imageBitmap = croppedBitmap, kropShape = kropShape)

        return@withContext KropResult.Success(bitmap = shapedBitmap)
    } catch (exception: IllegalArgumentException) {

        val message = "Image Crop Failed: Invalid dimensions for bitmap. ${exception.message}"

        Log.e(LOG_TAG, message, exception)
        return@withContext KropResult.Failed(message = message)
    } catch (exception: OutOfMemoryError) {

        val message = "Image Crop Failed: Out of memory. ${exception.message}"

        Log.e(LOG_TAG, message, exception)
        return@withContext KropResult.Failed(message = message)
    } catch (exception: Exception) {

        val message = "Image Crop Failed: An unexpected error occurred. ${exception.message}"

        ensureActive()
        Log.e(LOG_TAG, message, exception)
        return@withContext KropResult.Failed(message = message)
    }
}

/**
 * Transforms a coordinate from the canvas's coordinate system to the bitmap's coordinate system.
 *
 * This function is essential for accurately mapping crop selections made on a scaled and offset
 * canvas representation of an image back to the original image's pixel coordinates.
 *
 * @param canvasCoordinate The coordinate value on the canvas (e.g., x or y position of a crop
 * handle).
 * @param canvasOffset The offset of the displayed image on the canvas. This accounts for any
 * padding
 * or centering of the image within the canvas.
 * @param scaleFactor The factor by which the original bitmap is scaled to fit the canvas.
 * @param maxBitmapDimension The maximum dimension (width or height) of the original bitmap. This is
 * used
 * to ensure the transformed coordinate does not exceed the bitmap's boundaries.
 * @return The transformed coordinate in the bitmap's pixel space, rounded to the nearest integer
 * and
 * coerced to be within the valid range [0, maxBitmapDimension]. If the `scaleFactor` is zero,
 * it returns 0 to prevent division by zero errors.
 */
private fun transformToBitmapCoordinate(
    canvasCoordinate: Float,
    canvasOffset: Float,
    scaleFactor: Float,
    maxBitmapDimension: Int
): Int {

    if (scaleFactor == 0f) return 0

    return ((canvasCoordinate - canvasOffset) / scaleFactor).roundToInt().coerceIn(
        range = 0..maxBitmapDimension
    )
}

/**
 * Applies a shape mask to an [ImageBitmap].
 *
 * This function takes an [ImageBitmap] and a [KropShape] and returns a new [ImageBitmap]
 * where the original image is clipped to the specified shape.
 *
 * @param imageBitmap The input [ImageBitmap] to be masked.
 * @param kropShape The [KropShape] to use as the mask.
 * @return A new [ImageBitmap] with the shape mask applied.
 */
private fun bitmapShapeMask(imageBitmap: ImageBitmap, kropShape: KropShape): ImageBitmap {

    val width = imageBitmap.width
    val height = imageBitmap.height
    val imageSize = Size(width = width.toFloat(), height = height.toFloat())

    val outputImageBitmap = ImageBitmap(
        width = width,
        height = height,
        config = ImageBitmapConfig.Argb8888
    )

    Canvas(image = outputImageBitmap).apply {

        val shapePath = kropShape.toPath(canvasSize = imageSize)

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
 * Creates a [Path] object representing the specified [KropShape] within the given dimensions.
 *
 * This function is used internally to generate the clipping mask for shaping the cropped image.
 *
 * @param canvasSize The size of the canvas on which the shape will be drawn.
 * @return A [Path] object representing the specified [KropShape].
 */
internal fun KropShape.toPath(canvasSize: Size): Path {

    val rect = Rect(left = 0.0F, top = 0.0F, right = canvasSize.width, bottom = canvasSize.height)

    return Path().apply {

        when (this@toPath) {

            is KropShape.None -> addRect(rect = rect)

            is KropShape.Circle -> {

                val center = Offset(x = canvasSize.width / 2, y = canvasSize.height / 2)
                val radius = minOf(canvasSize.width, canvasSize.height) / 2

                addOval(oval = Rect(center = center, radius = radius))
            }

            is KropShape.Triangle -> {

                moveTo(rect.center.x, rect.top)
                lineTo(rect.right, rect.bottom)
                lineTo(rect.left, rect.bottom)
                close()
            }

            is KropShape.Rectangle -> {

                val rectRadius = minOf(canvasSize.width, canvasSize.height) * radius
                val cornerRadius = CornerRadius(x = rectRadius, y = rectRadius)

                addRoundRect(RoundRect(rect = rect, cornerRadius = cornerRadius))
            }

            is KropShape.Polygon -> {

                addPath(path = createPolygonPath(rect = rect, sides = sides))
            }

            is KropShape.CutCorner -> {

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

            is KropShape.Star -> {

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
internal fun createPolygonPath(rect: Rect, sides: Short): Path {

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
internal fun createStarPath(rect: Rect, edges: Short, distance: Float): Path {

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

/**
 * Checks if this [ImageBitmap] is the same as another [ImageBitmap].
 *
 * This function converts both [ImageBitmap] instances to Android [Bitmap] objects
 * and then uses the [Bitmap.sameAs] method to compare them.
 *
 * @param other The [ImageBitmap] to compare with.
 * @return `true` if the bitmaps are the same, `false` otherwise.
 */
fun ImageBitmap.sameAs(other: ImageBitmap): Boolean {

    return this.asAndroidBitmap().sameAs(other.asAndroidBitmap())
}