package io.bashpsk.emptylibs.imageview.lazy

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize

/**
 * Data representing a high-resolution fragment of an image.
 *
 * @property bitmap The high-res bitmap fragment.
 * @property topLeft The offset of this fragment relative to the base image's top-left corner.
 * @property dstSize The display size of this fragment.
 */
@Immutable
data class ScaledImageFragment(
    val bitmap: ImageBitmap,
    val topLeft: Offset,
    val dstSize: IntSize
)