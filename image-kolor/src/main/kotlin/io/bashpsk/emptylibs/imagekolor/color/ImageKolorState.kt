package io.bashpsk.emptylibs.imagekolor.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import io.bashpsk.emptylibs.imagekolor.filter.getKolorFilterBitmap
import kotlin.math.pow

/**
 * Remembers and creates an [ImageKolorState] instance that can survive configuration changes.
 *
 * This composable function is used to create and manage the state for image color adjustments.
 * It takes an [ImageBitmap] and an optional [ImageKolorConfig] as input.
 * The state is remembered across recompositions and configuration changes.
 *
 * @param imageBitmap The [ImageBitmap] to apply color adjustments to. Can be null if no image is
 * loaded.
 * @param config The [ImageKolorConfig] to use for configuring the color adjustments. Defaults to a
 * default [ImageKolorConfig].
 * @return An [ImageKolorState] instance that holds the current color adjustment values and provides
 * methods to modify them.
 */
@Composable
fun rememberImageKolorState(
    imageBitmap: ImageBitmap?,
    config: ImageKolorConfig = ImageKolorConfig()
): ImageKolorState {

    return rememberSaveable(imageBitmap, config, saver = ImageKolorState.StateSaver) {
        ImageKolorState(imageBitmap = imageBitmap, config = config)
    }
}

/**
 * State object that can be used to control the color adjustments applied to an image.
 * This state is typically remembered using [rememberImageKolorState].
 *
 * @param imageBitmap The [ImageBitmap] to apply color adjustments to. Can be null if no image is
 * loaded yet.
 * @param config The [ImageKolorConfig] that defines the behavior and appearance of the color
 * adjustment controls.
 *
 * @property brightness Controls the overall lightness or darkness of the image.
 * Value typically ranges from -1F (darker) to 1F (lighter), with 0F being the original brightness.
 * @property exposure Controls the exposure level of the image, simulating changes in camera
 * exposure.
 * Value typically ranges from -1F (underexposed) to 1F (overexposed), with 0F being the original
 * exposure.
 * @property contrast Controls the difference between light and dark areas of the image.
 * Value typically ranges from 0F (no contrast) to 2F (high contrast), with 1F being the original
 * contrast.
 * @property highlights Adjusts the brightness of the brightest areas of the image.
 * Value typically ranges from -1F (darker highlights) to 1F (brighter highlights), with 0F being no
 * change.
 * @property shadows Adjusts the brightness of the darkest areas of the image.
 * Value typically ranges from -1F (darker shadows) to 1F (brighter shadows), with 0F being no
 * change.
 * @property saturation Controls the intensity of colors in the image.
 * Value typically ranges from 0F (grayscale) to 2F (highly saturated), with 1F being the original
 * saturation.
 * @property warmth Adjusts the color temperature of the image, making it appear warmer
 * (more orange) or cooler (more blue).
 * Value typically ranges from -1F (cooler) to 1F (warmer), with 0F being the original warmth.
 * @property tint Adjusts the green-magenta balance of the image.
 * Value typically ranges from -1F (more green) to 1F (more magenta), with 0F being the original
 * tint.
 */
@Stable
class ImageKolorState(val imageBitmap: ImageBitmap?, val config: ImageKolorConfig) {

    /**
     * Controls the overall lightness or darkness of the image.
     * The value typically ranges from -1F (darker) to 1F (lighter), with 0F representing the
     * original brightness.
     * This property is internally mutable and can be updated to reflect changes in brightness.
     */
    var brightness by mutableFloatStateOf(0F)
        internal set

    /**
     * Controls the exposure level of the image, simulating changes in camera exposure.
     * Value typically ranges from -1F (underexposed) to 1F (overexposed), with 0F being the
     * original exposure.
     */
    var exposure by mutableFloatStateOf(0F)
        internal set

    /**
     * Controls the difference between light and dark areas of the image.
     * Value typically ranges from 0F (no contrast) to 2F (high contrast), with 1F being the
     * original contrast.
     */
    var contrast by mutableFloatStateOf(1F)
        internal set

    /**
     * Adjusts the brightness of the brightest areas of the image.
     * Value typically ranges from -1F (darker highlights) to 1F (brighter highlights), with 0F
     * being no change.
     */
    var highlights by mutableFloatStateOf(0F)
        internal set

    /**
     * Adjusts the brightness of the darkest areas of the image.
     * Value typically ranges from -1F (darker shadows) to 1F (brighter shadows), with 0F being no
     * change.
     */
    var shadows by mutableFloatStateOf(0F)
        internal set

    /**
     * Controls the intensity of colors in the image.
     * A value of 0F results in a grayscale image.
     * A value of 1F represents the original saturation.
     * Values greater than 1F increase saturation, making colors more vivid.
     * Values between 0F and 1F decrease saturation, making colors more muted.
     * The typical range is from 0F (grayscale) to 2F (highly saturated).
     */
    var saturation by mutableFloatStateOf(1F)
        internal set

    /**
     * Adjusts the color temperature of the image, making it appear warmer (more orange) or cooler
     * (more blue).
     * Value typically ranges from -1F (cooler) to 1F (warmer), with 0F being the original warmth.
     */
    var warmth by mutableFloatStateOf(0F)
        internal set

    /**
     * Adjusts the green-magenta balance of the image.
     * Value typically ranges from -1F (more green) to 1F (more magenta), with 0F being the
     * original tint.
     */
    var tint by mutableFloatStateOf(0F)
        internal set

    /**
     * Resets all color adjustment values to their default states.
     * This function will revert brightness, exposure, contrast, highlights, shadows, saturation,
     * warmth, and tint to their initial, neutral values.
     */
    fun resetAllValues() {

        brightness = 0F
        exposure = 0F
        contrast = 1F
        highlights = 0F
        shadows = 0F
        saturation = 1F
        warmth = 0F
        tint = 0F
    }

    /**
     * Calculates and returns a [ColorMatrix] based on the current color adjustment values.
     *
     * This function combines the effects of brightness, exposure, contrast, saturation, warmth,
     * tint, highlights, and shadows into a single [ColorMatrix]. This matrix can then be applied
     * to an image to achieve the desired color adjustments.
     *
     * The order of operations is:
     * 1. Brightness
     * 2. Exposure
     * 3. Contrast
     * 4. Saturation
     * 5. Warmth
     * 6. Tint
     * 7. Highlights (if highlights is not 0)
     * 8. Shadows (if shadows is not 0)
     *
     * Each adjustment is represented by a separate [ColorMatrix], and these matrices are
     * multiplied together to produce the final combined matrix.
     *
     * @return A [ColorMatrix] representing the combined effect of all current color adjustments.
     */
    fun getColorMatrix(): ColorMatrix {

        val finalMatrix = ColorMatrix()

        finalMatrix.timesAssign(getBrightnessMatrix(brightness))
        finalMatrix.timesAssign(getExposureMatrix(exposure))
        finalMatrix.timesAssign(getContrastMatrix(contrast))
        finalMatrix.timesAssign(getSaturationMatrix(saturation))
        finalMatrix.timesAssign(getWarmthMatrix(warmth))
        finalMatrix.timesAssign(getTintMatrix(tint))

        if (highlights != 0F) {

            val highlightBrightnessShift = highlights * 20F

            val matrixArray = floatArrayOf(
                1F, 0F, 0F, 0F, highlightBrightnessShift,
                0F, 1F, 0F, 0F, highlightBrightnessShift,
                0F, 0F, 1F, 0F, highlightBrightnessShift,
                0F, 0F, 0F, 1F, 0F
            )

            val newColorMatrix = ColorMatrix(matrixArray)

            finalMatrix.timesAssign(newColorMatrix)
        }

        if (shadows != 0F) {

            val shadowBrightnessShift = shadows * 20F

            val matrixArray = floatArrayOf(
                1F, 0F, 0F, 0F, shadowBrightnessShift,
                0F, 1F, 0F, 0F, shadowBrightnessShift,
                0F, 0F, 1F, 0F, shadowBrightnessShift,
                0F, 0F, 0F, 1F, 0F
            )

            val newColorMatrix = ColorMatrix(matrixArray)

            finalMatrix.timesAssign(newColorMatrix)
        }

        return finalMatrix
    }

    /**
     * Creates a [ColorFilter] based on the current color adjustment values.
     *
     * This function combines all the individual color adjustment matrices (brightness, exposure,
     * contrast, etc.) into a single [ColorMatrix] and then creates a [ColorFilter] from it.
     * This [ColorFilter] can be applied to an image to render it with the adjusted colors.
     *
     * @return A [ColorFilter] instance representing the combined color adjustments.
     */
    fun getColorFilter(): ColorFilter {

        return ColorFilter.colorMatrix(colorMatrix = getColorMatrix())
    }

    /**
     * Applies the current color adjustments to the [imageBitmap] and returns the resulting
     * [ImageBitmap].
     *
     * This function first retrieves the [ColorFilter] based on the current state of color
     * adjustments using [getColorFilter]. It then applies this filter to the original
     * [imageBitmap] using the `getKolorFilterBitmap` extension function.
     *
     * @return A new [ImageBitmap] with the color adjustments applied, or `null` if the
     * original [imageBitmap] is `null`.
     */
    fun getColorImage(): ImageBitmap? {

        return imageBitmap?.getKolorFilterBitmap(filter = getColorFilter())
    }

    /**
     * Creates a [ColorMatrix] for adjusting the brightness of an image.
     *
     * This function takes a float value representing the desired brightness adjustment and returns
     * a [ColorMatrix] that can be applied to an image to achieve that effect.
     * The brightness adjustment is applied by adding the `brightnessValue` to the R, G, and B
     * channels.
     *
     * @param value The brightness adjustment value. Typically ranges from -1F (darker) to 1F
     * (lighter).
     * A value of 0F results in no change to the brightness.
     * @return A [ColorMatrix] that will adjust the brightness of an image when applied.
     */
    private fun getBrightnessMatrix(value: Float): ColorMatrix {

        val brightnessValue = value * 100F

        val matrixArray = floatArrayOf(
            1F, 0F, 0F, 0F, brightnessValue,
            0F, 1F, 0F, 0F, brightnessValue,
            0F, 0F, 1F, 0F, brightnessValue,
            0F, 0F, 0F, 1F, 0F
        )

        return ColorMatrix(matrixArray)
    }

    /**
     * Creates a [ColorMatrix] for adjusting the exposure of an image.
     *
     * The exposure adjustment is achieved by scaling the R, G, and B color channels.
     * The scaling factor is calculated as 2 raised to the power of the input [value].
     *
     * @param value The exposure adjustment value. Typically ranges from -1F (underexposed)
     * to 1F (overexposed), with 0F representing no change in exposure.
     * @return A [ColorMatrix] that can be applied to an image to adjust its exposure.
     */
    private fun getExposureMatrix(value: Float): ColorMatrix {

        val scale = 2.0.pow(value.toDouble()).toFloat()

        val matrixArray = floatArrayOf(
            scale, 0F, 0F, 0F, 0F,
            0F, scale, 0F, 0F, 0F,
            0F, 0F, scale, 0F, 0F,
            0F, 0F, 0F, 1F, 0F
        )

        return ColorMatrix(matrixArray)
    }

    /**
     * Creates a [ColorMatrix] for adjusting the contrast of an image.
     *
     * The contrast adjustment is achieved by scaling the color values and then translating them.
     * A `value` of 1F represents the original contrast. Values less than 1F reduce contrast,
     * while values greater than 1F increase contrast.
     *
     * The formula for the translation is `(-0.5F * value + 0.5F) * 255F`. This centers the
     * contrast adjustment around the mid-gray point (128).
     *
     * @param value The contrast adjustment factor. Typically ranges from 0F (no contrast) to
     * 2F (high contrast).
     * @return A [ColorMatrix] that applies the specified contrast adjustment.
     */
    private fun getContrastMatrix(value: Float): ColorMatrix {

        val translate = (-0.5F * value + 0.5F) * 255F

        val matrixArray = floatArrayOf(
            value, 0F, 0F, 0F, translate,
            0F, value, 0F, 0F, translate,
            0F, 0F, value, 0F, translate,
            0F, 0F, 0F, 1F, 0F
        )

        return ColorMatrix(matrixArray)
    }

    /**
     * Creates a [ColorMatrix] for adjusting the saturation of an image.
     *
     * This function takes a saturation [value] as input and returns a [ColorMatrix] that can be
     * applied to an image to change its saturation.
     *
     * @param value The saturation adjustment value.
     *  - 0F represents grayscale (no color).
     *  - 1F represents the original saturation.
     *  - Values greater than 1F increase saturation.
     *  - Values between 0F and 1F decrease saturation.
     * @return A [ColorMatrix] configured to adjust saturation.
     */
    private fun getSaturationMatrix(value: Float): ColorMatrix {

        return ColorMatrix().apply {

            setToSaturation(value)
        }
    }

    /**
     * Creates a [ColorMatrix] to adjust the warmth of the image.
     *
     * This function modifies the red and blue channels to make the image appear warmer
     * (more orange) or cooler (more blue).
     *
     * @param value The warmth adjustment value. Positive values increase warmth, negative values
     * decrease it.
     * @return A [ColorMatrix] that applies the warmth adjustment.
     */
    private fun getWarmthMatrix(value: Float): ColorMatrix {

        val warmFactor = value * 0.2F

        val matrixArray = floatArrayOf(
            1F + warmFactor, 0F, 0F, 0F, 0F,
            0F, 1F, 0F, 0F, 0F,
            0F, 0F, 1F - warmFactor, 0F, 0F,
            0F, 0F, 0F, 1F, 0F
        )

        return ColorMatrix(matrixArray)
    }

    /**
     * Creates a [ColorMatrix] to adjust the tint of an image.
     *
     * This function generates a color matrix that modifies the green-magenta balance of the image.
     * A positive [value] increases magenta, while a negative value increases green.
     *
     * The `tintFactor` is calculated by multiplying the input [value] by `0.15F`.
     * This factor is then used to adjust the red and blue channels (increasing them for positive
     * tint, effectively adding magenta) and the green channel (decreasing it for positive tint).
     *
     * @param value The tint adjustment value. Typically ranges from -1F (more green) to 1F
     * (more magenta).
     * @return A [ColorMatrix] that applies the specified tint adjustment.
     */
    private fun getTintMatrix(value: Float): ColorMatrix {

        val tintFactor = value * 0.15F

        val matrixArray = floatArrayOf(
            1F + tintFactor, 0F, 0F, 0F, 0F,
            0F, 1F - tintFactor, 0F, 0F, 0F,
            0F, 0F, 1F + tintFactor, 0F, 0F,
            0F, 0F, 0F, 1F, 0F
        )

        return ColorMatrix(matrixArray)
    }

    companion object {

        val StateSaver: Saver<ImageKolorState, List<Any?>> = Saver(
            save = { state ->

                listOf(
                    state.imageBitmap,
                    state.config,
                    state.brightness,
                    state.exposure,
                    state.contrast,
                    state.highlights,
                    state.shadows,
                    state.saturation,
                    state.warmth,
                    state.tint
                )
            },
            restore = { elements ->

                val savedImageBitmap = elements.getOrNull(0) as? ImageBitmap
                val savedConfig = elements.getOrNull(1) as? ImageKolorConfig ?: ImageKolorConfig()

                val savedBrightness = elements.getOrNull(2) as? Float ?: 0.0F
                val savedExposure = elements.getOrNull(3) as? Float ?: 0.0F
                val savedContrast = elements.getOrNull(4) as? Float ?: 1.0F
                val savedHighlights = elements.getOrNull(5) as? Float ?: 0.0F
                val savedShadows = elements.getOrNull(6) as? Float ?: 0.0F
                val savedSaturation = elements.getOrNull(7) as? Float ?: 1.0F
                val savedWarmth = elements.getOrNull(8) as? Float ?: 0.0F
                val savedTint = elements.getOrNull(9) as? Float ?: 0.0F

                ImageKolorState(
                    imageBitmap = savedImageBitmap,
                    config = savedConfig
                ).apply {

                    brightness = savedBrightness
                    exposure = savedExposure
                    contrast = savedContrast
                    highlights = savedHighlights
                    shadows = savedShadows
                    saturation = savedSaturation
                    warmth = savedWarmth
                    tint = savedTint
                }
            }
        )
    }
}