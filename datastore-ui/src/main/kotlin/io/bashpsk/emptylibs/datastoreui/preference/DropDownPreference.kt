package io.bashpsk.emptylibs.datastoreui.preference

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
import io.bashpsk.emptylibs.datastoreui.component.PreferenceSummary
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.setPreference
import kotlinx.collections.immutable.ImmutableMap
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
 * @param datastore The [DataStore] instance to use for this preference. If the DataStore instance
 * is `null`, it will attempt to use the [LocalDatastore] provided via `CompositionLocalProvider`.
 * @param key The [Preferences.Key] for this preference.
 * @param initialValue The initial value for this preference if no value is stored.
 * @param entities An [ImmutableMap] of key-value pairs representing the options in the drop-down
 * menu. The key is displayed in the menu, and the value is stored.
 * @param title A Composable lambda function that defines the title text for this preference.
 * @param summary A Composable lambda function that defines the summary text for this preference,
 * which can depend on the current selected entry. Defaults to [PreferenceSummary].
 * @param leadingContent An optional Composable lambda for content to be displayed at the beginning
 * of the preference item.
 * @param trailingContent An optional Composable lambda for content to be displayed at the ending
 * of the preference item, which can depend on whether the menu isMenuExpanded.
 * @param colors [ListItemColors] to be used for this list item.
 * @param tonalElevation The tonal elevation of this list item.
 * @param shadowElevation The shadow elevation of this list item.
 */
@Composable
inline fun <K, V> DropDownPreference(
    modifier: Modifier = Modifier,
    datastore: DataStore<Preferences>?,
    key: Preferences.Key<V>,
    initialValue: V,
    entities: ImmutableMap<K, V>,
    noinline title: @Composable () -> Unit,
    crossinline summary: @Composable (entry: V) -> Unit = {

        PreferenceSummary()
    },
    noinline leadingContent: @Composable () -> Unit = {},
    crossinline trailingContent: @Composable (isMenuExpanded: Boolean) -> Unit = { isMenuExpanded ->

        Icon(
            modifier = Modifier.rotate(degrees = if (isMenuExpanded) 180.0F else 0.0F),
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = "Drop Down Menu"
        )
    },
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation
) {

    val preferenceDatastore = datastore ?: LocalDatastore.current
    val coroutineScope = rememberCoroutineScope()

    val currentValue by preferenceDatastore.getPreference(
        key = key,
        initial = initialValue
    ).collectAsStateWithLifecycle(initialValue = initialValue)

    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }

    val onClick = remember {
        { menuItem: Map.Entry<K, V> ->

            coroutineScope.launch(context = Dispatchers.IO) {

                preferenceDatastore.setPreference(key = key, value = menuItem.value)
            }

            isMenuExpanded = false
        }
    }

    ListItem(
        modifier = modifier.clickable(role = Role.Button, onClick = { isMenuExpanded = true }),
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        leadingContent = leadingContent,
        trailingContent = {

            trailingContent(isMenuExpanded)

            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = {

                    isMenuExpanded = false
                }
            ) {

                entities.forEach { menuItem ->

                    val isSelected by remember(currentValue, menuItem) {
                        derivedStateOf { currentValue == menuItem.value }
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
                        onClick = { onClick(menuItem) }
                    )
                }
            }
        },
        headlineContent = title,
        supportingContent = { summary(currentValue) }
    )
}