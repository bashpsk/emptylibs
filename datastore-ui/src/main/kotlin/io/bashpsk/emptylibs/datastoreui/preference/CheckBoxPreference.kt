package io.bashpsk.emptylibs.datastoreui.preference

import androidx.annotation.FloatRange
import androidx.compose.foundation.clickable
import androidx.compose.material3.Checkbox
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
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import io.bashpsk.emptylibs.datastoreui.resources.DatastoreUIDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a checkbox preference item.
 * This preference item allows the user to toggle a boolean value, which is stored in DataStore.
 *
 * @param modifier Modifier to be applied to the ListItem.
 * @param key A lambda function that returns the DataStore [Preferences.Key] for this preference.
 * @param initialValue A lambda function that returns the initial boolean value if no value is
 * stored in DataStore. Defaults to `false`.
 * @param title A lambda function that returns the title of the preference.
 * @param summary A lambda function that returns the summary text for the preference. Defaults to an
 * empty string.
 * @param leadingContent A Composable lambda function to display content at the beginning of the
 * ListItem. Defaults to an empty Composable.
 * @param colors [ListItemColors] to be used for this ListItem. Defaults to
 * `ListItemDefaults.colors()`.
 * @param tonalElevation The tonal elevation of this ListItem. Defaults to
 * `ListItemDefaults.Elevation`.
 * @param shadowElevation The shadow elevation of this ListItem. Defaults to
 * `ListItemDefaults.Elevation`.
 * @param summaryAlpha The alpha value for the summary text, ranging from 0.0 to 1.0. Defaults to
 * `DatastoreUIDefaults.SUMMARY_ALPHA`.
 *
 * Note: Must be provided `LocalDatastore` using `CompositionLocalProvider`.
 */
@Composable
fun CheckBoxPreference(
    modifier: Modifier = Modifier,
    key: Preferences.Key<Boolean>,
    initialValue: Boolean = false,
    title: String,
    summary: String = "",
    leadingContent: @Composable (() -> Unit) = {},
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    @FloatRange(from = 0.0, to = 1.0)
    summaryAlpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA
) {

    val datastore = LocalDatastore.current

    CheckBoxPreference(
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
        summaryAlpha = summaryAlpha
    )
}

/**
 * A Composable function that displays a checkbox preference item.
 * This preference item allows the user to toggle a boolean value, which is stored in DataStore.
 *
 * @param modifier Modifier to be applied to the ListItem.
 * @param datastore The DataStore instance to use for this preference.
 * @param key A lambda function that returns the DataStore [Preferences.Key] for this preference.
 * @param initialValue A lambda function that returns the initial boolean value if no value is
 * stored in DataStore. Defaults to `false`.
 * @param title A lambda function that returns the title of the preference.
 * @param summary A lambda function that returns the summary text for the preference. Defaults to an
 * empty string.
 * @param leadingContent A Composable lambda function to display content at the beginning of the
 * ListItem. Defaults to an empty Composable.
 * @param colors [ListItemColors] to be used for this ListItem. Defaults to
 * `ListItemDefaults.colors()`.
 * @param tonalElevation The tonal elevation of this ListItem. Defaults to
 * `ListItemDefaults.Elevation`.
 * @param shadowElevation The shadow elevation of this ListItem. Defaults to
 * `ListItemDefaults.Elevation`.
 * @param summaryAlpha The alpha value for the summary text, ranging from 0.0 to 1.0. Defaults to
 * `DatastoreUIDefaults.SUMMARY_ALPHA`.
 */
@Composable
fun CheckBoxPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>,
    key: Preferences.Key<Boolean>,
    initialValue: Boolean = false,
    title: String,
    summary: String = "",
    leadingContent: @Composable (() -> Unit) = {},
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    @FloatRange(from = 0.0, to = 1.0)
    summaryAlpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA
) {

    val coroutineScope = rememberCoroutineScope()

    val getChecked by datastore.getPreference(
        key = key,
        initial = initialValue
    ).collectAsStateWithLifecycle(initialValue = initialValue)

    ListItem(
        modifier = modifier
            .clickable(
                role = Role.Checkbox,
                onClick = {

                    coroutineScope.launch(context = Dispatchers.IO) {

                        datastore.setPreference(key = key, value = getChecked.not())
                    }
                }
            ),
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        leadingContent = leadingContent,
        trailingContent = {

            Checkbox(checked = getChecked, onCheckedChange = null)
        },
        headlineContent = {

            PreferenceTitle(title = title)
        },
        supportingContent = {

            PreferenceSummary(summary = summary, alpha = summaryAlpha)
        }
    )
}