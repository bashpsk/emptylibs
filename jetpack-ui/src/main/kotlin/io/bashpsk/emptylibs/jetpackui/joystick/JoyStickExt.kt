package io.bashpsk.emptylibs.jetpackui.joystick

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
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

    when (properties.type) {

        JoyStickType._01 -> drawJoyStick01(
            colors = colors,
            properties = properties,
            thumbPosition = thumbPosition,
            isPressed = isPressed
        )

        JoyStickType._02 -> drawJoyStick02(
            colors = colors,
            properties = properties,
            thumbPosition = thumbPosition,
            isPressed = isPressed
        )

        JoyStickType._03 -> drawJoyStick03(
            colors = colors,
            properties = properties,
            thumbPosition = thumbPosition,
            isPressed = isPressed
        )
    }
}

/**
 * Draws the default style of the joystick (Type 01).
 *
 * This style consists of a simple circular base with a border and a solid circular thumb
 * that scales when pressed.
 *
 * @param colors The color configuration for the joystick.
 * @param properties The behavioral and sizing properties.
 * @param thumbPosition The current offset of the thumb from the center.
 * @param isPressed Whether the joystick is currently being interacted with.
 */
private fun DrawScope.drawJoyStick01(
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

/**
 * Draws the second variation of the joystick design (_02).
 *
 * This design features:
 * - A base circle with a border.
 * - Four cardinal markers (dots) at the top, bottom, left, and right.
 * - A static background circle indicating the neutral thumb area.
 * - A layered thumb consisting of a semi-transparent outer glow and a solid inner core.
 *
 * @param colors The color configuration for the joystick.
 * @param properties The behavioral and sizing properties.
 * @param thumbPosition The current offset of the thumb from the center.
 * @param isPressed Whether the joystick is currently being interacted with.
 */
private fun DrawScope.drawJoyStick02(
    colors: JoyStickColors,
    properties: JoyStickProperties,
    thumbPosition: Offset,
    isPressed: Boolean
) {

    val radius = size.minDimension / 2F
    val center = Offset(x = radius, y = radius)
    val strokeWidth = properties.borderThickness.toPx()

    val markerRadius = radius * 0.9F
    val markerSize = properties.borderThickness.toPx()

    val thumbRadius = (radius * properties.thumbRatio) - (2 * strokeWidth)
    val thumbCenter = center + thumbPosition

    drawCircle(
        center = center,
        radius = radius,
        color = colors.baseColor
    )

    drawCircle(
        center = center,
        radius = radius,
        style = Stroke(width = strokeWidth),
        color = colors.borderColor
    )

    drawCircle(
        center = center + Offset(x = 0F, y = -markerRadius),
        radius = markerSize,
        color = colors.borderColor
    )

    drawCircle(
        center = center + Offset(x = 0F, y = markerRadius),
        radius = markerSize,
        color = colors.borderColor
    )

    drawCircle(
        center = center + Offset(x = -markerRadius, y = 0F),
        radius = markerSize,
        color = colors.borderColor
    )

    drawCircle(
        center = center + Offset(x = markerRadius, y = 0F),
        radius = markerSize,
        color = colors.borderColor
    )

    drawCircle(
        center = center,
        radius = thumbRadius + strokeWidth,
        color = colors.borderColor.copy(alpha = colors.borderColor.alpha / 1.5F)
    )

    drawCircle(
        center = center,
        radius = thumbRadius + strokeWidth,
        style = Stroke(width = strokeWidth / 2),
        color = colors.borderColor
    )

    drawCircle(
        center = thumbCenter,
        radius = thumbRadius,
        color = colors.thumbColor.copy(alpha = colors.thumbColor.alpha / 2)
    )

    drawCircle(
        center = thumbCenter,
        radius = thumbRadius / 1.45F,
        color = colors.thumbColor
    )
}

/**
 * Draws the third variation of the joystick (Type _03) on a [DrawScope].
 *
 * This implementation features a dashed circular marker path in the background,
 * a stationary decorative ring at the center, and a multi-layered thumb.
 *
 * @param colors The color configuration for the joystick.
 * @param properties The behavioral and sizing properties.
 * @param thumbPosition The current offset of the thumb from the center.
 * @param isPressed Whether the joystick is currently being interacted with.
 */
private fun DrawScope.drawJoyStick03(
    colors: JoyStickColors,
    properties: JoyStickProperties,
    thumbPosition: Offset,
    isPressed: Boolean
) {

    val radius = size.minDimension / 2F
    val center = Offset(x = radius, y = radius)
    val strokeWidth = properties.borderThickness.toPx()

    val markerRadius = radius * 0.9F
    val markerSize = properties.borderThickness.toPx()

    val thumbRadius = (radius * properties.thumbRatio) - (2 * strokeWidth)
    val thumbCenter = center + thumbPosition

    drawCircle(
        center = center,
        radius = radius,
        color = colors.baseColor
    )

    drawCircle(
        center = center,
        radius = radius,
        style = Stroke(width = strokeWidth),
        color = colors.borderColor
    )

    drawCircle(
        center = center,
        radius = markerRadius,
        style = Stroke(
            width = markerSize,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(markerSize, 2 * markerSize), 0F),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        ),
        color = colors.borderColor.copy(alpha = colors.borderColor.alpha / 1.5F)
    )

    drawCircle(
        center = center,
        radius = thumbRadius + strokeWidth,
        color = colors.borderColor.copy(alpha = colors.borderColor.alpha / 1.5F)
    )

    drawCircle(
        center = center,
        radius = thumbRadius + strokeWidth,
        style = Stroke(width = strokeWidth / 2),
        color = colors.borderColor
    )

    drawCircle(
        center = thumbCenter,
        radius = thumbRadius,
        color = colors.thumbColor.copy(alpha = colors.thumbColor.alpha / 2)
    )

    drawCircle(
        center = thumbCenter,
        radius = thumbRadius / 1.45F,
        color = colors.thumbColor
    )
}