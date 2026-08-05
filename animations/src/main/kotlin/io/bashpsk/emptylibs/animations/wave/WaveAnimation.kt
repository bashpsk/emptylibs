package io.bashpsk.emptylibs.animations.wave

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A modifier that draws a wave animation behind the content.
 *
 * @param progress The progress of the wave, from 0.0 to 1.0.
 * @param waveOffset The offset for wave animation.
 * @param waveColor The color of the wave.
 * @param amplitude The amplitude of the wave.
 */
fun Modifier.waveAnimation(
    progress: Float,
    waveOffset: Float,
    waveColor: Color = Color.Green,
    amplitude: Dp = 8.dp
): Modifier = this then WaveAnimationElement(
    progress = progress,
    waveOffset = waveOffset,
    waveColor = waveColor,
    amplitude = amplitude
)