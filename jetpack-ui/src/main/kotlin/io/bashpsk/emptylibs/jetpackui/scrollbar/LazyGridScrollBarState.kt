package io.bashpsk.emptylibs.jetpackui.scrollbar

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.retain.retain

/**
 * Creates and remembers a [LazyGridScrollBarState] for the given [state].
 *
 * @param state The [LazyGridState] to attach the scrollbar state to.
 */
@PublishedApi
@Composable
internal fun rememberLazyGridScrollBarState(state: LazyGridState): LazyGridScrollBarState {

    return retain(state) { LazyGridScrollBarState(state = state) }
}

/**
 * An implementation of [LazyScrollBarState] designed for use with [LazyGridState].
 *
 * This class calculates the scrollbar position and handles scroll events for a [LazyVerticalGrid]
 * or [LazyHorizontalGrid]. It maps the grid's items and layout information, accounting for multiple
 * items per line (rows or columns), to a normalized scroll ratio between 0.0 and 1.0.
 *
 * @property state The underlying [LazyGridState] used to track the scroll position of the grid.
 */
@PublishedApi
@Stable
internal class LazyGridScrollBarState(
    private val state: LazyGridState
) : LazyScrollBarState {

    override val firstVisibleItemIndex: Int
        get() = state.firstVisibleItemIndex

    override val totalItemsCount: Int
        get() = state.layoutInfo.totalItemsCount

    override val visibleItemsCount: Int
        get() = state.layoutInfo.visibleItemsInfo.size

    override val isScrollInProgress: Boolean
        get() = state.isScrollInProgress

    override val scrollRatio: Float
        get() {

            val visibleItemsInfo = state.layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty() || state.layoutInfo.totalItemsCount == 0) return 0f

            val orientation = state.layoutInfo.orientation
            val itemsPerLine = when (orientation) {
                Orientation.Vertical -> visibleItemsInfo.maxOf { info -> info.column } + 1
                Orientation.Horizontal -> visibleItemsInfo.maxOf { info -> info.row } + 1
            }

            val lineSize = when (orientation) {
                Orientation.Vertical -> visibleItemsInfo.maxOf { info -> info.size.height }
                Orientation.Horizontal -> visibleItemsInfo.maxOf { info -> info.size.width }
            }

            val firstVisibleItem = visibleItemsInfo.first()
            val firstVisibleLine = when (orientation) {
                Orientation.Vertical -> firstVisibleItem.row
                Orientation.Horizontal -> firstVisibleItem.column
            }

            val totalLines = (state.layoutInfo.totalItemsCount + itemsPerLine - 1) / itemsPerLine
            val totalLinesSize = totalLines * lineSize
            val viewportSize = state.layoutInfo.viewportEndOffset - state.layoutInfo
                .viewportStartOffset
            val scrollableDistance = (totalLinesSize - viewportSize).coerceAtLeast(0)

            return if (scrollableDistance > 0) {
                ((firstVisibleLine * lineSize + state.firstVisibleItemScrollOffset).toFloat()
                        / scrollableDistance).coerceIn(0f..1f)
            } else 0f
        }

    override suspend fun scrollToRatio(ratio: Float) {

        val visibleItemsInfo = state.layoutInfo.visibleItemsInfo
        if (visibleItemsInfo.isEmpty() || state.layoutInfo.totalItemsCount == 0) return

        val itemsPerLine = when (state.layoutInfo.orientation) {

            Orientation.Vertical -> visibleItemsInfo.maxOf { info -> info.column } + 1
            Orientation.Horizontal -> visibleItemsInfo.maxOf { info -> info.row } + 1
        }

        val lineSize = when (state.layoutInfo.orientation) {

            Orientation.Vertical -> visibleItemsInfo.maxOf { info -> info.size.height }
            Orientation.Horizontal -> visibleItemsInfo.maxOf { info -> info.size.width }
        }

        val totalLines = (state.layoutInfo.totalItemsCount + itemsPerLine - 1) / itemsPerLine
        val viewportSize = state.layoutInfo.viewportEndOffset - state.layoutInfo.viewportStartOffset
        val scrollableDistance = (totalLines * lineSize - viewportSize).coerceAtLeast(0)

        if (scrollableDistance > 0) {

            val targetScroll = ratio.coerceIn(0f..1f) * scrollableDistance
            val targetLine = (targetScroll / lineSize).toInt()
            val targetItem = (targetLine * itemsPerLine)
                .coerceIn(0 until state.layoutInfo.totalItemsCount)
            val targetOffset = (targetScroll % lineSize).toInt()

            state.scrollToItem(index = targetItem, scrollOffset = targetOffset)
        }
    }
}