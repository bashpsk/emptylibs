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
import androidx.compose.material3.Checkbox
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
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.resetPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import io.bashpsk.emptylibs.datastoreui.resources.DatastoreUIDefaults
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
 * @param isDismissOnBackPress Whether the dialog should be dismissed when the back button is
 * pressed. Defaults to `true`.
 * @param isDismissOnClickOutside Whether the dialog should be dismissed when clicking outside the
 * dialog. Defaults to `true`.
 * @param summaryAlpha The alpha transparency for the summary text, ranging from 0.0 to 1.0.
 * Defaults to [DatastoreUIDefaults.SUMMARY_ALPHA].
 * @param enableResetButton A lambda function that returns `true` if a reset button should be shown
 * in the dialog, `false` otherwise. Defaults to `false`.
 *
 * Note: Must be provide `LocalDatastore` using `CompositionLocalProvider`.
 */
@Composable
fun <K> SetOptionPreference(
    modifier: Modifier = Modifier,
    key: Preferences.Key<Set<String>>,
    initialValue: Set<String>,
    entities: Map<K, String> = emptyMap(),
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
    enableResetButton: Boolean = false
) {

    val datastore = LocalDatastore.current

    SetOptionPreference(
        modifier = modifier,
        datastore = datastore,
        key = key,
        initialValue = initialValue,
        entities = entities,
        title = title,
        summary = summary,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        isDismissOnBackPress = isDismissOnBackPress,
        isDismissOnClickOutside = isDismissOnClickOutside,
        summaryAlpha = summaryAlpha,
        enableResetButton = enableResetButton
    )
}

/**
 * A Composable function that displays a preference item with multiple selectable options.
 * When clicked, it shows a dialog with a list of options (entities) where the user can select
 * multiple items.
 * The selected items are stored in DataStore.
 *
 * @param K The type of the key for the entities map.
 * @param modifier Optional [Modifier] for the preference item.
 * @param datastore The DataStore instance to use for this preference.
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
 * @param isDismissOnBackPress Whether the dialog should be dismissed when the back button is
 * pressed. Defaults to `true`.
 * @param isDismissOnClickOutside Whether the dialog should be dismissed when clicking outside the
 * dialog. Defaults to `true`.
 * @param summaryAlpha The alpha transparency for the summary text, ranging from 0.0 to 1.0.
 * Defaults to [DatastoreUIDefaults.SUMMARY_ALPHA].
 * @param enableResetButton A lambda function that returns `true` if a reset button should be shown
 * in the dialog, `false` otherwise. Defaults to `false`.
 */
@Composable
fun <K> SetOptionPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>,
    key: Preferences.Key<Set<String>>,
    initialValue: Set<String>,
    entities: Map<K, String> = emptyMap(),
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
    enableResetButton: Boolean = false
) {

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
                            derivedStateOf { getOptionSelectedItem.contains(entryItem.second) }
                        }

                        OptionItemView(
                            modifier = Modifier.fillMaxWidth(),
                            item = entryItem,
                            isSelected = isSelected,
                            onItemClick = { item ->

                                coroutineScope.launch(context = Dispatchers.IO) {

                                    val newEntities = when (isSelected) {

                                        true -> getOptionSelectedItem - item.second
                                        false -> getOptionSelectedItem + item.second
                                    }

                                    datastore.setPreference(key = key, value = newEntities)
                                }

                                dialogVisibleState.targetState = false
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
 * Composable function that represents a single option item in the list.
 *
 * @param K The type of the key in the item pair.
 * @param modifier Modifier to be applied to the Row.
 * @param item The pair of key and string representing the option item.
 * @param isSelected Boolean indicating whether the item is currently selected.
 * @param onItemClick Lambda function to be invoked when the item is clicked.
 */
@Composable
private fun <K> OptionItemView(
    modifier: Modifier = Modifier,
    item: Pair<K, String>,
    isSelected: Boolean,
    onItemClick: (item: Pair<K, String>) -> Unit
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

        Checkbox(checked = isSelected, onCheckedChange = null)

        Text(
            text = "${item.first}",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}