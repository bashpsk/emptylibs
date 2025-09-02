package io.bashpsk.emptylibs.composeutils.stroke

import androidx.compose.ui.graphics.StrokeCap

fun String.toStrokeCap(): StrokeCap {

    return when (this) {

        StrokeCap.Butt.toString() -> StrokeCap.Butt
        StrokeCap.Round.toString() -> StrokeCap.Round
        StrokeCap.Square.toString() -> StrokeCap.Square
        else -> StrokeCap.Round
    }
}