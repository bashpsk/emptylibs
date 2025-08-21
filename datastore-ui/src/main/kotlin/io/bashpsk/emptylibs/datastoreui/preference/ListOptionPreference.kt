package io.bashpsk.emptylibs.datastoreui.preference

import androidx.annotation.FloatRange
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.extension.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.resetPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import io.bashpsk.emptylibs.datastoreui.resources.DatastoreUIDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a list of options as a preference.
 *
 * This preference allows the user to select one option from a list. The selected
 * option is saved to DataStore.
 *
 * @param modifier Modifier to be applied to the underlying `ListItem`.
 * @param key A lambda function that returns the [Preferences.Key] for this preference.
 * @param initialValue A lambda function that returns the initial value for this preference.
 * @param entities A lambda function that returns a [Map] of options, where the key (`K`)
 *   is typically the display name and the value (`V`) is the value to be stored.
 * @param title A lambda function that returns the title of the preference.
 * @param summary A lambda function that returns the summary text for the preference.
 *   Defaults to an empty string.
 * @param leadingContent A Composable lambda for displaying content at the beginning of the list
 * item. Defaults to an empty Composable.
 * @param trailingContent A Composable lambda for displaying content at the end of the list item.
 *   Defaults to an empty Composable.
 * @param colors [ListItemColors] to be used for the underlying `ListItem`.
 * @param tonalElevation The tonal elevation of the `ListItem`.
 * @param shadowElevation The shadow elevation of the `ListItem`.
 * @param isDismissOnBackPress Whether the dialog should be dismissed when the back button is
 * pressed. Defaults to `true`.
 * @param isDismissOnClickOutside Whether the dialog should be dismissed when clicking outside its
 * bounds. Defaults to `true`.
 * @param summaryAlpha The alpha (transparency) of the summary text. Defaults to
 *   [DatastoreUIDefaults.SUMMARY_ALPHA].
 * @param enableResetButton A lambda function that returns a boolean indicating whether to show a
 *   "Reset" button in the dialog. Defaults to `false`.
 * @param K The type of the key in the `entities` map (usually the display name).
 * @param V The type of the value in the `entities` map and the type of the preference value.
 */
@Composable
fun <K, V> ListOptionPreference(
    modifier: Modifier = Modifier,
    key: Preferences.Key<V>,
    initialValue: V,
    entities: Map<K, V> = emptyMap(),
    title: String,
    summary: String = "",
    leadingContent: @Composable (() -> Unit) = {},
    trailingContent: @Composable (() -> Unit) = {},
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    isDismissOnBackPress: Boolean = true,
    isDismissOnClickOutside: Boolean = true,
    @FloatRange(from = 0.0, to = 1.0)
    summaryAlpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA,
    enableResetButton:Boolean = false
) {

    val datastore = LocalDatastore.current
    val coroutineScope = rememberCoroutineScope()
    val dialogVisibleState = remember { MutableTransitionState(false) }

    val getOptionSelectedItem by datastore.getPreference(
        key = key,
        initial = initialValue
    ).collectAsStateWithLifecycle(initialValue = initialValue)

    AnimatedVisibility(visibleState = dialogVisibleState) {

        AlertDialog(
            modifier = Modifier.fillMaxWidth(fraction = 0.95F),
            onDismissRequest = {

                dialogVisibleState.targetState = false
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

                        val isSelected by remember(entryItem, getOptionSelectedItem) {
                            derivedStateOf { getOptionSelectedItem == entryItem.second }
                        }

                        OptionItemView(
                            modifier = Modifier.fillMaxWidth(),
                            item = entryItem,
                            isSelected = isSelected,
                            onItemClick = { item ->

                                coroutineScope.launch(context = Dispatchers.IO) {

                                    datastore.setPreference(key = key, value = item.second)
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {

                when (enableResetButton) {

                    true -> PreferenceDialogButton(
                        modifier = Modifier.fillMaxWidth(),
                        onDoneClick = {

                            dialogVisibleState.targetState = false
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
                        }
                    )
                }
            }
        )
    }

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
        trailingContent = trailingContent,
        headlineContent = {

            PreferenceTitle(title = title)
        },
        supportingContent = {

            PreferenceSummary(summary = summary, alpha = summaryAlpha)
        }
    )
}

/**
 * A composable function that displays a single option item in a list.
 * It includes a radio button to indicate selection and the item's text.
 *
 * @param K The type of the key in the key-value pair.
 * @param V The type of the value in the key-value pair.
 * @param modifier The modifier to be applied to the row.
 * @param item The key-value pair representing the option item. The `first` element of the pair is
 * displayed as text.
 * @param isSelected A boolean indicating whether this item is currently selected.
 * @param onItemClick A lambda function that is invoked when the item is clicked. It receives the
 * `item` as a parameter.
 */
@Composable
private fun <K, V> OptionItemView(
    modifier: Modifier = Modifier,
    item: Pair<K, V>,
    isSelected: Boolean,
    onItemClick: (item: Pair<K, V>) -> Unit
) {

    Row(
        modifier = modifier
            .selectable(
                selected = isSelected,
                role = Role.RadioButton,
                onClick = {

                    onItemClick(item)
                }
            )
            .padding(all = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(selected = isSelected, onClick = null)

        Text(
            text = "${item.first}",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}