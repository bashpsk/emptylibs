package io.bashpsk.emptylibs.datastoreui.preference

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.component.DialogConfirmButton
import io.bashpsk.emptylibs.datastoreui.component.DialogResetButton
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.resetPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Composable function for a text field preference item.
 *
 * @param V The type of the value stored in the preference.
 * @param modifier Modifier for styling the preference item.
 * @param datastore The [DataStore] instance to use for this preference. If the DataStore instance
 * is `null`, it will attempt to use the [LocalDatastore] provided via `CompositionLocalProvider`.
 * @param key The [Preferences.Key] for the preference.
 * @param initialValue The initial value for this preference.
 * @param title A Composable lambda function that defines the title of the preference.
 * @param summary A Composable lambda function that defines the summary of the preference,
 * which can depend on the current item value. Defaults to an empty Composable.
 * @param dialogTitle A title for option selection dialog.
 * @param leadingContent Composable content to be displayed at the leading edge of the preference
 * item.
 * @param trailingContent Composable content to be displayed at the trailing edge of the preference
 * item.
 * @param colors Colors for the list item.
 * @param tonalElevation Tonal elevation for the list item.
 * @param shadowElevation Shadow elevation for the list item.
 * @param properties The [DialogProperties] to be applied to the text field dialog.
 * @param textFieldValue The current value of the text field.
 * @param textFieldContent Composable content for the text field.
 * @param confirmButton A Composable lambda function for the confirmation button in the dialog.
 * @param resetButton A Composable lambda function for the reset button in the dialog,
 * which provides access to the [CoroutineScope] and [DataStore].
 */
@Composable
inline fun <reified V> TextFieldPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>?,
    key: Preferences.Key<V>,
    initialValue: V,
    noinline title: @Composable () -> Unit,
    crossinline summary: @Composable (item: V) -> Unit = {},
    dialogTitle: String = "Enter Option",
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
    textFieldValue: TextFieldValue,
    crossinline textFieldContent: @Composable () -> Unit,
    crossinline confirmButton: @Composable CoroutineScope.(
        datastore: DataStore<Preferences>,
        state: MutableTransitionState<Boolean>
    ) -> Unit = { preferenceDatastore, state ->

        DialogConfirmButton {

            (textFieldValue.text as? V)?.let { currentValue ->

                launch(context = Dispatchers.IO) {

                    preferenceDatastore.setPreference(key = key, value = currentValue)
                }
            }

            state.targetState = false
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
            text = { textFieldContent() },
            confirmButton = {

                coroutineScope.confirmButton(preferenceDatastore, dialogVisibleState)
            },
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