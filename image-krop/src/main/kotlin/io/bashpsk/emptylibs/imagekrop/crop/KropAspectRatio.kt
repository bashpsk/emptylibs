package io.bashpsk.emptylibs.imagekrop.crop

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Represents predefined aspect ratios for image cropping.
 *
 * Each enum constant defines a specific width to height ratio.
 *
 * @property label The width component of the aspect ratio label.
 * @property ratio The calculated float value of the aspect ratio (width / height). This can be null
 * if not explicitly defined.
 */
enum class KropAspectRatio(val label: String, val ratio: Float? = null) {

    /**
     * Represents a 1:1 aspect ratio (Square).
     */
    Ratio1to1(label = "1:1", ratio = 1F / 1F),

    /**
     * Represents a 1:2 aspect ratio (Portrait).
     */
    Ratio1to2(label = "1:2", ratio = 1F / 2F),

    /**
     * Represents a 2:1 aspect ratio (Landscape).
     */
    Ratio2to1(label = "2:1", ratio = 2F / 1F),

    /**
     * Represents a 2:3 aspect ratio (Portrait).
     */
    Ratio2to3(label = "2:3", ratio = 2F / 3F),

    /**
     * Represents a 3:2 aspect ratio (Landscape).
     */
    Ratio3to2(label = "3:2", ratio = 3F / 2F),

    /**
     * Represents a 3:4 aspect ratio (Portrait).
     */
    Ratio3to4(label = "3:4", ratio = 3F / 4F),

    /**
     * Represents a 4:3 aspect ratio (Landscape).
     */
    Ratio4to3(label = "4:3", ratio = 4F / 3F),

    /**
     * Represents a 4:5 aspect ratio (Portrait).
     */
    Ratio4to5(label = "4:5", ratio = 4F / 5F),

    /**
     * Represents a 5:4 aspect ratio (Landscape).
     */
    Ratio5to4(label = "5:4", ratio = 5F / 4F),

    /**
     * Represents a 5:7 aspect ratio (Portrait).
     */
    Ratio5to7(label = "5:7", ratio = 5F / 7F),

    /**
     * Represents a 7:5 aspect ratio (Landscape).
     */
    Ratio7to5(label = "7:5", ratio = 7F / 5F),

    /**
     * Represents a 9:16 aspect ratio (Portrait).
     */
    Ratio9to16(label = "9:16", ratio = 9F / 16F),

    /**
     * Represents a 9:20 aspect ratio (Portrait).
     */
    Ratio9to20(label = "9:20", ratio = 9F / 20F),

    /**
     * Represents a 9:21 aspect ratio (Portrait).
     */
    Ratio9to21(label = "9:21", ratio = 9F / 21F),

    /**
     * Represents a 16:9 aspect ratio (Landscape).
     */
    Ratio16to9(label = "16:9", ratio = 16F / 9F),

    /**
     * Represents a 20:9 aspect ratio (Landscape).
     */
    Ratio20to9(label = "20:9", ratio = 20F / 9F),

    /**
     * Represents a 21:9 aspect ratio (Landscape).
     */
    Ratio21to9(label = "21:9", ratio = 21F / 9F);

    companion object {

        /**
         * A predefined list of commonly used aspect ratios.
         *
         * This list includes:
         * - [Ratio1to1]
         * - [Ratio3to4]
         * - [Ratio4to3]
         * - [Ratio9to16]
         * - [Ratio9to20]
         * - [Ratio16to9]
         * - [Ratio20to9]
         */
        val Basic = persistentListOf(
            Ratio1to1,
            Ratio3to4,
            Ratio4to3,
            Ratio9to16,
            Ratio9to20,
            Ratio16to9,
            Ratio20to9,
        ).toImmutableList()

        /**
         * An immutable list containing all predefined [KropAspectRatio] values.
         * This list provides access to every available aspect ratio defined in this enum.
         */
        val Advanced = entries.toImmutableList()
    }
}