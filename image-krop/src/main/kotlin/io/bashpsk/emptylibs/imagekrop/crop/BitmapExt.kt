package io.bashpsk.emptylibs.imagekrop.crop

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import io.bashpsk.emptylibs.imagekrop.utils.LOG_TAG
import io.bashpsk.emptylibs.imageutils.shape.ImageShape
import io.bashpsk.emptylibs.imageutils.shape.bitmapMask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

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
 * @param kropShape The [ImageShape] to apply to the cropped image. Defaults to
 * [ImageShape.None].
 * @return A [KropResult] which is either:
 *   - [KropResult.Success] containing the cropped and shaped [ImageBitmap] and the original
 *   [ImageBitmap].
 *   - [KropResult.Failed] containing an error message and the original [ImageBitmap] if any error
 *   occurs during the process (e.g., invalid dimensions, out of memory).
 */
internal suspend fun ImageBitmap.getCroppedImageBitmap(
    cropRect: Rect,
    canvasSize: Size,
    imageFlip: KropImageFlip? = null,
    kropShape: ImageShape = ImageShape.None
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

    val canvasWidth = canvasSize.width
    val canvasHeight = canvasSize.height
    val actualBitmapWidth = sourceImageBitmap.width.toFloat()
    val actualBitmapHeight = sourceImageBitmap.height.toFloat()

    val widthRatio = canvasWidth / actualBitmapWidth
    val heightRatio = canvasHeight / actualBitmapHeight
    val scaleFactor = min(widthRatio, heightRatio)

    val displayedImageWidth = actualBitmapWidth * scaleFactor
    val displayedImageHeight = actualBitmapHeight * scaleFactor

    val offsetX = (canvasWidth - displayedImageWidth) / 2F
    val offsetY = (canvasHeight - displayedImageHeight) / 2F

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

    return@withContext try {

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

        val shapedBitmap = kropShape.bitmapMask(imageBitmap = croppedBitmap)

        KropResult.Success(bitmap = shapedBitmap)
    } catch (exception: IllegalArgumentException) {

        val message = "Image Crop Failed: Invalid dimensions for bitmap. ${exception.message}"

        Log.e(LOG_TAG, message, exception)
        KropResult.Failed(message = message)
    } catch (exception: OutOfMemoryError) {

        val message = "Image Crop Failed: Out of memory. ${exception.message}"

        Log.e(LOG_TAG, message, exception)
        KropResult.Failed(message = message)
    } catch (exception: Exception) {

        val message = "Image Crop Failed: An unexpected error occurred. ${exception.message}"

        ensureActive()
        Log.e(LOG_TAG, message, exception)
        KropResult.Failed(message = message)
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