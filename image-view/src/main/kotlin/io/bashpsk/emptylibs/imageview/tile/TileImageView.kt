package io.bashpsk.emptylibs.imageview.tile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.roundToIntSize
import io.bashpsk.emptylibs.imageutils.extension.findAspectRatio
import io.bashpsk.emptylibs.imageutils.extension.toSize
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * A composable that displays a large [ImageBitmap] by breaking it into smaller tiles.
 * This is useful for displaying very large images that might otherwise exceed memory limits.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param imageBitmap The large [ImageBitmap] to be displayed.
 * @param contentScale Strategy used to determine how to scale the image content within the layout bounds.
 * @param alignment Alignment parameter used to place the image content in the layout bounds.
 * @param alpha Opacity to be applied to the image.
 * @param colorFilter ColorFilter to be applied to the image.
 * @param tileSize The size of each tile in pixels. Defaults to 512.
 * @param zoomScale The current zoom level of the image.
 * @param centerPosition The center position of the viewport within the image.
 * @param viewportSize The size of the visible area of the image.
 */
@Composable
fun TileImageView(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    alpha: Float = 1.0F,
    colorFilter: ColorFilter? = null,
    tileSize: Int = 512,
    zoomScale: Float = 1.0F,
    centerPosition: IntOffset = IntOffset.Zero,
    viewportSize: Size = Size.Unspecified
) {

    val coroutineScope = rememberCoroutineScope()
    val state = rememberTileImageViewState()

    val aspectRatio by remember(imageBitmap) {
        derivedStateOf { imageBitmap.findAspectRatio() ?: 1.0F }
    }

    RetainedEffect(imageBitmap) {

        coroutineScope.launch { state.setParseImageTile(bitmap = imageBitmap, tileSize = tileSize) }

        onRetire { state.onStateClear() }
    }

    Layout(
        modifier = modifier
            .clipToBounds()
            .drawBehind {

                val srcSize = imageBitmap.toSize()

                val baseScale = contentScale.computeScaleFactor(
                    srcSize = srcSize,
                    dstSize = size
                )

                val baseAlignment = alignment.align(
                    size = IntSize(
                        width = (srcSize.width * baseScale.scaleX).roundToInt(),
                        height = (srcSize.height * baseScale.scaleY).roundToInt()
                    ),
                    space = size.roundToIntSize(),
                    layoutDirection = layoutDirection
                )

                val (positionX, positionY) = centerPosition
                val boundSize = (if (viewportSize.isSpecified) viewportSize else size) / zoomScale

                val viewportRect = Rect(
                    offset = Offset(
                        x = (size.width / 2F) - (positionX / zoomScale) - (boundSize.width / 2F),
                        y = (size.height / 2F) - (positionY / zoomScale) - (boundSize.height / 2F)
                    ),
                    size = boundSize
                )

                state.imageGridList.forEach { tileImage ->

                    val tileImageRect = Rect(
                        offset = Offset(
                            x = (tileImage.position.x * baseScale.scaleX) + baseAlignment.x,
                            y = (tileImage.position.y * baseScale.scaleY) + baseAlignment.y
                        ),
                        size = Size(
                            width = tileImage.size.width * baseScale.scaleX,
                            height = tileImage.size.height * baseScale.scaleY
                        )
                    )

                    if (viewportRect.overlaps(tileImageRect)) drawImage(
                        image = tileImage.bitmap,
                        dstOffset = tileImageRect.topLeft.round(),
                        dstSize = tileImageRect.size.roundToIntSize(),
                        alpha = alpha,
                        colorFilter = colorFilter
                    )
                }
            },
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