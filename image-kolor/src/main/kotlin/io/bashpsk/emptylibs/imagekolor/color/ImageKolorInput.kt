package io.bashpsk.emptylibs.imagekolor.color

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.BlurLinear
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.BlurLinear
import androidx.compose.material.icons.outlined.Brightness6
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.Exposure
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Represents different types of image color adjustments that can be applied.
 * This sealed class defines common properties for all color adjustments,
 * such as a human-readable label and the valid range for the adjustment value.
 *
 * Each specific color adjustment (e.g., Brightness, Contrast) is a data class
 * that inherits from `ImageKolorInput` and provides its default value.
 *
 * This class is designed to be Parcelable and Serializable for easy use in Android
 * components and data persistence.
 *
 * @property label A user-friendly name for the color adjustment.
 * @property rangeMin The minimum valid value for this color adjustment.
 * @property rangeMax The maximum valid value for this color adjustment.
 */
@Parcelize
@Serializable
sealed class ImageKolorInput(
    val label: String,
    val rangeMin: Float,
    val rangeMax: Float
) : Parcelable {

    /**
     * Represents the brightness adjustment for an image.
     *
     * @property value The brightness value, ranging from -1F (darker) to 1F (brighter), with 0F
     * being the default (no change).
     */
    data class Brightness(val value: Float = 0F) : ImageKolorInput(
        label = "Brightness",
        rangeMin = -1F,
        rangeMax = 1F
    )

    /**
     * Represents the exposure adjustment for an image.
     *
     * Exposure controls the overall brightness of an image. Increasing exposure makes the image
     * brighter, while decreasing it makes the image darker.
     *
     * @property value The exposure value, typically ranging from -1F (darker) to 1F (brighter),
     * with 0F being the default (no change).
     */
    data class Exposure(val value: Float = 0F) : ImageKolorInput(
        label = "Exposure",
        rangeMin = -1F,
        rangeMax = 1F
    )

    /**
     * Represents the contrast adjustment for an image.
     *
     * @property value The contrast value, ranging from 0F to 2F.
     * A value of 1F represents no change in contrast.
     * Values less than 1F decrease contrast, while values greater than 1F increase contrast.
     */
    data class Contrast(val value: Float = 1F) : ImageKolorInput(
        label = "Contrast",
        rangeMin = 0F,
        rangeMax = 2F
    )

    /**
     * Represents the saturation adjustment for an image.
     *
     * Saturation controls the intensity of colors in an image.
     * A value of 0 results in a grayscale image.
     * A value of 1 represents the original saturation.
     * Values greater than 1 increase saturation, making colors more vivid.
     * Values between 0 and 1 decrease saturation, making colors more muted.
     *
     * @property value The saturation level. Defaults to 1F (original saturation).
     *                 The typical range is from 0F (grayscale) to 2F (highly saturated).
     */
    data class Saturation(val value: Float = 1F) : ImageKolorInput(
        label = "Saturation",
        rangeMin = 0F,
        rangeMax = 2F
    )

    /**
     * Represents the warmth adjustment for an image.
     *
     * @property value The warmth value, ranging from -1F (cooler) to 1F (warmer). Default is 0F.
     */
    data class Warmth(val value: Float = 0F) : ImageKolorInput(
        label = "Warmth",
        rangeMin = -1F,
        rangeMax = 1F
    )

    /**
     * Represents a tint adjustment for an image.
     *
     * Tint adjustments shift the colors of an image towards magenta (positive values) or
     * green (negative values).
     *
     * @property value The tint adjustment value. A value of 0 indicates no tint adjustment.
     * Positive values shift colors towards magenta. Negative values shift colors towards green.
     * The valid range is from -1F to 1F.
     */
    data class Tint(val value: Float = 0F) : ImageKolorInput(
        label = "Tint",
        rangeMin = -1F,
        rangeMax = 1F
    )

    /**
     * Represents the highlight adjustment for an image.
     *
     * Highlights refer to the brightest areas of an image. Adjusting this value
     * can help to recover details in overexposed areas or to brighten dull highlights.
     *
     * @property value The intensity of the highlight adjustment.
     * A value of 0 indicates no change.
     * Positive values increase the brightness of highlights.
     * Negative values decrease the brightness of highlights.
     * The valid range is from -1F to 1F.
     */
    data class Highlights(val value: Float = 0F) : ImageKolorInput(
        label = "Highlights",
        rangeMin = -1F,
        rangeMax = 1F
    )

    /**
     * Represents the shadows adjustment for an image.
     *
     * This input controls the brightness of the darker areas of an image.
     * A positive value lightens shadows, while a negative value darkens them.
     *
     * @param value The intensity of the shadows adjustment. Defaults to 0F (no change).
     * The valid range is from -1F (maximum darkening) to 1F (maximum lightening).
     */
    data class Shadows(val value: Float = 0F) : ImageKolorInput(
        label = "Shadows",
        rangeMin = -1F,
        rangeMax = 1F
    );

    companion object {

        /**
         * A list of all available [ImageKolorInput] types.
         * This list is immutable and can be used to iterate over all possible color adjustments.
         */
        val AllTypes = persistentListOf(
            Brightness(),
            Exposure(),
            Contrast(),
            Saturation(),
            Warmth(),
            Tint(),
            Highlights(),
            Shadows()
        ).toImmutableList()

        /**
         * Returns the valid range for the input value.
         * The range is defined by [rangeMin] and [rangeMax].
         */
        val ImageKolorInput.range: ClosedFloatingPointRange<Float>
            get() = rangeMin..rangeMax

        /**
         * Returns an appropriate [ImageVector] icon for the given [ImageKolorInput] type.
         *
         * This function maps each specific subclass of `ImageKolorInput` to a corresponding
         * Material Design icon, typically used to represent the adjustment in a user interface.
         * The icon will be filled if `isSelected` is true, and outlined otherwise.
         *
         * @param isSelected A boolean indicating whether the icon should be in a selected (filled)
         * state.
         * @return The [ImageVector] associated with this `ImageKolorInput` type and selection
         * state.
         */
        fun ImageKolorInput.getIcon(isSelected: Boolean): ImageVector {

            return isSelected.takeIf { it }?.let {

                when (this) {

                    is Brightness -> Icons.Filled.Brightness6
                    is Exposure -> Icons.Filled.Exposure
                    is Contrast -> Icons.Filled.Contrast
                    is Saturation -> Icons.Filled.Colorize
                    is Warmth -> Icons.Filled.Thermostat
                    is Tint -> Icons.Filled.InvertColors
                    is Highlights -> Icons.Filled.Animation
                    is Shadows -> Icons.Filled.BlurLinear
                }
            } ?: when (this) {

                is Brightness -> Icons.Outlined.Brightness6
                is Exposure -> Icons.Outlined.Exposure
                is Contrast -> Icons.Outlined.Contrast
                is Saturation -> Icons.Outlined.Colorize
                is Warmth -> Icons.Outlined.Thermostat
                is Tint -> Icons.Outlined.InvertColors
                is Highlights -> Icons.Outlined.Animation
                is Shadows -> Icons.Outlined.BlurLinear
            }
        }

        /**
         * Returns the current value of the ImageKolorInput.
         *
         * This function provides a convenient way to access the underlying float value
         * of any specific `ImageKolorInput` type without needing to explicitly cast it.
         *
         * @return The float value associated with this `ImageKolorInput`.
         */
        fun ImageKolorInput.getValue(): Float {

            return when (val kolorInput = this) {

                is Brightness -> kolorInput.value
                is Exposure -> kolorInput.value
                is Contrast -> kolorInput.value
                is Saturation -> kolorInput.value
                is Warmth -> kolorInput.value
                is Tint -> kolorInput.value
                is Highlights -> kolorInput.value
                is Shadows -> kolorInput.value
            }
        }
    }
}