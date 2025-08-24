package io.bashpsk.emptylibs.imageutils.extension

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlin.math.min

fun ImageBitmap?.toSize(): Size {

    return this?.let { bitmap ->

        Size(width = bitmap.width.toFloat(), height = bitmap.height.toFloat())
    } ?: Size.Zero
}

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
fun ImageBitmap.sameAs(other: ImageBitmap): Boolean {

    return this.asAndroidBitmap().sameAs(other.asAndroidBitmap())
}