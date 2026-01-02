package io.bashpsk.emptylibs.jetpackui.sevensegment

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Represents the properties of a 7-segment display.
 *
 * @property thickness The thickness of the segments.
 * @property space The space between the segments.
 */
@Immutable
data class SevenSegmentProperties(val thickness: Dp = 4.dp, val space: Dp = 1.2.dp)