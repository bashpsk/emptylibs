package io.bashpsk.emptylibs.jetpackui.joystick

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Represents the colors used by the [JoyStick] component.
 *
 * @property baseColor The background color of the joystick area.
 * @property thumbColor The color of the draggable thumb.
 * @property borderColor The color of the joystick's border outline.
 */
@Immutable
data class JoyStickColors(
    val baseColor: Color = Color.White.copy(alpha = 0.1F),
    val thumbColor: Color = Color.Cyan.copy(alpha = 0.85F),
    val borderColor: Color = Color.Cyan.copy(alpha = 0.85F)
)