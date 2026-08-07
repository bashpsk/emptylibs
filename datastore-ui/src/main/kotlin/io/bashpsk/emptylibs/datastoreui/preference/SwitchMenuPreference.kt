package io.bashpsk.emptylibs.datastoreui.preference

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
 * A Composable function that displays a switch preference item within a dropdown menu.
 * This item allows users to toggle a boolean preference, which is persisted using DataStore.
 *
 * @param modifier Optional [Modifier] for styling the [DropdownMenuItem].
 * @param datastore The [DataStore] instance to use for this preference. If the DataStore instance
 * is `null`, it will attempt to use the [LocalDatastore] provided via `CompositionLocalProvider`.
 * @param key The [Preferences.Key] for the boolean preference.
 * @param initialValue The initial boolean value of the preference if not already set.
 * Defaults to `false`.
 * @param title A Composable lambda function that defines the title string to be displayed for the
 * preference.
 * @param leadingContent An optional Composable lambda to display content at the leading edge of the
 * menu item (e.g., an icon). Defaults to an empty Composable.
 * @param trailingContent A Composable lambda for displaying content at the end of the menu item,
 * which can depend on the current checked state. Defaults to a [SwitchButton].
 * @param colors Optional [MenuItemColors] to customize the appearance of the menu item. Defaults to
 * [MenuDefaults.itemColors].
 * @param onMenuDismiss A lambda function that will be invoked when the menu item is clicked,
 * typically to dismiss the dropdown menu.
 */
@Composable
inline fun SwitchMenuPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>?,
    key: Preferences.Key<Boolean>,
    initialValue: Boolean = false,
    noinline title: @Composable () -> Unit,
    noinline leadingContent: @Composable () -> Unit = {},
    crossinline trailingContent: @Composable (checked: Boolean) -> Unit = { checked ->

        SwitchButton(checked = checked)
    },
    colors: MenuItemColors = MenuDefaults.itemColors(),
    crossinline onMenuDismiss: () -> Unit = {}
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

            onMenuDismiss()
        }
    }

    DropdownMenuItem(
        modifier = modifier,
        colors = colors,
        text = title,
        leadingIcon = leadingContent,
        trailingIcon = { trailingContent(currentValue) },
        onClick = onClick
    )
}