package io.bashpsk.emptylibs

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.datastoreui.extension.toReverseMap
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
import io.bashpsk.emptylibs.screen.datastoreui.AppTheme
import io.bashpsk.emptylibs.screen.datastoreui.datastore2
import io.bashpsk.emptylibs.screen.datastoreui.fontEntities

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatastoreUIScreen() {

    val context = LocalContext.current
    val datastore = LocalDatastore.current
    val datastore2 = context.datastore2
    val optionMenuVisibleState = remember { MutableTransitionState(initialState = false) }

    val getFieldText by datastore.getPreference(
        key = stringPreferencesKey("TEXT-FIELD-PREFERENCE"),
        initial = ""
    ).collectAsStateWithLifecycle(initialValue = "")

    val getAppTheme by datastore.getPreference(
        key = stringPreferencesKey("SINGLE-OPTION-MENU-PREFERENCE"),
        initial = AppTheme.SYSTEM.name
    ).collectAsStateWithLifecycle(initialValue = AppTheme.SYSTEM.name)

    val getSelectedItem by datastore.getPreference(
        key = stringSetPreferencesKey("MULTI-OPTION-PREFERENCE"),
        initial = emptySet()
    ).collectAsStateWithLifecycle(initialValue = emptySet())

    var textFieldValue by remember { mutableStateOf(value = TextFieldValue(text = getFieldText)) }

    val sampleEntities = mapOf("One" to "Kotlin", "Two" to "Bash PSK", "Three" to "Empty Layer")
    val sampleTwoEntities = mapOf(1 to "Kotlin", 2 to "Bash PSK", 3 to "Empty Layer")

    val themeEntities = AppTheme.entries.associate { theme ->

        theme.name.lowercase().replaceFirstChar { char -> char.uppercaseChar() } to theme.name
    }

    val reverseEntities by remember(sampleEntities) {
        derivedStateOf { sampleEntities.toReverseMap() }
    }

    val summaryItems by remember(getSelectedItem, reverseEntities) {
        derivedStateOf { getSelectedItem.mapNotNull { item -> reverseEntities[item] } }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            TopAppBar(
                title = {

                    Text(
                        text = "Datastore UI",
                        textAlign = TextAlign.Start,
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
                            key = stringPreferencesKey("SINGLE-OPTION-MENU-PREFERENCE"),
                            initialValue = AppTheme.SYSTEM.name,
                            entities = themeEntities,
                            title = "App Theme",
                            leadingContent = {

                                when (AppTheme.valueOf(getAppTheme)) {

                                    AppTheme.SYSTEM -> Icon(
                                        imageVector = Icons.Filled.BrightnessAuto,
                                        contentDescription = "App Theme"
                                    )

                                    AppTheme.DARK -> Icon(
                                        imageVector = Icons.Filled.Brightness2,
                                        contentDescription = "App Theme"
                                    )

                                    AppTheme.LIGHT -> Icon(
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
                            key = booleanPreferencesKey("SWITCH-MENU-PREFERENCE"),
                            initialValue = false,
                            title = "Switch Menu",
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
            columns = GridCells.Adaptive(minSize = 320.dp),
            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(space = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 4.dp)
        ) {

            item { HorizontalDivider() }

            item {

                CardPreference(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 250),
                        fadeOutSpec = tween(durationMillis = 100),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    title = "Card Preference",
                    summary = "Select video download path.",
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
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 250),
                        fadeOutSpec = tween(durationMillis = 100),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    datastore = datastore2,
                    key = booleanPreferencesKey("CHECK-BOX-PREFERENCE"),
                    initialValue = false,
                    title = "Check Box Preference",
                    summary = "Enable the check box preference.",
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
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 250),
                        fadeOutSpec = tween(durationMillis = 100),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    key = intPreferencesKey("COLOR-PICK-PREFERENCE"),
                    title = "Color Picker Preference",
                    summary = "Select a color for color pick preference.",
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.Colorize,
                            contentDescription = ""
                        )
                    },
                    enableAlphaPanel = true
                )
            }

            item {

                ColorPickPreference(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 250),
                        fadeOutSpec = tween(durationMillis = 100),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    key = intPreferencesKey("COLOR-PICK-PREFERENCE-TWO"),
                    title = "Color Picker Preference Two",
                    summary = "Select a color for color pick preference two.",
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.Colorize,
                            contentDescription = ""
                        )
                    },
                    enableResetButton = true
                )
            }

            item { HorizontalDivider() }

            item {

                DropDownPreference(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 250),
                        fadeOutSpec = tween(durationMillis = 100),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    key = stringPreferencesKey("DROP-DOWN-PREFERENCE"),
                    initialValue = "",
                    entities = sampleEntities,
                    title = "Drop Down Preference",
                    summary = "Select one entity from the list.",
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
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 250),
                        fadeOutSpec = tween(durationMillis = 100),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    key = stringPreferencesKey("FONT-PREFERENCE"),
                    initialValue = "",
                    entities = fontEntities,
                    title = "Font Preference",
                    summary = "Select font from the list.",
                    previewText = "This Font Preference UI. In 2000.",
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.FontDownload,
                            contentDescription = ""
                        )
                    },
                    enableResetButton = true
                )
            }

            item { HorizontalDivider() }

            item {

                SetOptionPreference(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 250),
                        fadeOutSpec = tween(durationMillis = 100),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    key = stringSetPreferencesKey("MULTI-OPTION-PREFERENCE"),
                    initialValue = emptySet(),
                    entities = sampleTwoEntities,
                    title = "Multiple Option Selection Preference",
                    summary = "Select entities from the list. $summaryItems",
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
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 250),
                        fadeOutSpec = tween(durationMillis = 100),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    key = stringPreferencesKey("SINGLE-OPTION-PREFERENCE"),
                    initialValue = "",
                    entities = sampleEntities,
                    title = "Single Option Selection Preference",
                    summary = "Select one entity from the list.",
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
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 250),
                        fadeOutSpec = tween(durationMillis = 100),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    key = floatPreferencesKey("SLIDER-PREFERENCE"),
                    initialValue = 0.0F,
                    title = "Slider Preference",
                    summary = "Adjust slider value.",
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = ""
                        )
                    },
                    valueRange = 0.0F..1.0F,
                    isValueVisible = true,
                    decimalFraction = 1
                )
            }

            item { HorizontalDivider() }

            item {

                SliderPreference(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 250),
                        fadeOutSpec = tween(durationMillis = 100),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    key = floatPreferencesKey("SLIDER-STEP-PREFERENCE"),
                    initialValue = 0.0F,
                    title = "Slider Step Preference",
                    summary = "Adjust slider value from 0 to 50.",
                    leadingContent = {

                        Icon(
                            imageVector = Icons.Filled.PinDrop,
                            contentDescription = ""
                        )
                    },
                    isValueVisible = false,
                    valueRange = 0.0F..50.0F,
                    steps = 9
                )
            }

            item { HorizontalDivider() }

            item {

                SwitchPreference(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 250),
                        fadeOutSpec = tween(durationMillis = 100),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    key = booleanPreferencesKey("SWITCH-PREFERENCE"),
                    initialValue = false,
                    title = "Switch Preference",
                    summary = "Enable the switch preference.",
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
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 250),
                        fadeOutSpec = tween(durationMillis = 100),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ),
                    key = stringPreferencesKey("TEXT-FIELD-PREFERENCE"),
                    title = "Text Field Preference",
                    summary = "Enter a text field preference.",
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
                            label = {

                                Text(text = "User Name")
                            },
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