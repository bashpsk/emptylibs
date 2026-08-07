package io.bashpsk.emptylibs.datastoreui.preference

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
import io.bashpsk.emptylibs.formatter.format.toRoundedDecimalString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a slider preference.
 * This preference allows the user to select a float value within a specified range using a slider.
 * The selected value is stored in and retrieved from DataStore.
 *
 * @param modifier Optional [Modifier] for this Composable.
 * @param datastore The [DataStore] instance to use for this preference. If the DataStore instance
 * is `null`, it will attempt to use the [LocalDatastore] provided via `CompositionLocalProvider`.
 * @param key The [Preferences.Key] for storing the float value.
 * @param initialValue The initial float value if no value is found in DataStore.
 * Defaults to `0.0F`.
 * @param title A Composable lambda function that defines the title of the preference.
 * @param summary A Composable lambda function that defines a brief summary or description of the
 * preference, which can depend on the current slider position. Defaults to an empty Composable.
 * @param leadingContent A Composable lambda for content to be displayed at the leading edge of the
 * preference item. Defaults to an empty Composable.
 * @param trailingContent A Composable lambda for displaying content at the end of the list item,
 * which can depend on the current slider position.
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
 * @param sliderColors [SliderColors] to be used for the slider. Defaults to
 * [SliderDefaults.colors].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun SliderPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>?,
    key: Preferences.Key<Float>,
    initialValue: Float = 0.0F,
    noinline title: @Composable () -> Unit,
    crossinline summary: @Composable (position: Float) -> Unit = {},
    noinline leadingContent: @Composable () -> Unit = {},
    noinline trailingContent: @Composable ((position: Float) -> Unit)? = { sliderPosition->

        val sliderValueLabel by remember(sliderPosition) {
            derivedStateOf { sliderPosition.toRoundedDecimalString(fraction = 1) }
        }

        Text(
            text = sliderValueLabel,
            textAlign = TextAlign.Center,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium,
            overflow = TextOverflow.Ellipsis
        )
    },
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    sliderColors: SliderColors = SliderDefaults.colors()
) {

    val preferenceDatastore = datastore ?: LocalDatastore.current
    val coroutineScope = rememberCoroutineScope()
    val sliderInteractionSource = remember { MutableInteractionSource() }

    val getPosition by preferenceDatastore.getPreference(
        key = key,
        initial = initialValue
    ).collectAsStateWithLifecycle(initialValue = initialValue)

    var sliderPosition by rememberSaveable { mutableFloatStateOf(getPosition) }

    ListItem(
        modifier = modifier,
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        leadingContent = leadingContent,
        trailingContent = { trailingContent?.invoke(sliderPosition) },
        headlineContent = title,
        supportingContent = {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 0.dp)
            ) {

                summary(sliderPosition)

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

                            preferenceDatastore.setPreference(key = key, value = sliderPosition)
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