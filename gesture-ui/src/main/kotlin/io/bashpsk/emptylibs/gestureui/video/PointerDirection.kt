package io.bashpsk.emptylibs.gestureui.video

/**
 * Represents the direction of a pointer gesture.
 *
 * This enum is used to categorize pointer movements into horizontal and vertical directions.
 * It also includes an `Unknown` state for gestures that don't fit into these categories.
 *
 * The class provides helper functions `hasHorizontal()` and `hasVertical()`
 * to easily check if a direction is primarily horizontal or vertical.
 */
internal enum class PointerDirection {

    /**
     * Represents a horizontal swipe from top side.
     */
    HorizontalTop,

    /**
     * Represents a horizontal swipe from bottom side.
     */
    HorizontalBottom,

    /**
     * Represents a vertical swipe from left side.
     */
    VerticalLeft,

    /**
     * Represents a vertical swipe from right side.
     */
    VerticalRight,

    /**
     * Represents an unknown or undefined pointer direction.
     */
    Unknown;

    /**
     * Checks if the pointer direction is horizontal (either top or bottom).
     *
     * @return `true` if the direction is horizontal, `false` otherwise.
     */
    fun hasHorizontal(): Boolean {

        return hasHorizontalTop() || hasHorizontalBottom()
    }

    /**
     * Checks if the pointer direction is vertical (either left or right).
     *
     * @return `true` if the pointer direction is [VerticalLeft] or [VerticalRight],
     * `false` otherwise.
     */
    fun hasVertical(): Boolean {

        return hasVerticalLeft() || hasVerticalRight()
    }

    fun hasHorizontalTop(): Boolean {

        return this == HorizontalTop
    }

    fun hasHorizontalBottom(): Boolean {

        return this == HorizontalBottom
    }

    fun hasVerticalLeft(): Boolean {

        return this == VerticalLeft
    }

    fun hasVerticalRight(): Boolean {

        return this == VerticalRight
    }
}