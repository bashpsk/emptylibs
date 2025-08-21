package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.kolorpicker.color.ColorPicker
import io.bashpsk.emptylibs.kolorpicker.color.rememberColorPickerState

@Composable
fun ColorPickerScreen() {

    val state = rememberColorPickerState(enableAlphaPanel = true)

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        ColorPicker(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            state = state
        )
    }
}