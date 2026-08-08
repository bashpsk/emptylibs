package io.bashpsk.emptylibs.imageview.tile

import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import io.bashpsk.emptylibs.imageutils.extension.findAspectRatio
import kotlin.math.roundToInt

/**
 * A composable that displays a large [ImageBitmap] by breaking it into smaller tiles.
 * This is useful for displaying very large images that might otherwise exceed memory limits.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param imageBitmap The large [ImageBitmap] to be displayed.
 * @param contentScale Strategy used to determine how to scale the image content within the layout
 * bounds.
 * @param alignment Alignment parameter used to place the image content in the layout bounds.
 * @param alpha Opacity to be applied to the image.
 * @param colorFilter ColorFilter to be applied to the image.
 * @param tileSize The size of each tile in pixels. Defaults to 512.
 */
@Composable
fun TileImageView(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    alpha: Float = 1.0F,
    colorFilter: ColorFilter? = null,
    @IntRange(1L, Int.MAX_VALUE.toLong())
    tileSize: Int = 512
) {

    val aspectRatio by remember(imageBitmap) {
        derivedStateOf { imageBitmap.findAspectRatio() ?: 0F }
    }

    Layout(
        modifier = modifier
            .clipToBounds()
            .tileImageViewModifier(
                imageBitmap = imageBitmap,
                contentScale = contentScale,
                alignment = alignment,
                alpha = alpha,
                colorFilter = colorFilter,
                tileSize = tileSize
            ),
        content = {}
    ) { _, constraints ->

        val layoutWidth: Int
        val layoutHeight: Int

        when {

            constraints.hasFixedWidth && constraints.hasFixedHeight -> {

                layoutWidth = constraints.maxWidth
                layoutHeight = constraints.maxHeight
            }

            constraints.hasFixedWidth -> {

                layoutWidth = constraints.maxWidth
                layoutHeight = (layoutWidth / aspectRatio).roundToInt()
                    .coerceIn(constraints.minHeight..constraints.maxHeight)
            }

            constraints.hasFixedHeight -> {

                layoutHeight = constraints.maxHeight
                layoutWidth = (layoutHeight * aspectRatio).roundToInt()
                    .coerceIn(constraints.minWidth..constraints.maxWidth)
            }

            else -> {

                layoutWidth = constraints.minWidth
                layoutHeight = constraints.minHeight
            }
        }

        layout(width = layoutWidth, height = layoutHeight) {}
    }
}