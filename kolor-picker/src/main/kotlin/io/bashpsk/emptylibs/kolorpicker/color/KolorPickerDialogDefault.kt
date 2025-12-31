package io.bashpsk.emptylibs.kolorpicker.color

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Restore
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

object KolorPickerDialogDefault {

    val Properties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = false,
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = false
    )

    @Composable
    fun TitleContent(title: String = "", dialogVisibleState: MutableTransitionState<Boolean>) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier.weight(weight = 1.0F),
                text = title.ifEmpty { "Select Color" },
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
    }

    @Composable
    fun ConfirmButton(
        dialogVisibleState: MutableTransitionState<Boolean>,
        state: KolorPickerState,
        onSelectedColor: (color: Color) -> Unit = {}
    ) {

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

    @Composable
    fun DismissButton(dialogVisibleState: MutableTransitionState<Boolean>) {

        OutlinedButton(
            onClick = {

                dialogVisibleState.targetState = false
            }
        ) {

            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Dismiss"
            )

            Spacer(modifier = Modifier.width(width = 2.dp))

            Text(
                text = "Dismiss",
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    @Composable
    fun ResetButton(
        dialogVisibleState: MutableTransitionState<Boolean>,
        state: KolorPickerState,
        onSelectedColor: (color: Color) -> Unit
    ) {

        OutlinedButton(
            onClick = {

                state.updateColor(color = Color.Unspecified)
                onSelectedColor(state.selectedColor)
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
}