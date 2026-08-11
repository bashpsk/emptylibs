package io.bashpsk.emptylibs.jetpackui.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import io.bashpsk.emptylibs.layouts.sticky.StickyRowLayout
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
    properties: LazyTextViewerProperties,
    horizontalScrollOffset: Int
) {

    var contentResult by retain { mutableStateOf<TextContentResult>(TextContentResult.Init) }

    val numberLabel by remember(itemPosition) {
        derivedStateOf { state.getFormattedLineNumber(index = itemPosition) }
    }

    val firstVisibleModifier = Modifier.onVisibilityChanged(minFractionVisible = 0.1F) { visible ->

        if (visible) state.apply {

            coroutineScope.launch { contentResult = readLineContent(index = itemPosition) }
        }
    }

    StickyRowLayout(
        modifier = modifier
            .fillMaxWidth()
            .then(firstVisibleModifier),
        horizontalScroll = horizontalScrollOffset,
        horizontalArrangement = Arrangement.spacedBy(space = properties.numberSpace),
        verticalAlignment = Alignment.Top
    ) {

        LineNumberView(
            modifier = Modifier.width(width = numberBoxSize),
            itemPosition = numberLabel,
            properties = properties
        )

        when (contentResult) {

            is TextContentResult.Init -> {}

            is TextContentResult.Content -> LineContentView(
                modifier = Modifier.wrapContentWidth(),
                content = (contentResult as TextContentResult.Content).text,
                properties = properties
            )

            is TextContentResult.Error -> {}
        }
    }
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