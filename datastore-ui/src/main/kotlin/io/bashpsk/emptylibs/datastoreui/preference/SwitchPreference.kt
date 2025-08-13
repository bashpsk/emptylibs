package io.bashpsk.emptylibs.datastoreui.preference

import androidx.annotation.FloatRange
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.extension.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import io.bashpsk.emptylibs.datastoreui.resources.DatastoreUIDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a switch preference item.
 * This preference allows users to toggle a boolean value which is stored in DataStore.
 *
 * @param modifier Optional [Modifier] for this Composable.
 * @param key A lambda function that returns the [Preferences.Key] for this preference.
 * This key is used to store and retrieve the boolean value from DataStore.
 * @param initialValue A lambda function that returns the initial boolean value of the preference
 * if it's not already set in DataStore. Defaults to `false`.
 * @param title A lambda function that returns the title string for the preference.
 * @param summary A lambda function that returns the summary string for the preference.
 * Displayed below the title. Defaults to an empty string.
 * @param leadingContent An optional Composable lambda to display content at the start of the
 * preference item.
 * Defaults to an empty Composable.
 * @param colors [ListItemColors] to be used for this list item.
 * @param tonalElevation The tonal elevation of this list item.
 * @param shadowElevation The shadow elevation of this list item.
 * @param summaryAlpha The alpha transparency for the summary text.
 * Must be a float between 0.0 and 1.0. Defaults to [DatastoreUIDefaults.SUMMARY_ALPHA].
 */
@Composable
fun SwitchPreference(
    modifier: Modifier = Modifier,
    key: () -> Preferences.Key<Boolean>,
    initialValue: () -> Boolean = { false },
    title: () -> String,
    summary: () -> String = { "" },
    leadingContent: @Composable (() -> Unit) = {},
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    @FloatRange(from = 0.0, to = 1.0)
    summaryAlpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA
) {

    val datastore = LocalDatastore.current
    val coroutineScope = rememberCoroutineScope()

    val getSwitchState by datastore.getPreference(
        key = key(),
        initial = initialValue()
    ).collectAsStateWithLifecycle(initialValue = initialValue())

    ListItem(
        modifier = modifier
            .clickable(
                role = Role.Checkbox,
                onClick = {

                    coroutineScope.launch(context = Dispatchers.IO) {

                        datastore.setPreference(key = key(), value = getSwitchState.not())
                    }
                }
            ),
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        leadingContent = leadingContent,
        trailingContent = {

            Switch(
                checked = getSwitchState,
                thumbContent = {

                    Icon(
                        modifier = Modifier.size(size = SwitchDefaults.IconSize),
                        imageVector = when (getSwitchState) {

                            true -> Icons.Filled.Done
                            false -> Icons.Filled.Close
                        },
                        contentDescription = "Switch Thumb"
                    )
                },
                onCheckedChange = null
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