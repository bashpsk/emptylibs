package io.bashpsk.emptylibs.kolorpicker.color

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * A Jetpack Compose dialog that allows the user to select a color.
 *
 * @param modifier The modifier to be applied to the dialog.
 * @param dialogVisibleState The state of the dialog's visibility.
 * @param state The state of the color picker.
 * @param properties The properties of the dialog.
 * @param shape The shape of the dialog's container.
 * @param containerColor The color of the dialog's container.
 * @param iconContentColor The color of the dialog's icon.
 * @param titleContentColor The color of the dialog's title.
 * @param textContentColor The color of the dialog's text.
 * @param tonalElevation The elevation of the dialog's container.
 * @param enableAlphaPanel Whether to enable the alpha panel in the color picker.
 * @param enableCopyButton Whether to enable the copy buttons in the color picker.
 * @param onSelectedColor A callback that is invoked when the user selects a color.
 * @param icon The icon to be displayed in the dialog.
 * @param title The title to be displayed in the dialog.
 * @param confirmButton The confirm button to be displayed in the dialog.
 * @param dismissButton The dismiss button to be displayed in the dialog.
 */
@Composable
fun KolorPickerDialog(
    modifier: Modifier = Modifier,
    dialogVisibleState: MutableTransitionState<Boolean>,
    state: KolorPickerState = rememberKolorPickerState(),
    properties: DialogProperties = KolorPickerDialogDefault.Properties,
    shape: Shape = MaterialTheme.shapes.small,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    enableAlphaPanel: Boolean = false,
    enableCopyButton: Boolean = false,
    enablePasteButton: Boolean = false,
    onSelectedColor: (color: Color) -> Unit = {},
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = {

        KolorPickerDialogDefault.TitleContent(dialogVisibleState = dialogVisibleState)
    },
    confirmButton: @Composable () -> Unit = {

        KolorPickerDialogDefault.ConfirmButton(
            dialogVisibleState = dialogVisibleState,
            state = state,
            onSelectedColor = onSelectedColor
        )
    },
    dismissButton: @Composable (() -> Unit)? = {

        KolorPickerDialogDefault.DismissButton(dialogVisibleState = dialogVisibleState)
    }
) {

    AnimatedVisibility(visibleState = dialogVisibleState) {

        AlertDialog(
            modifier = modifier,
            onDismissRequest = {

                dialogVisibleState.targetState = false
            },
            properties = properties,
            shape = shape,
            containerColor = containerColor,
            iconContentColor = iconContentColor,
            titleContentColor = titleContentColor,
            textContentColor = textContentColor,
            tonalElevation = tonalElevation,
            icon = icon,
            title = title,
            text = {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {

                    item {

                        KolorPicker(
                            modifier = Modifier.fillParentMaxSize(),
                            state = state,
                            enableAlphaPanel = enableAlphaPanel,
                            enableCopyButton = enableCopyButton,
                            enablePasteButton = enablePasteButton
                        )
                    }
                }
            },
            confirmButton = confirmButton,
            dismissButton = dismissButton
        )
    }
}

/**
 * A Jetpack Compose dialog that allows users to pick a color from an image.
 *
 * @param modifier The modifier to be applied to the dialog.
 * @param dialogVisibleState The state of the dialog's visibility.
 * @param state The state of the color picker.
 * @param imageBitmap The image bitmap to use for the color picker.
 * @param properties The properties of the dialog.
 * @param shape The shape of the dialog's container.
 * @param containerColor The color of the dialog's container.
 * @param iconContentColor The color of the dialog's icon.
 * @param titleContentColor The color of the dialog's title.
 * @param textContentColor The color of the dialog's text.
 * @param tonalElevation The elevation of the dialog's container.
 * @param enableCopyButton Whether to enable the copy buttons in the color picker.
 * @param onSelectedColor A callback that is invoked when the user selects a color.
 * @param icon The icon to be displayed in the dialog.
 * @param title The title to be displayed in the dialog.
 * @param confirmButton The confirm button to be displayed in the dialog.
 * @param dismissButton The dismiss button to be displayed in the dialog.
 */
@Composable
fun KolorPickerDialog(
    modifier: Modifier = Modifier,
    dialogVisibleState: MutableTransitionState<Boolean>,
    state: KolorPickerState = rememberKolorPickerState(),
    imageBitmap: ImageBitmap,
    properties: DialogProperties = KolorPickerDialogDefault.Properties,
    shape: Shape = MaterialTheme.shapes.small,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    enableCopyButton: Boolean = false,
    onSelectedColor: (color: Color) -> Unit = {},
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = {

        KolorPickerDialogDefault.TitleContent(dialogVisibleState = dialogVisibleState)
    },
    confirmButton: @Composable () -> Unit = {

        KolorPickerDialogDefault.ConfirmButton(
            dialogVisibleState = dialogVisibleState,
            state = state,
            onSelectedColor = onSelectedColor
        )
    },
    dismissButton: @Composable (() -> Unit)? = {

        KolorPickerDialogDefault.DismissButton(dialogVisibleState = dialogVisibleState)
    }
) {

    AnimatedVisibility(visibleState = dialogVisibleState) {

        AlertDialog(
            modifier = modifier,
            onDismissRequest = {

                dialogVisibleState.targetState = false
            },
            properties = properties,
            shape = shape,
            containerColor = containerColor,
            iconContentColor = iconContentColor,
            titleContentColor = titleContentColor,
            textContentColor = textContentColor,
            tonalElevation = tonalElevation,
            icon = icon,
            title = title,
            text = {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                ) {

                    item {

                        ImageKolorPicker(
                            modifier = Modifier.fillParentMaxSize(),
                            imageBitmap = imageBitmap,
                            state = state,
                            enableCopyButton = enableCopyButton
                        )
                    }
                }
            },
            confirmButton = confirmButton,
            dismissButton = dismissButton
        )
    }
}