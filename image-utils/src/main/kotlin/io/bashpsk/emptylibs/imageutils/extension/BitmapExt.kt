package io.bashpsk.emptylibs.imageutils.extension

import android.graphics.Bitmap
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.IntSize
import io.bashpsk.emptylibs.formatter.format.findAspectRatio
import kotlin.math.min

/**
 * Converts an [ImageBitmap] to a [Size] object.
 *
 * If the [ImageBitmap] is null, it returns [Size.Zero].
 * Otherwise, it creates a [Size] object with the width and height of the bitmap.
 *
 * @return A [Size] object representing the dimensions of the [ImageBitmap], or [Size.Zero] if the
 * bitmap is null.
 */
@Stable
fun ImageBitmap?.toSize(): Size {

    return this?.let { bitmap ->

        Size(width = bitmap.width.toFloat(), height = bitmap.height.toFloat())
    } ?: Size.Zero
}

/**
 * Converts an [ImageBitmap] to a [IntSize] object.
 *
 * If the [ImageBitmap] is null, it returns [IntSize.Zero].
 * Otherwise, it creates a [IntSize] object with the width and height of the bitmap.
 *
 * @return A [IntSize] object representing the dimensions of the [ImageBitmap],
 * or [IntSize.Zero] if the bitmap is null.
 */
@Stable
fun ImageBitmap?.toIntSize(): IntSize {

    return this?.let { bitmap ->

        IntSize(width = bitmap.width, height = bitmap.height)
    } ?: IntSize.Zero
}

/**
 * Calculates the aspect ratio of the [ImageBitmap].
 *
 * The aspect ratio is defined as the width divided by the height.
 *
 * @return The aspect ratio of the bitmap as a [Float], or null if the bitmap is null.
 */
@Stable
fun ImageBitmap?.findAspectRatio(): Float? {

    return this?.let { bitmap -> findAspectRatio(width = bitmap.width, height = bitmap.height) }
}

/**
 * Calculates the size of an image that fits within a given canvas size, maintaining aspect ratio
 * and applying an optional reduction.
 *
 * This function determines the optimal dimensions for an image to be displayed within a specified
 * canvas area.
 * It ensures that the image's aspect ratio is preserved.
 *
 * The `reduction` parameter allows for shrinking the fitted image by a certain percentage.
 * For example, a `reduction` of 10 means the final image size will be 90% of the initially fitted
 * size.
 *
 * @param imageSize The original dimensions of the image.
 * @param reduction An optional percentage (0-100) by which to reduce the fitted image size.
 * Defaults to 10.
 * @return The calculated [Size] of the image that fits within the canvas, with the applied
 * reduction.
 */
@Stable
fun Size.fittedImageSize(imageSize: Size, reduction: Int = 10): Size {

    val imageAspectRatio = imageSize.width / imageSize.height
    val canvasAspectRatio = this.width / this.height

    val newWidth: Float
    val newHeight: Float

    when {

        imageAspectRatio > canvasAspectRatio -> {

            newWidth = this.width
            newHeight = newWidth / imageAspectRatio
        }

        else -> {

            newHeight = this.height
            newWidth = newHeight * imageAspectRatio
        }
    }

    val reductionFactor = 1.0F - (reduction / 100.0F)

    val finalWidth = min(newWidth, this.width) * reductionFactor
    val finalHeight = min(newHeight, this.height) * reductionFactor

    return Size(width = finalWidth, height = finalHeight)
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
@Stable
fun ImageBitmap.sameAs(other: ImageBitmap): Boolean {

    return this.asAndroidBitmap().sameAs(other.asAndroidBitmap())
}