package io.bashpsk.emptylibs.kolorpicker.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

internal fun Modifier.saturationLightnessGestures(
    onSelectionChanged: (saturation: Float, lightness: Float) -> Unit
): Modifier {

    return this then SaturationLightnessGesturesElement(onSelectionChanged = onSelectionChanged)
}

internal fun Modifier.hueGestures(
    thumbRadiusPx: Float,
    onHueChanged: (hue: Float) -> Unit
): Modifier {

    return this then HueGesturesElement(thumbRadiusPx = thumbRadiusPx, onHueChanged = onHueChanged)
}

internal fun Modifier.alphaGestures(
    thumbRadiusPx: Float,
    onAlphaChanged: (alpha: Float) -> Unit
): Modifier {

    return this then AlphaGesturesElement(
        thumbRadiusPx = thumbRadiusPx,
        onAlphaChanged = onAlphaChanged
    )
}

internal fun Modifier.imageKolorPickerGestures(
    onColorSelection: (position: Offset) -> Unit
): Modifier {

    return this then ImageKolorPickerGesturesElement(onColorSelection = onColorSelection)
}