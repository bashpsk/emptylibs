package io.bashpsk.emptylibs.composeutils.stroke

import androidx.compose.ui.graphics.StrokeJoin

fun String.toStrokeJoin(): StrokeJoin {

    return when (this) {

        StrokeJoin.Miter.toString() -> StrokeJoin.Miter
        StrokeJoin.Round.toString() -> StrokeJoin.Round
        StrokeJoin.Bevel.toString() -> StrokeJoin.Bevel
        else -> StrokeJoin.Round
    }
}