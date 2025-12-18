package io.bashpsk.emptylibs.datastoreui.preference

import androidx.annotation.FloatRange
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import io.bashpsk.emptylibs.datastoreui.resources.DatastoreUIDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A Composable function that creates a drop-down preference item.
 * This item allows the user to select a value from a list of options.
 * The selected value is stored in DataStore.
 *
 * @param K The type of the key in the `entities` map.
 * @param V The type of the value in the `entities` map and the type of the preference.
 * @param modifier Optional [Modifier] for this Composable.
 * @param key A lambda function that returns the [Preferences.Key] for this preference.
 * @param initialValue A lambda function that returns the initial value for this preference if no
 * value is stored.
 * @param entities A lambda function that returns a [Map] of key-value pairs representing the
 * options in the drop-down menu. The key is displayed in the menu, and the value is stored.
 * Defaults to an empty map.
 * @param title A lambda function that returns the title text for this preference.
 * @param summary A lambda function that returns the summary text for this preference. Defaults to
 * an empty string.
 * @param leadingContent An optional Composable lambda for content to be displayed at the beginning
 * of the preference item.
 * @param colors [ListItemColors] to be used for this list item.
 * @param tonalElevation The tonal elevation of this list item.
 * @param shadowElevation The shadow elevation of this list item.
 * @param summaryAlpha The alpha value for the summary text, ranging from 0.0 to 1.0. Defaults to
 * [DatastoreUIDefaults.SUMMARY_ALPHA].
 *
 * Note: Must be provide `LocalDatastore` using `CompositionLocalProvider`.
 */
@Composable
fun <K, V> DropDownPreference(
    modifier: Modifier = Modifier,
    key: Preferences.Key<V>,
    initialValue: V,
    entities: Map<K, V> = emptyMap(),
    title: String,
    summary: String = "",
    leadingContent: @Composable (() -> Unit) = {},
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    @FloatRange(from = 0.0, to = 1.0)
    summaryAlpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA
) {

    val datastore = LocalDatastore.current

    DropDownPreference(
        modifier = modifier,
        datastore = datastore,
        key = key,
        initialValue = initialValue,
        entities = entities,
        title = title,
        summary = summary,
        leadingContent = leadingContent,
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        summaryAlpha = summaryAlpha
    )
}

/**
 * A Composable function that creates a drop-down preference item.
 * This item allows the user to select a value from a list of options.
 * The selected value is stored in DataStore.
 *
 * @param K The type of the key in the `entities` map.
 * @param V The type of the value in the `entities` map and the type of the preference.
 * @param modifier Optional [Modifier] for this Composable.
 * @param datastore The DataStore instance to use for this preference.
 * @param key A lambda function that returns the [Preferences.Key] for this preference.
 * @param initialValue A lambda function that returns the initial value for this preference if no
 * value is stored.
 * @param entities A lambda function that returns a [Map] of key-value pairs representing the
 * options in the drop-down menu. The key is displayed in the menu, and the value is stored.
 * Defaults to an empty map.
 * @param title A lambda function that returns the title text for this preference.
 * @param summary A lambda function that returns the summary text for this preference. Defaults to
 * an empty string.
 * @param leadingContent An optional Composable lambda for content to be displayed at the beginning
 * of the preference item.
 * @param colors [ListItemColors] to be used for this list item.
 * @param tonalElevation The tonal elevation of this list item.
 * @param shadowElevation The shadow elevation of this list item.
 * @param summaryAlpha The alpha value for the summary text, ranging from 0.0 to 1.0. Defaults to
 * [DatastoreUIDefaults.SUMMARY_ALPHA].
 */
@Composable
fun <K, V> DropDownPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>,
    key: Preferences.Key<V>,
    initialValue: V,
    entities: Map<K, V> = emptyMap(),
    title: String,
    summary: String = "",
    leadingContent: @Composable (() -> Unit) = {},
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    @FloatRange(from = 0.0, to = 1.0)
    summaryAlpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA
) {

    val coroutineScope = rememberCoroutineScope()

    val getSelectedItem by datastore.getPreference(
        key = key,
        initial = initialValue
    ).collectAsStateWithLifecycle(initialValue = initialValue)

    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }

    val menuArrowDegree by remember(isMenuExpanded) {
        derivedStateOf { if (isMenuExpanded) 180.0F else 0.0F }
    }

    ListItem(
        modifier = modifier
            .clickable(
                role = Role.Button,
                onClick = {

                    isMenuExpanded = true
                }
            ),
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        leadingContent = leadingContent,
        trailingContent = {

            Icon(
                modifier = Modifier.rotate(degrees = menuArrowDegree),
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Drop Down Menu"
            )

            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = {

                    isMenuExpanded = false
                }
            ) {

                entities.forEach { menuItem ->

                    val isSelected by remember(getSelectedItem, menuItem) {
                        derivedStateOf { getSelectedItem == menuItem.value }
                    }

                    DropdownMenuItem(
                        text = {

                            Text(
                                text = "${menuItem.key}",
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        trailingIcon = {

                            AnimatedVisibility(
                                visible = isSelected,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {

                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected"
                                )
                            }
                        },
                        onClick = {

                            coroutineScope.launch(context = Dispatchers.IO) {

                                datastore.setPreference(key = key, value = menuItem.value)
                            }

                            isMenuExpanded = false
                        }
                    )
                }
            }
        },
        headlineContent = {

            PreferenceTitle(title = title)
        },
        supportingContent = {

            PreferenceSummary(summary = summary, alpha = summaryAlpha)
        }
    )
}