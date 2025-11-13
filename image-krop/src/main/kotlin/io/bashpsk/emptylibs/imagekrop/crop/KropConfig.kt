package io.bashpsk.emptylibs.imagekrop.crop

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration data class for customizing the appearance and behavior of the image cropping UI.
 *
 * @property handleWidth The width of the corner and side drag handles.
 * @property handleHeight The height of the corner and side drag handles.
 * @property centerHandleWidth The width of the center drag handle
 * (used for moving the entire crop area).
 * @property minimumCropSize The minimum allowable size (width or height) for the crop selection.
 * @property handleColor The color of the drag handles.
 * @property borderThickness The thickness of the border around the crop selection.
 * @property borderColor The color of the border around the crop selection.
 * @property targetSize The size of the target lines (crosshairs) in the center of the crop
 * selection.
 * @property targetThickness The thickness of the target lines.
 * @property targetColor The color of the target lines.
 * @property overlayColor The color of the overlay outside the crop selection area.
 * @property shapeBorder The thickness of the border for any predefined shapes (if applicable).
 * @property shapeColor The fill color for any predefined shapes (if applicable).
 */
@Immutable
data class KropConfig(
    val handleWidth: Dp = 20.dp,
    val handleHeight: Dp = 4.dp,
    val centerHandleWidth: Dp = 16.dp,
    val minimumCropSize: Dp = 40.dp,
    val handleColor: Color = Color.White,
    val borderThickness: Dp = 2.dp,
    val borderColor: Color = Color.Cyan,
    val targetSize: Dp = 16.dp,
    val targetThickness: Dp = 1.dp,
    val targetColor: Color = Color.Yellow,
    val overlayColor: Color = Color.Black.copy(alpha = 0.5F),
    val shapeBorder: Dp = 0.5.dp,
    val shapeColor: Color = Color.Green.copy(alpha = 0.5F),
) {

    companion object {

        /**
         * Creates a [KropConfig] instance with colors based on the current Material Theme's
         * surface colors.
         *
         * This provides a pre-configured setup that adapts to the application's theme,
         * ensuring visual consistency.
         *
         * Specifically, it sets:
         * - `handleColor` to `MaterialTheme.colorScheme.onSurface`
         * - `targetColor` to `MaterialTheme.colorScheme.surfaceTint`
         * - `borderColor` to `MaterialTheme.colorScheme.errorContainer`
         * - `overlayColor` to `MaterialTheme.colorScheme.surfaceVariant` with 50% alpha
         * - `minimumCropSize` to 100.dp
         *
         * @return A [KropConfig] instance themed with surface-based colors.
         */
        @Composable
        fun surfaceBased(): KropConfig {

            val handleColor = MaterialTheme.colorScheme.onSurface
            val targetColor = MaterialTheme.colorScheme.surfaceTint
            val borderColor = MaterialTheme.colorScheme.errorContainer
            val overlayColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5F)

            return KropConfig(
                handleColor = handleColor,
                targetColor = targetColor,
                borderColor = borderColor,
                overlayColor = overlayColor
            )
        }
    }
}