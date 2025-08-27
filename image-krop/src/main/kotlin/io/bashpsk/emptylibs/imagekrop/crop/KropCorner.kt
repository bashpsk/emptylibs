package io.bashpsk.emptylibs.imagekrop.crop

/**
 * Enum class representing the different corners and center points of a crop selection.
 * These are used to determine which part of the selection is being manipulated.
 */
internal enum class KropCorner {

    /**
     * Represents the top left corner of the crop view.
     */
    TOP_LEFT,

    /**
     * Represents the top right corner of the crop view.
     */
    TOP_RIGHT,

    /**
     * Represents the bottom left corner of the crop view.
     */
    BOTTOM_LEFT,

    /**
     * Represent bottom right corner of the crop view.
     */
    BOTTOM_RIGHT,

    /**
     * Represent top centre of the crop view.
     */
    TOP_CENTRE,

    /**
     * Represent left centre of the crop view.
     */
    LEFT_CENTRE,

    /**
     * Represent right centre of the crop view.
     */
    RIGHT_CENTRE,

    /**
     * Represent bottom centre of the crop view.
     */
    BOTTOM_CENTRE;

    companion object {

        fun KropCorner?.hasCornerEdge(): Boolean {

            return this == TOP_LEFT || this == TOP_RIGHT || this == BOTTOM_LEFT
                    || this == BOTTOM_RIGHT
        }

        fun KropCorner?.hasCornerCenter(): Boolean {

            return this == TOP_CENTRE || this == LEFT_CENTRE || this == RIGHT_CENTRE
                    || this == BOTTOM_CENTRE
        }
    }
}