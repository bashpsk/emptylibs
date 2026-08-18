package io.bashpsk.emptylibs.screen

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.R
import io.bashpsk.emptylibs.kolorpicker.color.KolorPickerDialog
import io.bashpsk.emptylibs.kolorpicker.color.rememberKolorPickerState

@Composable
fun ImageColorPickerDialogScreen() {

    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.wallpaper01)
    val kolorPickerState = rememberKolorPickerState()

    var selectedColor by remember { mutableStateOf(Color.Unspecified) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        KolorPickerDialog(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
            state = kolorPickerState,
            imageBitmap = imageBitmap,
            onSelectedColor = { newColor ->

                selectedColor = newColor
            },
            enableCopyButton = true
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

                    kolorPickerState.dialogVisible.targetState = true
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