package io.bashpsk.emptylibs.datastoreui.component

import androidx.annotation.ColorInt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import io.bashpsk.emptylibs.datastoreui.resources.DatastoreUIDefaults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Composable function for displaying a preference title.
 *
 * @param modifier The modifier to be applied to the component.
 * @param title A lambda function that returns the title string to be displayed.
 */
@Composable
fun PreferenceTitle(modifier: Modifier = Modifier, title: String) {

    Text(
        modifier = modifier.fillMaxWidth(),
        text = title,
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.bodyMedium
    )
}

/**
 * Composable function to display the summary of a preference.
 *
 * @param modifier The modifier to be applied to the component.
 * @param summary A lambda function that returns the summary string to be displayed.
 * @param alpha The alpha value for the summary text. Defaults to
 * [DatastoreUIDefaults.SUMMARY_ALPHA].
 */
@Composable
fun PreferenceSummary(
    modifier: Modifier = Modifier,
    summary: String = "",
    alpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA
) {

    Text(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha = alpha),
        text = summary,
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.labelSmall
    )
}

/**
 * Displays a summary text for a preference item.
 *
 * This composable is used to display a brief description or additional information
 * related to a preference.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param summary A lambda function that returns the summary string to be displayed. Defaults to an
 * empty string.
 * @param alpha The transparency of the summary text. Defaults to
 * [DatastoreUIDefaults.SUMMARY_ALPHA].
 * @param fontFamily The font family to be used for the summary text.
 */
@Composable
fun PreferenceSummary(
    modifier: Modifier = Modifier,
    summary: String = "",
    alpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA,
    fontFamily: FontFamily
) {

    Text(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha = alpha),
        text = summary,
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.labelSmall,
        fontFamily = fontFamily
    )
}

/**
 * Composable function that displays a preview box for a color preference.
 *
 * This box is typically used to provide a visual representation of the currently selected
 * color within a preference item.
 *
 * @param modifier The [Modifier] to be applied to the preview box.
 * @param color The color to be displayed, represented as a color integer.
 */
@Composable
fun PreferenceColorPreviewBox(modifier: Modifier = Modifier, @ColorInt color: Int) {

    Box(
        modifier = modifier
            .size(width = 24.dp, height = 24.dp)
            .clip(shape = MaterialTheme.shapes.extraSmall)
            .border(
                width = 0.8.dp,
                color = MaterialTheme.colorScheme.surfaceTint,
                shape = MaterialTheme.shapes.extraSmall
            )
            .background(color = Color(color = color))
    )
}

/**
 * A composable function that represents a single selectable item within a preference selection UI.
 * This item displays a radio button, label and handles updating the [DataStore] when selected.
 *
 * @receiver The [CoroutineScope] used to launch the preference update operation.
 * @param K The type of the display label.
 * @param V The type of the value to be stored in the [DataStore].
 * @param modifier The [Modifier] to be applied to the item's layout.
 * @param preferenceDatastore The [DataStore] instance where the preference is stored.
 * @param key The [Preferences.Key] used to identify the specific preference.
 * @param entryItem A [Pair] containing the display label ([Pair.first]) and the actual value
 * ([Pair.second]).
 * @param isSelected A boolean flag indicating whether this specific item is currently selected.
 */
@PublishedApi
@Composable
internal fun <K, V> CoroutineScope.PreferenceListEntryItem(
    modifier: Modifier = Modifier,
    preferenceDatastore: DataStore<Preferences>,
    key: Preferences.Key<V>,
    entryItem: Pair<K, V>,
    isSelected: Boolean,
) {

    Row(
        modifier = modifier
            .selectable(
                selected = isSelected,
                role = Role.RadioButton,
                onClick = {

                    launch(context = Dispatchers.IO) {

                        preferenceDatastore.setPreference(key = key, value = entryItem.second)
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

/**
 * A composable function that represents a multi selectable item within a preference selection UI.
 * This item displays a checkbox, label and handles updating the [DataStore] when selected.
 *
 * @receiver The [CoroutineScope] used to launch the preference update operation.
 * @param K The type of the display label.
 * @param modifier The [Modifier] to be applied to the item's layout.
 * @param preferenceDatastore The [DataStore] instance where the preference is stored.
 * @param key The [Preferences.Key] used to identify the specific preference.
 * @param entryItem A [Pair] containing the display label ([Pair.first]) and the actual value
 * ([Pair.second]).
 * @param isSelected A boolean flag indicating whether this specific item is currently selected.
 */
@PublishedApi
@Composable
internal fun <K> CoroutineScope.PreferenceListEntryItem(
    modifier: Modifier = Modifier,
    preferenceDatastore: DataStore<Preferences>,
    key: Preferences.Key<Set<String>>,
    selectedItems: Set<String>,
    entryItem: Pair<K, String>,
    isSelected: Boolean,
) {

    Row(
        modifier = modifier
            .selectable(
                selected = isSelected,
                role = Role.Checkbox,
                onClick = {

                    launch(context = Dispatchers.IO) {

                        val newEntities = when (isSelected) {

                            true -> selectedItems - entryItem.second
                            false -> selectedItems + entryItem.second
                        }

                        preferenceDatastore.setPreference(key = key, value = newEntities)
                    }
                }
            )
            .padding(all = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Checkbox(checked = isSelected, onCheckedChange = null)

        Text(
            text = "${entryItem.first}",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@PublishedApi
@Composable
internal fun SwitchButton(checked: Boolean) {

    Switch(
        checked = checked,
        thumbContent = {

            Icon(
                modifier = Modifier.size(size = SwitchDefaults.IconSize),
                imageVector = if (checked) Icons.Filled.Done else Icons.Filled.Close,
                contentDescription = "Switch Thumb"
            )
        },
        onCheckedChange = null
    )
}

@PublishedApi
@Composable
internal fun DialogConfirmButton(onClick: () -> Unit) {

    Button(onClick = onClick) {

        Icon(imageVector = Icons.Filled.Done, contentDescription = "Done")

        Spacer(modifier = Modifier.width(width = 2.dp))

        Text(
            text = "Done",
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@PublishedApi
@Composable
internal fun DialogResetButton(onClick: () -> Unit) {

    OutlinedButton(onClick = onClick) {

        Icon(imageVector = Icons.Filled.Restore, contentDescription = "Reset")

        Spacer(modifier = Modifier.width(width = 2.dp))

        Text(
            text = "Reset",
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}