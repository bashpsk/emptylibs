package io.bashpsk.emptylibs.pdfviewer.layout

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import io.bashpsk.emptylibs.gestureui.transform.TransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.rememberTransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.transformableGestures
import io.bashpsk.emptylibs.jetpackui.scrollbar.LazyListScrollBar
import io.bashpsk.emptylibs.pdfviewer.page.PdfPageView

/**
 * A vertical scrollable PDF viewer using [LazyColumn].
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state The [PdfViewerState] managing the PDF document and pages.
 * @param lazyListState The state object to be used to control or observe the list's state.
 * @param transformState The state for managing zoom and pan gestures.
 * @param contentPadding The padding to apply around the content.
 * @param reverseLayout Whether to reverse the direction of scrolling and layout.
 * @param horizontalAlignment The horizontal alignment for the items.
 * @param verticalArrangement The vertical arrangement for the items.
 * @param flingBehavior The fling behavior to use for scrolling.
 * @param scrollBarAlignment The alignment of the scrollbar.
 * @param colorFilter Optional [ColorFilter] to apply to the PDF pages.
 * @param onClick Callback triggered when a page is clicked.
 * @param loadingContent Composable to display while the PDF is loading.
 * @param errorContent Composable to display if an error occurs during loading.
 * @param content Additional content to overlay on the PDF viewer.
 */
@Composable
fun PdfLazyColumn(
    modifier: Modifier = Modifier,
    state: PdfViewerState,
    lazyListState: LazyListState = rememberLazyListState(),
    transformState: TransformableGesturesState = rememberTransformableGesturesState(),
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
    reverseLayout: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(space = 4.dp),
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    scrollBarAlignment: Alignment = Alignment.TopEnd,
    colorFilter: ColorFilter? = null,
    onClick: (offset: Offset) -> Unit = {},
    loadingContent: @Composable @UiComposable BoxWithConstraintsScope.(
        loadingState: PdfLoadingState.Loading
    ) -> Unit = { loadingState ->

        PdfStateLoading(modifier = Modifier.matchParentSize(), state = loadingState)
    },
    errorContent: @Composable @UiComposable BoxWithConstraintsScope.(
        loadingState: PdfLoadingState.Error
    ) -> Unit = { loadingState ->

        PdfStateError(modifier = Modifier.matchParentSize(), state = loadingState)
    },
    content: @Composable @UiComposable BoxWithConstraintsScope.() -> Unit = {},
) {

    val isScrollEnabled by remember(transformState) {
        derivedStateOf { transformState.touchCount == 1 }
    }

    val layoutOffset by remember(transformState) {
        derivedStateOf { transformState.position.round().copy(y = 0) }
    }

    BoxWithConstraints(modifier = modifier) {

        when (val loadingState = state.loadingState) {

            is PdfLoadingState.Init -> {}
            is PdfLoadingState.Loading -> loadingContent(loadingState)
            is PdfLoadingState.Error -> errorContent(loadingState)

            is PdfLoadingState.Ready -> LazyColumn(
                modifier = Modifier
                    .matchParentSize()
                    .transformableGestures(state = transformState, onClick = onClick),
                state = lazyListState,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                flingBehavior = flingBehavior,
                userScrollEnabled = isScrollEnabled
            ) {

                items(
                    items = state.pdfPages,
                    key = { page -> page.index }
                ) { page ->

                    PdfPageView(
                        modifier = Modifier.fillParentMaxWidth(),
                        page = page,
                        zoomScale = transformState.zoom,
                        layoutOffset = layoutOffset,
                        colorFilter = colorFilter
                    )
                }
            }
        }

        LazyListScrollBar(
            modifier = Modifier,
            state = lazyListState,
            orientation = Orientation.Vertical,
            alignment = scrollBarAlignment,
            label = { index, visibleItemsCount, itemsCount ->

                PdfScrollBarLabel(
                    index = index,
                    visibleItemsCount = visibleItemsCount,
                    itemsCount = itemsCount
                )
            }
        )

        content()
    }
}

/**
 * A horizontal scrollable PDF viewer using [LazyRow].
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state The [PdfViewerState] managing the PDF document and pages.
 * @param lazyListState The state object to be used to control or observe the list's state.
 * @param transformState The state for managing zoom and pan gestures.
 * @param contentPadding The padding to apply around the content.
 * @param reverseLayout Whether to reverse the direction of scrolling and layout.
 * @param horizontalArrangement The horizontal arrangement for the items.
 * @param verticalAlignment The vertical alignment for the items.
 * @param flingBehavior The fling behavior to use for scrolling.
 * @param scrollBarAlignment The alignment of the scrollbar.
 * @param colorFilter Optional [ColorFilter] to apply to the PDF pages.
 * @param onClick Callback triggered when a page is clicked.
 * @param loadingContent Composable to display while the PDF is loading.
 * @param errorContent Composable to display if an error occurs during loading.
 * @param content Additional content to overlay on the PDF viewer.
 */
@Composable
fun PdfLazyRow(
    modifier: Modifier = Modifier,
    state: PdfViewerState,
    lazyListState: LazyListState = rememberLazyListState(),
    transformState: TransformableGesturesState = rememberTransformableGesturesState(),
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
    reverseLayout: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    scrollBarAlignment: Alignment = Alignment.BottomStart,
    colorFilter: ColorFilter? = null,
    onClick: (offset: Offset) -> Unit = {},
    loadingContent: @Composable @UiComposable BoxWithConstraintsScope.(
        loadingState: PdfLoadingState.Loading
    ) -> Unit = { loadingState ->

        PdfStateLoading(modifier = Modifier.matchParentSize(), state = loadingState)
    },
    errorContent: @Composable @UiComposable BoxWithConstraintsScope.(
        loadingState: PdfLoadingState.Error
    ) -> Unit = { loadingState ->

        PdfStateError(modifier = Modifier.matchParentSize(), state = loadingState)
    },
    content: @Composable @UiComposable BoxWithConstraintsScope.() -> Unit = {},
) {

    val isScrollEnabled by remember(transformState) {
        derivedStateOf { transformState.touchCount == 1 }
    }

    val layoutOffset by remember(transformState) {
        derivedStateOf { transformState.position.round().copy(x = 0) }
    }

    BoxWithConstraints(modifier = modifier) {

        when (val loadingState = state.loadingState) {

            is PdfLoadingState.Init -> {}
            is PdfLoadingState.Loading -> loadingContent(loadingState)
            is PdfLoadingState.Error -> errorContent(loadingState)

            is PdfLoadingState.Ready -> LazyRow(
                modifier = Modifier
                    .matchParentSize()
                    .transformableGestures(state = transformState, onClick = onClick),
                state = lazyListState,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = verticalAlignment,
                flingBehavior = flingBehavior,
                userScrollEnabled = isScrollEnabled
            ) {

                items(
                    items = state.pdfPages,
                    key = { page -> page.index }
                ) { page ->

                    PdfPageView(
                        modifier = Modifier.fillParentMaxHeight(),
                        page = page,
                        zoomScale = transformState.zoom,
                        layoutOffset = layoutOffset,
                        colorFilter = colorFilter
                    )
                }
            }
        }

        LazyListScrollBar(
            modifier = Modifier,
            state = lazyListState,
            orientation = Orientation.Horizontal,
            alignment = scrollBarAlignment,
            label = { index, visibleItemsCount, itemsCount ->

                PdfScrollBarLabel(
                    index = index,
                    visibleItemsCount = visibleItemsCount,
                    itemsCount = itemsCount
                )
            }
        )

        content()
    }
}