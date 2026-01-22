package io.bashpsk.emptylibs.jetpackui.text

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp

/**
 * A composable that efficiently displays large amounts of text by loading lines lazily.
 *
 * It uses a [LazyColumn] to only render the lines currently visible on the screen.
 * Combined with [LazyTextViewerState], it can handle files with millions of lines
 * without loading the entire content into memory, thus preventing OutOfMemory errors.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state The state object that manages the text source and loading logic.
 * @param layoutState The state object for the underlying [LazyColumn].
 * @param properties The visual and behavioral properties for the viewer.
 * @param contentPadding The padding to be applied around the content.
 */
@Composable
fun LazyTextViewer(
    modifier: Modifier = Modifier,
    state: LazyTextViewerState,
    layoutState: LazyListState = rememberLazyListState(),
    properties: LazyTextViewerProperties = LazyTextViewerDefaults.properties(),
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
) {

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val horizontalScrollState = rememberScrollState()

    val firstVisibleItemIndex by remember { derivedStateOf { layoutState.firstVisibleItemIndex } }

    val numberIndex by remember(firstVisibleItemIndex, state.lineCount) {
        derivedStateOf {
            when {

                state.lineCount < 1000 -> state.lineCount
                else -> firstVisibleItemIndex.coerceIn(0 until state.lineCount)
            }
        }
    }

    val numberBoxSize by remember(numberIndex, properties) {
        derivedStateOf {
            with(density) {
                textMeasurer.measure(
                    density = density,
                    text = state.getFormattedLineNumber(index = state.lineCount),
                    style = properties.numberStyle,
                    maxLines = 1
                ).size.width.toDp()
            }
        }
    }

    val horizontalScrollModifier = when (properties.softWrapEnabled) {

        true -> Modifier
        false -> Modifier.horizontalScroll(horizontalScrollState)
    }

    LazyColumn(
        modifier = modifier
            .clipToBounds()
            .then(horizontalScrollModifier),
        state = layoutState,
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(space = properties.lineSpace)
    ) {

        items(
            count = state.lineCount,
            key = { itemPosition -> "Line: $itemPosition unnecessary" }
        ) { itemPosition ->

            TextContentResultView(
                modifier = Modifier,
                itemPosition = itemPosition,
                state = state,
                numberBoxSize = numberBoxSize,
                properties = properties,
                horizontalScrollState = horizontalScrollState
            )
        }
    }
}