package io.bashpsk.emptylibs.imageview.lazy

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale

/**
 * A composable that displays a base image with an optional high-resolution fragment overlay,
 * optimized for large images and zooming scenarios.
 *
 * @param modifier The [Modifier] to be applied to the layout.
 * @param baseImage The base [ImageBitmap] to be drawn.
 * @param fragment An optional [ScaledImageFragment] to overlay, typically used for high-detail
 * areas.
 * @param contentDescription Text used by accessibility services to describe what this image
 * represents.
 * @param alignment Optional alignment parameter used to place the [ImageBitmap] in the given
 * bounds.
 * @param contentScale Optional scale parameter used to determine the aspect ratio scaling to be
 * used.
 * @param alpha Optional opacity to be applied to the image when it is rendered onscreen.
 */
@Composable
fun LazyImage(
    modifier: Modifier = Modifier,
    baseImage: ImageBitmap,
    fragment: ScaledImageFragment?,
    contentDescription: String?,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {

    val imagePainter = rememberLazyImagePainter(baseImage = baseImage, imageFragment = fragment)

    Image(
        modifier = modifier,
        painter = imagePainter,
        contentDescription = contentDescription,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter
    )
}