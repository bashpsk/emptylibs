package io.bashpsk.emptylibs.formatter.format

import android.util.Log
import androidx.compose.runtime.Stable
import io.bashpsk.emptylibs.formatter.utils.LOG_TAG
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

/**
 * Rounds a `Double` to a specified number of decimal places.
 *
 * This function takes a `Double` value and rounds it to the nearest decimal
 * place specified by the `fraction` parameter. It uses the default locale
 * for formatting. If an exception occurs during the rounding process, it
 * will log the error using `Log.e()` and return 0.0.
 *
 * @param decimal The `Double` value to be rounded.
 * @param fraction The number of decimal places to round to. Defaults to 0.0.
 * @return The rounded `Double` value, or 0.0 if an error occurs.
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
 * Rounds a `Double` to a specified number of decimal places.
 *
 * This function takes a `Double` value and rounds it to the nearest decimal
 * place specified by the `fraction` parameter. It uses the default locale
 * for formatting. If an exception occurs during the rounding process, it
 * will log the error using `Log.e()` and return 0.0.
 *
 * @param decimal The `Double` value to be rounded.
 * @param fraction The number of decimal places to round to. Defaults to 0.0.
 * @return The rounded `Double` value, or 0.0 if an error occurs.
 */
@Stable
fun Double.toRoundedDecimal(fraction: Int = 0): Double {

    return this.toRoundedDecimalOrNull(fraction = fraction) ?: 0.0
}

/**
 * Rounds a `Float` to a specified number of decimal places.
 *
 * Similar to `toRoundedDecimal(Double, Int)`, this function rounds a `Float`
 * value to the nearest decimal place specified by the `fraction` parameter.
 * It also uses the default locale for formatting. If an error occurs during
 * rounding, it will log the error using `Log.e()` and return 0F.
 *
 * @param decimal The `Float` value to be rounded.
 * @param fraction The number of decimal places to round to. Defaults to 0F.
 * @return The rounded `Float` value, or 0F if an error occurs.
 */
@Stable
fun Float.toRoundedDecimalOrNull(fraction: Int = 0): Float? {

    return try {

        "%.${fraction}f".format(locale = Locale.getDefault(), this).toFloatOrNull()
    } catch (exception: Exception) {

        Log.e(LOG_TAG, exception.message, exception)
        0F
    }
}

/**
 * Rounds a `Float` to a specified number of decimal places.
 *
 * Similar to `toRoundedDecimal(Double, Int)`, this function rounds a `Float`
 * value to the nearest decimal place specified by the `fraction` parameter.
 * It also uses the default locale for formatting. If an error occurs during
 * rounding, it will log the error using `Log.e()` and return 0F.
 *
 * @param decimal The `Float` value to be rounded.
 * @param fraction The number of decimal places to round to. Defaults to 0F.
 * @return The rounded `Float` value, or 0F if an error occurs.
 */
@Stable
fun Float.toRoundedDecimal(fraction: Int = 0): Float {

    return this.toRoundedDecimalOrNull(fraction = fraction) ?: 0.0F
}

/**
 * Formats a `Long` value into a human-readable string with scaling suffixes.
 *
 * @param value The `Long` value to format.
 * @return A formatted `String` representation of the input value.
 * Example:
 * - 1234 -> "1.2K"
 * - 1234567 -> "1.2M"
 */
@Stable
fun Long.shortenedNumericalNotation(): String {

    return this.toDouble().shortenedNumericalNotation()
}

/**
 * Formats an `Int` value into a human-readable string with scaling suffixes.
 *
 * @param value The `Int` value to format.
 * @return A formatted `String` representation of the input value.
 * Example:
 * - 1234 -> "1.2K"
 * - 1234567 -> "1.2M"
 */
@Stable
fun Int.shortenedNumericalNotation(): String {

    return this.toLong().shortenedNumericalNotation()
}

/**
 * Formats a `Double` value into a human-readable string with scaling suffixes.
 *
 * @param value The `Double` value to format.
 * @return A formatted `String` representation of the input value.
 * Example:
 * - 1234 -> "1.2K"
 * - 1234567 -> "1.2M"
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
 * @param total The total possible value.
 * @param obtained The obtained value.
 * @return The percentage value rounded to one decimal place, or 0.0 if total is zero.
 * @see toRoundedDecimal for rounding the result.
 */
@Stable
fun findPercentage(total: Double, obtained: Double): Double {

    return when (total) {

        0.0 -> 0.0
        else -> ((obtained / total) * 100).toRoundedDecimal( fraction = 1)
    }
}

/**
 * Calculates the percentage of obtained value relative to the total.
 * @param total The total possible value.
 * @param obtained The obtained value.
 * @return The percentage value rounded to one decimal place, or 0.0F if total is zero.
 * @see toRoundedDecimal for rounding the result.
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
 * This function divides the width by the height to determine the aspect ratio.
 * If the height is zero, it returns 0.0F to prevent division by zero errors.
 *
 * @param width The width of the dimension.
 * @param height The height of the dimension.
 * @return The aspect ratio as a Float (width / height), or 0.0F if height is 0.
 */
@Stable
fun findAspectRatio(width: Int, height: Int): Float {

    return if (height == 0) 0.0F else width / height.toFloat()
}

/**
 * Calculates the aspect ratio of a given width and height.
 *
 * This function divides the width by the height to determine the aspect ratio.
 * If the height is zero, it returns 0.0F to prevent division by zero errors.
 *
 * @param width The width of the dimension.
 * @param height The height of the dimension.
 * @return The aspect ratio as a Float (width / height), or 0.0F if height is 0.0F.
 */
@Stable
fun findAspectRatio(width: Float, height: Float): Float {

    return if (height == 0.0F) 0.0F else width / height
}

/**
 * Calculates the simplified aspect ratio of a given width and height.
 *
 * This function uses the greatest common divisor (GCD) to reduce the given dimensions
 * to their simplest integer ratio form, commonly used in video and display resolutions.
 *
 * @param width The horizontal resolution in pixels.
 * @param height The vertical resolution in pixels.
 * @return A string representing the simplified aspect ratio (e.g., "16:9").
 *
 * Example:
 * ```
 * aspectRatioLabel(1920, 1080) // returns "16:9"
 * aspectRatioLabel(1080, 1920) // returns "9:16"
 * ```
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
 * Converts the file length to megabytes (MB).
 *
 * This function calculates the size by dividing the byte length by 1,048,576 (1024 * 1024).
 *
 * @return The size of the file in megabytes as a [Double].
 */
fun Long.toMegabytes(): Double {

    if (this <= 0) return 0.0

    return this.toDouble() / (1024 * 1024)
}