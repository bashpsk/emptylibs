package io.bashpsk.emptylibs.jetpackui.joystick

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Contains the default values used by the [JoyStick].
 */
object JoyStickDefaults {

    /**
     * Creates a [JoyStickColors] with default values.
     *
     * @param baseColor The background color of the joystick area.
     * @param thumbColor The color of the draggable thumb.
     * @param borderColor The color of the joystick's border outline.
     * @return A [JoyStickColors] instance.
     */
    @Composable
    fun colors(
        baseColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.1F),
        thumbColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85F),
        borderColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85F)
    ): JoyStickColors {

        return JoyStickColors(
            baseColor = baseColor,
            thumbColor = thumbColor,
            borderColor = borderColor
        )
    }

    /**
     * Creates a [JoyStickProperties] with default values.
     *
     * @param thumbRatio The size ratio of the thumb relative to the joystick base radius.
     * @param pressedThumbScale The scale factor applied to the thumb when it is pressed.
     * @param speed The movement speed applied per frame.
     * @param borderThickness The thickness of the joystick base border.
     * @param faceToDirection If true, rotation will follow the joystick angle.
     * @param type The visual style of the joystick.
     * @return A [JoyStickProperties] instance.
     */
    @Stable
    fun properties(
        thumbRatio: Float = 0.5F,
        pressedThumbScale: Float = 1.25F,
        speed: Dp = 0.5.dp,
        borderThickness: Dp = 2.dp,
        faceToDirection: Boolean = false,
        type: JoyStickType = JoyStickType._01
    ): JoyStickProperties {

        return JoyStickProperties(
            thumbRatio = thumbRatio,
            pressedThumbScale = pressedThumbScale,
            speed = speed,
            borderThickness = borderThickness,
            faceToDirection = faceToDirection,
            type = type
        )
    }
}