package io.bashpsk.emptylibs.imageview.tile

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

/**
 * A [ModifierNodeElement] that creates and updates a [TileImageViewNode] to render a tiled image.
 *
 * This element efficiently manages the lifecycle of the node responsible for drawing an
 * [ImageBitmap] repeatedly across the layout area based on the specified [tileSize].
 *
 * @property imageBitmap The [ImageBitmap] to be drawn as a tile.
 * @property contentScale The strategy used to determine how the image is scaled within each tile.
 * @property alignment The alignment of the image within each tile.
 * @property alpha The opacity to be applied to the image when rendered.
 * @property colorFilter An optional [ColorFilter] to apply to the image.
 * @property tileSize The size (in pixels) of the square area each tile should occupy.
 */
internal data class TileImageViewElement(
    val imageBitmap: ImageBitmap,
    val contentScale: ContentScale,
    val alignment: Alignment,
    val alpha: Float,
    val colorFilter: ColorFilter?,
    val tileSize: Int
) : ModifierNodeElement<TileImageViewNode>() {

    override fun create(): TileImageViewNode {

        return TileImageViewNode(
            imageBitmap = imageBitmap,
            contentScale = contentScale,
            alignment = alignment,
            alpha = alpha,
            colorFilter = colorFilter,
            tileSize = tileSize
        )
    }

    override fun update(node: TileImageViewNode) {

        node.update(
            imageBitmap = imageBitmap,
            contentScale = contentScale,
            alignment = alignment,
            alpha = alpha,
            colorFilter = colorFilter,
            tileSize = tileSize
        )
    }

    override fun InspectorInfo.inspectableProperties() {

        name = "TileImageViewModifier"
    }
}