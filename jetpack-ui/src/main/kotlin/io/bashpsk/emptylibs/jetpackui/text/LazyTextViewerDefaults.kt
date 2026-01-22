package io.bashpsk.emptylibs.jetpackui.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Contains the default values used by [LazyTextViewer].
 */
object LazyTextViewerDefaults {

    /**
     * Creates a [LazyTextViewerProperties] with default or custom values.
     *
     * @param contentStyle The [TextStyle] for text content.
     * Defaults to [MaterialTheme.typography.bodyMedium].
     * @param numberStyle The [TextStyle] for line numbers.
     * Defaults to [MaterialTheme.typography.bodyMedium].
     * @param lineSpace The vertical spacing between lines. Defaults to 6.dp.
     * @param numberSpace The horizontal spacing between line number and content. Defaults to 8.dp.
     * @param softWrapEnabled Whether soft wrap is enabled. Defaults to false.
     * @return A new [LazyTextViewerProperties] instance.
     */
    @Composable
    fun properties(
        contentStyle: TextStyle = MaterialTheme.typography.bodyMedium,
        numberStyle: TextStyle = MaterialTheme.typography.bodyMedium,
        lineSpace: Dp = 6.dp,
        numberSpace: Dp = 8.dp,
        softWrapEnabled: Boolean = false
    ): LazyTextViewerProperties {

        return LazyTextViewerProperties(
            contentStyle = contentStyle,
            numberStyle = numberStyle,
            lineSpace = lineSpace,
            numberSpace = numberSpace,
            softWrapEnabled = softWrapEnabled
        )
    }
}