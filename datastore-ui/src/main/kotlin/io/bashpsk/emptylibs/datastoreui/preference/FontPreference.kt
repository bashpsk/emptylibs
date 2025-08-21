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
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.extension.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.resetPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import io.bashpsk.emptylibs.datastoreui.font.rememberFontRes
import io.bashpsk.emptylibs.datastoreui.resources.DatastoreUIDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a font preference item.
 * This preference allows the user to select a font from a list of available fonts.
 * The selected font is saved in DataStore.
 *
 * @param modifier The modifier to be applied to the preference item.
 * @param key A lambda function that returns the DataStore key for the font preference.
 * @param initialValue A lambda function that returns the initial value of the font preference.
 * @param entities A lambda function that returns a map of font resource IDs to font names.
 * @param title A lambda function that returns the title of the preference item.
 * @param summary A lambda function that returns the summary of the preference item.
 * @param previewText A lambda function that returns the text to be displayed in the font preview.
 * @param leadingContent A Composable function that displays content at the beginning of the
 * preference item.
 * @param trailingContent A Composable function that displays content at the end of the preference
 * item.
 * @param colors The colors to be used for the preference item.
 * @param tonalElevation The tonal elevation of the preference item.
 * @param shadowElevation The shadow elevation of the preference item.
 * @param isDismissOnBackPress Whether the dialog should be dismissed when the back button is
 * pressed.
 * @param isDismissOnClickOutside Whether the dialog should be dismissed when the user clicks
 * outside of it.
 * @param summaryAlpha The alpha value for the summary text.
 * @param enableResetButton A lambda function that returns whether the reset button should be
 * enabled.
 */
@Composable
fun FontPreference(
    modifier: Modifier = Modifier,
    key: Preferences.Key<String>,
    initialValue: String,
    entities: Map<Int, String> = emptyMap(),
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

    val datastore = LocalDatastore.current
    val coroutineScope = rememberCoroutineScope()

    val getSelectedItem by datastore.getPreference(
        key = key,
        initial = initialValue
    ).collectAsStateWithLifecycle(initialValue = initialValue)

    val dialogVisibleState = remember { MutableTransitionState(false) }

    val selectedFontRes by rememberFontRes(id = getSelectedItem, entities = entities)

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
                            derivedStateOf { getSelectedItem == fontItem.second }
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
                                                value = fontItem.second
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
                                fontFamily = FontFamily(Font(resId = fontItem.first))
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
                fontFamily = when (selectedFontRes != null) {

                    true -> FontFamily(Font(resId = selectedFontRes!!))
                    false -> FontFamily.Default
                }
            )
        }
    )
}