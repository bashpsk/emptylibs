package io.bashpsk.emptylibs.gestureui.video

/**
 * Represents the change in value of a gesture.
 */
enum class ValueChange {

    /**
     * Represents a Value Unknown or indeterminate change in value.
     * This is typically the initial state or a state where the change cannot be determined.
     */
    Unknown,

    /**
    * Represents a Value Increased.
    */
    Increased,

    /**
     * Represents a Value Decreased.
     */
    Decreased;
}