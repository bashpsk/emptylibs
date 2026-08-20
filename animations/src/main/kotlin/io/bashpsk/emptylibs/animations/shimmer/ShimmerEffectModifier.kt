package io.bashpsk.emptylibs.animations.shimmer

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

/**
 * Applies a shimmer effect to the modifier.
 *
 * @param properties The properties to configure the shimmer effect.
 */
@Stable
fun Modifier.shimmerEffect(
    properties: ShimmerEffectProperties = ShimmerEffectDefault.properties()
): Modifier {

    return this then ShimmerEffectElement(properties = properties)
}