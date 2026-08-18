package io.bashpsk.emptylibs.pdfviewer.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.formatter.format.findPercentage

/**
 * A Composable that displays a loading indicator and progress text for PDF loading.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state The current loading state containing progress information.
 */
@Composable
internal fun PdfStateLoading(
    modifier: Modifier = Modifier,
    state: PdfLoadingState.Loading
) {

    val progressPercentage by remember(state) {
        derivedStateOf { findPercentage(total = state.totalPage, obtained = state.loadedPage) }
    }

    val progress by remember(progressPercentage) {
        derivedStateOf { { progressPercentage / 100.0F } }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterVertically
        )
    ) {

        LinearProgressIndicator(progress =  progress )

        Text(
            text = "Loading pages: ${progressPercentage}%",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * A Composable that displays an error message when PDF loading fails.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state The current error state containing the exception.
 */
@Composable
internal fun PdfStateError(
    modifier: Modifier = Modifier,
    state: PdfLoadingState.Error
) {

    Text(
        modifier = modifier,
        text = "Error loading PDF: \n${state.exception?.message ?: "Unknown error."}",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error
    )
}

/**
 * A Composable that displays a label for the scrollbar showing current page progress.
 *
 * @param index The index of the first visible item.
 * @param visibleItemsCount The number of currently visible items.
 * @param itemsCount The total number of items in the list.
 */
@Composable
internal fun PdfScrollBarLabel(index: Int, visibleItemsCount: Int, itemsCount: Int) {

    val barLabel by remember(index, itemsCount, visibleItemsCount) {
        derivedStateOf {
            when (visibleItemsCount) {

                0 -> "${index}/$itemsCount"
                1 -> "${index + 1}/$itemsCount"
                else -> "${index + 1}-${index + visibleItemsCount}/$itemsCount"
            }
        }
    }

    Text(
        text = barLabel,
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis
    )
}