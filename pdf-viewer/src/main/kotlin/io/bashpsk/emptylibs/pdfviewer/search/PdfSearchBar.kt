package io.bashpsk.emptylibs.pdfviewer.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.pdfviewer.pdf.PdfLazyColumnState

/**
 * A composable search bar component designed for use with a PDF viewer.
 *
 * This component utilizes a [DockedSearchBar] to provide an interface for text search operations
 * within a PDF document. It integrates directly with [PdfLazyColumnState] to manage the search
 * query, expansion state, and execution of search actions.
 *
 * @param modifier The [Modifier] to be applied to the search bar.
 * @param state The [PdfLazyColumnState] that holds the search logic, including the current query,
 * expansion status, and search handlers.
 * @param shapes The [Shape] of the search bar container.
 * @param placeholder A composable lambda providing the placeholder content displayed when the
 * query is empty.
 * @param leadingContent A composable lambda providing the icon or content at the start of the
 * search bar.
 * @param trailingContent A composable lambda providing the icon or content at the end of the search
 * bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PdfSearchBar(
    modifier: Modifier = Modifier,
    state: PdfLazyColumnState,
    shapes: Shape = MaterialTheme.shapes.extraSmall,
    placeholder: @Composable () -> Unit = {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier.weight(weight = 1.0F),
                text = "Search Here",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search"
            )
        }
    },
    leadingContent: @Composable () -> Unit = {},
    trailingContent: @Composable () -> Unit = {}
) {

    DockedSearchBar(
        modifier = modifier,
        inputField = {

            SearchBarDefaults.InputField(
                modifier = Modifier.fillMaxWidth(),
                query = state.searchQuery,
                onQueryChange = state::onSearchQueryChange,
                onSearch = state::onTextSearch,
                expanded = state.isSearchExpanded,
                onExpandedChange = state::onSearchExpandedChange,
                placeholder = placeholder,
                leadingIcon = leadingContent,
                trailingIcon = trailingContent
            )
        },
        shape = shapes,
        expanded = false,
        onExpandedChange = {}
    ) {
    }
}