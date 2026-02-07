package io.bashpsk.emptylibs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.kolorpicker.color.KolorPickerDialog
import io.bashpsk.emptylibs.kolorpicker.color.rememberKolorPickerState

@Composable
fun ColorPickerDialogScreen() {

    val kolorPickerState = rememberKolorPickerState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        KolorPickerDialog(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
            state = kolorPickerState,
            enableAlphaPanel = true,
            enableCopyButton = true,
            enablePasteButton = true
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.CenterVertically
            )
        ) {

            Button(
                onClick = {

                    kolorPickerState.dialogVisibleState.targetState = true
                }
            ) {

                Text("Pick Color")
            }

            Box(
                modifier = Modifier
                    .size(size = 80.dp)
                    .background(
                        color = kolorPickerState.selectedColor,
                        shape = MaterialTheme.shapes.small
                    )
                    .border(
                        width = 0.6.dp,
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
    }
}