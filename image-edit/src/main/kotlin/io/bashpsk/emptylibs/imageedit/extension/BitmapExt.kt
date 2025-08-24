package io.bashpsk.emptylibs.imageedit.extension

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import kotlin.math.min

internal val ImageBitmap?.size: Size
    get() = this?.let { bitmap ->

        Size(width = bitmap.width.toFloat(), height = bitmap.height.toFloat())
    } ?: Size.Zero

internal fun Size.fittedImageSize(imageSize: Size, reduction: Int = 10): Size {

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