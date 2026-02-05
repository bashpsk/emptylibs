package io.bashpsk.emptylibs.formatter.format

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import androidx.core.graphics.toColorLong
import io.bashpsk.emptylibs.formatter.utils.LOG_TAG
import java.util.Locale

/**
 * Converts a [Color] to its hex string representation in the format #AARRGGBB.
 *
 * @return A string representing the color in "#AARRGGBB" hexadecimal format.
 */
@Stable
fun Color.toHexString(): String {

    return "#%08X".format(locale = Locale.getDefault(), this.toArgb())
}

/**
 * Parses a hex string to a [Color].
 * Supports formats: #RGB, #RRGGBB, #AARRGGBB (with or without # prefix).
 * Returns null if the string is not a valid hex color.
 *
 * @return The corresponding [Color] object, or `null` if parsing fails.
 */
@Stable
fun String.parseHexToColor(): Color? {

    return try {

        Color.fromColorLong(colorLong = this.toColorInt().toColorLong())
    } catch (exception: Exception) {

        Log.e(LOG_TAG, "Failed to parse hex to Color : $this", exception)
        null
    }
}