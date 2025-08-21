package io.bashpsk.emptylibs.datastoreui.preference

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.extension.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.resetPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a list of options in a drop-down menu items,
 * allowing the user to select one. The selected option is saved to DataStore.
 *
 * @param modifier Modifier to be applied to the drop-down menu.
 * @param key A lambda function that returns the DataStore key for this preference.
 * @param initialValue A lambda function that returns the initial value for this preference.
 * @param entities A lambda function that returns a map of options, where the key is the display
 * name and the value is the actual value to be stored.
 * @param title A lambda function that returns the title to be displayed for the preference.
 * @param leadingContent A Composable lambda function to display content at the beginning of the
 * menu item.
 * @param trailingContent A Composable lambda function to display content at the end of the menu
 * item.
 * @param colors Colors to be used for the menu item.
 * @param isDismissOnBackPress Whether the dialog should be dismissed when the back button is
 * pressed.
 * @param isDismissOnClickOutside Whether the dialog should be dismissed when clicking outside the
 * dialog.
 * @param onMenuDismiss A lambda function to be called when the dialog is dismissed.
 * @param enableResetButton A lambda function that determines whether a reset button should be shown
 * in the dialog.
 */
@Composable
fun <K, V> ListOptionMenuPreference(
    modifier: Modifier = Modifier,
    key: Preferences.Key<V>,
    initialValue: V,
    entities: Map<K, V> = emptyMap(),
    title: String,
    leadingContent: @Composable (() -> Unit) = {},
    trailingContent: @Composable (() -> Unit) = {},
    colors: MenuItemColors = MenuDefaults.itemColors(),
    isDismissOnBackPress: Boolean = true,
    isDismissOnClickOutside: Boolean = true,
    onMenuDismiss: () -> Unit = {},
    enableResetButton: Boolean = false
) {

    val datastore = LocalDatastore.current
    val coroutineScope = rememberCoroutineScope()
    val dialogVisibleState = remember { MutableTransitionState(initialState = false) }

    val getSelectedItem by datastore.getPreference(
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
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = isDismissOnBackPress,
                dismissOnClickOutside = isDismissOnClickOutside
            ),
            shape = MaterialTheme.shapes.small,
            title = {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier = Modifier.weight(weight = 1.0F),
                        text = title,
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

                        val isSelected by remember(getSelectedItem, entryItem) {
                            derivedStateOf { getSelectedItem == entryItem.second }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = isSelected,
                                    role = Role.RadioButton,
                                    onClick = {

                                        coroutineScope.launch(context = Dispatchers.IO) {

                                            datastore.setPreference(
                                                key = key,
                                                value = entryItem.second
                                            )
                                        }
                                    }
                                )
                                .padding(all = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            RadioButton(selected = isSelected, onClick = null)

                            Text(
                                text = "${entryItem.first}",
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            },
            confirmButton = {

                when (enableResetButton) {

                    true -> PreferenceDialogButton(
                        modifier = Modifier.fillMaxWidth(),
                        onDoneClick = {

                            dialogVisibleState.targetState = false
                            onMenuDismiss()
                        },
                        onResetClick = {

                            coroutineScope.launch(context = Dispatchers.IO) {

                                datastore.resetPreference(key = key)
                            }
                        }
                    )

                    false -> PreferenceDialogButton(
                        modifier = Modifier.fillMaxWidth(),
                        onDoneClick = {

                            dialogVisibleState.targetState = false
                            onMenuDismiss()
                        }
                    )
                }
            }
        )
    }

    DropdownMenuItem(
        modifier = modifier,
        colors = colors,
        text = {

            PreferenceTitle(modifier = Modifier.wrapContentWidth(), title = title)
        },
        leadingIcon = leadingContent,
        trailingIcon = trailingContent,
        onClick = {

            dialogVisibleState.targetState = true
        }
    )
}