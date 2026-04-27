package io.bashpsk.emptylibs.datastoreui.preference

import androidx.annotation.FloatRange
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import io.bashpsk.emptylibs.datastoreui.resources.DatastoreUIDefaults
import io.bashpsk.emptylibs.formatter.format.toRoundedDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a slider preference.
 * This preference allows the user to select a float value within a specified range using a slider.
 * The selected value is stored in and retrieved from DataStore.
 *
 * @param modifier Optional [Modifier] for this Composable.
 * @param key A lambda function that returns the [Preferences.Key] for storing the float value.
 * @param initialValue A lambda function that returns the initial float value if no value is found
 * in DataStore. Defaults to `0.0F`.
 * @param title A lambda function that returns the title of the preference.
 * @param summary A lambda function that returns a brief summary or description of the preference.
 * Defaults to an empty string.
 * @param leadingContent A Composable lambda for content to be displayed at the leading edge of the
 * preference item. Defaults to an empty Composable.
 * @param colors [ListItemColors] to be used for this preference item. Defaults to
 * [ListItemDefaults.colors].
 * @param tonalElevation The tonal elevation of this preference item. Defaults to
 * [ListItemDefaults.Elevation].
 * @param shadowElevation The shadow elevation of this preference item. Defaults to
 * [ListItemDefaults.Elevation].
 * @param valueRange The [ClosedFloatingPointRange] representing the valid range of values for the
 * slider.
 * @param steps The number of discrete steps the slider can take. If `0`, the slider is continuous.
 * Defaults to `0`.
 * @param isValueVisible A boolean indicating whether the current slider value should be displayed.
 * Defaults to `false`.
 * @param decimalFraction The number of decimal places to display for the slider value if
 * `isValueVisible` is true. Defaults to `1`.
 * @param sliderColors [SliderColors] to be used for the slider. Defaults to
 * [SliderDefaults.colors].
 * @param summaryAlpha The alpha (transparency) of the summary text, ranging from `0.0`
 * (fully transparent) to `1.0` (fully opaque). Defaults to [DatastoreUIDefaults.SUMMARY_ALPHA].
 *
 * Note: Must be provide `LocalDatastore` using `CompositionLocalProvider`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderPreference(
    modifier: Modifier = Modifier,
    key: Preferences.Key<Float>,
    initialValue: Float = 0.0F,
    title: String,
    summary: String = "",
    leadingContent: @Composable (() -> Unit) = {},
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    isValueVisible: Boolean = false,
    decimalFraction: Int = 1,
    sliderColors: SliderColors = SliderDefaults.colors(),
    @FloatRange(from = 0.0, to = 1.0)
    summaryAlpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA
) {

    val datastore = LocalDatastore.current

    SliderPreference(
        modifier = modifier,
        datastore = datastore,
        key = key,
        initialValue = initialValue,
        title = title,
        summary = summary,
        leadingContent = leadingContent,
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        valueRange = valueRange,
        steps = steps,
        isValueVisible = isValueVisible,
        decimalFraction = decimalFraction,
        sliderColors = sliderColors,
        summaryAlpha = summaryAlpha
    )
}

/**
 * A Composable function that displays a slider preference.
 * This preference allows the user to select a float value within a specified range using a slider.
 * The selected value is stored in and retrieved from DataStore.
 *
 * @param modifier Optional [Modifier] for this Composable.
 * @param datastore The DataStore instance to use for this preference.
 * @param key A lambda function that returns the [Preferences.Key] for storing the float value.
 * @param initialValue A lambda function that returns the initial float value if no value is found
 * in DataStore. Defaults to `0.0F`.
 * @param title A lambda function that returns the title of the preference.
 * @param summary A lambda function that returns a brief summary or description of the preference.
 * Defaults to an empty string.
 * @param leadingContent A Composable lambda for content to be displayed at the leading edge of the
 * preference item. Defaults to an empty Composable.
 * @param colors [ListItemColors] to be used for this preference item. Defaults to
 * [ListItemDefaults.colors].
 * @param tonalElevation The tonal elevation of this preference item. Defaults to
 * [ListItemDefaults.Elevation].
 * @param shadowElevation The shadow elevation of this preference item. Defaults to
 * [ListItemDefaults.Elevation].
 * @param valueRange The [ClosedFloatingPointRange] representing the valid range of values for the
 * slider.
 * @param steps The number of discrete steps the slider can take. If `0`, the slider is continuous.
 * Defaults to `0`.
 * @param isValueVisible A boolean indicating whether the current slider value should be displayed.
 * Defaults to `false`.
 * @param decimalFraction The number of decimal places to display for the slider value if
 * `isValueVisible` is true. Defaults to `1`.
 * @param sliderColors [SliderColors] to be used for the slider. Defaults to
 * [SliderDefaults.colors].
 * @param summaryAlpha The alpha (transparency) of the summary text, ranging from `0.0`
 * (fully transparent) to `1.0` (fully opaque). Defaults to [DatastoreUIDefaults.SUMMARY_ALPHA].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>,
    key: Preferences.Key<Float>,
    initialValue: Float = 0.0F,
    title: String,
    summary: String = "",
    leadingContent: @Composable (() -> Unit) = {},
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    isValueVisible: Boolean = false,
    decimalFraction: Int = 1,
    sliderColors: SliderColors = SliderDefaults.colors(),
    @FloatRange(from = 0.0, to = 1.0)
    summaryAlpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA
) {

    val coroutineScope = rememberCoroutineScope()
    val sliderInteractionSource = remember { MutableInteractionSource() }

    val getPosition by datastore.getPreference(
        key = key,
        initial = initialValue
    ).collectAsStateWithLifecycle(initialValue = initialValue)

    var sliderPosition by rememberSaveable { mutableFloatStateOf(getPosition) }

    val sliderValueLabel by remember(sliderPosition, decimalFraction) {
        derivedStateOf { "${sliderPosition.toRoundedDecimal(fraction = decimalFraction)}" }
    }

    ListItem(
        modifier = modifier,
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        leadingContent = leadingContent,
        trailingContent = {

            AnimatedVisibility(visible = isValueVisible, enter = fadeIn(), exit = fadeOut()) {

                Text(
                    text = sliderValueLabel,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        headlineContent = {

            PreferenceTitle(title = title)
        },
        supportingContent = {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 0.dp)
            ) {

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(alpha = summaryAlpha),
                    text = summary,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.labelSmall
                )

                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = sliderPosition,
                    valueRange = valueRange,
                    steps = steps,
                    onValueChange = { position ->

                        sliderPosition = position
                    },
                    onValueChangeFinished = {

                        coroutineScope.launch(context = Dispatchers.IO) {

                            datastore.setPreference(key = key, value = sliderPosition)
                        }
                    },
                    colors = sliderColors,
                    interactionSource = sliderInteractionSource,
                    thumb = { _ ->

                        SliderDefaults.Thumb(
                            interactionSource = sliderInteractionSource,
                            colors = sliderColors
                        )
                    },
                    track = { sliderState ->

                        SliderDefaults.Track(
                            modifier = Modifier.fillMaxWidth(),
                            sliderState = sliderState,
                            thumbTrackGapSize = 0.dp,
                            colors = sliderColors
                        )
                    }
                )
            }
        }
    )
}