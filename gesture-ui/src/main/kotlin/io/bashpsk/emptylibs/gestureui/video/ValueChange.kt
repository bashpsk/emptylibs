package io.bashpsk.emptylibs.gestureui.video

/**
 * Represents the change in value of a gesture.
 */
enum class ValueChange {

    /**
     * Represents an Value Unknown or indeterminate change in value.
     * This is typically the initial state or a state where the change cannot be determined.
     */
    Unknown,

    /**
    * Represents an Value Increased.
    */
    Increased,

    /**
     * Represents an Value Decreased.
     */
    Decreased;
}