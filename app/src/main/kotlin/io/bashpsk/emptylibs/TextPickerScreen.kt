package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.bashpsk.emptylibs.jetpackui.textpicker.WheelTextPicker
import kotlinx.collections.immutable.persistentListOf

@Composable
fun TextPickerScreen() {

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

    var selectedText by remember { mutableStateOf("") }

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            WheelTextPicker(
                modifier = Modifier.fillMaxWidth(),
                textList.toList(),
                onSelected = {selectedText=it}
            )

            Text(selectedText)
        }
    }
}