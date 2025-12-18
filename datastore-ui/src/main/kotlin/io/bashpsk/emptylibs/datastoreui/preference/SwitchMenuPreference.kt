package io.bashpsk.emptylibs.datastoreui.preference

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a switch preference item within a dropdown menu.
 * This item allows users to toggle a boolean preference, which is persisted using DataStore.
 *
 * @param modifier Optional [Modifier] for styling the [DropdownMenuItem].
 * @param key A lambda function that returns the [Preferences.Key] for the boolean preference.
 * @param initialValue A lambda function that returns the initial boolean value of the preference
 * if not already set. Defaults to `false`.
 * @param title A lambda function that returns the title string to be displayed for the preference.
 * @param leadingContent An optional Composable lambda to display content at the leading edge of the
 * menu item (e.g., an icon). Defaults to an empty Composable.
 * @param colors Optional [MenuItemColors] to customize the appearance of the menu item. Defaults to
 * [MenuDefaults.itemColors].
 * @param onMenuDismiss A lambda function that will be invoked when the menu item is clicked,
 * typically to dismiss the dropdown menu.
 *
 * Note: Must be provide `LocalDatastore` using `CompositionLocalProvider`.
 */
@Composable
fun SwitchMenuPreference(
    modifier: Modifier = Modifier,
    key: Preferences.Key<Boolean>,
    initialValue: Boolean = false,
    title: String,
    leadingContent: @Composable (() -> Unit) = {},
    colors: MenuItemColors = MenuDefaults.itemColors(),
    onMenuDismiss: () -> Unit
) {

    val datastore = LocalDatastore.current

    SwitchMenuPreference(
        modifier = modifier,
        datastore = datastore,
        key = key,
        initialValue = initialValue,
        title = title,
        leadingContent = leadingContent,
        colors = colors,
        onMenuDismiss = onMenuDismiss
    )
}

/**
 * A Composable function that displays a switch preference item within a dropdown menu.
 * This item allows users to toggle a boolean preference, which is persisted using DataStore.
 *
 * @param modifier Optional [Modifier] for styling the [DropdownMenuItem].
 * @param datastore The DataStore instance to use for this preference.
 * @param key A lambda function that returns the [Preferences.Key] for the boolean preference.
 * @param initialValue A lambda function that returns the initial boolean value of the preference
 * if not already set. Defaults to `false`.
 * @param title A lambda function that returns the title string to be displayed for the preference.
 * @param leadingContent An optional Composable lambda to display content at the leading edge of the
 * menu item (e.g., an icon). Defaults to an empty Composable.
 * @param colors Optional [MenuItemColors] to customize the appearance of the menu item. Defaults to
 * [MenuDefaults.itemColors].
 * @param onMenuDismiss A lambda function that will be invoked when the menu item is clicked,
 * typically to dismiss the dropdown menu.
 */
@Composable
fun SwitchMenuPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>,
    key: Preferences.Key<Boolean>,
    initialValue: Boolean = false,
    title: String,
    leadingContent: @Composable (() -> Unit) = {},
    colors: MenuItemColors = MenuDefaults.itemColors(),
    onMenuDismiss: () -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    val getSwitchState by datastore.getPreference(
        key = key,
        initial = initialValue
    ).collectAsStateWithLifecycle(initialValue = initialValue)

    DropdownMenuItem(
        modifier = modifier,
        colors = colors,
        text = {

            PreferenceTitle(modifier = Modifier.wrapContentWidth(), title = title)
        },
        leadingIcon = leadingContent,
        trailingIcon = {

            Switch(
                checked = getSwitchState,
                thumbContent = {

                    Icon(
                        modifier = Modifier.size(size = SwitchDefaults.IconSize),
                        imageVector = when (getSwitchState) {

                            true -> Icons.Filled.Check
                            false -> Icons.Filled.Close
                        },
                        contentDescription = "Switch Thumb"
                    )
                },
                onCheckedChange = null
            )
        },
        onClick = {

            coroutineScope.launch(context = Dispatchers.IO) {

                datastore.setPreference(key = key, value = getSwitchState.not())
            }

            onMenuDismiss()
        }
    )
}