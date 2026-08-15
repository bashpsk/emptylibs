package io.bashpsk.emptylibs.imageview.lazy

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

/**
 * Data representing a high-resolution fragment of an image.
 *
 * @property bitmap The high-res bitmap fragment.
 * @property offset The offset of this fragment relative to the base image's top-left corner.
 * @property size The display size of this fragment.
 */
@Immutable
data class ScaledImageFragment(
    val bitmap: ImageBitmap,
    val offset: IntOffset,
    val size: IntSize
)