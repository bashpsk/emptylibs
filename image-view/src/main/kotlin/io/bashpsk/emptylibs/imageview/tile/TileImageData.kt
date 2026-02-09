package io.bashpsk.emptylibs.imageview.tile

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

/**
 * Represents the data for a specific tile within a larger tiled image.
 *
 * @property bitmap The [ImageBitmap] containing the visual content of the tile.
 * @property position The [IntOffset] representing the top-left corner of this tile relative to the
 * source image.
 * @property size The [IntSize] representing the width and height of this tile in pixels.
 */
@Immutable
internal data class TileImageData(
    val bitmap: ImageBitmap,
    val position: IntOffset = IntOffset.Zero,
    val size: IntSize = IntSize.Zero
)