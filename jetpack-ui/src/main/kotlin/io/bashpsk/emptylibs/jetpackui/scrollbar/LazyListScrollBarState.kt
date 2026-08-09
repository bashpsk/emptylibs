package io.bashpsk.emptylibs.jetpackui.scrollbar

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.retain.retain

/**
 * Creates and remembers a [LazyListScrollBarState] for the given [state].
 *
 * @param state The [LazyListState] to attach the scrollbar state to.
 */
@PublishedApi
@Composable
internal fun rememberLazyListScrollBarState(state: LazyListState): LazyListScrollBarState {

    return retain(state) { LazyListScrollBarState(state) }
}

/**
 * An implementation of [LazyScrollBarState] designed for use with [LazyListState].
 *
 * This class calculates the scrollbar position and handles scroll events for a [LazyColumn]
 * or [LazyRow]. It maps the list's items and layout information to a normalized scroll ratio
 * between 0.0 and 1.0.
 *
 * @property state The underlying [LazyListState] used to track the scroll position of the list.
 */
@PublishedApi
@Stable
internal class LazyListScrollBarState(
    private val state: LazyListState
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

            val firstVisibleItem = visibleItemsInfo.first()
            val totalItemsSize = state.layoutInfo.totalItemsCount * firstVisibleItem.size
            val viewportSize = state.layoutInfo.viewportEndOffset - state.layoutInfo
                .viewportStartOffset
            val scrollableDistance = (totalItemsSize - viewportSize).coerceAtLeast(0)

            return if (scrollableDistance > 0) {
                ((firstVisibleItem.index * firstVisibleItem.size + state
                    .firstVisibleItemScrollOffset).toFloat() / scrollableDistance)
                    .coerceIn(0f..1f)
            } else 0f
        }

    override suspend fun scrollToRatio(ratio: Float) {

        val itemSize = state.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 1
        val totalItemsSize = state.layoutInfo.totalItemsCount * itemSize
        val viewportSize = state.layoutInfo.viewportEndOffset - state.layoutInfo.viewportStartOffset
        val scrollableDistance = (totalItemsSize - viewportSize).coerceAtLeast(0)

        val scrollPosition = scrollableDistance * ratio.coerceIn(0f..1f)
        val targetItem = (scrollPosition / itemSize).toInt()
            .coerceIn(0 until state.layoutInfo.totalItemsCount.coerceAtLeast(1))
        val targetOffset = (scrollPosition % itemSize).toInt()

        state.scrollToItem(index = targetItem, scrollOffset = targetOffset)
    }
}