package io.bashpsk.emptylibs

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.formatter.format.EmptyFormat
import io.bashpsk.emptylibs.jetpackui.picker.DialTextPicker
import io.bashpsk.emptylibs.jetpackui.picker.rememberDialTextPickerState
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun DialClockScreen() {

    val textList1 = remember { (0..23).map { EmptyFormat.toRoundTime(it) }.toPersistentList() }
    val textList2 = remember { (0..59).map { EmptyFormat.toRoundTime(it) }.toPersistentList() }
    val textList3 = remember { (0..59).map { "*" }.toPersistentList() }

    val textPickerState1 = rememberDialTextPickerState(textList = textList1)
    val textPickerState2 = rememberDialTextPickerState(textList = textList2)
    val textPickerState3 = rememberDialTextPickerState(textList = textList3)

    val animationSpec = tween<Float>(durationMillis = 250, easing = LinearOutSlowInEasing)

    val currentTimeFormatted by remember(
        textPickerState1.selectedIndex,
        textPickerState2.selectedIndex,
        textPickerState3.selectedIndex
    ) {
        derivedStateOf {
            "TIME : ${
                EmptyFormat.toRoundTime(textPickerState1.selectedIndex)
            }:${
                EmptyFormat.toRoundTime(textPickerState2.selectedIndex)
            }:${
                EmptyFormat.toRoundTime(textPickerState3.selectedIndex)
            }"
        }
    }

    LaunchedEffect(Unit) {

        while (isActive) {

            val localDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            textPickerState1.updateSelectedTextFromIndex(
                newIndex = localDateTime.hour,
                animationSpec = animationSpec
            )
            textPickerState2.updateSelectedTextFromIndex(
                newIndex = localDateTime.minute,
                animationSpec = animationSpec
            )
            textPickerState3.updateSelectedTextFromIndex(
                newIndex = localDateTime.second,
                animationSpec = animationSpec
            )
            delay(100.milliseconds)
        }
    }

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            BoxWithConstraints(
                modifier = Modifier
                    .weight(1.0F)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {

                DialTextPicker(
                    modifier = Modifier.fillMaxSize(),
                    state = textPickerState3,
                    textStyle = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    selectedTextStyle = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    enableUserScrollable = false
                )

                DialTextPicker(
                    modifier = Modifier.sizeIn(
                        minWidth = 100.dp,
                        minHeight = 100.dp,
                        maxWidth = maxWidth / 1.25F,
                        maxHeight = maxHeight / 1.25F
                    ),
                    state = textPickerState2,
                    textStyle = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    selectedTextStyle = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    enableUserScrollable = false
                )

                DialTextPicker(
                    modifier = Modifier.sizeIn(
                        minWidth = 80.dp,
                        minHeight = 80.dp,
                        maxWidth = maxWidth / 2.0F,
                        maxHeight = maxHeight / 2.0F
                    ),
                    state = textPickerState1,
                    textStyle = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    selectedTextStyle = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    enableUserScrollable = false
                )
            }

            Text(text = currentTimeFormatted)
        }
    }
}