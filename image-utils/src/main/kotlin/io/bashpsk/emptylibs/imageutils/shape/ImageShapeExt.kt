package io.bashpsk.emptylibs.imageutils.shape

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import io.bashpsk.emptylibs.composeutils.shape.PathShape
import io.bashpsk.emptylibs.composeutils.shape.toPath
import io.bashpsk.emptylibs.imageutils.extension.toSize
import kotlin.math.roundToInt

/**
 * Applies a shape mask to an [ImageBitmap].
 *
 * This function takes an [ImageBitmap] and an [PathShape] and returns a new [ImageBitmap]
 * where the original image is clipped to the specified shape.
 *
 * @param imageBitmap The input [ImageBitmap] to be masked.
 * @return A new [ImageBitmap] with the shape mask applied.
 */
fun PathShape.bitmapMask(imageBitmap: ImageBitmap): ImageBitmap {

    val imageSize = imageBitmap.toSize()

    val outputImageBitmap = ImageBitmap(
        width = imageSize.width.roundToInt(),
        height = imageSize.height.roundToInt(),
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