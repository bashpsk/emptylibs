package io.bashpsk.emptylibs.datastoreui.preference

import androidx.annotation.ColorInt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.component.PreferenceColorPreviewBox
import io.bashpsk.emptylibs.datastoreui.component.PreferenceSummary
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.resetPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import io.bashpsk.emptylibs.kolorpicker.color.KolorPickerDialog
import io.bashpsk.emptylibs.kolorpicker.color.KolorPickerDialogDefault
import io.bashpsk.emptylibs.kolorpicker.color.KolorPickerState
import io.bashpsk.emptylibs.kolorpicker.color.rememberKolorPickerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Composable function that displays a color picker preference item.
 *
 * This preference item allows the user to select a color, which is then stored in DataStore.
 *
 * @param modifier The modifier to be applied to the preference item.
 * @param datastore The [DataStore] instance to use for this preference. If the DataStore instance
 * is `null`, it will attempt to use the [LocalDatastore] provided via `CompositionLocalProvider`.
 * @param key The DataStore [Preferences.Key] for storing the selected color.
 * @param initialValue The initial color value. Defaults to [Color.Unspecified].
 * @param title A Composable lambda function that defines the title of the preference item.
 * @param summary A Composable lambda function that defines the summary text for the preference
 * item, which can depend on the current argb color value. Defaults to [PreferenceSummary].
 * @param leadingContent A Composable lambda function to display content at the beginning of the
 * preference item.
 * @param trailingContent A Composable lambda function to display content at the end of the
 * preference item, which can depend on the current argb color value. Defaults to
 * [PreferenceColorPreviewBox].
 * @param colors The colors to be used for the list item. Defaults to [ListItemDefaults.colors].
 * @param tonalElevation The tonal elevation of the list item. Defaults to
 * [ListItemDefaults.Elevation].
 * @param shadowElevation The shadow elevation of the list item. Defaults to
 * [ListItemDefaults.Elevation].
 * @param enableAlphaPanel Whether to enable the alpha panel in the color picker.
 * Defaults to `true`.
 * @param enableCopyPasteButton Whether to enable the copy & paste buttons in the color picker.
 * Defaults to `true`.
 * @param resetButton A Composable lambda defining the dismiss/secondary action button in the
 * dialog. Defaults to a reset button that clears the preference key.
 */
@Composable
inline fun ColorPickPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>?,
    key: Preferences.Key<Int>,
    @ColorInt
    initialValue: Int = Color.Unspecified.toArgb(),
    noinline title: @Composable () -> Unit,
    crossinline summary: @Composable (argb: Int) -> Unit = {

        PreferenceSummary()
    },
    noinline leadingContent: @Composable () -> Unit = {},
    crossinline trailingContent: @Composable (argb: Int) -> Unit = { color ->

        PreferenceColorPreviewBox(color = color)
    },
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    enableAlphaPanel: Boolean = true,
    enableCopyPasteButton: Boolean = true,
    crossinline resetButton: @Composable CoroutineScope.(
        state: KolorPickerState,
        datastore: DataStore<Preferences>
    ) -> Unit = { state, preferenceDatastore ->

        KolorPickerDialogDefault.ResetButton(
            state = state,
            onSelectedColor = {

                launch(context = Dispatchers.IO) {

                    preferenceDatastore.resetPreference(key = key)
                }
            }
        )
    }
) {

    val preferenceDatastore = datastore ?: LocalDatastore.current
    val coroutineScope = rememberCoroutineScope()
    val colorPickerState = rememberKolorPickerState()

    val currentValue by preferenceDatastore.getPreference(
        key = key,
        initial = initialValue
    ).collectAsStateWithLifecycle(initialValue = initialValue)

    val onClick = remember { { colorPickerState.dialogVisible.targetState = true } }

    KolorPickerDialog(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        state = colorPickerState,
        enableAlphaPanel = enableAlphaPanel,
        enableCopyButton = enableCopyPasteButton,
        enablePasteButton = enableCopyPasteButton,
        dismissButton = {

            coroutineScope.resetButton(colorPickerState, preferenceDatastore)
        },
        onSelectedColor = { color ->

            coroutineScope.launch(context = Dispatchers.IO) {

                preferenceDatastore.setPreference(key = key, value = color.toArgb())
            }
        }
    )

    ListItem(
        modifier = modifier.clickable(role = Role.Button, onClick = onClick),
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        leadingContent = leadingContent,
        trailingContent = { trailingContent(currentValue) },
        headlineContent = title,
        supportingContent = { summary(currentValue) }
    )
}