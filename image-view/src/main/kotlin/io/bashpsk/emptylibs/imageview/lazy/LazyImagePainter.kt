package io.bashpsk.emptylibs.imageview.lazy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.toIntSize
import io.bashpsk.emptylibs.imageutils.extension.toSize

/**
 * Creates and remembers a [LazyImagePainter] that handles the drawing of a base image and
 * an optional overlay fragment.
 *
 * The painter is retained across recompositions as long as the [baseImage] and [imageFragment]
 * instances remain the same.
 *
 * @param baseImage The primary [ImageBitmap] to be drawn as the background.
 * @param imageFragment An optional [ScaledImageFragment] representing a specific detail or
 * high-resolution portion to be rendered over the base image.
 * @return A [LazyImagePainter] instance initialized with the provided image data.
 */
@Composable
internal fun rememberLazyImagePainter(
    baseImage: ImageBitmap,
    imageFragment: ScaledImageFragment?
): LazyImagePainter {

    return retain(baseImage, imageFragment) {
        LazyImagePainter(baseImage = baseImage, imageFragment = imageFragment)
    }
}

/**
 * A [Painter] that draws a base [ImageBitmap] and an optional [ScaledImageFragment] on top of it.
 *
 * This painter is typically used in lazy-loading scenarios where a base image
 * (e.g., a low-resolution placeholder) is displayed, and a specific high-resolution fragment is
 * layered over a portion of it once loaded.
 *
 * @property baseImage The primary [ImageBitmap] that defines the intrinsic size and background.
 * @property imageFragment An optional [ScaledImageFragment] representing a localized detail or
 * high-resolution tile to be drawn at a specific offset over the [baseImage].
 */
@Stable
internal class LazyImagePainter(
    private val baseImage: ImageBitmap,
    private val imageFragment: ScaledImageFragment?
) : Painter() {

    override val intrinsicSize: Size
        get() = baseImage.toSize()

    override fun DrawScope.onDraw() {

        drawImage(
            image = baseImage,
            dstSize = size.toIntSize(),
            blendMode = BlendMode.Src
        )

        imageFragment?.let { fragment ->

            translate(
                left = fragment.topLeft.x,
                top = fragment.topLeft.y
            ) {

                drawImage(
                    image = fragment.bitmap,
                    dstSize = fragment.dstSize,
                    blendMode = BlendMode.Src
                )
            }
        }
    }
}