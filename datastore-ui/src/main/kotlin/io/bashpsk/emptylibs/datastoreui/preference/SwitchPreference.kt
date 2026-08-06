package io.bashpsk.emptylibs.datastoreui.preference

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.component.SwitchButton
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a switch preference item.
 * This preference allows users to toggle a boolean value which is stored in DataStore.
 *
 * @param modifier Optional [Modifier] for this Composable.
 * @param datastore The DataStore instance to use for this preference. If DataStore instance is
 * `null` must be provided [LocalDatastore] using `CompositionLocalProvider`.
 * @param key A lambda function that returns the [Preferences.Key] for this preference.
 * This key is used to store and retrieve the boolean value from DataStore.
 * @param initialValue A lambda function that returns the initial boolean value of the preference
 * if it's not already set in DataStore. Defaults to `false`.
 * @param title A lambda function that returns the title string for the preference.
 * @param summary A lambda function that returns the summary string for the preference.
 * Displayed below the title. Defaults to an empty string.
 * @param leadingContent An optional Composable lambda to display content at the start of the
 * preference item. Defaults to an empty Composable.
 * @param trailingContent A Composable lambda for displaying content at the end of the list item.
 * @param colors [ListItemColors] to be used for this list item.
 * @param tonalElevation The tonal elevation of this list item.
 * @param shadowElevation The shadow elevation of this list item.
 */
@Composable
inline fun SwitchPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>?,
    key: Preferences.Key<Boolean>,
    initialValue: Boolean = false,
    noinline title: @Composable () -> Unit,
    crossinline summary: @Composable (checked: Boolean) -> Unit = {},
    noinline leadingContent: @Composable () -> Unit = {},
    crossinline trailingContent: @Composable (checked: Boolean) -> Unit = { checked ->

        SwitchButton(checked = checked)
    },
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation
) {

    val preferenceDatastore = datastore ?: LocalDatastore.current
    val coroutineScope = rememberCoroutineScope()

    val currentValue by preferenceDatastore.getPreference(
        key = key,
        initial = initialValue
    ).collectAsStateWithLifecycle(initialValue = initialValue)

    ListItem(
        modifier = modifier.clickable(
            role = Role.Switch,
            onClick = {

                coroutineScope.launch(context = Dispatchers.IO) {

                    preferenceDatastore.setPreference(key = key, value = !currentValue)
                }
            }
        ),
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        leadingContent = leadingContent,
        trailingContent = { trailingContent(currentValue) },
        headlineContent = title,
        supportingContent = { summary(currentValue) }
    )
}