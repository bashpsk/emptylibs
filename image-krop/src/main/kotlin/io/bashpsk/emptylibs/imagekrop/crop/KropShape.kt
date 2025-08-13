package io.bashpsk.emptylibs.imagekrop.crop

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Represents the different shapes that can be used for cropping an image.
 *
 * Each enum constant defines a specific geometric shape.
 */
enum class KropShape {

    /**
     * Represents a star shape.
     */
    Star,

    /**
     * Represents a circular shape.
     */
    Circle,

    /**
     * Represents a cut corners, typically a rectangle with its corners diagonally sliced off.
     */
    CutCorner,

    /**
     * Represents a triangle shape.
     */
    Triangle,

    /**
     * Represents a pentagon shape.
     */
    Pentagon,

    /**
     * Represents a hexagon shape for cropping.
     */
    Hexagon,

    /**
     * Represents a regular polygon with seven sides.
     */
    Heptagon,

    /**
     * Represents a regular polygon with eight sides.
     */
    Octagon,

    /**
     * Represents a regular polygon with nine sides.
     */
    Nonagon,

    /**
     * Represents a regular polygon with ten sides.
     */
    Decagon,

    /**
     * Represents a sharp corners. Its Default Shape.
     */
    SharpeCorner,

    /**
     * Represents a rounded corners.
     */
    RoundedCorner;

    companion object {

        /**
         * A predefined list of basic shapes, suitable for common cropping scenarios.
         * This list includes: [Star], [Circle], [CutCorner], [SharpeCorner], [Triangle],
         * [Pentagon], [Hexagon], and [RoundedCorner].
         */
        val Basic = persistentListOf(
            Star,
            Circle,
            CutCorner,
            SharpeCorner,
            Triangle,
            Pentagon,
            Hexagon,
            RoundedCorner,
        ).toImmutableList()

        /**
         * An immutable list containing all the `KropShape` enum entries.
         * This list provides a complete collection of all available cropping shapes.
         */
        val Advanced = entries.toImmutableList()
    }
}