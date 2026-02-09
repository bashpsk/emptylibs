package io.bashpsk.emptylibs.formatter.format

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import io.bashpsk.emptylibs.formatter.utils.LOG_TAG
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

/**
 * Rounds a [Double] to a specified number of decimal places, returning `null` if an error occurs.
 *
 * @param fraction The number of decimal places to round to. Defaults to 0.
 * @return The rounded [Double] value, or `null` if an exception occurs during formatting.
 */
@Stable
fun Double.toRoundedDecimalOrNull(fraction: Int = 0): Double? {

    return try {

        "%.${fraction}f".format(locale = Locale.getDefault(), this).toDoubleOrNull()
    } catch (exception: Exception) {

        Log.e(LOG_TAG, exception.message, exception)
        null
    }
}

/**
 * Rounds a [Double] to a specified number of decimal places.
 *
 * @param fraction The number of decimal places to round to. Defaults to 0.
 * @return The rounded [Double] value, or 0.0 if an error occurs.
 */
@Stable
fun Double.toRoundedDecimal(fraction: Int = 0): Double {

    return this.toRoundedDecimalOrNull(fraction = fraction) ?: 0.0
}

/**
 * Rounds a [Float] to a specified number of decimal places, returning `null` if an error occurs.
 *
 * @param fraction The number of decimal places to round to. Defaults to 0.
 * @return The rounded [Float] value, or `null` if an exception occurs during formatting.
 */
@Stable
fun Float.toRoundedDecimalOrNull(fraction: Int = 0): Float? {

    return try {

        "%.${fraction}f".format(locale = Locale.getDefault(), this).toFloatOrNull()
    } catch (exception: Exception) {

        Log.e(LOG_TAG, exception.message, exception)
        null
    }
}

/**
 * Rounds a [Float] to a specified number of decimal places.
 *
 * @param fraction The number of decimal places to round to. Defaults to 0.
 * @return The rounded [Float] value, or 0.0F if an error occurs.
 */
@Stable
fun Float.toRoundedDecimal(fraction: Int = 0): Float {

    return this.toRoundedDecimalOrNull(fraction = fraction) ?: 0.0F
}

/**
 * Formats a [Long] value into a human-readable string with scaling suffixes (K, M, B, etc.).
 *
 * @return A formatted [String] representation of the input value.
 * Example:
 * - 1234 -> "1.2K"
 * - 1234567 -> "1.2M"
 */
@Stable
fun Long.shortenedNumericalNotation(): String {

    return this.toDouble().shortenedNumericalNotation()
}

/**
 * Formats an [Int] value into a human-readable string with scaling suffixes (K, M, B, etc.).
 *
 * @return A formatted [String] representation of the input value.
 * Example:
 * - 1234 -> "1.2K"
 * - 1234567 -> "1.2M"
 */
@Stable
fun Int.shortenedNumericalNotation(): String {

    return this.toLong().shortenedNumericalNotation()
}

/**
 * Formats a [Double] value into a human-readable string with scaling suffixes (K, M, B, etc.).
 *
 * @return A formatted [String] representation of the input value.
 * Example:
 * - 1234.0 -> "1.2K"
 * - 1234567.0 -> "1.2M"
 */
@Stable
fun Double.shortenedNumericalNotation(): String {

    val units = arrayOf("", "K", "M", "B", "T", "Q")
    val powerOfThousand = (log10(this).toInt() / 3).coerceAtMost(units.lastIndex)
    val displayValue = this / 10.0.pow(powerOfThousand * 3)

    return "%.1f%s".format(locale = Locale.getDefault(), displayValue, units[powerOfThousand])
}

/**
 * Calculates the percentage of obtained value relative to the total.
 *
 * @param total The total possible value.
 * @param obtained The obtained value.
 * @return The percentage value as an integer, or 0 if total is zero.
 */
@Stable
fun findPercentage(total: Long, obtained: Long): Int {

    return when (total) {

        0L -> 0
        else -> ((obtained.toDouble() / total) * 100).toInt()
    }
}

/**
 * Calculates the percentage of obtained value relative to the total.
 *
 * @param total The total possible value.
 * @param obtained The obtained value.
 * @return The percentage value as an integer, or 0 if total is zero.
 */
@Stable
fun findPercentage(total: Int, obtained: Int): Int {

    return when (total) {

        0 -> 0
        else -> ((obtained.toDouble() / total) * 100).toInt()
    }
}

/**
 * Calculates the percentage of obtained value relative to the total.
 *
 * @param total The total possible value.
 * @param obtained The obtained value.
 * @return The percentage value rounded to one decimal place, or 0.0 if total is zero.
 */
@Stable
fun findPercentage(total: Double, obtained: Double): Double {

    return when (total) {

        0.0 -> 0.0
        else -> ((obtained / total) * 100).toRoundedDecimal(fraction = 1)
    }
}

/**
 * Calculates the percentage of obtained value relative to the total.
 *
 * @param total The total possible value.
 * @param obtained The obtained value.
 * @return The percentage value rounded to one decimal place, or 0.0F if total is zero.
 */
@Stable
fun findPercentage(total: Float, obtained: Float): Float {

    return when (total) {

        0.0F -> 0.0F
        else -> ((obtained / total) * 100).toRoundedDecimal(fraction = 1)
    }
}

/**
 * Calculates the aspect ratio of a given width and height.
 *
 * @param width The width of the dimension.
 * @param height The height of the dimension.
 * @return The aspect ratio as a [Float] (width / height), or 0.0F if height is 0.
 */
@Stable
fun findAspectRatio(width: Int, height: Int): Float {

    return if (height == 0) 0.0F else width / height.toFloat()
}

/**
 * Calculates the aspect ratio of a given width and height.
 *
 * @param width The width of the dimension.
 * @param height The height of the dimension.
 * @return The aspect ratio as a [Float] (width / height), or 0.0F if height is 0.0F.
 */
@Stable
fun findAspectRatio(width: Float, height: Float): Float {

    return if (height == 0.0F) 0.0F else width / height
}

/**
 * Calculates the aspect ratio of a given [Size].
 *
 * @return The aspect ratio as a [Float] (width / height), or 0.0F if height is 0.
 */
@Stable
fun Size.findAspectRatio(): Float {

    return findAspectRatio(width = this.width, height = this.height)
}

/**
 * Calculates the aspect ratio of a given [IntSize].
 *
 * @return The aspect ratio as a [Float] (width / height), or 0.0F if height is 0.
 */
@Stable
fun IntSize.findAspectRatio(): Float {

    return findAspectRatio(width = this.width, height = this.height)
}

/**
 * Calculates the simplified aspect ratio of a given width and height.
 *
 * Uses the greatest common divisor (GCD) to reduce dimensions to their simplest integer ratio form.
 *
 * @param width The horizontal resolution in pixels.
 * @param height The vertical resolution in pixels.
 * @return A string representing the simplified aspect ratio (e.g., "16:9").
 */
@Stable
fun aspectRatioLabel(width: Int, height: Int): String {

    fun gcd(a: Int, b: Int): Int {

        return if (b == 0) a else gcd(b, a % b)
    }

    val divisor = gcd(a = width, b = height)

    return "${width / divisor}:${height / divisor}"
}

/**
 * Converts the file length in bytes to megabytes (MB).
 *
 * @return The size of the file in megabytes as a [Double].
 */
fun Long.toMegabytes(): Double {

    if (this <= 0) return 0.0

    return this.toDouble() / (1024 * 1024)
}