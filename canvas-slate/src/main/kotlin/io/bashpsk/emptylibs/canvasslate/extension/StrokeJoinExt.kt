package io.bashpsk.emptylibs.canvasslate.extension

import androidx.compose.ui.graphics.StrokeJoin

/**
 * Converts a string representation of a stroke join to a [StrokeJoin] object.
 *
 * This function takes a string as input and attempts to match it with the string representations
 * of the predefined [StrokeJoin] values (Miter, Round, Bevel).
 *
 * @return The corresponding [StrokeJoin] object if a match is found.
 * If the input string does not match any known stroke join, it defaults to [StrokeJoin.Round].
 */
internal fun String.toStrokeJoin(): StrokeJoin {

    return when (this) {

        StrokeJoin.Miter.toString() -> StrokeJoin.Miter
        StrokeJoin.Round.toString() -> StrokeJoin.Round
        StrokeJoin.Bevel.toString() -> StrokeJoin.Bevel
        else -> StrokeJoin.Round
    }
}