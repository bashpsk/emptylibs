package io.bashpsk.emptylibs.datastoreui.preference

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
import io.bashpsk.emptylibs.datastoreui.component.DialogConfirmButton
import io.bashpsk.emptylibs.datastoreui.component.DialogResetButton
import io.bashpsk.emptylibs.datastoreui.component.PreferenceSummary
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.resetPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import io.bashpsk.emptylibs.datastoreui.font.FontPreferenceItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a font preference item using the [LocalDatastore].
 * This preference allows the user to select a font from a list of available [entities].
 * The selected font label is persisted in the DataStore.
 *
 * @param modifier The [Modifier] to be applied to the preference item.
 * @param datastore The [DataStore] instance to use for this preference. If the DataStore instance
 * is `null`, it will attempt to use the [LocalDatastore] provided via `CompositionLocalProvider`.
 * @param key The [Preferences.Key] used to store the selected font label.
 * @param entities An [ImmutableList] of [FontPreferenceItem] containing font resources and labels.
 * @param title A Composable lambda function that defines the title text for the preference item.
 * @param summary A Composable lambda function that defines the summary text for the preference
 * item, which can depend on the current [font] family. Defaults to [PreferenceSummary].
 * @param dialogTitle The title displayed in the font selection dialog.
 * @param previewText The sample text displayed in the selection dialog to preview different fonts.
 * @param leadingContent An optional Composable to be displayed at the start of the item.
 * @param trailingContent An optional Composable to be displayed at the end of the item.
 * @param colors [ListItemColors] to be used for the preference item.
 * @param tonalElevation The tonal elevation of the preference item.
 * @param shadowElevation The shadow elevation of the preference item.
 * @param properties The [DialogProperties] to be applied to the font selection dialog.
 * @param confirmButton A Composable lambda function for the confirmation button in the dialog.
 * @param resetButton A Composable lambda function for the reset button in the dialog,
 * which provides access to the [CoroutineScope] and [DataStore].
 */
@Composable
inline fun FontPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>?,
    key: Preferences.Key<String>,
    entities: ImmutableList<FontPreferenceItem>,
    noinline title: @Composable () -> Unit,
    crossinline summary: @Composable (font: FontFamily) -> Unit = { font ->

        PreferenceSummary(fontFamily = font)
    },
    dialogTitle: String = "Select Font",
    previewText: String = "This is sample text. Time is 03:33 AM",
    noinline leadingContent: @Composable () -> Unit = {},
    noinline trailingContent: @Composable () -> Unit = {},
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

    val currentValue by preferenceDatastore.getPreference(
        key = key,
        entities = entities
    ).collectAsStateWithLifecycle(initialValue = null)

    val dialogVisibleState = remember { MutableTransitionState(false) }

    val currentFontFamily by remember(currentValue) {
        derivedStateOf {
            currentValue?.resId?.let { fontRes ->

                FontFamily(Font(resId = fontRes))
            } ?: FontFamily.Default
        }
    }

    val onClick = remember { { dialogVisibleState.targetState = true } }

    AnimatedVisibility(visibleState = dialogVisibleState) {

        AlertDialog(
            modifier = Modifier.fillMaxWidth(fraction = 0.95F),
            onDismissRequest = {

                dialogVisibleState.targetState = false
            },
            properties = properties,
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

                    items(
                        items = entities,
                        key = { fontItem -> fontItem.label }
                    ) { fontItem ->

                        val isSelected by remember(currentValue, fontItem) {
                            derivedStateOf { currentValue?.label == fontItem.label }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = isSelected,
                                    role = Role.RadioButton,
                                    onClick = {

                                        coroutineScope.launch(context = Dispatchers.IO) {

                                            preferenceDatastore.setPreference(
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
        supportingContent = { summary(currentFontFamily) }
    )
}