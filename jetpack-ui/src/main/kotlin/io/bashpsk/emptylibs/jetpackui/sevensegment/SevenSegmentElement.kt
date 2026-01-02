package io.bashpsk.emptylibs.jetpackui.sevensegment

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Represents the seven segments of a 7-segment display.
 */
enum class SevenSegmentElement {

    /**
     * The top segment.
     */
    _1,

    /**
     * The top-right segment.
     */
    _2,

    /**
     * The bottom-right segment.
     */
    _3,

    /**
     * The bottom segment.
     */
    _4,

    /**
     * The bottom-left segment.
     */
    _5,

    /**
     * The top-left segment.
     */
    _6,

    /**
     * The middle segment.
     */
    _7;

    companion object {

        internal val ZeroActiveElements = persistentListOf(_1, _2, _3, _4, _5, _6)
        internal val ZeroInactiveElements = persistentListOf(_7)

        internal val OneActiveElements = persistentListOf(_2, _3)
        internal val OneInactiveElements = persistentListOf(_1, _4, _5, _6, _7)

        internal val TwoActiveElements = persistentListOf(_1, _2, _4, _5, _7)
        internal val TwoInactiveElements = persistentListOf(_3, _6)

        internal val ThreeActiveElements = persistentListOf(_1, _2, _3, _4, _7)
        internal val ThreeInactiveElements = persistentListOf(_5, _6)

        internal val FourActiveElements = persistentListOf(_2, _3, _6, _7)
        internal val FourInactiveElements = persistentListOf(_1, _4, _5)

        internal val FiveActiveElements = persistentListOf(_1, _3, _4, _6, _7)
        internal val FiveInactiveElements = persistentListOf(_2, _5)

        internal val SixActiveElements = persistentListOf(_1, _3, _4, _5, _6, _7)
        internal val SixInactiveElements = persistentListOf(_2)

        internal val SevenActiveElements = persistentListOf(_1, _2, _3)
        internal val SevenInactiveElements = persistentListOf(_4, _5, _6, _7)

        internal val EightActiveElements = persistentListOf(_1, _2, _3, _4, _5, _6, _7)
        internal val EightInactiveElements = persistentListOf<SevenSegmentElement>()

        internal val NineActiveElements = persistentListOf(_1, _2, _3, _4, _6, _7)
        internal val NineInactiveElements = persistentListOf(_5)

        internal val DotActiveElements = persistentListOf(_2)
        internal val DotInactiveElements = persistentListOf<SevenSegmentElement>()

        internal val ColonActiveElements = persistentListOf(_1, _2)
        internal val ColonInactiveElements = persistentListOf<SevenSegmentElement>()

        /**
         * Converts a list of [SevenSegmentElement] to a [SevenSegmentData] object.
         *
         * @return A [SevenSegmentData] object with the active and inactive elements from the list
         * of active elements.
         */
        fun ImmutableList<SevenSegmentElement>.findSegmentData(): SevenSegmentData {

            return SevenSegmentData(
                activeElements = this.toSet().toImmutableList(),
                inactiveElements = entries.subtract(this.toSet()).toImmutableList()
            )
        }
    }
}