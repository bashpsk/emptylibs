package io.bashpsk.emptylibs.canvasslate.extension

import androidx.compose.ui.graphics.StrokeCap

/**
 * Converts a string representation of a stroke cap to a [StrokeCap] object.
 *
 * @return The corresponding [StrokeCap] object, or [StrokeCap.Round] if the string is not a valid
 * stroke cap.
 */
internal fun String.toStrokeCap(): StrokeCap {

    return when (this) {

        StrokeCap.Butt.toString() -> StrokeCap.Butt
        StrokeCap.Round.toString() -> StrokeCap.Round
        StrokeCap.Square.toString() -> StrokeCap.Square
        else -> StrokeCap.Round
    }
}