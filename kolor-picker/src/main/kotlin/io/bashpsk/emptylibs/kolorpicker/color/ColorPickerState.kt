package io.bashpsk.emptylibs.kolorpicker.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Remembers the state of a color picker.
 *
 * @param initialColor The initial color to be selected. Defaults to [Color.Unspecified].
 * @param enableAlphaPanel Whether to enable the alpha panel. Defaults to false.
 * @return A [ColorPickerState] instance that can be used to control the color picker.
 */
@Composable
fun rememberColorPickerState(
    initialColor: Color = Color.Unspecified,
    enableAlphaPanel: Boolean = false
): ColorPickerState {

    return remember(enableAlphaPanel, initialColor) {
        ColorPickerState(initialColor = initialColor, isAlphaPanelEnabled = enableAlphaPanel)
    }
}

/**
 * A state object that can be hoisted to control and observe color picker changes.
 *
 * In most cases, this will be created via [rememberColorPickerState].
 *
 * @param initialColor the initial color to set on the picker
 * @param isAlphaPanelEnabled whether the alpha panel is enabled or not
 */
@Stable
class ColorPickerState(
    val initialColor: Color,
    val isAlphaPanelEnabled: Boolean = false
) {

    /**
     * Represents the currently selected color in the color picker.
     *
     * This property holds the [Color] value that is currently selected by the user.
     * It is initialized with [initialColor] and can be updated through interactions
     * with the color picker UI.
     *
     * The setter for this property is private, meaning it can only be modified internally by the
     * [ColorPickerState] class, typically through methods like [updateColor] or [updateHslA].
     */
    var selectedColor by mutableStateOf(initialColor)
        private set

    /**
     * Hue component of the selected color, in the range [0..360]
     */
    internal var hueValue by mutableFloatStateOf(0F)
        private set

    /**
     * Represents the saturation value (0-1) of the current color, derived from HSL representation.
     * This property is observed by Compose for UI updates.
     */
    internal var saturationValue by mutableFloatStateOf(0F)
        private set

    /**
     * The current lightness value of the color picker, ranging from 0.0 to 1.0.
     * This property is observed by Compose and will trigger recomposition when its value changes.
     * It can only be set internally within the `ColorPickerState` class.
     */
    internal var lightnessValue by mutableFloatStateOf(0F)
        private set

    /**
     * The alpha component of the [selectedColor], ranging from 0.0 to 1.0.
     * This value is updated when [selectedColor] changes or when [updateHslA] is called.
     * It can be observed to react to changes in the alpha of the selected color.
     */
    internal var alphaValue by mutableFloatStateOf(initialColor.alpha)
        private set

    init {

        val hslComponents = initialColor.toHslComponents()

        hueValue = hslComponents[0]
        saturationValue = hslComponents[1]
        lightnessValue = hslComponents[2]
    }

    /**
     * Updates the selected color and its corresponding HSL and alpha components.
     *
     * @param color The new color to set.
     */
    fun updateColor(color: Color) {

        val hslComponents = color.toHslComponents()

        selectedColor = color
        hueValue = hslComponents[0]
        saturationValue = hslComponents[1]
        lightnessValue = hslComponents[2]
        alphaValue = color.alpha
    }

    /**
     * Updates the color picker state based on HSL (Hue, Saturation, Lightness) and Alpha values.
     *
     * @param hue The hue value of the color, ranging from 0 to 360.
     * @param saturation The saturation value of the color, ranging from 0 to 1.
     * @param lightness The lightness value of the color, ranging from 0 to 1.
     * @param alpha The alpha value of the color, ranging from 0 to 1.
     */
    fun updateHslA(hue: Float, saturation: Float, lightness: Float, alpha: Float) {

        hueValue = hue.coerceIn(range = 0F..360F)
        saturationValue = saturation.coerceIn(range = 0F..1F)
        lightnessValue = lightness.coerceIn(range = 0F..1F)
        alphaValue = alpha.coerceIn(range = 0F..1F)
        selectedColor = Color.hsl(hueValue, saturationValue, lightnessValue, alphaValue)
    }
}