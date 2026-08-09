package io.bashpsk.emptylibs.jetpackui.sevensegment

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet

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

        internal val ZeroActiveElements = persistentSetOf(_1, _2, _3, _4, _5, _6)
        internal val ZeroInactiveElements = persistentSetOf(_7)

        internal val OneActiveElements = persistentSetOf(_2, _3)
        internal val OneInactiveElements = persistentSetOf(_1, _4, _5, _6, _7)

        internal val TwoActiveElements = persistentSetOf(_1, _2, _4, _5, _7)
        internal val TwoInactiveElements = persistentSetOf(_3, _6)

        internal val ThreeActiveElements = persistentSetOf(_1, _2, _3, _4, _7)
        internal val ThreeInactiveElements = persistentSetOf(_5, _6)

        internal val FourActiveElements = persistentSetOf(_2, _3, _6, _7)
        internal val FourInactiveElements = persistentSetOf(_1, _4, _5)

        internal val FiveActiveElements = persistentSetOf(_1, _3, _4, _6, _7)
        internal val FiveInactiveElements = persistentSetOf(_2, _5)

        internal val SixActiveElements = persistentSetOf(_1, _3, _4, _5, _6, _7)
        internal val SixInactiveElements = persistentSetOf(_2)

        internal val SevenActiveElements = persistentSetOf(_1, _2, _3)
        internal val SevenInactiveElements = persistentSetOf(_4, _5, _6, _7)

        internal val EightActiveElements = persistentSetOf(_1, _2, _3, _4, _5, _6, _7)
        internal val EightInactiveElements = persistentSetOf<SevenSegmentElement>()

        internal val NineActiveElements = persistentSetOf(_1, _2, _3, _4, _6, _7)
        internal val NineInactiveElements = persistentSetOf(_5)

        internal val DotActiveElements = persistentSetOf(_2)
        internal val DotInactiveElements = persistentSetOf<SevenSegmentElement>()

        internal val ColonActiveElements = persistentSetOf(_1, _2)
        internal val ColonInactiveElements = persistentSetOf<SevenSegmentElement>()

        /**
         * Converts a list of [SevenSegmentElement] to a [SevenSegmentData] object.
         *
         * @return A [SevenSegmentData] object with the active and inactive elements from the list
         * of active elements.
         */
        fun ImmutableSet<SevenSegmentElement>.findSegmentData(): SevenSegmentData {

            return SevenSegmentData(
                activeElements = this.toImmutableSet(),
                inactiveElements = entries.subtract(this).toImmutableSet()
            )
        }
    }
}