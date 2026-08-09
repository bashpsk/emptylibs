package io.bashpsk.emptylibs.jetpackui.scrollbar

/**
 * Interface defining the state required for a generic lazy scrollbar.
 * This abstracts the differences between different types of lazy layouts (List, Grid, etc.)
 */
@PublishedApi
internal interface LazyScrollBarState {

    /**
     * The index of the first currently visible item.
     */
    val firstVisibleItemIndex: Int

    /**
     * The total number of items in the lazy layout.
     */
    val totalItemsCount: Int

    /**
     * The number of items currently visible in the viewport.
     */
    val visibleItemsCount: Int

    /**
     * Whether the lazy layout is currently being scrolled.
     */
    val isScrollInProgress: Boolean

    /**
     * A value between 0.0 and 1.0 representing the current scroll position relative to the
     * total scrollable distance.
     */
    val scrollRatio: Float

    /**
     * Scrolls the lazy layout to a specific position defined by a [ratio] between 0.0 and 1.0.
     *
     * @param ratio The target scroll position ratio (0.0 for start, 1.0 for end).
     */
    suspend fun scrollToRatio(ratio: Float)
}