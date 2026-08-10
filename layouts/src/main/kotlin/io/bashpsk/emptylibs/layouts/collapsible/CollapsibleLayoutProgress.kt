package io.bashpsk.emptylibs.layouts.collapsible

/**
 * Represents the progress states of a collapsible layout.
 */
enum class CollapsibleLayoutProgress {

    /**
     * The layout is in its collapsed state.
     */
    Collapsed,

    /**
     * The layout is in its expanded state.
     */
    Expanded,

    /**
     * The layout is hidden and not occupying space.
     */
    Dismissed;
}