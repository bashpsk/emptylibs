package io.bashpsk.emptylibs.kolorpicker.color

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.get
import io.bashpsk.emptylibs.composeutils.offset.toOffsetData
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
 * Defaults to a new `rememberColorPickerState()` if not provided.
 * @param enableAlphaPanel A boolean indicating whether to show the alpha panel for transparency
 * selection. Defaults to `false`.
 * @param enableCopyButtons A boolean indicating whether to show copy and paste buttons for the
 * color. Defaults to `false`.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    state: ColorPickerState = rememberColorPickerState(),
    enableAlphaPanel: Boolean = false,
    enableCopyButtons: Boolean = false
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {

        SaturationLightnessPanel(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1.0F)
                .padding(horizontal = TrackHeight / 2, vertical = TrackHeight / 2),
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

        if (enableAlphaPanel) {

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TrackHeight / 2),
            color = state.selectedColor
        )

        if (enableCopyButtons) {

            ColorCopyPasteButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = TrackHeight / 2),
                state = state
            )
        }
    }
}

/**
 * A composable that allows picking a color from a provided [ImageBitmap].
 *
 * This composable displays the image, fitting it within the layout while preserving its
 * aspect ratio. It listens for tap and drag gestures, and a draggable handle indicates
 * the selected pixel. The selected color is updated in the provided [state].
 *
 * @param modifier The modifier to be applied to this composable.
 * @param imageBitmap The [ImageBitmap] to display and pick colors from.
 * @param state The [ColorPickerState] to update with the selected color.
 */
@Composable
fun ImageColorPicker(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    state: ColorPickerState = rememberColorPickerState()
) {

    val thumbColor = MaterialTheme.colorScheme.onSurfaceVariant
    val thumbRadius = 10.dp
    val thumbWidth = 2.4.dp

    var thumbPosition by rememberSaveable {
        mutableStateOf(Offset.Unspecified.toOffsetData())
    }

    val imageAspectRatio by remember(imageBitmap) {
        derivedStateOf {
            EmptyFormat.findAspectRatio(width = imageBitmap.width, height = imageBitmap.height)
        }
    }

    val scaledBitmap by remember(imageBitmap) { derivedStateOf { imageBitmap.asAndroidBitmap() } }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        BoxWithConstraints(
            modifier = Modifier
                .weight(weight = 1.0F)
                .aspectRatio(ratio = imageAspectRatio)
                .clip(shape = MaterialTheme.shapes.extraSmall)
                .border(
                    width = 0.6.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65F),
                    shape = MaterialTheme.shapes.extraSmall
                ),
            contentAlignment = Alignment.Center
        ) {

            val boxWidth = constraints.maxWidth.toFloat()
            val boxHeight = constraints.maxHeight.toFloat()
            val bitmapWidth = imageBitmap.width.toFloat()
            val bitmapHeight = imageBitmap.height.toFloat()

            val scale = minOf(boxWidth / bitmapWidth, boxHeight / bitmapHeight)
            val scaledWidth = bitmapWidth * scale
            val scaledHeight = bitmapHeight * scale
            val offsetX = (boxWidth - scaledWidth) / 2
            val offsetY = (boxHeight - scaledHeight) / 2

            val handleColorSelection = { touchOffset: Offset ->

                val imageX = touchOffset.x - offsetX
                val imageY = touchOffset.y - offsetY

                val bitmapX = (imageX / scale).coerceIn(0.0F, bitmapWidth - 1)
                val bitmapY = (imageY / scale).coerceIn(0.0F, bitmapHeight - 1)

                if (imageX in 0.0F..scaledWidth && imageY in 0.0F..scaledHeight) {

                    thumbPosition = touchOffset.toOffsetData()
                    state.updateColor(Color(scaledBitmap[bitmapX.toInt(), bitmapY.toInt()]))
                }
            }

            val tapPointerInput = Modifier.pointerInput(Unit) {

                detectTapGestures(
                    onPress = { offset ->

                        handleColorSelection(offset)
                    }
                )
            }

            val dragPointerInput = Modifier.pointerInput(Unit) {

                detectDragGestures { change, _ ->

                    change.consume()
                    handleColorSelection(change.position)
                }
            }

            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = MaterialTheme.shapes.extraSmall)
                    .then(tapPointerInput)
                    .then(dragPointerInput)
                    .drawWithContent {

                        drawContent()

                        thumbPosition.toOffset().takeIf { position ->

                            position.isSpecified
                        }?.let { position ->

                            drawDragHandle(
                                position = position,
                                radius = thumbRadius,
                                color = thumbColor,
                                width = thumbWidth
                            )
                        }
                    },
                bitmap = imageBitmap,
                contentScale = ContentScale.Fit,
                contentDescription = "Image Color Picker"
            )
        }

        Spacer(modifier = Modifier.height(height = 4.dp))

        ColorPreview(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TrackHeight / 2),
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
        modifier = modifier,
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
                    width = 0.6.dp,
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

    val thumbRadius = TrackHeight / 2
    val thumbWidth = 2.4.dp

    val trackHeightPx = with(density) { TrackHeight.toPx() }
    val thumbRadiusPx = with(density) { thumbRadius.toPx() }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
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

            val currentThumbX by remember(currentHue, panelWidth, thumbRadiusPx) {
                derivedStateOf {
                    val hueStart = currentHue.coerceIn(0F..360F) - (0F..360F).start
                    val hueRange = (0F..360F).endInclusive - (0F..360F).start
                    val normalizedHue = hueStart / hueRange
                    val hueSliderWidth = panelWidth - (2 * thumbRadiusPx)

                    (normalizedHue * hueSliderWidth) + thumbRadiusPx
                }
            }

            val tapPointerInput = Modifier.pointerInput(panelWidth, thumbRadiusPx) {

                detectTapGestures(
                    onPress = { position ->

                        val newX = position.x.coerceIn(
                            range = thumbRadiusPx..panelWidth - thumbRadiusPx
                        )

                        val minHue = (0F..360F).start
                        val maxHue = (0F..360F).endInclusive
                        val sliderWidth = panelWidth - (2 * thumbRadiusPx)
                        val normalizedPosition = (newX - thumbRadiusPx) / sliderWidth
                        val newValue = minHue + (normalizedPosition * (maxHue - minHue))

                        onHueChanged(newValue.coerceIn(range = 0F..360F))
                    }
                )
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
                    .then(tapPointerInput)
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

    val thumbRadius = TrackHeight / 2
    val thumbWidth = 2.4.dp

    val trackHeightPx = with(density) { TrackHeight.toPx() }
    val thumbRadiusPx = with(density) { thumbRadius.toPx() }

    val cellColorLight = Color.White
    val cellColorDark = Color.LightGray

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
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

            val currentThumbX by remember(currentAlpha, panelWidth, thumbRadiusPx) {
                derivedStateOf {
                    val alphaStart = currentAlpha.coerceIn(0F..1F) - (0F..1F).start
                    val alphaRange = (0F..1F).endInclusive - (0F..1F).start
                    val normalizedAlpha = alphaStart / alphaRange
                    val alphaSliderWidth = panelWidth - (2 * thumbRadiusPx)

                    (normalizedAlpha * alphaSliderWidth) + thumbRadiusPx
                }
            }

            val tapPointerInput = Modifier.pointerInput(panelWidth, thumbRadiusPx) {

                detectTapGestures(
                    onPress = { position ->

                        val newX = position.x.coerceIn(
                            range = thumbRadiusPx..panelWidth - thumbRadiusPx
                        )

                        val minAlpha = (0F..1F).start
                        val maxAlpha = (0F..1F).endInclusive
                        val sliderWidth = panelWidth - (2 * thumbRadiusPx)
                        val normalizedPosition = (newX - thumbRadiusPx) / sliderWidth
                        val newValue = minAlpha + (normalizedPosition * (maxAlpha - minAlpha))

                        onAlphaChanged(newValue.coerceIn(range = 0F..1F))
                    }
                )
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
                    .then(tapPointerInput)
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
fun ColorPreview(modifier: Modifier = Modifier, color: Color = Color.Unspecified) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(size = 60.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .clip(shape = MaterialTheme.shapes.extraSmall)
                .background(color = color)
        )

        ColorInfoPreview(modifier = Modifier, color = color)
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
            modifier = Modifier.weight(weight = 0.4F),
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
 * A composable function that displays "Copy" and "Paste" buttons for color operations.
 *
 * The "Paste" button attempts to read a HEX color string from the clipboard. If a valid
 * HEX color is found, it updates the [ColorPickerState] with the pasted color.
 *
 * The "Copy" button takes the currently selected color from the [ColorPickerState],
 * converts it to a HEX string, and copies it to the clipboard.
 *
 * @param modifier The modifier to be applied to the row containing the buttons.
 * @param state The [ColorPickerState] that holds the current selected color and will be
 * updated when a color is pasted.
 */
@Composable
private fun ColorCopyPasteButtons(modifier: Modifier = Modifier, state: ColorPickerState) {

    val context = LocalContext.current

    val clipboardManager by lazy {

        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedButton(
            onClick = {

                clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()?.let { hexString ->

                    EmptyFormat.hexToColor(hex = hexString)?.let { color ->

                        state.updateColor(color = color)
                    }
                }
            }
        ) {

            Icon(
                modifier = Modifier.size(size = 18.dp),
                imageVector = Icons.Filled.ContentPaste,
                contentDescription = "Paste Color"
            )

            Spacer(modifier = Modifier.width(width = 2.dp))

            Text(
                text = "Paste",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Button(
            onClick = {

                val colorHex = EmptyFormat.toColorHex(color = state.selectedColor)
                val clipData = ClipData.newPlainText(colorHex, colorHex)

                clipboardManager.setPrimaryClip(clipData)
            }
        ) {

            Icon(
                modifier = Modifier.size(size = 18.dp),
                imageVector = Icons.Filled.ContentCopy,
                contentDescription = "Copy Color"
            )

            Spacer(modifier = Modifier.width(width = 2.dp))

            Text(
                text = "Copy",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}