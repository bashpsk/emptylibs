package io.bashpsk.emptylibs.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.formatter.format.toRoundTime
import io.bashpsk.emptylibs.jetpackui.picker.DialTextPicker
import io.bashpsk.emptylibs.jetpackui.picker.rememberDialTextPickerState
import kotlinx.collections.immutable.toPersistentList

@Composable
fun DialTextPickerScreen() {

    val textList1 = remember { (0..23).map { it.toRoundTime() }.toPersistentList() }

    val textPickerState1 = rememberDialTextPickerState(textList = textList1)

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .weight(1.0F)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {

                DialTextPicker(
                    modifier = Modifier.fillMaxWidth(),
                    state = textPickerState1,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    selectedTextStyle = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    enableTextBox = true,
                    enableUserScrollable = true
                )
            }

            Text("SELECTED : ${textPickerState1.selectedText}")
        }
    }
}