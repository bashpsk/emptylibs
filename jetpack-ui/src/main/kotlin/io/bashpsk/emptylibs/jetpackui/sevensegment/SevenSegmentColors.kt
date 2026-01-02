package io.bashpsk.emptylibs.jetpackui.sevensegment

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Represents the colors of a 7-segment display.
 *
 * @property active The color of the active segments.
 * @property inactive The color of the inactive segments.
 */
@Immutable
data class SevenSegmentColors(
    val active: Color = Color.Unspecified,
    val inactive: Color = Color.Unspecified
)