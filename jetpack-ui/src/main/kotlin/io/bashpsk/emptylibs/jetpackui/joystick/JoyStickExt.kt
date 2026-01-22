package io.bashpsk.emptylibs.jetpackui.joystick

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Internal extension to draw the [JoyStick] components on a [DrawScope].
 *
 * @param colors The color configuration for the joystick.
 * @param properties The behavioral and sizing properties.
 * @param thumbPosition The current offset of the thumb from the center.
 * @param isPressed Whether the joystick is currently being interacted with.
 */
internal fun DrawScope.drawJoyStick(
    colors: JoyStickColors,
    properties: JoyStickProperties,
    thumbPosition: Offset,
    isPressed: Boolean
) {

    val radius = size.minDimension / 2F
    val center = Offset(x = radius, y = radius)

    drawCircle(center = center, radius = radius, color = colors.baseColor)

    drawCircle(
        center = center,
        radius = radius,
        style = Stroke(width = properties.borderThickness.toPx()),
        color = colors.borderColor
    )

    val thumbScale = if (isPressed) properties.pressedThumbScale else 1F

    drawCircle(
        center = center + thumbPosition,
        radius = radius * properties.thumbRatio * thumbScale,
        color = colors.thumbColor
    )
}