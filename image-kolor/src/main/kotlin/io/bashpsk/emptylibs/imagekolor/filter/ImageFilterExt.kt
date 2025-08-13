package io.bashpsk.emptylibs.imagekolor.filter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.round

/**
 * Applies a color filter to an ImageBitmap.
 *
 * @param filter The type of image filter to apply.
 * @return A new ImageBitmap with the color filter applied.
 */
internal fun ImageBitmap.getKolorFilterBitmap(filter: ImageFilterType): ImageBitmap {

    return getKolorFilterBitmap(filter = filter.colorFilter)
}

/**
 * Applies a [ColorFilter] to an [ImageBitmap] and returns a new [ImageBitmap] with the filter
 * applied.
 *
 * @param filter The [ColorFilter] to apply to the image.
 * @return A new [ImageBitmap] with the specified [ColorFilter] applied.
 */
internal fun ImageBitmap.getKolorFilterBitmap(filter: ColorFilter): ImageBitmap {

    val sourceImage = this@getKolorFilterBitmap

    val imageBitmap = ImageBitmap(
        width = sourceImage.width,
        height = sourceImage.height,
        config = sourceImage.config,
        colorSpace = sourceImage.colorSpace
    )

    val paint = Paint().apply {

        colorFilter = filter
    }

    Canvas(image = imageBitmap).drawImageRect(
        image = sourceImage,
        dstOffset = Offset.Zero.round(),
        paint = paint
    )

    return imageBitmap
}