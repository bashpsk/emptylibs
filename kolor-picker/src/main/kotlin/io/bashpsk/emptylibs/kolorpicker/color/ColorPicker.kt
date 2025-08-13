package io.bashpsk.emptylibs.kolorpicker.color

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.formatter.format.EmptyFormat

/**
 * A composable function that provides a color picker interface.
 *
 * It allows users to select a color by manipulating its hue, saturation, lightness, and alpha
 * components. The selected color is displayed in a preview area, along with its HEX and ARGB
 * representations.
 *
 * @param modifier The modifier to be applied to the ColorPicker.
 * @param state The state object that holds the current color selection and configuration.
 *              Defaults to a new `rememberColorPickerState()` if not provided.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    state: ColorPickerState = rememberColorPickerState()
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
    ) {

        SaturationLightnessPanel(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1.0F),
            hueValue = state.hueValue,
            saturationValue = state.saturationValue,
            lightnessValue = state.lightnessValue,
            onSelectionChanged = { saturation, lightness ->

                state.updateHslA(
                    hue = state.hueValue,
                    saturation = saturation,
                    lightness = lightness,
                    alpha = state.alphaValue
                )
            }
        )

        HuePanel(
            modifier = Modifier.fillMaxWidth(),
            currentHue = state.hueValue,
            onHueChanged = { newHue ->

                state.updateHslA(
                    hue = newHue,
                    saturation = state.saturationValue,
                    lightness = state.lightnessValue,
                    alpha = state.alphaValue
                )
            }
        )

        if (state.isAlphaPanelEnabled) {

            AlphaPanel(
                modifier = Modifier.fillMaxWidth(),
                currentAlpha = state.alphaValue,
                baseColor = Color.hsl(
                    hue = state.hueValue,
                    saturation = state.saturationValue,
                    lightness = state.lightnessValue
                ),
                onAlphaChanged = { newAlpha ->

                    state.updateHslA(
                        hue = state.hueValue,
                        saturation = state.saturationValue,
                        lightness = state.lightnessValue,
                        alpha = newAlpha
                    )
                }
            )
        }

        ColorPreview(
            modifier = Modifier,
            color = state.selectedColor
        )
    }
}

/**
 * A composable function that displays a saturation and lightness panel for color selection.
 *
 * @param modifier The modifier to be applied to the panel.
 * @param hueValue The current hue value (0F to 360F).
 * @param saturationValue The current saturation value (0F to 1F).
 * @param lightnessValue The current lightness value (0F to 1F).
 * @param onSelectionChanged A callback function that is invoked when the saturation or lightness
 * value changes. It provides the new saturation and lightness values as parameters.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun SaturationLightnessPanel(
    modifier: Modifier = Modifier,
    hueValue: Float,
    saturationValue: Float,
    lightnessValue: Float,
    onSelectionChanged: (saturation: Float, lightness: Float) -> Unit
) {

    BoxWithConstraints(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {

        val panelWidth = constraints.maxWidth.toFloat()
        val panelHeight = constraints.maxHeight.toFloat()

        val thumbPosition = Offset(
            x = saturationValue * panelWidth,
            y = (1F - lightnessValue) * panelHeight
        )

        val thumbColor = MaterialTheme.colorScheme.onSurfaceVariant

        val thumbRadius = 10.dp
        val thumbWidth = 2.4.dp

        val tapPointerInput = Modifier.pointerInput(panelWidth, panelHeight) {

            detectTapGestures(
                onPress = { offset ->

                    val newSaturation = (offset.x / panelWidth).coerceIn(range = 0F..1F)
                    val newLightness = (1F - (offset.y / panelHeight)).coerceIn(range = 0F..1F)

                    onSelectionChanged(newSaturation, newLightness)
                }
            )
        }

        val dragPointerInput = Modifier.pointerInput(panelWidth, panelHeight) {

            detectDragGestures { change, _ ->

                val newX = (change.position.x).coerceIn(0F..panelWidth)
                val newY = (change.position.y).coerceIn(0F..panelHeight)
                val newSaturation = (newX / panelWidth).coerceIn(range = 0F..1F)
                val newLightness = (1F - (newY / panelHeight)).coerceIn(range = 0F..1F)

                onSelectionChanged(newSaturation, newLightness)
                change.consume()
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .then(tapPointerInput)
                .then(dragPointerInput)
                .clip(shape = MaterialTheme.shapes.extraSmall)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65F),
                    shape = MaterialTheme.shapes.extraSmall
                ),
            contentDescription = "Saturation Lightness Panel"
        ) {

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.hsl(hueValue, 0F, 0.5F), Color.hsl(hueValue, 1F, 0.5F))
                )
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color.Transparent),
                    startY = 0F,
                    endY = center.y
                )
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = center.y,
                    endY = size.height
                )
            )

            drawDragHandle(
                position = thumbPosition,
                radius = thumbRadius,
                color = thumbColor,
                width = thumbWidth
            )
        }
    }
}

/**
 * A Composable function that displays a hue panel.
 * The panel allows the user to select a hue value by dragging a thumb along a horizontal track.
 * The track is filled with a gradient of colors representing the hue spectrum.
 *
 * @param modifier The modifier to be applied to the HuePanel.
 * @param currentHue The current hue value (0F to 360F).
 * @param onHueChanged A lambda function that is called when the hue value changes.
 * It receives the new hue value as a parameter.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun HuePanel(
    modifier: Modifier = Modifier,
    currentHue: Float,
    onHueChanged: (Float) -> Unit
) {

    val density = LocalDensity.current

    val hueColors = remember {
        (0..359).map { hue ->
            Color.hsl(hue = hue.toFloat(), saturation = 1F, lightness = 0.5F)
        } + Color.hsl(0F, 1F, 0.5F)
    }

    val thumbColor = MaterialTheme.colorScheme.onSurfaceVariant
    val panelBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.65F)

    val trackHeight = 32.dp
    val thumbRadius = trackHeight / 2
    val thumbWidth = 2.4.dp

    val trackHeightPx = with(density) { trackHeight.toPx() }
    val thumbRadiusPx = with(density) { thumbRadius.toPx() }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 12.dp)
    ) {

        Text(
            text = "Hue : ${currentHue.toInt()}°",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = (thumbRadius * 2) + (thumbWidth * 2)),
            contentAlignment = Alignment.Center
        ) {

            val panelWidth = constraints.maxWidth.toFloat()

            val currentThumbX = remember(currentHue, panelWidth, thumbRadiusPx) {

                val hueStart = currentHue.coerceIn(0F..360F) - (0F..360F).start
                val hueRange = (0F..360F).endInclusive - (0F..360F).start
                val normalizedHue = hueStart / hueRange
                val hueSliderWidth = panelWidth - (2 * thumbRadiusPx)

                (normalizedHue * hueSliderWidth) + thumbRadiusPx
            }


            val dragPointerInput = Modifier.pointerInput(panelWidth, thumbRadiusPx) {

                detectDragGestures { change, _ ->

                    val newX = change.position.x.coerceIn(
                        range = thumbRadiusPx..panelWidth - thumbRadiusPx
                    )

                    val minHue = (0F..360F).start
                    val maxHue = (0F..360F).endInclusive
                    val sliderWidth = panelWidth - (2 * thumbRadiusPx)
                    val normalizedPosition = (newX - thumbRadiusPx) / sliderWidth
                    val newValue = minHue + (normalizedPosition * (maxHue - minHue))

                    onHueChanged(newValue.coerceIn(range = 0F..360F))
                    change.consume()
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .then(dragPointerInput),
                contentDescription = "Hue Panel"
            ) {

                val centerY = size.height / 2F
                val trackStartX = thumbRadiusPx
                val trackEndX = size.width - thumbRadiusPx
                val cornerRadius = 4.dp.toPx()

                drawRoundRect(
                    topLeft = Offset(trackStartX, centerY - (trackHeightPx / 2)),
                    size = Size(width = trackEndX - trackStartX, height = trackHeightPx),
                    brush = Brush.horizontalGradient(colors = hueColors),
                    cornerRadius = CornerRadius(x = cornerRadius, y = cornerRadius)
                )

                drawRoundRect(
                    topLeft = Offset(trackStartX, centerY - (trackHeightPx / 2)),
                    size = Size(width = trackEndX - trackStartX, height = trackHeightPx),
                    color = panelBorderColor,
                    cornerRadius = CornerRadius(x = cornerRadius, y = cornerRadius),
                    style = Stroke(width = 0.6.dp.toPx())
                )

                val thumbPosition = Offset(currentThumbX, centerY)

                drawDragHandle(
                    position = thumbPosition,
                    radius = thumbRadius,
                    color = thumbColor,
                    width = thumbWidth
                )
            }
        }
    }
}

/**
 * A composable function that displays an alpha slider panel.
 *
 * This panel allows the user to select the alpha (transparency) value of a color.
 * It displays the current alpha value as a percentage and provides a visual slider
 * with a checkerboard background to indicate transparency.
 *
 * @param modifier The modifier to be applied to the AlphaPanel.
 * @param currentAlpha The current alpha value, ranging from 0.0 (fully transparent) to 1.0
 * (fully opaque).
 * @param baseColor The base color to which the alpha will be applied. This is used to render the
 * gradient on the slider.
 * @param onAlphaChanged A callback function that is invoked when the alpha value changes.
 * It receives the new alpha value as a Float.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun AlphaPanel(
    modifier: Modifier = Modifier,
    currentAlpha: Float,
    baseColor: Color,
    onAlphaChanged: (Float) -> Unit
) {

    val density = LocalDensity.current

    val thumbColor = MaterialTheme.colorScheme.onSurfaceVariant
    val panelBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.65F)

    val trackHeight = 32.dp
    val thumbRadius = trackHeight / 2
    val thumbWidth = 2.4.dp

    val trackHeightPx = with(density) { trackHeight.toPx() }
    val thumbRadiusPx = with(density) { thumbRadius.toPx() }

    val cellColorLight = Color.White
    val cellColorDark = Color.LightGray

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 12.dp)
    ) {

        Text(
            text = "Alpha : ${(currentAlpha * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = (thumbRadius * 2) + (thumbWidth * 2)),
            contentAlignment = Alignment.Center
        ) {

            val panelWidth = constraints.maxWidth.toFloat()

            val currentThumbX = remember(currentAlpha, panelWidth, thumbRadiusPx) {

                val alphaStart = currentAlpha.coerceIn(0F..1F) - (0F..1F).start
                val alphaRange = (0F..1F).endInclusive - (0F..1F).start
                val normalizedAlpha = alphaStart / alphaRange
                val alphaSliderWidth = panelWidth - (2 * thumbRadiusPx)

                (normalizedAlpha * alphaSliderWidth) + thumbRadiusPx
            }

            val dragPointerInput = Modifier.pointerInput(panelWidth, thumbRadiusPx) {

                detectDragGestures { change, _ ->

                    val newX = change.position.x.coerceIn(
                        range = thumbRadiusPx..panelWidth - thumbRadiusPx
                    )

                    val minAlpha = (0F..1F).start
                    val maxAlpha = (0F..1F).endInclusive
                    val sliderWidth = panelWidth - (2 * thumbRadiusPx)
                    val normalizedPosition = (newX - thumbRadiusPx) / sliderWidth
                    val newValue = minAlpha + (normalizedPosition * (maxAlpha - minAlpha))

                    onAlphaChanged(newValue.coerceIn(range = 0F..1F))
                    change.consume()
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .then(dragPointerInput),
                contentDescription = "Alpha Panel"
            ) {

                val trackActualHeight = trackHeightPx.coerceAtMost(maximumValue = size.height)
                val trackTopY = (size.height - trackActualHeight) / 2F
                val trackBottomY = trackTopY + trackActualHeight

                val trackStartX = thumbRadiusPx
                val trackEndX = size.width - thumbRadiusPx
                val trackWidth = trackEndX - trackStartX
                val cornerRadius = 4.dp.toPx()
                val checkerSizePx = trackActualHeight / 3F

                val clipPath = Path().apply {

                    val cellRect = RoundRect(
                        rect = Rect(
                            left = trackStartX,
                            top = trackTopY,
                            right = trackEndX,
                            bottom = trackBottomY
                        ),
                        cornerRadius = CornerRadius(x = cornerRadius, y = cornerRadius)
                    )

                    addRoundRect(roundRect = cellRect)
                }

                clipPath(path = clipPath) {

                    val rowCount = 3
                    val columnCount = (trackWidth / checkerSizePx).toInt() + 2

                    (0 until rowCount).forEach { rowIndex ->

                        (0 until columnCount).forEach { columnIndex ->

                            val rectLeft = trackStartX + columnIndex * checkerSizePx
                            val rectTop = trackTopY + rowIndex * checkerSizePx


                            if (rectLeft < trackEndX && rectTop < trackBottomY) {

                                val cellColor = when {

                                    (rowIndex + columnIndex) % 2 == 0 -> cellColorLight
                                    else -> cellColorDark
                                }

                                drawRect(
                                    color = cellColor,
                                    topLeft = Offset(rectLeft, rectTop),
                                    size = Size(width = checkerSizePx, height = checkerSizePx)
                                )
                            }
                        }
                    }
                }

                drawRoundRect(
                    topLeft = Offset(trackStartX, trackTopY),
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            baseColor.copy(alpha = 0F),
                            baseColor.copy(alpha = 1F)
                        )
                    ),
                    size = Size(width = trackWidth, height = trackActualHeight),
                    cornerRadius = CornerRadius(x = cornerRadius, y = cornerRadius)
                )

                drawRoundRect(
                    topLeft = Offset(trackStartX, trackTopY),
                    size = Size(width = trackWidth, height = trackActualHeight),
                    color = panelBorderColor,
                    cornerRadius = CornerRadius(x = cornerRadius, y = cornerRadius),
                    style = Stroke(width = 0.6.dp.toPx())
                )

                val thumbPosition = Offset(currentThumbX, size.height / 2F)

                drawDragHandle(
                    position = thumbPosition,
                    radius = thumbRadius,
                    color = thumbColor,
                    width = thumbWidth
                )
            }
        }
    }
}

/**
 * Composable function that displays a preview of the selected color along with its information.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param color The color to be displayed. Defaults to [Color.Unspecified].
 */
@Composable
private fun ColorPreview(modifier: Modifier = Modifier, color: Color = Color.Unspecified) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = modifier
                .size(size = 64.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .clip(shape = MaterialTheme.shapes.extraSmall)
                .background(color = color)
        )

        ColorInfoPreview(
            modifier = Modifier,
            color = color
        )
    }


}

/**
 * A composable function that displays the color information in HEX and ARGB formats.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param color The color to display information for. Defaults to [Color.Unspecified].
 */
@Composable
private fun ColorInfoPreview(modifier: Modifier = Modifier, color: Color = Color.Unspecified) {

    val hexColorInfo by remember(color) {
        derivedStateOf { Pair(first = "HEX", second = EmptyFormat.toColorHex(color = color)) }
    }

    val argbColorInfo by remember(color) {
        derivedStateOf {
            Pair(
                first = "ARGB",
                second = "${(color.alpha * 255).toInt()}    ${(color.red * 255).toInt()}    ${
                    (color.green * 255).toInt()
                }   ${(color.blue * 255).toInt()}"
            )
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        ColorInfoItem(modifier = Modifier.fillMaxWidth(), infoItem = argbColorInfo)
        ColorInfoItem(modifier = Modifier.fillMaxWidth(), infoItem = hexColorInfo)
    }
}

/**
 * A composable function that displays a single color information item.
 * It shows a label and its corresponding value in a row.
 *
 * @param modifier The modifier to be applied to the row.
 * @param infoItem A pair containing the label (String) and the value (String) of the color
 * information.
 */
@Composable
private fun ColorInfoItem(modifier: Modifier = Modifier, infoItem: Pair<String, String>) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = Modifier.weight(weight = 0.35F),
            text = infoItem.first,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = ":",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            modifier = Modifier.weight(weight = 1.60F),
            text = infoItem.second,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Draws a drag handle on the canvas.
 *
 * @param position The center position of the drag handle.
 * @param radius The radius of the outer circle of the drag handle.
 * @param color The color of the drag handle.
 * @param width The width of the stroke for the outer circle and the radius of the inner circle.
 */
private fun DrawScope.drawDragHandle(position: Offset, radius: Dp, color: Color, width: Dp) {

    val stroke = Stroke(width = width.toPx())

    drawCircle(center = position, radius = radius.toPx(), style = stroke, color = color)
    drawCircle(center = position, radius = width.toPx(), color = color)
}

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