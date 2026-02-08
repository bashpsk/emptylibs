package io.bashpsk.emptylibs.kolorpicker.color

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Remembers the state of a color picker.
 *
 * @param initialColor The initial color to be selected. Defaults to [Color.Black].
 * @return A [KolorPickerState] instance that can be used to control the color picker.
 */
@Composable
fun rememberKolorPickerState(initialColor: Color = Color.Black): KolorPickerState {

    return retain(initialColor) { KolorPickerState(initialColor = initialColor) }
}

/**
 * A state object that can be hoisted to control and observe color picker changes.
 *
 * In most cases, this will be created via [rememberKolorPickerState].
 *
 * @param initialColor the initial color to set on the picker
 */
@Stable
class KolorPickerState(val initialColor: Color) {

    /**
     * Represents the currently selected color in the color picker.
     *
     * This property holds the [Color] value that is currently selected by the user.
     * It is initialized with [initialColor] and can be updated through interactions
     * with the color picker UI.
     *
     * The setter for this property is private, meaning it can only be modified internally by the
     * [KolorPickerState] class, typically through methods like [updateColor] or [updateHslA].
     */
    var selectedColor by mutableStateOf(initialColor)
        private set

    /**
     * A boolean state that controls the visibility of the color picker dialog.
     *
     * When set to `true`, the dialog is displayed to the user. When set to `false`,
     * the dialog is hidden. This state is observed by the UI to trigger the
     * presentation or dismissal of the color picker interface.
     */
    val dialogVisible = MutableTransitionState(false)

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
     * It can only be set internally within the `KolorPickerState` class.
     */
    internal var lightnessValue by mutableFloatStateOf(0F)
        private set

    /**
     * The alpha component of the [selectedColor], ranging from 0.0 to 1.0.
     * This value is updated when [selectedColor] changes or when [updateHslA] is called.
     * It can be observed to react to changes in the alpha of the selected color.
     */
    internal var alphaValue by mutableFloatStateOf(0F)
        private set

    init {

        updateColor(color = initialColor)
    }

    /**
     * Updates the selected color and its corresponding HSL and alpha components.
     *
     * @param color The new color to set.
     */
    fun updateColor(color: Color) {

        val hslComponents = color.toHslComponents()

        selectedColor = color
        alphaValue = color.alpha
        hslComponents.getOrNull(0)?.let { value -> hueValue = value }
        hslComponents.getOrNull(1)?.let { value -> saturationValue = value }
        hslComponents.getOrNull(2)?.let { value -> lightnessValue = value }
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