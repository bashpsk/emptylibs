package io.bashpsk.emptylibs.imagekrop.crop

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import io.bashpsk.emptylibs.imagekrop.crop.KropAspectRatio.Companion.Ratio16to9
import io.bashpsk.emptylibs.imagekrop.crop.KropAspectRatio.Companion.Ratio1to1
import io.bashpsk.emptylibs.imagekrop.crop.KropAspectRatio.Companion.Ratio20to9
import io.bashpsk.emptylibs.imagekrop.crop.KropAspectRatio.Companion.Ratio3to4
import io.bashpsk.emptylibs.imagekrop.crop.KropAspectRatio.Companion.Ratio4to3
import io.bashpsk.emptylibs.imagekrop.crop.KropAspectRatio.Companion.Ratio9to16
import io.bashpsk.emptylibs.imagekrop.crop.KropAspectRatio.Companion.Ratio9to20
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Represents predefined aspect ratios for image cropping.
 *
 * Each enum constant defines a specific width to height ratio.
 *
 * @property label The width component of the aspect ratio label.
 * @property ratio The calculated float value of the aspect ratio (width / height). This can be null
 * if not explicitly defined.
 */
@Immutable
@Parcelize
@Serializable
data class KropAspectRatio(val label: String = "None", val ratio: Float? = null) : Parcelable {

    companion object {

        /**
         * Represents a 1:1 aspect ratio (Square).
         */
        val Ratio1to1 = KropAspectRatio(label = "1:1", ratio = 1F / 1F)

        /**
         * Represents a 1:2 aspect ratio (Portrait).
         */
        val Ratio1to2 = KropAspectRatio(label = "1:2", ratio = 1F / 2F)

        /**
         * Represents a 2:1 aspect ratio (Landscape).
         */
        val Ratio2to1 = KropAspectRatio(label = "2:1", ratio = 2F / 1F)

        /**
         * Represents a 2:3 aspect ratio (Portrait).
         */
        val Ratio2to3 = KropAspectRatio(label = "2:3", ratio = 2F / 3F)

        /**
         * Represents a 3:2 aspect ratio (Landscape).
         */
        val Ratio3to2 = KropAspectRatio(label = "3:2", ratio = 3F / 2F)

        /**
         * Represents a 3:4 aspect ratio (Portrait).
         */
        val Ratio3to4 = KropAspectRatio(label = "3:4", ratio = 3F / 4F)

        /**
         * Represents a 4:3 aspect ratio (Landscape).
         */
        val Ratio4to3 = KropAspectRatio(label = "4:3", ratio = 4F / 3F)

        /**
         * Represents a 4:5 aspect ratio (Portrait).
         */
        val Ratio4to5 = KropAspectRatio(label = "4:5", ratio = 4F / 5F)

        /**
         * Represents a 5:4 aspect ratio (Landscape).
         */
        val Ratio5to4 = KropAspectRatio(label = "5:4", ratio = 5F / 4F)

        /**
         * Represents a 5:7 aspect ratio (Portrait).
         */
        val Ratio5to7 = KropAspectRatio(label = "5:7", ratio = 5F / 7F)

        /**
         * Represents a 7:5 aspect ratio (Landscape).
         */
        val Ratio7to5 = KropAspectRatio(label = "7:5", ratio = 7F / 5F)

        /**
         * Represents a 9:16 aspect ratio (Portrait).
         */
        val Ratio9to16 = KropAspectRatio(label = "9:16", ratio = 9F / 16F)

        /**
         * Represents a 9:20 aspect ratio (Portrait).
         */
        val Ratio9to20 = KropAspectRatio(label = "9:20", ratio = 9F / 20F)

        /**
         * Represents a 9:21 aspect ratio (Portrait).
         */
        val Ratio9to21 = KropAspectRatio(label = "9:21", ratio = 9F / 21F)

        /**
         * Represents a 16:9 aspect ratio (Landscape).
         */
        val Ratio16to9 = KropAspectRatio(label = "16:9", ratio = 16F / 9F)

        /**
         * Represents a 20:9 aspect ratio (Landscape).
         */
        val Ratio20to9 = KropAspectRatio(label = "20:9", ratio = 20F / 9F)

        /**
         * Represents a 21:9 aspect ratio (Landscape).
         */
        val Ratio21to9 = KropAspectRatio(label = "21:9", ratio = 21F / 9F)

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
        val Advanced = persistentListOf(
            Ratio1to1,
            Ratio1to2,
            Ratio2to1,
            Ratio2to3,
            Ratio3to2,
            Ratio3to4,
            Ratio4to3,
            Ratio4to5,
            Ratio5to4,
            Ratio5to7,
            Ratio7to5,
            Ratio9to16,
            Ratio9to20,
            Ratio9to21,
            Ratio16to9,
            Ratio20to9,
            Ratio21to9,
        ).toImmutableList()
    }
}