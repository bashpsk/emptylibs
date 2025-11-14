package io.bashpsk.emptylibs.kolorpicker.color

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * A Jetpack Compose dialog that allows the user to select a color.
 *
 * @param dialogVisibleState The state of the dialog's visibility.
 * @param state The state of the color picker.
 * @param onSelectedColor A callback that is invoked when the user selects a color.
 */
@Composable
fun ColorPickerDialog(
    dialogVisibleState: MutableTransitionState<Boolean>,
    state: ColorPickerState = rememberColorPickerState(),
    enableAlphaPanel: Boolean = false,
    enableCopyButtons: Boolean = false,
    onSelectedColor: (color: Color) -> Unit
) {

    AnimatedVisibility(visibleState = dialogVisibleState) {

        AlertDialog(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
            onDismissRequest = {

                dialogVisibleState.targetState = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            ),
            shape = MaterialTheme.shapes.medium,
            title = {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier = Modifier.weight(weight = 1.0F),
                        text = "Select Color",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
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
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {

                    item {

                        ColorPicker(
                            modifier = Modifier.fillParentMaxSize(),
                            state = state,
                            enableAlphaPanel = enableAlphaPanel,
                            enableCopyButtons = enableCopyButtons
                        )
                    }
                }
            },
            confirmButton = {

                Button(
                    onClick = {

                        onSelectedColor(state.selectedColor)
                        dialogVisibleState.targetState = false
                    }
                ) {

                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done"
                    )

                    Spacer(modifier = Modifier.width(width = 2.dp))

                    Text(
                        text = "Done",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            dismissButton = {

                OutlinedButton(
                    onClick = {

                        dialogVisibleState.targetState = false
                    }
                ) {

                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close"
                    )

                    Spacer(modifier = Modifier.width(width = 2.dp))

                    Text(
                        text = "Close",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        )
    }
}

/**
 * A Jetpack Compose dialog that allows users to pick a color.
 *
 * @param dialogVisibleState The state that controls the visibility of the dialog.
 * @param state The state of the color picker.
 * @param onResetClick A callback that is invoked when the reset button is clicked.
 * @param onSelectedColor A callback that is invoked when a color is selected.
 */
@Composable
fun ColorPickerDialog(
    dialogVisibleState: MutableTransitionState<Boolean>,
    state: ColorPickerState = rememberColorPickerState(),
    enableAlphaPanel: Boolean = false,
    enableCopyButtons: Boolean = false,
    onResetClick: () -> Unit,
    onSelectedColor: (color: Color) -> Unit
) {

    AnimatedVisibility(visibleState = dialogVisibleState) {

        AlertDialog(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
            onDismissRequest = {

                dialogVisibleState.targetState = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            ),
            shape = MaterialTheme.shapes.medium,
            title = {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier = Modifier.weight(weight = 1.0F),
                        text = "Select Color",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
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
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                ) {

                    item {

                        ColorPicker(
                            modifier = Modifier.fillParentMaxSize(),
                            state = state,
                            enableAlphaPanel = enableAlphaPanel,
                            enableCopyButtons = enableCopyButtons
                        )
                    }
                }
            },
            confirmButton = {

                Button(
                    onClick = {

                        onSelectedColor(state.selectedColor)
                        dialogVisibleState.targetState = false
                    }
                ) {

                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done"
                    )

                    Spacer(modifier = Modifier.width(width = 2.dp))

                    Text(
                        text = "Done",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            dismissButton = {

                OutlinedButton(
                    onClick = {

                        state.updateColor(color = Color.Unspecified)
                        onResetClick()
                        dialogVisibleState.targetState = false
                    }
                ) {

                    Icon(
                        imageVector = Icons.Filled.Restore,
                        contentDescription = "Reset"
                    )

                    Spacer(modifier = Modifier.width(width = 2.dp))

                    Text(
                        text = "Reset",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        )
    }
}

/**
 * A Jetpack Compose dialog that allows users to pick a color.
 *
 * @param dialogVisibleState The state that controls the visibility of the dialog.
 * @param imageBitmap The image bitmap to use for the color picker.
 * @param state The state of the color picker.
 * @param onSelectedColor A callback that is invoked when a color is selected.
 */
@Composable
fun ColorPickerDialog(
    dialogVisibleState: MutableTransitionState<Boolean>,
    state: ColorPickerState = rememberColorPickerState(),
    imageBitmap: ImageBitmap,
    onSelectedColor: (color: Color) -> Unit
) {

    AnimatedVisibility(visibleState = dialogVisibleState) {

        AlertDialog(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
            onDismissRequest = {

                dialogVisibleState.targetState = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            ),
            shape = MaterialTheme.shapes.medium,
            title = {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier = Modifier.weight(weight = 1.0F),
                        text = "Select Color",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
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
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                ) {

                    item {

                        ImageColorPicker(
                            modifier = Modifier.fillParentMaxSize(),
                            imageBitmap = imageBitmap,
                            state = state
                        )
                    }
                }
            },
            confirmButton = {

                Button(
                    onClick = {

                        onSelectedColor(state.selectedColor)
                        dialogVisibleState.targetState = false
                    }
                ) {

                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done"
                    )

                    Spacer(modifier = Modifier.width(width = 2.dp))

                    Text(
                        text = "Done",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        )
    }
}