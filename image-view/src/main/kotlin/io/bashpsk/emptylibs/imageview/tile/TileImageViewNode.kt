package io.bashpsk.emptylibs.imageview.tile

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toIntSize
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.unit.toSize
import io.bashpsk.emptylibs.composeutils.layout.calculateViewport
import io.bashpsk.emptylibs.imageutils.extension.toSize
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * A [Modifier.Node] that implements efficient large image rendering by splitting an [ImageBitmap]
 * into smaller tiles and only drawing those that intersect with the current visible viewport.
 *
 * This node manages the lifecycle of tile generation asynchronously and updates its drawing
 * state based on global position changes, making it suitable for high-resolution images
 * within scrollable containers.
 *
 * @property imageBitmap The source image to be tiled and displayed.
 * @property contentScale The strategy used to determine how the image should be scaled to fit the
 * node's size.
 * @property alignment The alignment of the image within the node's bounds.
 * @property alpha The opacity applied to the drawn tiles.
 * @property colorFilter The [ColorFilter] applied to the drawn tiles.
 * @property tileSize The maximum width and height (in pixels) for each generated tile.
 */
internal class TileImageViewNode(
    private var imageBitmap: ImageBitmap,
    private var contentScale: ContentScale,
    private var alignment: Alignment,
    private var alpha: Float,
    private var colorFilter: ColorFilter?,
    private var tileSize: Int
) : Modifier.Node(), DrawModifierNode, GlobalPositionAwareModifierNode {

    private var tileList by mutableStateOf(persistentListOf<TileImageData>())

    private var viewportRect by mutableStateOf(Rect.Zero)

    private var tileJob by mutableStateOf<Job?>(null)

    override fun onAttach() {
        super.onAttach()

        rebuildTiles()
    }

    override fun onDetach() {
        super.onDetach()

        tileJob?.cancel()
        tileJob = null
        tileList = persistentListOf()
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {

        viewportRect = coordinates.calculateViewport()
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {

        val srcSize = imageBitmap.toSize()
        val baseScale = contentScale.computeScaleFactor(srcSize = srcSize, dstSize = size)

        val scaledImageSize = IntSize(
            width = (srcSize.width * baseScale.scaleX).roundToInt(),
            height = (srcSize.height * baseScale.scaleY).roundToInt()
        )

        val baseAlignment = alignment.align(
            size = scaledImageSize,
            space = size.toIntSize(),
            layoutDirection = layoutDirection
        )

        val imageViewport = Rect(
            left = (viewportRect.left - baseAlignment.x) / baseScale.scaleX,
            top = (viewportRect.top - baseAlignment.y) / baseScale.scaleY,
            right = (viewportRect.right - baseAlignment.x) / baseScale.scaleX,
            bottom = (viewportRect.bottom - baseAlignment.y) / baseScale.scaleY
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

            tileList.forEach { tile ->

                val tileRect = Rect(offset = tile.position.toOffset(), size = tile.size.toSize())

                if (!imageViewport.overlaps(other = tileRect)) return@forEach

                drawImage(
                    image = tile.bitmap,
                    dstOffset = tile.position,
                    alpha = alpha,
                    colorFilter = colorFilter
                )
            }
        }
    }

    fun update(
        imageBitmap: ImageBitmap,
        contentScale: ContentScale,
        alignment: Alignment,
        alpha: Float,
        colorFilter: ColorFilter?,
        tileSize: Int
    ) {

        val imageChanged = this.imageBitmap !== imageBitmap
        val tileSizeChanged = this.tileSize != tileSize

        this.imageBitmap = imageBitmap
        this.contentScale = contentScale
        this.alignment = alignment
        this.alpha = alpha
        this.colorFilter = colorFilter
        this.tileSize = tileSize

        if (imageChanged || tileSizeChanged) rebuildTiles()
        invalidateDraw()
    }

    private fun rebuildTiles() {

        tileJob?.cancel()
        tileList = persistentListOf()
        invalidateDraw()

        val bitmap = imageBitmap
        val size = tileSize

        tileJob = coroutineScope.launch(context = Dispatchers.IO) {

            val result = persistentListOf<TileImageData>().mutate { list ->

                val imageWidth = bitmap.width
                val imageHeight = bitmap.height
                val androidBitmap = bitmap.asAndroidBitmap()

                for (y in 0 until imageHeight step size) {

                    for (x in 0 until imageWidth step size) {

                        val width = minOf(a = size, b = imageWidth - x)
                        val height = minOf(a = size, b = imageHeight - y)

                        val tileBitmap = Bitmap.createBitmap(
                            androidBitmap,
                            x,
                            y,
                            width,
                            height
                        )

                        list.add(
                            element = TileImageData(
                                bitmap = tileBitmap.asImageBitmap(),
                                position = IntOffset(x = x, y = y),
                                size = IntSize(width = width, height = height)
                            )
                        )
                    }
                }
            }

            withContext(context = Dispatchers.Main.immediate) {

                if (isAttached) {
                    tileList = result
                    invalidateDraw()
                }
            }
        }
    }
}