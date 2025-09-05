package io.bashpsk.emptylibs.imagekolor.color

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * A Composable function that displays an image with applied color filters.
 *
 * This function takes an [ImageKolorState] which contains the image bitmap and
 * the color filter parameters. It renders the image using the Jetpack Compose `Image`
 * Composable. If the `imageBitmap` in the `state` is null, it displays a
 * "Image Load Failed!" message.
 *
 * The color filter is derived from the `state` and applied to the image.
 * The image is displayed with a fixed aspect ratio of 16:9 and fills the available width.
 *
 * @param modifier Optional [Modifier] to be applied to the root Composable of this component.
 * @param state The [ImageKolorState] that holds the image data and color filter settings.
 */
@Composable
fun KolorImageView(modifier: Modifier = Modifier, state: ImageKolorState) {

    val colorFilter by remember(state) { derivedStateOf { state.getColorFilter() } }

    state.imageBitmap?.let { bitmap ->

        Image(
            modifier = modifier,
            bitmap = bitmap,
            contentScale = ContentScale.Fit,
            colorFilter = colorFilter,
            contentDescription = "Image with Color Filter"
        )
    } ?: Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        BasicText(
            modifier = Modifier.fillMaxWidth(),
            text = "Image Load Failed!",
            style = MaterialTheme.typography.titleMedium,
            autoSize = TextAutoSize.StepBased(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * A Composable function that displays a set of sliders for adjusting image color properties.
 *
 * This function arranges multiple [AdjustmentSlider] components in a vertical [Column].
 * Each slider controls a specific image adjustment like brightness, exposure, contrast, etc.
 * The state of these adjustments is managed by the [ImageKolorState] object.
 *
 * @param modifier Optional [Modifier] for this composable. Defaults to [Modifier].
 * @param state The [ImageKolorState] that holds the current values of the adjustments and their
 * enabled status.
 */
@Composable
fun KolorAdjustmentSliders(modifier: Modifier = Modifier, state: ImageKolorState) {

    Column(
        modifier = modifier.padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {

        AdjustmentSlider(
            modifier = Modifier.fillMaxWidth(),
            label = "Brightness",
            value = state.brightness,
            valueRange = -1F..1F,
            enabled = state.config.enableBrightness,
            onValueChange = { newValue ->

                state.brightness = newValue
            }
        )

        AdjustmentSlider(
            modifier = Modifier.fillMaxWidth(),
            label = "Exposure",
            value = state.exposure,
            valueRange = -1F..1F,
            enabled = state.config.enableExposure,
            onValueChange = { newValue ->

                state.exposure = newValue
            }
        )

        AdjustmentSlider(
            modifier = Modifier.fillMaxWidth(),
            label = "Contrast",
            value = state.contrast,
            valueRange = 0F..2F,
            enabled = state.config.enableContrast,
            onValueChange = { newValue ->

                state.contrast = newValue
            }
        )

        AdjustmentSlider(
            modifier = Modifier.fillMaxWidth(),
            label = "Saturation",
            value = state.saturation,
            valueRange = 0F..2F,
            enabled = state.config.enableSaturation,
            onValueChange = { newValue ->

                state.saturation = newValue
            }
        )

        AdjustmentSlider(
            modifier = Modifier.fillMaxWidth(),
            label = "Warmth",
            value = state.warmth,
            valueRange = -1F..1F,
            enabled = state.config.enableWarmth,
            onValueChange = { newValue ->

                state.warmth = newValue
            }
        )

        AdjustmentSlider(
            modifier = Modifier.fillMaxWidth(),
            label = "Tint",
            value = state.tint,
            valueRange = -1F..1F,
            enabled = state.config.enableTint,
            onValueChange = { newValue ->

                state.tint = newValue
            }
        )

        AdjustmentSlider(
            modifier = Modifier.fillMaxWidth(),
            label = "Highlights",
            value = state.highlights,
            valueRange = -1F..1F,
            enabled = state.config.enableHighlights,
            onValueChange = { newValue ->

                state.highlights = newValue
            }
        )

        AdjustmentSlider(
            modifier = Modifier.fillMaxWidth(),
            label = "Shadows",
            value = state.shadows,
            valueRange = -1F..1F,
            enabled = state.config.enableShadows,
            onValueChange = { newValue ->

                state.shadows = newValue
            }
        )
    }
}

/**
 * A Composable function that displays a slider for adjusting a value.
 *
 * This function creates a vertical layout containing a label and a slider.
 * The label displays the provided [label] text along with the current [value]
 * represented as a percentage within the given [valueRange].
 * The slider allows the user to change the [value].
 * The slider is only displayed and interactive if [enabled] is true.
 *
 * @param modifier The modifier to be applied to the Column that holds the label and slider.
 * @param label The text label to display above the slider.
 * @param value The current value of the slider.
 * @param valueRange The range of possible values for the slider.
 * @param enabled A boolean indicating whether the slider is interactive. Defaults to true.
 * @param onValueChange A callback function that is invoked when the slider's value changes.
 * It receives the new Float value as an argument.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustmentSlider(
    modifier: Modifier = Modifier,
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    enabled: Boolean = true,
    onValueChange: (Float) -> Unit
) {

    val valuePercentage by remember(value, valueRange) {
        derivedStateOf {

            val range = valueRange.endInclusive - valueRange.start
            val adjustedValue = value - valueRange.start
            val normalizedValue = adjustedValue / range

            ((normalizedValue * 200) - 100).roundToInt().coerceIn(range = -100..100)
        }
    }

    val sliderLabel by remember(label, valuePercentage) {
        derivedStateOf {

            val percentage = if (valuePercentage > 0) "+${valuePercentage}" else "$valuePercentage"
            "$label : $percentage%"
        }
    }

    val sliderColors = SliderDefaults.colors(
        thumbColor = MaterialTheme.colorScheme.inversePrimary
    )

    if (enabled) {

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(space = 4.dp)
        ) {

            Text(
                text = sliderLabel,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )

            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                colors = sliderColors,
                thumb = { sliderState ->

                    SliderDefaults.Thumb(
                        interactionSource = remember { MutableInteractionSource() },
                        thumbSize = DpSize(
                            ButtonDefaults.IconSize,
                            ButtonDefaults.IconSize
                        ),
                        colors = sliderColors
                    )
                },
                track = { sliderState ->

                    SliderDefaults.Track(
                        sliderState = sliderState,
                        colors = sliderColors,
                        thumbTrackGapSize = 0.dp
                    )
                }
            )
        }
    }
}