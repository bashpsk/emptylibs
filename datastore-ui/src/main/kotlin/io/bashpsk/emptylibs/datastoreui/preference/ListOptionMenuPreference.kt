package io.bashpsk.emptylibs.datastoreui.preference

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.component.DialogConfirmButton
import io.bashpsk.emptylibs.datastoreui.component.DialogResetButton
import io.bashpsk.emptylibs.datastoreui.component.PreferenceListEntryItem
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.resetPreference
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a list of options in a drop-down menu items,
 * allowing the user to select one. The selected option is saved to DataStore.
 *
 * @param modifier Modifier to be applied to the drop-down menu.
 * @param datastore The [DataStore] instance to use for this preference. If the DataStore instance
 * is `null`, it will attempt to use the [LocalDatastore] provided via `CompositionLocalProvider`.
 * @param key The DataStore [Preferences.Key] for this preference.
 * @param initialValue The initial value for this preference.
 * @param entities An [ImmutableMap] of options, where the key is the display name and the value is
 * the actual value to be stored.
 * @param title A Composable lambda function that defines the title to be displayed for the
 * preference.
 * @param dialogTitle The title displayed in the selection dialog.
 * @param leadingContent A Composable lambda function to display content at the beginning of the
 * menu item.
 * @param trailingContent A Composable lambda function to display content at the end of the menu
 * item.
 * @param itemContent Composable lambda for rendering each option in the dialog list.
 * @param colors Colors to be used for the menu item.
 * @param properties The [DialogProperties] to be applied to the option selection dialog.
 * @param onMenuDismiss A lambda function to be called when the dialog is dismissed.
 * @param confirmButton A Composable lambda function for the confirmation button in the dialog.
 * @param resetButton A Composable lambda function for the reset button in the dialog,
 * which provides access to the [CoroutineScope] and [DataStore].
 */
@Composable
inline fun <K, V> ListOptionMenuPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>?,
    key: Preferences.Key<V>,
    initialValue: V,
    entities: ImmutableMap<K, V>,
    noinline title: @Composable () -> Unit,
    dialogTitle: String = "Select Option",
    noinline leadingContent: @Composable () -> Unit = {},
    noinline trailingContent: @Composable () -> Unit = {},
    crossinline itemContent: @Composable CoroutineScope.(
        datastore: DataStore<Preferences>,
        entry: Pair<K, V>,
        selected: Boolean
    ) -> Unit = { preferenceDatastore, entryItem, isSelected ->

        PreferenceListEntryItem(
            modifier = Modifier.fillMaxWidth(),
            preferenceDatastore = preferenceDatastore,
            key = key,
            entryItem = entryItem,
            isSelected = isSelected
        )
    },
    colors: MenuItemColors = MenuDefaults.itemColors(),
    properties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false,
        dismissOnBackPress = true,
        dismissOnClickOutside = false
    ),
    noinline onMenuDismiss: () -> Unit = {},
    crossinline confirmButton: @Composable (
        state: MutableTransitionState<Boolean>
    ) -> Unit = { state ->

        DialogConfirmButton {

            state.targetState = false
            onMenuDismiss()
        }
    },
    crossinline resetButton: @Composable CoroutineScope.(
        datastore: DataStore<Preferences>,
        state: MutableTransitionState<Boolean>
    ) -> Unit = { preferenceDatastore, state ->

        DialogResetButton {

            launch(context = Dispatchers.IO) {

                preferenceDatastore.resetPreference(key = key)
            }

            state.targetState = false
            onMenuDismiss()
        }
    }
) {

    val preferenceDatastore = datastore ?: LocalDatastore.current
    val coroutineScope = rememberCoroutineScope()
    val dialogVisibleState = remember { MutableTransitionState(false) }

    val currentValue by preferenceDatastore.getPreference(
        key = key,
        initial = initialValue
    ).collectAsStateWithLifecycle(initialValue = initialValue)

    AnimatedVisibility(
        visible = dialogVisibleState.targetState,
        enter = fadeIn(),
        exit = fadeOut()
    ) {

        AlertDialog(
            modifier = Modifier.fillMaxWidth(fraction = 0.95F),
            onDismissRequest = {

                dialogVisibleState.targetState = false
                onMenuDismiss()
            },
            properties = properties,
            shape = MaterialTheme.shapes.small,
            title = {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier = Modifier.weight(weight = 1.0F),
                        text = dialogTitle,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(
                        onClick = {

                            dialogVisibleState.targetState = false
                            onMenuDismiss()
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            },
            text = {

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {

                    items(
                        items = entities.toList(),
                        key = { entryItem -> entryItem.first.toString() }
                    ) { entryItem ->

                        val isSelected by remember(currentValue, entryItem) {
                            derivedStateOf { currentValue == entryItem.second }
                        }

                        coroutineScope.itemContent(preferenceDatastore, entryItem, isSelected)
                    }
                }
            },
            confirmButton = { confirmButton(dialogVisibleState) },
            dismissButton = { coroutineScope.resetButton(preferenceDatastore, dialogVisibleState) }
        )
    }

    DropdownMenuItem(
        modifier = modifier,
        colors = colors,
        text = title,
        leadingIcon = leadingContent,
        trailingIcon = trailingContent,
        onClick = {

            dialogVisibleState.targetState = true
        }
    )
}