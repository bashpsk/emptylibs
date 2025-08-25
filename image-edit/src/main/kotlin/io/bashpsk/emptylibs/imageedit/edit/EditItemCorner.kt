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
}