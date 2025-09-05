package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.jetpackui.picker.WheelTextPicker
import io.bashpsk.emptylibs.jetpackui.picker.rememberWheelTextPickerState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun WheelTextPickerScreen() {

    val textList = remember {
        persistentListOf(
            "PSK",
            "Empty Layer",
            "Kotlin",
            "Jetpack Compose",
            "Android Studio",
            "App Development",
            "Coding"
        )
    }

    val textPickerState = rememberWheelTextPickerState(
        textList = textList,
        initial = textList[3]
    )

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            WheelTextPicker(
                modifier = Modifier.fillMaxWidth(),
                state = textPickerState
            )

            Spacer(modifier = Modifier.height(height = 64.dp))

            Text("SELECTED : ${textPickerState.selectedText}")
        }
    }
}