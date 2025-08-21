package io.bashpsk.emptylibs.imageedit.extension

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path

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