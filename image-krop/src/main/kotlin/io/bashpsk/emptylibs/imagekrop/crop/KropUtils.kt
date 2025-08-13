package io.bashpsk.emptylibs.imagekrop.crop

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

/**
 * Converts a Dp value to its equivalent pixel value using the current local density.
 *
 * This Composable function is intended to be used within a Composable context where
 * `LocalDensity` is available.
 *
 * @return The pixel value as a Float.
 */
@Composable
internal fun Dp.toPixel(): Float {

    return with(receiver = LocalDensity.current) { this@toPixel.toPx() }
}

/**
 * Converts a Dp value to its equivalent pixel value using the provided Density.
 *
 * @param density The Density to use for the conversion.
 * @return The pixel value as a Float.
 */
internal fun Dp.toPixel(density: Density): Float {

    return with(receiver = density) { this@toPixel.toPx() }
}