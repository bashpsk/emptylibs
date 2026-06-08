package io.bashpsk.emptylibs.formatter.format

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import io.bashpsk.emptylibs.formatter.utils.LOG_TAG
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * Rounds a [Double] to a specified number of decimal places, returning `null` if an error occurs.
 *
 * @param fraction The number of decimal places to round to. Defaults to 0.
 * @return The rounded [Double] value, or `null` if an exception occurs during formatting.
 */
@Stable
fun Double.toRoundedDecimalOrNull(fraction: Int = 0): Double? {

    return try {

        val factor = 10.0.pow(fraction)
        round(this * factor) / factor
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

        val factor = 10.0F.pow(fraction)
        round(this * factor) / factor
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
 * Formats an any [Number] value into a human-readable string with scaling suffixes (K, M, B, etc.).
 *
 * @return A formatted [String] representation of the input value.
 * Example:
 * - 1234.0 -> "1.2K"
 * - 1234567L -> "1.2M"
 */
@Stable
fun Number.shortenedNumericalNotation(): String {

    val value = this.toDouble()

    return when(value.roundToInt()) {

         in -999 .. 999 -> "$value"

        else -> {

            val units = arrayOf("", "K", "M", "B", "T", "Q")
            val powerOfThousand = (log10(value).toInt() / 3).coerceAtMost(units.lastIndex)
            val displayValue = value / 10.0.pow(powerOfThousand * 3)

            "%.1f%s".format(locale = Locale.getDefault(), displayValue, units[powerOfThousand])
        }
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
fun findPercentage(total: Number, obtained: Number): Int {

    return when (total) {

        0 -> 0
        else -> ((obtained.toDouble() / total.toDouble()) * 100).roundToInt()
    }
}

/**
 * Calculates the aspect ratio of a given width and height.
 *
 * @param width The width of the dimension.
 * @param height The height of the dimension.
 * @return The aspect ratio as a [Float] (width / height), or 0.0F if width or height is 0.
 */
@Stable
fun findAspectRatio(width: Number, height: Number): Float {

    return if (width == 0 || height == 0) 0.0F else (width.toDouble() / height.toDouble()).toFloat()
}

/**
 * Calculates the aspect ratio of a given [Size].
 *
 * @return The aspect ratio as a [Float] (width / height), or 0.0F if width or height is 0.
 */
@Stable
fun Size.findAspectRatio(): Float {

    return findAspectRatio(width = width, height = height)
}

/**
 * Calculates the aspect ratio of a given [IntSize].
 *
 * @return The aspect ratio as a [Float] (width / height), or 0.0F if width or height is 0.
 */
@Stable
fun IntSize.findAspectRatio(): Float {

    return findAspectRatio(width = width, height = height)
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