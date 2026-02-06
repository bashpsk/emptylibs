package io.bashpsk.emptylibs.pdftemplate.sheet

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import io.bashpsk.emptylibs.formatter.format.aspectRatioLabel
import io.bashpsk.emptylibs.formatter.format.findAspectRatio

/**
 * Standard ISO sheet sizes in points (1/72 inch).
 *
 * @property width The width of the sheet.
 * @property height The height of the sheet.
 */
enum class SheetSize(val width: Int, val height: Int) {

    // ISO A Series
    A0(width = 2384, height = 3370),
    A1(width = 1684, height = 2384),
    A2(width = 1191, height = 1684),
    A3(width = 842, height = 1191),
    A4(width = 595, height = 842),
    A5(width = 420, height = 595),
    A6(width = 298, height = 420),
    A7(width = 210, height = 298),
    A8(width = 147, height = 210),
    A9(width = 105, height = 147),
    A10(width = 74, height = 105),

    // ISO B Series
    B0(width = 2835, height = 4008),
    B1(width = 2004, height = 2835),
    B2(width = 1417, height = 2004),
    B3(width = 1001, height = 1417),
    B4(width = 709, height = 1001),
    B5(width = 499, height = 709),
    B6(width = 354, height = 499),
    B7(width = 249, height = 354),
    B8(width = 176, height = 249),
    B9(width = 125, height = 176),
    B10(width = 88, height = 125),

    // ISO C Series
    C0(width = 2599, height = 3677),
    C1(width = 1837, height = 2599),
    C2(width = 1298, height = 1837),
    C3(width = 918, height = 1298),
    C4(width = 649, height = 918),
    C5(width = 459, height = 649),
    C6(width = 323, height = 459),
    C7(width = 230, height = 323),
    C8(width = 162, height = 230),
    C9(width = 113, height = 162),
    C10(width = 79, height = 113);

    companion object {

        /** Maximum width among all predefined sheet sizes. */
        val MaxWidth = entries.maxOf { sheetSize -> sheetSize.width }

        /** Maximum height among all predefined sheet sizes. */
        val MaxHeight = entries.maxOf { sheetSize -> sheetSize.height }

        /**
         * Converts the enum name to a user-friendly label.
         * For example, "A4" remains "A4", or "LEGAL_SIZE" becomes "Legal Size".
         */
        @Stable
        fun SheetSize.toLabel(): String {

            return name.lowercase().split("_").joinToString(" ") { word ->

                word.replaceFirstChar { firstChar -> firstChar.titlecase() }
            }
        }

        /**
         * Calculates the content rectangle for this sheet size given a [SheetMargin].
         */
        fun SheetSize.toRect(margin: SheetMargin): Rect {

            return Rect(
                left = width * margin.left,
                top = height * margin.top,
                right = width - (width * margin.right),
                bottom = height - (height * margin.bottom)
            )
        }

        /** Converts [SheetSize] to a Compose [Size]. */
        fun SheetSize.toSize(): Size {

            return toIntSize().toSize()
        }

        /** Converts [SheetSize] to a Compose [IntSize]. */
        fun SheetSize.toIntSize(): IntSize {

            return IntSize(width = width, height = height)
        }

        /** Calculates the aspect ratio (width / height) of the sheet. */
        fun SheetSize.getAspectRatio(): Float {

            return findAspectRatio(width = width, height = height)
        }

        /** Returns a string representation of the aspect ratio. */
        fun SheetSize.getAspectRatioLabel(): String {

            return aspectRatioLabel(width = width, height = height)
        }
    }
}