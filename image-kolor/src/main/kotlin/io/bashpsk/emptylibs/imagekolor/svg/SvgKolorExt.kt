package io.bashpsk.emptylibs.imagekolor.svg

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Converts a [Color] to a 6-digit hex string for use in SVG.
 *
 * @return The SVG-compatible hex string (e.g., "#RRGGBB").
 */
internal fun Color.toSvgHexString(): String {

    return "#%06X".format(this.toArgb() and 0x00FFFFFF)
}