package io.bashpsk.emptylibs.pdfviewer.pdf

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.gestureui.transform.transformableGestures
import io.bashpsk.emptylibs.jetpackui.scrollbar.LazyListScrollBar
import kotlinx.collections.immutable.toImmutableList

/**
 * A lazy column that displays PDF pages.
 *
 * @param modifier The modifier to apply to this layout.
 * @param state The state of the PDF viewer.
 * @param pageSpace The space between pages.
 * @param scrollBarAlignment The alignment of the scrollbar.
 * @param onClick A callback that is invoked when the user clicks on the PDF.
 * @param content A slot for composable content to be displayed on top of the PDF.
 */
@Composable
fun PdfLazyColumn(
    modifier: Modifier = Modifier,
    state: PdfLazyColumnState,
    pageSpace: Dp = 4.dp,
    scrollBarAlignment: Alignment = Alignment.TopEnd,
    colorFilter: ColorFilter? = null,
    placeholder: Color = MaterialTheme.colorScheme.surface,
    onClick: (offset: Offset) -> Unit = {},
    content: @Composable @UiComposable BoxWithConstraintsScope.() -> Unit = {},
) {

    val pdfLazyListState = rememberLazyListState()

    val pageDataList by remember(state.pageDataList) {
        derivedStateOf { state.pageDataList.values.toImmutableList() }
    }

    val pageCount by remember(pageDataList) { derivedStateOf { pageDataList.size } }
    val isScrolling by remember { derivedStateOf { pdfLazyListState.isScrollInProgress } }
    val visibleItemsCount by remember {
        derivedStateOf { pdfLazyListState.layoutInfo.visibleItemsInfo.size }
    }

    val isScrollEnabled by remember(state.transformableState) {
        derivedStateOf { state.transformableState.touchCount == 1 }
    }

    BoxWithConstraints(
        modifier = modifier.transformableGestures(
            state = state.transformableState,
            onClick = onClick,
            onLongClick = {}
        ),
        contentAlignment = Alignment.Center
    ) {

        LaunchedEffect(constraints.maxWidth, constraints.maxHeight) {

            state.apply {

                containerWidth = constraints.maxWidth
                containerHeight = constraints.maxHeight
            }
        }

        LaunchedEffect(constraints.maxWidth, pageCount) {

            for (page in 0 until pageCount) {

                state.setRenderLowQuality(pageIndex = page)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = pdfLazyListState,
            userScrollEnabled = isScrollEnabled,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(pageSpace),
        ) {

            items(
                items = pageDataList,
                key = { pageData -> pageData.page }
            ) { pageData ->

                PdfPageView(
                    modifier = Modifier,
                    state = state,
                    pageData = pageData,
                    isScrolling = isScrolling,
                    placeholder = placeholder,
                    colorFilter = colorFilter
                )
            }
        }

        LazyListScrollBar(
            modifier = Modifier,
            state = pdfLazyListState,
            orientation = Orientation.Vertical,
            alignment = scrollBarAlignment,
            label = { index ->

                val barLabel by remember(index, pageCount, visibleItemsCount) {
                    derivedStateOf {
                        when (visibleItemsCount) {

                            0, 1 -> "${index + 1}/$pageCount"
                            else -> "${index + 1}-${index + visibleItemsCount}/$pageCount"
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
        )

        content()
    }
}