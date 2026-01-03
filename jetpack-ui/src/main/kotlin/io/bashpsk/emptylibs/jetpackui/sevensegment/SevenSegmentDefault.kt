package io.bashpsk.emptylibs.jetpackui.sevensegment

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Provides default values for the 7-segment display composable.
 */
object SevenSegmentDefault {

    /**
     * The default map of characters to their corresponding 7-segment data.
     */
    val SegmentDataModel = SevenSegmentData.NumberSegmentList

    /**
     * Returns the default colors for the 7-segment display.
     *
     * @param active The color of the active segments.
     * @param inactive The color of the inactive segments.
     * @return The default colors for the 7-segment display.
     */
    @Composable
    fun colors(
        active: Color = MaterialTheme.colorScheme.onSurface,
        inactive: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.20F),
    ): SevenSegmentColors {

        return SevenSegmentColors(active = active, inactive = inactive)
    }

    /**
     * Returns the default properties for the 7-segment display.
     *
     * @param width The width of the segments.
     * @property aspectRatio The aspect ratio of the segments.
     * @param thickness The thickness of the segments.
     * @param space The space between the segments.
     * @return The default properties for the 7-segment display.
     * @property itemSpace The space between the items.
     * @property isRoundedDot Whether the dot should be rounded.
     */
    @Composable
    fun properties(
        width: Dp = 60.dp,
        aspectRatio: Float = 0.60F,
        thickness: Dp = 4.dp,
        space: Dp = 1.2.dp,
        itemSpace: Dp = 0.dp,
        isRoundedDot: Boolean = false
    ): SevenSegmentProperties {

        return SevenSegmentProperties(
            width = width,
            aspectRatio = aspectRatio,
            thickness = thickness,
            space = space,
            itemSpace = itemSpace,
            isRoundedDot = isRoundedDot
        )
    }
}