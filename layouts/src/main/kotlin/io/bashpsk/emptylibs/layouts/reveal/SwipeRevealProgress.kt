package io.bashpsk.emptylibs.layouts.reveal

/**
 * Represents the progress states of a [SwipeRevealItem].
 */
enum class SwipeRevealProgress {

    /**
     * The state where the content is not revealed.
     */
    Hidden,

    /**
     * The state where the left content is revealed.
     */
    LeftRevealed,

    /**
     * The state where the right content is revealed.
     */
    RightRevealed;
}