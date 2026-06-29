package io.bashpsk.emptylibs.imageview.tile

import androidx.annotation.IntRange
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.roundToIntSize
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.unit.toSize
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
 * @param contentScale Strategy used to determine how to scale the image content within the layout
 * bounds.
 * @param alignment Alignment parameter used to place the image content in the layout bounds.
 * @param alpha Opacity to be applied to the image.
 * @param colorFilter ColorFilter to be applied to the image.
 * @param tileSize The size of each tile in pixels.
 * Defaults to [TileImageViewState.TILE_SIZE_DEFAULT].
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
    tileSize: Int = TileImageViewState.TILE_SIZE_DEFAULT
) {

    val coroutineScope = rememberCoroutineScope()
    val state = rememberTileImageViewState()

    val aspectRatio by remember(imageBitmap) {
        derivedStateOf { imageBitmap.findAspectRatio() ?: 0F }
    }

    RetainedEffect(imageBitmap, tileSize) {

        coroutineScope.launch { state.setParseImageTile(bitmap = imageBitmap, tileSize = tileSize) }

        onRetire { state.onStateClear() }
    }

    Layout(
        modifier = modifier
            .clipToBounds()
            .onGloballyPositioned { coordinates ->

                val rootCoordinates = coordinates.findRootCoordinates()
                val rootRect = Rect(offset = Offset.Zero, size = rootCoordinates.size.toSize())
                val visibleInRoot = coordinates.boundsInRoot().intersect(rootRect)

                state.viewportRect = when (visibleInRoot.isEmpty) {

                    true -> Rect.Zero

                    false -> Rect(
                        topLeft = coordinates.localPositionOf(
                            sourceCoordinates = rootCoordinates,
                            relativeToSource = visibleInRoot.topLeft
                        ),
                        bottomRight = coordinates.localPositionOf(
                            sourceCoordinates = rootCoordinates,
                            relativeToSource = visibleInRoot.bottomRight
                        )
                    )
                }
            }
            .drawBehind {

//                var visibleTiles = 0
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

                val imageViewport = Rect(
                    left = (state.viewportRect.left - baseAlignment.x) / baseScale.scaleX,
                    top = (state.viewportRect.top - baseAlignment.y) / baseScale.scaleY,
                    right = (state.viewportRect.right - baseAlignment.x) / baseScale.scaleX,
                    bottom = (state.viewportRect.bottom - baseAlignment.y) / baseScale.scaleY
                )

                withTransform(
                    transformBlock = {

                        translate(left = baseAlignment.x.toFloat(), top = baseAlignment.y.toFloat())
                        scale(
                            scaleX = baseScale.scaleX,
                            scaleY = baseScale.scaleY,
                            pivot = Offset.Zero
                        )
                    }
                ) {

                    state.imageGridList.forEach { tileImage ->

                        val tileImageRect = Rect(
                            offset = tileImage.position.toOffset(),
                            size = tileImage.size.toSize()
                        )

                        if (imageViewport.overlaps(tileImageRect)) {

//                            visibleTiles++

                            drawImage(
                                image = tileImage.bitmap,
                                dstOffset = tileImage.position,
                                alpha = alpha,
                                colorFilter = colorFilter
                            )
                        }
                    }
                }

//                "VISIBLE TILES: $visibleTiles".setDebug()
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