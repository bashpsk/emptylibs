package io.bashpsk.emptylibs.formatter.format

import android.content.Context
import android.text.format.Formatter
import androidx.compose.runtime.Stable
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

/**
 * Defines the unit system used to format file sizes.
 *
 * [Decimal] uses a base of 1000 and follows the decimal/SI convention:
 * B, KB, MB, GB, TB, PB, EB, ZB, YB, RB, and QB.
 *
 * [Binary] uses a base of 1024 and follows the binary/IEC convention:
 * B, KiB, MiB, GiB, TiB, PiB, EiB, ZiB, YiB, RiB, and QiB.
 *
 * For example:
 * - 1000 bytes = 1.0 KB using [Decimal].
 * - 1024 bytes = 1.0 KiB using [Binary].
 *
 * @property base The numeric base used to calculate the file-size magnitude.
 * @property units The ordered list of units used for each file-size magnitude,
 * from bytes to the largest supported unit.
 */
enum class SizeFormatSystem(val base: Double, val units: Array<String>) {

    /**
     * Decimal file-size system using a base of 1000.
     *
     * Units:
     * B, KB, MB, GB, TB, PB, EB, ZB, YB, RB, QB.
     */
    Decimal(
        base = 1000.0,
        units = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "RB", "QB")
    ),

    /**
     * Binary file-size system using a base of 1024.
     *
     * Units:
     * B, KiB, MiB, GiB, TiB, EiB, ZiB, YiB, RiB, QiB.
     */
    Binary(
        base = 1024.0,
        units = arrayOf("B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB", "RiB", "QiB")
    );
}

/**
 * Formats a file size represented as a [Long] into a human-readable string using the
 * system's default formatting.
 *
 * This method utilizes the Android framework's [Formatter] class to format the provided
 * size into a file size string appropriate for the user's locale.
 *
 * @param context The application's context, used by the underlying [Formatter].
 * @return A string representing the formatted file size (e.g., "1.5 MB", "1024 KB").
 *
 * @see Formatter.formatFileSize
 */
@Stable
fun Long.toFileSize(context: Context): String {

    return Formatter.formatShortFileSize(context, this).uppercase()
}

/**
 * Formats a file size represented as a [Long] into a human-readable string.
 *
 * This method converts the provided size (in bytes) into a string with the appropriate
 * magnitude suffix (B, KB, MB, GB, TB, PB, EB). The output string is formatted
 * to one decimal place and uses the current locale's formatting.
 *
 * @return A string representing the formatted file size (e.g., "1.5 MB", "1024.0 KB").
 */
@Stable
fun Long.toFileSize(formatSystem: SizeFormatSystem = SizeFormatSystem.Decimal): String {

    if (this <= 0) return "0 ${formatSystem.units.first()}"

    val digitGroups = (log10(this.toDouble()) / log10(formatSystem.base)).toInt()
        .coerceAtMost(formatSystem.units.lastIndex)
    val displayValue = this / formatSystem.base.pow(digitGroups.toDouble())

    return "%.1f %s".format(
        locale = Locale.getDefault(),
        displayValue,
        formatSystem.units.getOrElse(digitGroups) { "Unknown" }
    )
}