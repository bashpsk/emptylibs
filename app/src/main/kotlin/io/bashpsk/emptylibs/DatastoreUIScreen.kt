package io.bashpsk.emptylibs

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.SwitchCamera
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.datastoreui.component.PreferenceSummary
import io.bashpsk.emptylibs.datastoreui.component.PreferenceTitle
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.preference.CardPreference
import io.bashpsk.emptylibs.datastoreui.preference.CheckBoxPreference
import io.bashpsk.emptylibs.datastoreui.preference.ColorPickPreference
import io.bashpsk.emptylibs.datastoreui.preference.DropDownPreference
import io.bashpsk.emptylibs.datastoreui.preference.FontPreference
import io.bashpsk.emptylibs.datastoreui.preference.ListOptionMenuPreference
import io.bashpsk.emptylibs.datastoreui.preference.ListOptionPreference
import io.bashpsk.emptylibs.datastoreui.preference.SetOptionPreference
import io.bashpsk.emptylibs.datastoreui.preference.SliderPreference
import io.bashpsk.emptylibs.datastoreui.preference.SwitchMenuPreference
import io.bashpsk.emptylibs.datastoreui.preference.SwitchPreference
import io.bashpsk.emptylibs.datastoreui.preference.TextFieldPreference
import io.bashpsk.emptylibs.screen.datastoreui.AppFont
import io.bashpsk.emptylibs.screen.datastoreui.AppTheme
import io.bashpsk.emptylibs.screen.datastoreui.datastore2
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatastoreUIScreen() {

    val context = LocalContext.current
    val datastore = LocalDatastore.current
    val datastore2 = context.datastore2
    val optionMenuVisibleState = remember { MutableTransitionState(false) }

    val getFieldText by datastore.getPreference(
        key = stringPreferencesKey("TEXT-FIELD-PREFERENCE"),
        initial = ""
    ).collectAsStateWithLifecycle(initialValue = "")

    val getAppTheme by datastore.getPreference(
        key = stringPreferencesKey("SINGLE-OPTION-MENU-PREFERENCE"),
        initial = AppTheme.System.name
    ).collectAsStateWithLifecycle(initialValue = AppTheme.System.name)

    var textFieldValue by remember { mutableStateOf(TextFieldValue(getFieldText)) }

    val sampleEntities = persistentMapOf(
        "One" to "Kotlin",
        "Two" to "Bash PSK",
        "Three" to "Empty Layer"
    )
    val sampleTwoEntities = persistentMapOf(
        1 to "Kotlin",
        2 to "Bash PSK",
        3 to "Empty Layer"
    )

    val themeEntities = AppTheme.entries.associate { theme ->
        theme.name to theme.name
    }.toImmutableMap()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            TopAppBar(
                title = {

                    Text(
                        text = "Datastore UI",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {

                    IconButton(
                        onClick = {

                            optionMenuVisibleState.targetState = true
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Option Menu"
                        )
                    }

                    DropdownMenu(
                        expanded = optionMenuVisibleState.targetState,
                        onDismissRequest = {

                            optionMenuVisibleState.targetState = false
                        }
                    ) {

                        ListOptionMenuPreference(
                            datastore = null,
                            key = stringPreferencesKey("SINGLE-OPTION-MENU-PREFERENCE"),
                            initialValue = AppTheme.System.name,
                            entities = themeEntities,
                            title = { PreferenceTitle(title = "App Theme") },
                            leadingContent = {

                                when (AppTheme.valueOf(getAppTheme)) {

                                    AppTheme.System -> Icon(
                                        imageVector = Icons.Filled.BrightnessAuto,
                                        contentDescription = "App Theme"
                                    )

                                    AppTheme.Dark -> Icon(
                                        imageVector = Icons.Filled.Brightness2,
                                        contentDescription = "App Theme"
                                    )

                                    AppTheme.Light -> Icon(
                                        imageVector = Icons.Filled.Brightness6,
                                        contentDescription = "App Theme"
                                    )
                                }
                            },
                            onMenuDismiss = {

                                optionMenuVisibleState.targetState = false
                            }
                        )

                        SwitchMenuPreference(
                            datastore = null,
                            key = booleanPreferencesKey("SWITCH-MENU-PREFERENCE"),
                            initialValue = false,
                            title = { PreferenceTitle(title = "Switch Menu") },
                            leadingContent = {

                                Icon(
                                    imageVector = Icons.Filled.Devices,
                                    contentDescription = ""
                                )
                            },
                            onMenuDismiss = {

                                optionMenuVisibleState.targetState = false
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues),
            columns = GridCells.Fixed(count = 1),
            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(space = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 4.dp)
        ) {

            item { HorizontalDivider() }

            item {

                CardPreference(
                    modifier = Modifier.fillMaxWidth(),
                    title = { PreferenceTitle(title = "Card Preference") },
                    summary = { PreferenceSummary(summary = "Select video download path.") },
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.SdStorage,
                            contentDescription = ""
                        )
                    },
                    trailingContent = {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = ""
                        )
                    }
                )
            }

            item { HorizontalDivider() }

            item {

                CheckBoxPreference(
                    modifier = Modifier.fillMaxWidth(),
                    datastore = datastore2,
                    key = booleanPreferencesKey("CHECK-BOX-PREFERENCE"),
                    initialValue = false,
                    title = { PreferenceTitle(title = "Check Box Preference") },
                    summary = { checked ->

                        PreferenceSummary(
                            summary = "${
                                if (checked) "Enable" else "Disable"
                            } the check box preference."
                        )
                    },
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.SelectAll,
                            contentDescription = ""
                        )
                    }
                )
            }

            item { HorizontalDivider() }

            item {

                ColorPickPreference(
                    modifier = Modifier.fillMaxWidth(),
                    datastore = null,
                    key = intPreferencesKey("COLOR-PICK-PREFERENCE"),
                    title = { PreferenceTitle(title = "Color Picker Preference") },
                    summary = {
                        PreferenceSummary(summary = "Select a color for color pick preference.")
                    },
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.Colorize,
                            contentDescription = ""
                        )
                    }
                )
            }

            item {

                ColorPickPreference(
                    modifier = Modifier.fillMaxWidth(),
                    datastore = null,
                    key = intPreferencesKey("COLOR-PICK-PREFERENCE-TWO"),
                    initialValue = Color.Black.toArgb(),
                    title = { PreferenceTitle(title = "Color Picker Preference") },
                    summary = {
                        PreferenceSummary(summary = "Select a color for color pick preference.")
                    },
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.Colorize,
                            contentDescription = ""
                        )
                    },
                    enableAlphaPanel = false
                )
            }

            item { HorizontalDivider() }

            item {

                DropDownPreference(
                    modifier = Modifier.fillMaxWidth(),
                    datastore = null,
                    key = stringPreferencesKey("DROP-DOWN-PREFERENCE"),
                    initialValue = "",
                    entities = sampleEntities,
                    title = { PreferenceTitle(title = "Drop Down Preference") },
                    summary = {
                        PreferenceSummary(summary = "Select one entity from the list. $it")
                    },
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.Code,
                            contentDescription = ""
                        )
                    }
                )
            }

            item { HorizontalDivider() }

            item {

                FontPreference(
                    modifier = Modifier.fillMaxWidth(),
                    datastore = null,
                    key = stringPreferencesKey("FONT-PREFERENCE"),
                    entities = AppFont.fontEntities,
                    title = { PreferenceTitle(title = "Font Preference") },
                    summary = {
                        PreferenceSummary(summary = "Select font from the list.", fontFamily = it)
                    },
                    previewText = "This Font Preference UI. In 2000.",
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.FontDownload,
                            contentDescription = ""
                        )
                    },
                )
            }

            item { HorizontalDivider() }

            item {

                SetOptionPreference(
                    modifier = Modifier.fillMaxWidth(),
                    datastore = null,
                    key = stringSetPreferencesKey("MULTI-OPTION-PREFERENCE"),
                    initialValue = emptySet(),
                    entities = sampleTwoEntities,
                    title = { PreferenceTitle(title = "Multiple Option Selection Preference") },
                    summary = {
                        PreferenceSummary(summary = "Select entities from the list. $it")
                    },
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.Sell,
                            contentDescription = ""
                        )
                    },
                    trailingContent = {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = ""
                        )
                    }
                )
            }

            item { HorizontalDivider() }

            item {

                ListOptionPreference(
                    modifier = Modifier.fillMaxWidth(),
                    datastore = null,
                    key = stringPreferencesKey("SINGLE-OPTION-PREFERENCE"),
                    initialValue = "",
                    entities = sampleEntities,
                    title = { PreferenceTitle(title = "Single Option Selection Preference") },
                    summary = {
                        PreferenceSummary(summary = "Select one entity from the list. $it")
                    },
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.Gamepad,
                            contentDescription = ""
                        )
                    },
                    trailingContent = {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = ""
                        )
                    }
                )
            }

            item { HorizontalDivider() }

            item {

                SliderPreference(
                    modifier = Modifier.fillMaxWidth(),
                    datastore = null,
                    key = floatPreferencesKey("SLIDER-PREFERENCE"),
                    initialValue = 0.0F,
                    title = { PreferenceTitle(title = "Slider Preference") },
                    summary = { PreferenceSummary(summary = "Adjust slider value.") },
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = ""
                        )
                    },
                    valueRange = 0.0F..1.0F
                )
            }

            item { HorizontalDivider() }

            item {

                SliderPreference(
                    modifier = Modifier.fillMaxWidth(),
                    datastore = null,
                    key = floatPreferencesKey("SLIDER-STEP-PREFERENCE"),
                    initialValue = 0.0F,
                    title = { PreferenceTitle(title = "Slider Step Preference") },
                    summary = { PreferenceSummary(summary = "Adjust slider value from 0 to 50.") },
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.PinDrop,
                            contentDescription = ""
                        )
                    },
                    trailingContent = null,
                    valueRange = 0.0F..50.0F,
                    steps = 9
                )
            }

            item { HorizontalDivider() }

            item {

                SwitchPreference(
                    modifier = Modifier.fillMaxWidth(),
                    datastore = null,
                    key = booleanPreferencesKey("SWITCH-PREFERENCE"),
                    initialValue = false,
                    title = { PreferenceTitle(title = "Switch Preference") },
                    summary = {
                        PreferenceSummary(
                            summary = "${
                                if (it) "Enable" else "Disable"
                            } the switch preference."
                        )
                    },
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.SwitchCamera,
                            contentDescription = ""
                        )
                    }
                )
            }

            item { HorizontalDivider() }

            item {

                TextFieldPreference(
                    modifier = Modifier.fillMaxWidth(),
                    datastore = null,
                    key = stringPreferencesKey("TEXT-FIELD-PREFERENCE"),
                    initialValue = "",
                    title = { PreferenceTitle(title = "Text Field Preference") },
                    summary = {
                        PreferenceSummary(summary = "Enter a text field preference. $it")
                    },
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = ""
                        )
                    },
                    trailingContent = {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = ""
                        )
                    },
                    textFieldValue = textFieldValue,
                    textFieldContent = {

                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = textFieldValue,
                            onValueChange = { newInput ->

                                textFieldValue = newInput
                            },
                            singleLine = true,
                            label = { Text(text = "User Name") },
                            leadingIcon = {

                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "User"
                                )
                            },
                            trailingIcon = {

                                IconButton(
                                    onClick = {

                                        textFieldValue = textFieldValue.copy(text = "")
                                    }
                                ) {

                                    Icon(
                                        imageVector = Icons.Filled.ClearAll,
                                        contentDescription = "Clear"
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                autoCorrectEnabled = false,
                                keyboardType = KeyboardType.Text
                            )
                        )
                    }
                )
            }

            item { HorizontalDivider() }
        }
    }
}