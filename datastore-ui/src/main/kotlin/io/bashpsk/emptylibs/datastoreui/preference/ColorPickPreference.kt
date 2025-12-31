package io.bashpsk.emptylibs.datastoreui.preference

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.resetPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import io.bashpsk.emptylibs.datastoreui.resources.DatastoreUIDefaults
import io.bashpsk.emptylibs.kolorpicker.color.KolorPickerDialog
import io.bashpsk.emptylibs.kolorpicker.color.KolorPickerDialogDefault
import io.bashpsk.emptylibs.kolorpicker.color.rememberKolorPickerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Composable function that displays a color picker preference item.
 *
 * This preference item allows the user to select a color, which is then stored in DataStore.
 *
 * @param modifier The modifier to be applied to the preference item.
 * @param key A function that returns the DataStore key for storing the selected color.
 * @param initialValue A function that returns the initial color value. Defaults to an unspecified
 * color.
 * @param title A function that returns the title of the preference item.
 * @param summary A function that returns the summary text for the preference item. Defaults to an
 * empty string.
 * @param leadingContent A composable function to display content at the beginning of the preference
 * item.
 * @param colors The colors to be used for the list item. Defaults to [ListItemDefaults.colors].
 * @param tonalElevation The tonal elevation of the list item. Defaults to
 * [ListItemDefaults.Elevation].
 * @param shadowElevation The shadow elevation of the list item. Defaults to
 * [ListItemDefaults.Elevation].
 * @param enableAlphaPanel A function that returns a boolean indicating whether to enable the alpha
 * panel in the color picker dialog. Defaults to false.
 * @param summaryAlpha The alpha value for the summary text. Defaults to
 * [DatastoreUIDefaults.SUMMARY_ALPHA].
 * @param enableResetButton A function that returns a boolean indicating whether to enable the reset
 * button in the color picker dialog. Defaults to false.
 *
 * Note: Must be provide `LocalDatastore` using `CompositionLocalProvider`.
 */
@Composable
fun ColorPickPreference(
    modifier: Modifier = Modifier,
    key: Preferences.Key<Int>,
    @ColorInt
    initialValue: Int = Color.Unspecified.toArgb(),
    title: String,
    summary: String = "",
    leadingContent: @Composable (() -> Unit) = {},
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    enableAlphaPanel: Boolean = false,
    @FloatRange(from = 0.0, to = 1.0)
    summaryAlpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA,
    enableResetButton: Boolean = false,
) {

    val datastore = LocalDatastore.current

    ColorPickPreference(
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
        enableAlphaPanel = enableAlphaPanel,
        summaryAlpha = summaryAlpha,
        enableResetButton = enableResetButton
    )
}

/**
 * Composable function that displays a color picker preference item.
 *
 * This preference item allows the user to select a color, which is then stored in DataStore.
 *
 * @param modifier The modifier to be applied to the preference item.
 * @param datastore The DataStore instance to use for this preference.
 * @param key A function that returns the DataStore key for storing the selected color.
 * @param initialValue A function that returns the initial color value. Defaults to an unspecified
 * color.
 * @param title A function that returns the title of the preference item.
 * @param summary A function that returns the summary text for the preference item. Defaults to an
 * empty string.
 * @param leadingContent A composable function to display content at the beginning of the preference
 * item.
 * @param colors The colors to be used for the list item. Defaults to [ListItemDefaults.colors].
 * @param tonalElevation The tonal elevation of the list item. Defaults to
 * [ListItemDefaults.Elevation].
 * @param shadowElevation The shadow elevation of the list item. Defaults to
 * [ListItemDefaults.Elevation].
 * @param enableAlphaPanel A function that returns a boolean indicating whether to enable the alpha
 * panel in the color picker dialog. Defaults to false.
 * @param summaryAlpha The alpha value for the summary text. Defaults to
 * [DatastoreUIDefaults.SUMMARY_ALPHA].
 * @param enableResetButton A function that returns a boolean indicating whether to enable the reset
 * button in the color picker dialog. Defaults to false.
 */
@Composable
fun ColorPickPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>,
    key: Preferences.Key<Int>,
    @ColorInt
    initialValue: Int = Color.Unspecified.toArgb(),
    title: String,
    summary: String = "",
    leadingContent: @Composable (() -> Unit) = {},
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    enableAlphaPanel: Boolean = false,
    @FloatRange(from = 0.0, to = 1.0)
    summaryAlpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA,
    enableResetButton: Boolean = false,
) {

    val coroutineScope = rememberCoroutineScope()
    val dialogVisibleState = remember { MutableTransitionState(false) }
    val colorPickerState = rememberKolorPickerState()

    val getColorArgb by datastore.getPreference(
        key = key,
        initial = initialValue
    ).collectAsStateWithLifecycle(initialValue = initialValue)

    KolorPickerDialog(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        dialogVisibleState = dialogVisibleState,
        state = colorPickerState,
        enableAlphaPanel = enableAlphaPanel,
        enableCopyButton = true,
        dismissButton = {

            when (enableResetButton) {

                true -> KolorPickerDialogDefault.ResetButton(
                    dialogVisibleState = dialogVisibleState,
                    state = colorPickerState,
                    onSelectedColor = {

                        coroutineScope.launch(context = Dispatchers.IO) {

                            datastore.resetPreference(key = key)
                        }
                    }
                )

                false -> KolorPickerDialogDefault.DismissButton(
                    dialogVisibleState = dialogVisibleState
                )
            }
        },
        onSelectedColor = { color ->

            coroutineScope.launch(context = Dispatchers.IO) {

                datastore.setPreference(key = key, value = color.toArgb())
            }
        }
    )

    ListItem(
        modifier = modifier
            .clickable(
                role = Role.Button,
                onClick = {

                    dialogVisibleState.targetState = true
                }
            ),
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        leadingContent = leadingContent,
        trailingContent = {

            Box(
                modifier = Modifier
                    .size(width = 24.dp, height = 24.dp)
                    .clip(shape = MaterialTheme.shapes.extraSmall)
                    .border(
                        width = 0.8.dp,
                        color = MaterialTheme.colorScheme.surfaceTint,
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .background(color = Color(color = getColorArgb))
            )
        },
        headlineContent = {

            PreferenceTitle(title = title)
        },
        supportingContent = {

            PreferenceSummary(summary = summary, alpha = summaryAlpha)
        }
    )
}