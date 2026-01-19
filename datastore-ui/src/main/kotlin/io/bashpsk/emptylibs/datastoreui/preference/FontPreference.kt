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
import androidx.compose.material3.AlertDialogDefaults
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import io.bashpsk.emptylibs.datastoreui.font.FontPreferenceItem
import io.bashpsk.emptylibs.datastoreui.resources.DatastoreUIDefaults
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a font preference item using the [LocalDatastore].
 * This preference allows the user to select a font from a list of available [entities].
 * The selected font label is persisted in the DataStore.
 *
 * @param modifier The [Modifier] to be applied to the preference item.
 * @param key The [Preferences.Key] used to store the selected font label.
 * @param entities An [ImmutableList] of [FontPreferenceItem] containing font resources and labels.
 * @param title The title text for the preference item.
 * @param summary The summary text for the preference item.
 * @param previewText The sample text displayed in the selection dialog to preview different fonts.
 * @param leadingContent An optional Composable to be displayed at the start of the item.
 * @param trailingContent An optional Composable to be displayed at the end of the item.
 * @param colors [ListItemColors] to be used for the preference item.
 * @param tonalElevation The tonal elevation of the preference item.
 * @param shadowElevation The shadow elevation of the preference item.
 * @param isDismissOnBackPress Whether the dialog can be dismissed by pressing the back button.
 * @param isDismissOnClickOutside Whether the dialog can be dismissed by clicking outside.
 * @param summaryAlpha The alpha value applied to the summary text.
 * @param enableResetButton Whether to show a reset button in the selection dialog.
 *
 * @note This version requires [LocalDatastore] to be provided via `CompositionLocalProvider`.
 */
@Composable
fun FontPreference(
    modifier: Modifier = Modifier,
    key: Preferences.Key<String>,
    entities: ImmutableList<FontPreferenceItem>,
    title: String,
    summary: String = "",
    previewText: String = "This is sample text. Time: 03:33.069 AM",
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

    FontPreference(
        modifier = modifier,
        datastore = datastore,
        key = key,
        entities = entities,
        title = title,
        summary = summary,
        previewText = previewText,
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
 * A Composable function that displays a font preference item using the [LocalDatastore].
 * This preference allows the user to select a font from a list of available [entities].
 * The selected font label is persisted in the DataStore.
 *
 * @param modifier The [Modifier] to be applied to the preference item.
 * @param datastore The [DataStore] instance to be used for preference management.
 * @param key The [Preferences.Key] used to store the selected font label.
 * @param entities An [ImmutableList] of [FontPreferenceItem] containing font resources and labels.
 * @param title The title text for the preference item.
 * @param summary The summary text for the preference item.
 * @param previewText The sample text displayed in the selection dialog to preview different fonts.
 * @param leadingContent An optional Composable to be displayed at the start of the item.
 * @param trailingContent An optional Composable to be displayed at the end of the item.
 * @param colors [ListItemColors] to be used for the preference item.
 * @param tonalElevation The tonal elevation of the preference item.
 * @param shadowElevation The shadow elevation of the preference item.
 * @param isDismissOnBackPress Whether the dialog can be dismissed by pressing the back button.
 * @param isDismissOnClickOutside Whether the dialog can be dismissed by clicking outside.
 * @param summaryAlpha The alpha value applied to the summary text.
 * @param enableResetButton Whether to show a reset button in the selection dialog.
 *
 * @note This version requires [LocalDatastore] to be provided via `CompositionLocalProvider`.
 */
@Composable
fun FontPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>,
    key: Preferences.Key<String>,
    entities: ImmutableList<FontPreferenceItem>,
    title: String,
    summary: String = "",
    previewText: String = "This is sample text. Time - 3:33 AM",
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

    val getSelectedItem by datastore.getPreference(
        key = key,
        entities = entities
    ).collectAsStateWithLifecycle(initialValue = null)

    val dialogVisibleState = remember { MutableTransitionState(false) }

    val selectedFontRes by remember(getSelectedItem) {
        derivedStateOf {
            getSelectedItem?.resId?.let { fontRes ->

                FontFamily(Font(resId = fontRes))
            } ?: FontFamily.Default
        }
    }

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
            titleContentColor = AlertDialogDefaults.titleContentColor,
            containerColor = AlertDialogDefaults.containerColor.copy(alpha = 0.70f),
            textContentColor = AlertDialogDefaults.textContentColor,
            iconContentColor = AlertDialogDefaults.iconContentColor,
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

                    items(items = entities.toList()) { fontItem ->

                        val isSelected by remember(getSelectedItem, fontItem) {
                            derivedStateOf { getSelectedItem?.label == fontItem.label }
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
                                                value = fontItem.label
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
                                text = previewText,
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily(Font(resId = fontItem.resId))
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

            PreferenceSummary(
                summary = summary,
                alpha = summaryAlpha,
                fontFamily = selectedFontRes
            )
        }
    )
}