package io.bashpsk.emptylibs.datastoreui.preference

import androidx.annotation.FloatRange
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.preferences.core.Preferences
import io.bashpsk.emptylibs.datastoreui.extension.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.resetPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import io.bashpsk.emptylibs.datastoreui.resources.DatastoreUIDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Composable function for a text field preference item.
 *
 * @param modifier Modifier for styling the preference item.
 * @param key Lambda function that returns the Preferences.Key for the preference.
 * @param title Lambda function that returns the title of the preference.
 * @param summary Lambda function that returns the summary of the preference.
 * @param leadingContent Composable content to be displayed at the leading edge of the preference
 * item.
 * @param trailingContent Composable content to be displayed at the trailing edge of the preference
 * item.
 * @param colors Colors for the list item.
 * @param tonalElevation Tonal elevation for the list item.
 * @param shadowElevation Shadow elevation for the list item.
 * @param isDismissOnBackPress Whether the dialog should be dismissed on back press.
 * @param isDismissOnClickOutside Whether the dialog should be dismissed when clicking outside.
 * @param textFieldValue The current value of the text field.
 * @param textFieldContent Composable content for the text field.
 * @param summaryAlpha Alpha value for the summary text.
 * @param enableResetButton Lambda function that determines whether the reset button should be
 * enabled.
 */
@Composable
fun TextFieldPreference(
    modifier: Modifier = Modifier,
    key: () -> Preferences.Key<String>,
    title: () -> String,
    summary: () -> String = { "" },
    leadingContent: @Composable (() -> Unit) = {},
    trailingContent: @Composable (() -> Unit) = {},
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    isDismissOnBackPress: Boolean = true,
    isDismissOnClickOutside: Boolean = true,
    textFieldValue: TextFieldValue,
    textFieldContent: @Composable (() -> Unit) = {},
    @FloatRange(from = 0.0, 1.0)
    summaryAlpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA,
    enableResetButton: () -> Boolean = { false }
) {

    val datastore = LocalDatastore.current
    val coroutineScope = rememberCoroutineScope()
    val dialogVisibleState = remember { MutableTransitionState(false) }

    AnimatedVisibility(visibleState = dialogVisibleState) {

        AlertDialog(
            modifier = Modifier.fillMaxWidth(fraction = 0.90F),
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
                        text = title(),
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
            text = textFieldContent,
            confirmButton = {

                when (enableResetButton()) {

                    true -> PreferenceDialogButton(
                        modifier = Modifier.fillMaxWidth(),
                        onDoneClick = {

                            coroutineScope.launch(context = Dispatchers.IO) {

                                datastore.setPreference(key = key(), value = textFieldValue.text)
                            }

                            dialogVisibleState.targetState = false
                        },
                        onResetClick = {

                            coroutineScope.launch(context = Dispatchers.IO) {

                                datastore.resetPreference(key = key())
                            }
                        }
                    )

                    false -> PreferenceDialogButton(
                        modifier = Modifier.fillMaxWidth(),
                        onDoneClick = {

                            coroutineScope.launch(context = Dispatchers.IO) {

                                datastore.setPreference(key = key(), value = textFieldValue.text)
                            }

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

            Text(
                text = title(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        supportingContent = {

            Text(
                modifier = modifier.alpha(alpha = summaryAlpha),
                text = summary(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.labelSmall
            )
        }
    )
}

/**
 * Composable function for a text field preference item.
 *
 * This function displays a preference item with a title and summary. Clicking on the item
 * opens an AlertDialog containing a text field for user input. The entered value is then
 * saved to DataStore.
 *
 * @param V The type of the value to be stored in DataStore.
 * @param modifier Modifier for customizing the layout and appearance of the preference item.
 * @param key A lambda function that returns the DataStore key for this preference.
 * @param title A lambda function that returns the title of the preference item.
 * @param summary A lambda function that returns the summary text displayed below the title.
 * Defaults to an empty string.
 * @param leadingContent A composable lambda for content to be displayed at the beginning of the
 * list item. Defaults to an empty composable.
 * @param trailingContent A composable lambda for content to be displayed at the end of the list
 * item. Defaults to an empty composable.
 * @param colors [ListItemColors] to be used for this list item.
 * @param tonalElevation When `colors.containerColor` is `ColorScheme.surface`, a translucent
 * primary color overlay is applied on top of the container. A higher tonal elevation value will
 * result in a darker color in light theme and lighter color in dark theme. See also: `Surface`.
 * @param shadowElevation The shadow elevation of this list item.
 * @param isDismissOnBackPress Whether the dialog can be dismissed by pressing the back button.
 * Defaults to true.
 * @param isDismissOnClickOutside Whether the dialog can be dismissed by clicking outside the
 * dialog. Defaults to true.
 * @param textFieldValue The current value of the text field.
 * @param textFieldContent A composable lambda for the content of the text field within the dialog.
 * Defaults to an empty composable.
 * @param summaryAlpha The alpha transparency of the summary text, ranging from 0.0
 * (fully transparent) to 1.0 (fully opaque). Defaults to [DatastoreUIDefaults.SUMMARY_ALPHA].
 * @param enableResetButton A lambda function that determines whether a reset button is shown in the
 * dialog. Defaults to false.
 */
@Composable
fun <V> TextFieldPreference(
    modifier: Modifier = Modifier,
    key: () -> Preferences.Key<V>,
    title: () -> String,
    summary: () -> String = { "" },
    leadingContent: @Composable (() -> Unit) = {},
    trailingContent: @Composable (() -> Unit) = {},
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    isDismissOnBackPress: Boolean = true,
    isDismissOnClickOutside: Boolean = true,
    textFieldValue: V,
    textFieldContent: @Composable (() -> Unit) = {},
    @FloatRange(from = 0.0, to = 1.0)
    summaryAlpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA,
    enableResetButton: () -> Boolean = { false }
) {

    val datastore = LocalDatastore.current
    val coroutineScope = rememberCoroutineScope()
    val dialogVisibleState = remember { MutableTransitionState(false) }

    AnimatedVisibility(visibleState = dialogVisibleState) {

        AlertDialog(
            modifier = Modifier.fillMaxWidth(fraction = 0.90F),
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
                        text = title(),
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
            text = textFieldContent,
            confirmButton = {

                when (enableResetButton()) {

                    true -> PreferenceDialogButton(
                        modifier = Modifier.fillMaxWidth(),
                        onDoneClick = {

                            coroutineScope.launch(context = Dispatchers.IO) {

                                datastore.setPreference(key = key(), value = textFieldValue)
                            }

                            dialogVisibleState.targetState = false
                        },
                        onResetClick = {

                            coroutineScope.launch(context = Dispatchers.IO) {

                                datastore.resetPreference(key = key())
                            }
                        }
                    )

                    false -> PreferenceDialogButton(
                        modifier = Modifier.fillMaxWidth(),
                        onDoneClick = {

                            coroutineScope.launch(context = Dispatchers.IO) {

                                datastore.setPreference(key = key(), value = textFieldValue)
                            }

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