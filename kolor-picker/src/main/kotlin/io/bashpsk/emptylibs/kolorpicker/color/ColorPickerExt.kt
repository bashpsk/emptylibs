package io.bashpsk.emptylibs.kolorpicker.color

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

/**
 * Converts a [Color] to its HSL (Hue, Saturation, Lightness) components.
 *
 * This function takes a [Color] object and calculates its corresponding HSL values.
 * The HSL color model is an alternative representation of the RGB color model,
 * designed to be more intuitive for human perception.
 *
 * - **Hue:** Represents the type of color (e.g., red, green, blue). It's an angle
 *   on the color wheel, typically ranging from 0 to 360 degrees.
 * - **Saturation:** Represents the intensity or purity of the color. A value of 0%
 *   means a shade of gray, while 100% is the full color.
 * - **Lightness (or Luminance):** Represents the brightness of the color. A value of 0%
 *   is black, 100% is white, and 50% is the "normal" color.
 *
 * The calculation involves finding the maximum and minimum RGB components to determine
 * the lightness and the difference between them for saturation and hue.
 *
 * @return A [FloatArray] containing the HSL components in the order:
 *         `[hue (0-360), saturation (0-1), lightness (0-1)]`.
 *         If the color is achromatic (a shade of gray, where red, green, and blue
 *         are equal), the hue will be 0.
 */
internal fun Color.toHslComponents(): FloatArray {

    val maxColorComponent = maxOf(red, green, blue)
    val minColorComponent = minOf(red, green, blue)
    val colorComponentDifference = maxColorComponent - minColorComponent

    var hue = 0F
    val saturation: Float
    val lightness = (maxColorComponent + minColorComponent) / 2F

    when (colorComponentDifference) {

        0F -> saturation = 0F

        else -> {

            saturation = when {

                lightness > 0.5F -> {

                    colorComponentDifference / (2F - maxColorComponent - minColorComponent)
                }

                else -> {

                    colorComponentDifference / (maxColorComponent + minColorComponent)
                }
            }

            hue = when (maxColorComponent) {

                red -> (green - blue) / colorComponentDifference + (if (green < blue) 6F else 0F)
                green -> (blue - red) / colorComponentDifference + 2F
                else -> (red - green) / colorComponentDifference + 4F
            }

            hue /= 6F
        }
    }

    return floatArrayOf(hue * 360F, saturation, lightness)
}

/**
 * Draws a drag handle on the canvas.
 *
 * @param position The center position of the drag handle.
 * @param radius The radius of the outer circle of the drag handle.
 * @param color The color of the drag handle.
 * @param width The width of the stroke for the outer circle and the radius of the inner circle.
 */
internal fun DrawScope.drawDragHandle(position: Offset, radius: Dp, color: Color, width: Dp) {

    val stroke = Stroke(width = width.toPx())

    drawCircle(center = position, radius = radius.toPx(), style = stroke, color = color)
    drawCircle(center = position, radius = width.toPx(), color = color)
}