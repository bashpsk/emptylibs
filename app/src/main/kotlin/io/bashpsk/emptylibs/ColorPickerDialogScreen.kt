package io.bashpsk.emptylibs

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.kolorpicker.color.ColorPickerDialog

@Composable
fun ColorPickerDialogScreen() {

    val pickerDialog = remember { MutableTransitionState(false) }

    var selectedColor by remember { mutableStateOf(Color.Unspecified) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        ColorPickerDialog(
            dialogVisibleState = pickerDialog,
            onSelectedColor = { newColor ->

                selectedColor = newColor
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Button(
                onClick = {

                    pickerDialog.targetState = true
                }
            ) {

                Text("Pick Color")
            }

            Box(
                modifier = Modifier
                    .size(size = 80.dp)
                    .background(color = selectedColor, shape = MaterialTheme.shapes.small)
                    .border(
                        width = 0.6.dp,
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
    }
}