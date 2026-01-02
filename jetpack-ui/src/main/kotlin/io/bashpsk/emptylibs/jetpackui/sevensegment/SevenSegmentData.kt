package io.bashpsk.emptylibs.jetpackui.sevensegment

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Represents the data for a 7-segment display, including the active and inactive elements.
 *
 * @property activeElements The list of active elements.
 * @property inactiveElements The list of inactive elements.
 */
@Immutable
data class SevenSegmentData(
    val activeElements: ImmutableList<SevenSegmentElement> = persistentListOf(),
    val inactiveElements: ImmutableList<SevenSegmentElement> = persistentListOf()
) {

    companion object {

        /**
         * 7-segment data for the digit '0'.
         */
        val Zero = SevenSegmentData(
            activeElements = SevenSegmentElement.ZeroActiveElements,
            inactiveElements = SevenSegmentElement.ZeroInactiveElements
        )

        /**
         * 7-segment data for the digit '1'.
         */
        val One = SevenSegmentData(
            activeElements = SevenSegmentElement.OneActiveElements,
            inactiveElements = SevenSegmentElement.OneInactiveElements
        )

        /**
         * 7-segment data for the digit '2'.
         */
        val Two = SevenSegmentData(
            activeElements = SevenSegmentElement.TwoActiveElements,
            inactiveElements = SevenSegmentElement.TwoInactiveElements
        )

        /**
         * 7-segment data for the digit '3'.
         */
        val Three = SevenSegmentData(
            activeElements = SevenSegmentElement.ThreeActiveElements,
            inactiveElements = SevenSegmentElement.ThreeInactiveElements
        )

        /**
         * 7-segment data for the digit '4'.
         */
        val Four = SevenSegmentData(
            activeElements = SevenSegmentElement.FourActiveElements,
            inactiveElements = SevenSegmentElement.FourInactiveElements
        )

        /**
         * 7-segment data for the digit '5'.
         */
        val Five = SevenSegmentData(
            activeElements = SevenSegmentElement.FiveActiveElements,
            inactiveElements = SevenSegmentElement.FiveInactiveElements
        )

        /**
         * 7-segment data for the digit '6'.
         */
        val Six = SevenSegmentData(
            activeElements = SevenSegmentElement.SixActiveElements,
            inactiveElements = SevenSegmentElement.SixInactiveElements
        )

        /**
         * 7-segment data for the digit '7'.
         */
        val Seven = SevenSegmentData(
            activeElements = SevenSegmentElement.SevenActiveElements,
            inactiveElements = SevenSegmentElement.SevenInactiveElements
        )

        /**
         * 7-segment data for the digit '8'.
         */
        val Eight = SevenSegmentData(
            activeElements = SevenSegmentElement.EightActiveElements,
            inactiveElements = SevenSegmentElement.EightInactiveElements
        )

        /**
         * 7-segment data for the digit '9'.
         */
        val Nine = SevenSegmentData(
            activeElements = SevenSegmentElement.NineActiveElements,
            inactiveElements = SevenSegmentElement.NineInactiveElements
        )

        /**
         * Empty 7-segment data.
         */
        val Empty = SevenSegmentData(
            activeElements = persistentListOf(),
            inactiveElements = SevenSegmentElement.entries.toImmutableList()
        )

        /**
         * A map of characters to their corresponding 7-segment data for numbers.
         */
        val NumberSegmentList = persistentMapOf<Char, SevenSegmentData>(
            ' ' to Empty,
            '0' to Zero,
            '1' to One,
            '2' to Two,
            '3' to Three,
            '4' to Four,
            '5' to Five,
            '6' to Six,
            '7' to Seven,
            '8' to Eight,
            '9' to Nine
        )
    }
}