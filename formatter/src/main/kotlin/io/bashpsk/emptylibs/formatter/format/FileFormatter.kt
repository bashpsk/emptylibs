package io.bashpsk.emptylibs.formatter.format

import android.content.Context
import android.text.format.Formatter
import androidx.compose.runtime.Stable
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

/**
 * Formats a file size represented as a Long into a human-readable string using the
 * system's default formatting.
 *
 * This method utilizes the Android framework's `Formatter` class to format the provided
 * `size` into a file size string appropriate for the user's locale.
 *
 * @param context The application's context, used by the underlying `Formatter`.
 * @param size The file size in bytes.
 * @return A string representing the formatted file size (e.g., "1.5 MB", "1024 KB").
 *
 * @see Formatter.formatFileSize
 */
@Stable
fun Long. toFileSize(context: Context): String {

    return Formatter.formatShortFileSize(context, this).uppercase()
}

/**
 * Formats a file size represented as a Long into a human-readable string.
 *
 * This method converts the provided `size` (in bytes) into a string with the appropriate
 * magnitude suffix (B, KB, MB, GB, TB, PB, EB, ZB, YB). The output string is formatted
 * to two decimal places and uses the current locale's formatting.
 *
 * @param size The file size in bytes.
 * @return A string representing the formatted file size (e.g., "1.5 MB", "1024.0 KB").
 */
@Stable
fun Long.toFileSize(): String {

    val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt().coerceAtMost(units.lastIndex)
    val displayValue = this / 1024.0.pow(digitGroups.toDouble())

    return "%.1f %s".format(
        locale = Locale.getDefault(),
        displayValue,
        units[digitGroups]
    ).uppercase()
}