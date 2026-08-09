package io.bashpsk.emptylibs.jetpackui.sevensegment

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Represents the properties of a 7-segment display.
 *
 * @property width The width of the segments.
 * @property aspectRatio The aspect ratio of the segments.
 * @property thickness The thickness of the segments.
 * @property space The space between the segments.
 * @property itemSpace The space between the items.
 * @property isRoundedDot Whether the dot should be rounded.
 */
@Immutable
data class SevenSegmentProperties(
    val width: Dp = 48.dp,
    val aspectRatio: Float = 1.0F,
    val thickness: Dp = 4.dp,
    val space: Dp = 1.dp,
    val itemSpace:Dp = 6.dp,
    val isRoundedDot: Boolean = false
)