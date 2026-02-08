package io.bashpsk.emptylibs.imagekolor.svg

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

internal fun Color.toSvgHexString(): String {

    return "#%06X".format(this.toArgb() and 0x00FFFFFF)
}