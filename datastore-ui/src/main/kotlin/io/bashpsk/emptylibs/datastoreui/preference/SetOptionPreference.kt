package io.bashpsk.emptylibs.datastoreui.preference

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.Dp
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
 * A Composable function that displays a preference item with multiple selectable options.
 * When clicked, it shows a dialog with a list of options (entities) where the user can select
 * multiple items.
 * The selected items are stored in DataStore.
 *
 * @param K The type of the key for the entities map.
 * @param modifier Optional [Modifier] for the preference item.
 * @param datastore The DataStore instance to use for this preference. If DataStore instance is
 * `null` must be provided [LocalDatastore] using `CompositionLocalProvider`.
 * @param key A lambda function that returns the [Preferences.Key] for storing the selected set of
 * strings.
 * @param initialValue A lambda function that returns the initial set of selected string values.
 * @param entities A lambda function that returns a map of options, where the key is of type [K] and
 * the value is the display string. Defaults to an empty map.
 * @param title A lambda function that returns the title of the preference.
 * @param summary A lambda function that returns the summary text displayed below the title.
 * Defaults to an empty string.
 * @param leadingContent A Composable lambda for content to be displayed at the beginning of the
 * preference item.
 * @param trailingContent A Composable lambda for content to be displayed at the end of the
 * preference item.
 * @param colors [ListItemColors] to be used for the preference item.
 * @param tonalElevation The tonal elevation of the preference item.
 * @param shadowElevation The shadow elevation of the preference item.
 * @param properties The [DialogProperties] to be applied to the option selection dialog.
 * @param confirmButton A Composable lambda function for the confirmation button in the dialog.
 * @param resetButton A Composable lambda function for the reset button in the dialog,
 * which provides access to the [CoroutineScope] and [DataStore].
 */
@Composable
inline fun <K> SetOptionPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>?,
    key: Preferences.Key<Set<String>>,
    initialValue: Set<String>,
    entities: ImmutableMap<K, String>,
    noinline title: @Composable () -> Unit,
    crossinline summary: @Composable (items: Set<String>) -> Unit = {},
    dialogTitle: String = "Select Options",
    noinline leadingContent: @Composable () -> Unit = {},
    noinline trailingContent: @Composable () -> Unit = {},
    crossinline itemContent: @Composable CoroutineScope.(
        datastore: DataStore<Preferences>,
        items: Set<String>,
        entry: Pair<K, String>,
        selected: Boolean
    ) -> Unit = { preferenceDatastore, selectedItems, entryItem, isSelected ->

        PreferenceListEntryItem(
            modifier = Modifier.fillMaxWidth(),
            preferenceDatastore = preferenceDatastore,
            key = key,
            selectedItems = selectedItems,
            entryItem = entryItem,
            isSelected = isSelected
        )
    },
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    properties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false,
        dismissOnBackPress = true,
        dismissOnClickOutside = false
    ),
    crossinline confirmButton: @Composable (state: MutableTransitionState<Boolean>) -> Unit = { state ->

        DialogConfirmButton { state.targetState = false }
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

    val onClick = remember { { dialogVisibleState.targetState = true } }

    AnimatedVisibility(visibleState = dialogVisibleState) {

        AlertDialog(
            modifier = Modifier.fillMaxWidth(fraction = 0.95F),
            onDismissRequest = {

                dialogVisibleState.targetState = false
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

                    items(items = entities.toList()) { entryItem ->

                        val isSelected by remember(entryItem, currentValue) {
                            derivedStateOf { currentValue.contains(entryItem.second) }
                        }

                        coroutineScope.itemContent(
                            preferenceDatastore,
                            currentValue,
                            entryItem,
                            isSelected
                        )
                    }
                }
            },
            confirmButton = { confirmButton(dialogVisibleState) },
            dismissButton = { coroutineScope.resetButton(preferenceDatastore, dialogVisibleState) }
        )
    }

    ListItem(
        modifier = modifier.clickable(role = Role.Button, onClick = onClick),
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        headlineContent = title,
        supportingContent = { summary(currentValue) }
    )
}