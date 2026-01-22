package io.bashpsk.emptylibs.jetpackui.text

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration properties for the [LazyTextViewer].
 *
 * @property contentStyle The [TextStyle] to be applied to the text content of each line.
 * @property numberStyle The [TextStyle] to be applied to the line numbers.
 * @property lineSpace The vertical spacing between lines.
 * @property numberSpace The horizontal spacing between the line number and the text content.
 * @property softWrapEnabled Whether the text should wrap at the edge of the screen.
 * If false, the viewer will be horizontally scrollable.
 */
@Immutable
data class LazyTextViewerProperties(
    val contentStyle: TextStyle = TextStyle.Default,
    val numberStyle: TextStyle = TextStyle.Default,
    val lineSpace: Dp = 6.dp,
    val numberSpace: Dp = 8.dp,
    val softWrapEnabled: Boolean = false
)