package io.bashpsk.emptylibs.jetpackui.joystick

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

/**
 * Represents the current state and movement data of the joystick.
 *
 * @property input The normalized input vector where x and y are in range [-1, 1].
 * @property displacement The normalized distance from the center in range [0, 1].
 * @property angle The angle of the joystick in degrees, from 0 to 360.
 * @property motion The cumulative movement vector calculated based on the input and speed.
 * @property rotation The rotation angle in degrees, useful for facing directions.
 */
@Immutable
data class JoyStickChanges(
    val input: Offset = Offset.Zero,
    val displacement: Float = 0F,
    val angle: Float = 0F,
    val motion: Offset = Offset.Zero,
    val rotation: Float = 0F
)