package io.bashpsk.emptylibs.imageedit.edit

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
 * @property handleColor The color of the drag handles.
 * @property borderThickness The thickness of the border around the crop selection.
 * @property borderColor The color of the border around the crop selection.
 * @property targetSize The size of the target lines (crosshairs) in the center of the crop
 * selection.
 * @property targetThickness The thickness of the target lines.
 * @property targetColor The color of the target lines.
 */
@Immutable
data class ImageEditConfig(
    val handleWidth: Dp = 16.dp,
    val handleHeight: Dp = 4.dp,
    val centerHandleWidth: Dp = 12.dp,
    val handleColor: Color = Color.White,
    val borderThickness: Dp = 2.dp,
    val borderColor: Color = Color.Cyan,
    val targetSize: Dp = 16.dp,
    val targetThickness: Dp = 1.dp,
    val targetColor: Color = Color.Yellow,
    val minItemSize: Dp = 40.dp,
    val itemBoxColor: Color = Color.DarkGray.copy(alpha = 0.45F),
) {

    companion object {

        /**
         * Creates a [ImageEditConfig] instance with colors based on the current Material Theme's
         * surface colors.
         *
         * This provides a pre-configured setup that adapts to the application's theme,
         * ensuring visual consistency.
         *
         * Specifically, it sets:
         * - `handleColor` to `MaterialTheme.colorScheme.onSurface`
         * - `targetColor` to `MaterialTheme.colorScheme.surfaceTint`
         * - `borderColor` to `MaterialTheme.colorScheme.errorContainer`
         *
         * @return A [ImageEditConfig] instance themed with surface-based colors.
         */
        @Composable
        fun surfaceBased(): ImageEditConfig {

            val handleColor = MaterialTheme.colorScheme.onSurface
            val targetColor = MaterialTheme.colorScheme.surfaceTint
            val borderColor = MaterialTheme.colorScheme.errorContainer

            return ImageEditConfig(
                handleColor = handleColor,
                targetColor = targetColor,
                borderColor = borderColor
            )
        }
    }
}