package io.bashpsk.emptylibs.imagekrop.modifier

import androidx.compose.ui.Modifier
import io.bashpsk.emptylibs.imagekrop.crop.ImageKropState

internal fun Modifier.imageKropModifier(state: ImageKropState): Modifier {

    return this then ImageKropElement(state = state)
}