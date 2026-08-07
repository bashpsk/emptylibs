package io.bashpsk.emptylibs.datastoreui.preference

import androidx.compose.foundation.clickable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.component.PreferenceSummary
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a checkbox preference item.
 * This preference item allows the user to toggle a boolean value, which is stored in DataStore.
 *
 * @param modifier Modifier to be applied to the ListItem.
 * @param datastore The [DataStore] instance to use for this preference. If the DataStore instance
 * is `null`, it will attempt to use the [LocalDatastore] provided via `CompositionLocalProvider`.
 * @param key The DataStore [Preferences.Key] for this preference.
 * @param initialValue The initial boolean value if no value is stored in DataStore.
 * Defaults to `false`.
 * @param title A Composable lambda function that defines the title of the preference.
 * @param summary A Composable lambda function that defines the summary text for the preference,
 * which can depend on the current checked state. Defaults to [PreferenceSummary].
 * @param leadingContent A Composable lambda function to display content at the beginning of the
 * ListItem. Defaults to an empty Composable.
 * @param trailingContent A Composable lambda function to display content at the end of the
 * ListItem, which can depend on the current checked state. Defaults to a [Checkbox].
 * @param colors [ListItemColors] to be used for this ListItem. Defaults to
 * `ListItemDefaults.colors()`.
 * @param tonalElevation The tonal elevation of this ListItem. Defaults to
 * `ListItemDefaults.Elevation`.
 * @param shadowElevation The shadow elevation of this ListItem. Defaults to
 * `ListItemDefaults.Elevation`.
 */
@Composable
inline fun CheckBoxPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>?,
    key: Preferences.Key<Boolean>,
    initialValue: Boolean = false,
    noinline title: @Composable () -> Unit,
    crossinline summary: @Composable (checked: Boolean) -> Unit = {

        PreferenceSummary()
    },
    noinline leadingContent: @Composable () -> Unit = {},
    crossinline trailingContent: @Composable (checked: Boolean) -> Unit = { checked ->

        Checkbox(checked = checked, onCheckedChange = null)
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

    val onClick = remember {
        {
            coroutineScope.launch(context = Dispatchers.IO) {
                preferenceDatastore.setPreference(key = key, value = !currentValue)
            }
        }
    }

    ListItem(
        modifier = modifier.clickable(role = Role.Checkbox, onClick = { onClick() }),
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        leadingContent = leadingContent,
        trailingContent = { trailingContent(currentValue) },
        headlineContent = title,
        supportingContent = { summary(currentValue) }
    )
}