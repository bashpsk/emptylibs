package io.bashpsk.emptylibs.imageview.tile

import androidx.annotation.IntRange
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale

/**
 * Applies a tiled rendering effect to an [ImageBitmap] within a [Modifier] chain.
 *
 * This modifier partitions the provided image into smaller tiles of a specified size to optimize
 * memory usage and rendering performance for large images, while still supporting standard
 * layout properties like scaling and alignment.
 *
 * @param imageBitmap The source image to be rendered as tiles.
 * @param contentScale Strategy for scaling the image within the component bounds.
 * Defaults to [ContentScale.Fit].
 * @param alignment The alignment of the image within the component bounds.
 * Defaults to [Alignment.Center].
 * @param alpha Opacity to be applied to the image, from 0.0 (transparent) to 1.0 (opaque).
 * Defaults to 1.0F.
 * @param colorFilter Optional [ColorFilter] to apply a visual effect (e.g., tinting) to the image.
 * @param tileSize The size (width and height) in pixels of each individual tile.
 * Must be at least 1. Defaults to 512.
 * @return A [Modifier] that draws the tiled image.
 */
internal fun Modifier.tileImageViewModifier(
    imageBitmap: ImageBitmap,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    alpha: Float = 1.0F,
    colorFilter: ColorFilter? = null,
    @IntRange(1L, Int.MAX_VALUE.toLong())
    tileSize: Int = 512
): Modifier {

    return this then TileImageViewElement(
        imageBitmap = imageBitmap,
        contentScale = contentScale,
        alignment = alignment,
        alpha = alpha,
        colorFilter = colorFilter,
        tileSize = tileSize
    )
}