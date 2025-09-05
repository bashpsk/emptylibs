package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.formatter.format.EmptyFormat
import io.bashpsk.emptylibs.jetpackui.picker.DialTextPicker
import io.bashpsk.emptylibs.jetpackui.picker.rememberDialTextPickerState
import kotlinx.collections.immutable.toPersistentList

@Composable
fun DialTextPickerScreen() {

    val textList = remember {
        (1..12).map { item -> EmptyFormat.toRoundTime(item) }.toPersistentList()
    }

    val textPickerState = rememberDialTextPickerState(
        textList = textList,
        initial = textList[3]
    )

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            DialTextPicker(
                modifier = Modifier
                    .weight(1.0F)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                state = textPickerState,
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                selectedTextStyle = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )

            Spacer(modifier = Modifier.height(height = 12.dp))

            Text("SELECTED : ${textPickerState.selectedItem}")
        }
    }
}