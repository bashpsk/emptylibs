package io.bashpsk.emptylibs.pdftemplate.sheet

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable

/**
 * Defines the margins for a [SheetSize] as a fraction of the total width and height.
 * All values should be between 0.0 and 0.45.
 *
 * @property left Fraction of the width to use as left margin.
 * @property top Fraction of the height to use as top margin.
 * @property right Fraction of the width to use as right margin.
 * @property bottom Fraction of the height to use as bottom margin.
 */
@Immutable
data class SheetMargin(
    @param:FloatRange(0.0, 0.45)
    val left: Float = 0F,
    @param:FloatRange(0.0, 0.45)
    val top: Float = 0F,
    @param:FloatRange(0.0, 0.45)
    val right: Float = 0F,
    @param:FloatRange(0.0, 0.45)
    val bottom: Float = 0F
) {

    companion object {

        /** A margin with 0.0 fraction for all sides. */
        val Zero = SheetMargin()

        /** A default margin of 0.05 (5%) for all sides. */
        val Default = SheetMargin(0.05F, 0.05F, 0.05F, 0.05F)

        /**
         * Creates a [SheetMargin] with symmetric horizontal and vertical margins.
         *
         * @param horizontal The fraction for left and right margins.
         * @param vertical The fraction for top and bottom margins.
         */
        fun symmetric(horizontal: Float = 0F, vertical: Float = 0F): SheetMargin {

            return SheetMargin(
                left = horizontal,
                top = vertical,
                right = horizontal,
                bottom = vertical
            )
        }

        /**
         * Creates a [SheetMargin] with the same fraction for all sides.
         *
         * @param margin The fraction for all margins.
         */
        fun all(margin: Float): SheetMargin {

            return symmetric(horizontal = margin, vertical = margin)
        }
    }
}