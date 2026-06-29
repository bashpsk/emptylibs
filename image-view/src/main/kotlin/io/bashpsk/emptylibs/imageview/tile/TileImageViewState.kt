package io.bashpsk.emptylibs.imageview.tile

import android.graphics.Bitmap
import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Creates and retains a [TileImageViewState] instance across recompositions.
 *
 * @return A [TileImageViewState] instance.
 */
@Composable
internal fun rememberTileImageViewState(): TileImageViewState {

    return retain { TileImageViewState() }
}

/**
 * A state class responsible for holding and managing the tiled image data for [TileImageView].
 */
@Stable
internal class TileImageViewState() {

    /**
     * A list of [TileImageData] representing the tiles of the image.
     */
    internal var imageGridList by mutableStateOf(persistentListOf<TileImageData>())

    /**
     * The visible rectangular area of the viewport in pixels, used to determine which tiles are
     * currently visible.
     */
    internal var viewportRect by mutableStateOf(Rect.Zero)

    /**
     * Parses the given [ImageBitmap] into tiles of the specified size and updates [imageGridList].
     *
     * @param bitmap The [ImageBitmap] to be tiled.
     * @param tileSize The size of each tile in pixels. Defaults to [TILE_SIZE_DEFAULT].
     */
    internal suspend fun setParseImageTile(
        bitmap: ImageBitmap,
        @IntRange(1L, Int.MAX_VALUE.toLong())
        tileSize: Int = TILE_SIZE_DEFAULT
    ) = withContext(context = Dispatchers.Default) {

        onStateClear()

        val imageWidth = bitmap.width
        val imageHeight = bitmap.height

        imageGridList = persistentListOf<TileImageData>().mutate { tiles ->

            (0 until imageHeight step tileSize).forEach { y ->

                (0 until imageWidth step tileSize).forEach { x ->

                    val tileWidth = minOf(tileSize, imageWidth - x)
                    val tileHeight = minOf(tileSize, imageHeight - y)

                    val tileBitmap = Bitmap.createBitmap(
                        bitmap.asAndroidBitmap(),
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