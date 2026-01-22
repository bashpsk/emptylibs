package io.bashpsk.emptylibs.jetpackui.joystick

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration properties for the [JoyStick] component.
 *
 * @property thumbRatio The size ratio of the thumb relative to the joystick base
 * radius(0.0 to 1.0).
 * @property pressedThumbScale The scale factor applied to the thumb when it is pressed.
 * @property speed The movement speed applied to [JoyStickChanges.motion] per frame.
 * @property borderThickness The thickness of the joystick base border.
 * @property faceToDirection If true, [JoyStickChanges.rotation] will follow the joystick angle.
 */
@Immutable
data class JoyStickProperties(
    @param:FloatRange(0.0, 1.0)
    val thumbRatio: Float = 0.5F,
    @param:FloatRange(1.0, 2.0)
    val pressedThumbScale: Float = 1.25F,
    val speed: Dp = 0.5.dp,
    val borderThickness: Dp = 2.dp,
    val faceToDirection: Boolean = false
)