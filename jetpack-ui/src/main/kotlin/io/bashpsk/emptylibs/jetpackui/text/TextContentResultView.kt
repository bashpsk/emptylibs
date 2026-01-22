package io.bashpsk.emptylibs.jetpackui.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onFirstVisible
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch

/**
 * Internal composable that represents a single line in the [LazyTextViewer].
 * It triggers content loading when the line becomes visible.
 *
 * @param modifier The modifier for this row.
 * @param itemPosition The index of the line in the text source.
 * @param state The [LazyTextViewerState] used to load the content.
 * @param numberBoxSize The width allocated for the line number column.
 * @param properties Configuration properties for styling.
 */
@Composable
internal fun TextContentResultView(
    modifier: Modifier = Modifier,
    itemPosition: Int,
    state: LazyTextViewerState,
    numberBoxSize: Dp,
    properties: LazyTextViewerProperties
) {

    var contentResult by retain { mutableStateOf<TextContentResult>(TextContentResult.Init) }

    val numberLabel by remember(itemPosition) {
        derivedStateOf { state.getFormattedLineNumber(index = itemPosition) }
    }

    val firstVisibleModifier = Modifier.onFirstVisible(minFractionVisible = 0.1F) {

        state.apply {

            coroutineScope.launch { contentResult = readLineContent(index = itemPosition) }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(firstVisibleModifier),
        horizontalArrangement = Arrangement.spacedBy(space = properties.numberSpace),
        verticalAlignment = Alignment.Top
    ) {

        when (contentResult) {

            is TextContentResult.Init -> TextLineLoading(
                numberLabel = numberLabel,
                numberBoxSize = numberBoxSize,
                properties = properties
            )

            is TextContentResult.Content -> TextLineContent(
                numberLabel = numberLabel,
                content = (contentResult as TextContentResult.Content).text,
                numberBoxSize = numberBoxSize,
                properties = properties
            )

            is TextContentResult.Error -> TextLineContent(
                numberLabel = numberLabel,
                content = (contentResult as TextContentResult.Error).message,
                numberBoxSize = numberBoxSize,
                properties = properties
            )
        }
    }
}

/**
 * Displays the loaded text content along with its line number.
 *
 * @param numberLabel The formatted line number string.
 * @param content The text to display.
 * @param numberBoxSize Fixed width for the line number.
 * @param properties Styling properties.
 */
@Composable
private fun RowScope.TextLineContent(
    numberLabel: String,
    content: String,
    numberBoxSize: Dp,
    properties: LazyTextViewerProperties
) {

    LineNumberView(
        modifier = Modifier.width(width = numberBoxSize),
        itemPosition = numberLabel,
        properties = properties
    )

    LineContentView(
        modifier = Modifier,
        content = content,
        properties = properties
    )
}

/**
 * Placeholder view shown while a line of text is being loaded.
 *
 * @param numberLabel The formatted line number string.
 * @param numberBoxSize Fixed width for the line number.
 * @param properties Styling properties.
 */
@Composable
private fun RowScope.TextLineLoading(
    numberLabel: String,
    numberBoxSize: Dp,
    properties: LazyTextViewerProperties
) {

    LineNumberView(
        modifier = Modifier.width(width = numberBoxSize),
        itemPosition = numberLabel,
        properties = properties
    )

    Box(modifier = Modifier.weight(weight = 1F))
}

/**
 * Displays the line number with specific alignment and styling.
 *
 * @param modifier Modifier for the text layout.
 * @param itemPosition The formatted line number.
 * @param properties Styling properties.
 */
@Composable
private fun LineNumberView(
    modifier: Modifier = Modifier,
    itemPosition: String,
    properties: LazyTextViewerProperties
) {

    Text(
        modifier = modifier,
        text = itemPosition,
        textAlign = TextAlign.End,
        style = properties.numberStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * Displays the actual text content of a line.
 *
 * @param modifier Modifier for the text layout.
 * @param content The line content.
 * @param properties Styling properties, including soft-wrap configuration.
 */
@Composable
private fun LineContentView(
    modifier: Modifier = Modifier,
    content: String,
    properties: LazyTextViewerProperties
) {

    Text(
        modifier = modifier,
        text = content,
        textAlign = TextAlign.Start,
        style = properties.contentStyle,
        softWrap = properties.softWrapEnabled,
        maxLines = if (properties.softWrapEnabled) Int.MAX_VALUE else 1
    )
}