package io.bashpsk.emptylibs.imageedit.edit

/**
 * Enum class representing the different corners and center points of a item selection.
 * These are used to determine which part of the selection is being manipulated.
 */
internal enum class EditItemCorner {

    /**
     * Represents the top left corner of the item view.
     */
    TOP_LEFT,

    /**
     * Represents the top right corner of the item view.
     */
    TOP_RIGHT,

    /**
     * Represents the bottom left corner of the item view.
     */
    BOTTOM_LEFT,

    /**
     * Represent bottom right corner of the item view.
     */
    BOTTOM_RIGHT,

    /**
     * Represent top centre of the item view.
     */
    TOP_CENTRE,

    /**
     * Represent left centre of the item view.
     */
    LEFT_CENTRE,

    /**
     * Represent right centre of the item view.
     */
    RIGHT_CENTRE,

    /**
     * Represent bottom centre of the item view.
     */
    BOTTOM_CENTRE;

    companion object {

        /**
         * Checks if the [EditItemCorner] represents one of the four corners
         * (top-left, top-right, bottom-left, bottom-right).
         *
         * @return `true` if the [EditItemCorner] is a corner edge, `false` otherwise.
         * Returns `false` if the receiver is `null`.
         */
        fun EditItemCorner?.hasCornerEdge(): Boolean {

            return this == TOP_LEFT || this == TOP_RIGHT || this == BOTTOM_LEFT
                    || this == BOTTOM_RIGHT
        }

        /**
         * Checks if the given [EditItemCorner] represents a center point of the item selection.
         *
         * @return `true` if the corner is [TOP_CENTRE], [LEFT_CENTRE], [RIGHT_CENTRE],
         * or [BOTTOM_CENTRE], `false` otherwise.
         */
        fun EditItemCorner?.hasCornerCenter(): Boolean {

            return this == TOP_CENTRE || this == LEFT_CENTRE || this == RIGHT_CENTRE
                    || this == BOTTOM_CENTRE
        }
    }
}