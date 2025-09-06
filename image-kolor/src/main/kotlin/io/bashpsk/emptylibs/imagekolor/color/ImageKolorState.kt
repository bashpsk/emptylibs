package io.bashpsk.emptylibs.imagekolor.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import io.bashpsk.emptylibs.imagekolor.color.ImageKolorInput.Companion.getValue
import io.bashpsk.emptylibs.imagekolor.filter.getKolorFilterBitmap
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.pow

/**
 * Remembers and creates an [ImageKolorState] instance that can survive configuration changes.
 *
 * This composable function is used to create and manage the state for image color adjustments.
 * It takes an [ImageBitmap] as input.
 * The state is remembered across recompositions and configuration changes.
 *
 * @param imageBitmap The [ImageBitmap] to apply color adjustments to. Can be null if no image is
 * loaded.
 * @return An [ImageKolorState] instance that holds the current color adjustment values and provides
 * methods to modify them.
 */
@Composable
fun rememberImageKolorState(imageBitmap: ImageBitmap?): ImageKolorState {

    return rememberSaveable(
        imageBitmap,
        saver = ImageKolorState.StateSaver(imageBitmap = imageBitmap)
    ) {
        ImageKolorState(imageBitmap = imageBitmap)
    }
}

/**
 * State object that can be used to control the color adjustments applied to an image.
 * This state is typically remembered using [rememberImageKolorState].
 *
 * @param imageBitmap The [ImageBitmap] to apply color adjustments to. Can be null if no image is
 * loaded yet.
 */
@Stable
class ImageKolorState(val imageBitmap: ImageBitmap?) {
    
    /**
     * A list of [ImageKolorInput] objects representing the current color adjustment settings.
     *
     * This property holds a list of different color adjustment types (e.g., brightness, contrast,
     * saturation) and their corresponding values. It is a mutable state, meaning that changes to
     * this list will trigger recomposition if observed by a Composable function.
     *
     * The list is initialized with [ImageKolorInput.AllTypes], which provides a default set of
     * all available color adjustments with their initial neutral values.
     *
     * This list is used internally by the [ImageKolorState] to:
     * - Store the current value for each color adjustment.
     * - Retrieve values when calculating the final [ColorMatrix] or [ColorFilter].
     * - Update individual adjustment values via the [updateValues] function.
     * - Reset all adjustments to their defaults via the [resetAllValues] function.
     *
     * @see ImageKolorInput
     * @see ImageKolorInput.AllTypes
     * @see updateValues
     * @see resetAllValues
     * @see getColorMatrix
     */
    internal var imageKolorInputList by mutableStateOf(ImageKolorInput.AllTypes)

    /**
     * Represents the currently selected color input type for adjustment.
     *
     * This property holds an instance of [ImageKolorInput] (e.g., Brightness, Contrast)
     * that the user is currently interacting with or that is targeted for updates.
     * It is initialized with the first element of [imageKolorInputList].
     * Changes to this property will trigger recomposition if observed in a Composable.
     */
    internal var currentKolorInput by mutableStateOf(imageKolorInputList.first())

    /**
     * Updates the value of the currently selected [ImageKolorInput] type.
     *
     * This function iterates through the [imageKolorInputList] and updates the
     * [ImageKolorInput] instance that matches the type of the [currentKolorInput].
     * The `value` property of the matching input is set to the provided [newValue].
     *
     * After updating the specific input, the [currentKolorInput] is also updated
     * to reflect this new input state. The entire [imageKolorInputList] is then
     * replaced with a new persistent list containing the updated input.
     *
     * @param newValue The new float value to set for the current color input.
     */
    fun updateValues(newValue: Float) {

        imageKolorInputList = imageKolorInputList.map { kolorInput ->

            currentKolorInput.takeIf { input -> kolorInput::class == input::class }?.run {

                val newInput = when (val input = kolorInput) {

                    is ImageKolorInput.Brightness -> input.copy(value = newValue)
                    is ImageKolorInput. Exposure ->input.copy(value = newValue)
                    is ImageKolorInput. Contrast -> input.copy(value = newValue)
                    is ImageKolorInput. Saturation -> input.copy(value = newValue)
                    is ImageKolorInput. Warmth -> input.copy(value = newValue)
                    is ImageKolorInput. Tint -> input.copy(value = newValue)
                    is ImageKolorInput. Highlights -> input.copy(value = newValue)
                    is ImageKolorInput. Shadows -> input.copy(value = newValue)
                }

                currentKolorInput = newInput
                newInput
            } ?: kolorInput
        }.toPersistentList()
    }

    /**
     * Resets all color adjustment values to their default states.
     * This function will revert brightness, exposure, contrast, highlights, shadows, saturation,
     * warmth, and tint to their initial, neutral values.
     */
    fun resetAllValues() {

        imageKolorInputList = ImageKolorInput.AllTypes
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

        val brightness = imageKolorInputList.find { kolorInput ->

            kolorInput is ImageKolorInput.Brightness
        }?.getValue() ?: ImageKolorInput.Brightness().value

        val exposure = imageKolorInputList.find { kolorInput ->

            kolorInput is ImageKolorInput.Exposure
        }?.getValue() ?: ImageKolorInput.Exposure().value

        val contrast = imageKolorInputList.find { kolorInput ->

            kolorInput is ImageKolorInput.Contrast
        }?.getValue() ?: ImageKolorInput.Contrast().value

        val saturation = imageKolorInputList.find { kolorInput ->

            kolorInput is ImageKolorInput.Saturation
        }?.getValue() ?: ImageKolorInput.Saturation().value

        val warmth = imageKolorInputList.find { kolorInput ->

            kolorInput is ImageKolorInput.Warmth
        }?.getValue() ?: ImageKolorInput.Warmth().value

        val tint = imageKolorInputList.find { kolorInput ->

            kolorInput is ImageKolorInput.Tint
        }?.getValue() ?: ImageKolorInput.Tint().value

        val highlights = imageKolorInputList.find { kolorInput ->

            kolorInput is ImageKolorInput.Highlights
        }?.getValue() ?: ImageKolorInput.Highlights().value

        val shadows = imageKolorInputList.find { kolorInput ->

            kolorInput is ImageKolorInput.Shadows
        }?.getValue() ?: ImageKolorInput.Shadows().value

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

        private const val KEY_CURRENT_INPUT = "IMAGE-KOLOR-CURRENT-INPUT"
        private const val KEY_INPUT_LIST = "IMAGE-KOLOR-INPUT-LIST"

        @Suppress("UNCHECKED_CAST")
        fun StateSaver(imageBitmap: ImageBitmap?): Saver<ImageKolorState, Any> = mapSaver(
            save = { state ->

                mapOf(
                    KEY_INPUT_LIST to state.imageKolorInputList.toTypedArray(),
                    KEY_CURRENT_INPUT to state.currentKolorInput,
                )
            },
            restore = { elements ->

                ImageKolorState(imageBitmap = imageBitmap).apply {
                    
                    imageKolorInputList = (elements.getOrElse(KEY_INPUT_LIST) {
                        ImageKolorInput.AllTypes.toTypedArray()
                    } as Array<ImageKolorInput>).toPersistentList()

                    currentKolorInput = elements.getOrElse(
                        KEY_CURRENT_INPUT
                    ) { ImageKolorInput.AllTypes.first() } as ImageKolorInput
                }
            }
        )
    }
}