package io.bashpsk.emptylibs.imageedit.extension

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import kotlin.math.min

fun ImageBitmap.eraseImage(area: Rect): ImageBitmap {

    val imageBitmap = this

    val outputImageBitmap = ImageBitmap(
        width = imageBitmap.width,
        height = imageBitmap.height,
        config = ImageBitmapConfig.Argb8888
    )

    val paint = Paint().apply {

        isAntiAlias = true
        shader = ImageShader(imageBitmap)
    }

    Canvas(image = outputImageBitmap).apply {

        save()
        clipRect(rect = area)
        drawImageRect(image = imageBitmap, paint = paint)
        restore()
    }

    return outputImageBitmap
}

fun ImageBitmap.eraseImage(area: Path): ImageBitmap {

    val imageBitmap = this

    val outputImageBitmap = ImageBitmap(
        width = imageBitmap.width,
        height = imageBitmap.height,
        config = ImageBitmapConfig.Argb8888
    )

    val paint = Paint().apply {

        isAntiAlias = true
        shader = ImageShader(imageBitmap)
    }

    Canvas(image = outputImageBitmap).apply {

        save()
        clipPath(path = area)
        drawImageRect(image = imageBitmap, paint = paint)
        restore()
    }

    return outputImageBitmap
}

fun ImageBitmap.shapeOverImage(area: Path, areaPaint: Paint = Paint()): ImageBitmap {

    val imageBitmap = this

    val outputImageBitmap = ImageBitmap(
        width = imageBitmap.width,
        height = imageBitmap.height,
        config = ImageBitmapConfig.Argb8888
    )

    val paint = Paint().apply {

        isAntiAlias = true
        shader = ImageShader(imageBitmap)
    }

    Canvas(image = outputImageBitmap).apply {

        save()
        drawImageRect(image = imageBitmap, paint = paint)
        drawPath(path = area, paint = areaPaint)
        restore()
    }

    return outputImageBitmap
}

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