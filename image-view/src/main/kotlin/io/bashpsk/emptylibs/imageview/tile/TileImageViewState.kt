package io.bashpsk.emptylibs.imageview.tile

import android.graphics.Bitmap
import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import io.bashpsk.emptylibs.imageutils.extension.toIntSize
import io.bashpsk.emptylibs.imageview.tile.TileImageViewState.Companion.TILE_SIZE_DEFAULT
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Creates and retains a [TileImageViewState] instance across recompositions.
 *
 * @param imageBitmap The [ImageBitmap] to be tiled.
 * @param tileSize The size of each tile in pixels. Defaults to [TILE_SIZE_DEFAULT].
 *
 * @return A [TileImageViewState] instance.
 */
@Composable
internal fun rememberTileImageViewState(
    imageBitmap: ImageBitmap,
    @IntRange(1L, Int.MAX_VALUE.toLong())
    tileSize: Int = TILE_SIZE_DEFAULT
): TileImageViewState {

    val coroutineScope = rememberCoroutineScope()

    return retain(coroutineScope, imageBitmap, tileSize) {
        TileImageViewState(coroutineScope = coroutineScope, imageBitmap = imageBitmap, tileSize = tileSize)
    }
}

/**
 * A state class responsible for holding and managing the tiled image data for [TileImageView].
 * @param imageBitmap The [ImageBitmap] to be tiled.
 * @param tileSize The size of each tile in pixels. Defaults to [TILE_SIZE_DEFAULT].
 */
@Stable
internal class TileImageViewState(
    internal val coroutineScope: CoroutineScope,
    internal val imageBitmap: ImageBitmap,
    @param:IntRange(1L, Int.MAX_VALUE.toLong())
    internal val tileSize: Int
) {

    /**
     * A list of [TileImageData] representing the tiles of the image.
     */
    internal var imageGridList by mutableStateOf(persistentListOf<TileImageData>())

    /**
     * The visible rectangular area of the viewport in pixels, used to determine which tiles are
     * currently visible.
     */
    internal var viewportRect by mutableStateOf(Rect.Zero)

    init {
        coroutineScope.launch { setParseImageTile() }
    }

    /**
     * Parses the given [ImageBitmap] into tiles of the specified size and updates [imageGridList].
     */
    internal suspend fun setParseImageTile() = withContext(context = Dispatchers.IO) {

        onStateClear()

        val imageWidth = imageBitmap.width
        val imageHeight = imageBitmap.height

        imageGridList = persistentListOf<TileImageData>().mutate { tiles ->

            (0 until imageHeight step tileSize).forEach { y ->

                (0 until imageWidth step tileSize).forEach { x ->

                    val tileWidth = minOf(tileSize, imageWidth - x)
                    val tileHeight = minOf(tileSize, imageHeight - y)

                    val tileBitmap = Bitmap.createBitmap(
                        imageBitmap.asAndroidBitmap(),
                        x,
                        y,
                        tileWidth,
                        tileHeight
                    ).asImageBitmap()

                    tiles.add(
                        TileImageData(
                            bitmap = tileBitmap,
                            position = IntOffset(x = x, y = y),
                            size = tileBitmap.toIntSize()
                        )
                    )
                }
            }
        }
    }

    /**
     * Clears the [imageGridList] to prepare for a new image.
     */
    internal fun onStateClear() {

        imageGridList = persistentListOf()
    }

    companion object {

        const val TILE_SIZE_DEFAULT = 512
    }
}