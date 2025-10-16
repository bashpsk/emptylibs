package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.formatter.format.EmptyFormat
import io.bashpsk.emptylibs.formatter.meter.FileSpeedData
import io.bashpsk.emptylibs.formatter.meter.fileSpeedMeter
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

@Composable
fun FileWriteSpeedScreen() {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val total by remember { mutableLongStateOf(3 * 1000 * 1000L) }
    var current by remember { mutableLongStateOf(0L) }
    var previous by remember { mutableLongStateOf(0L) }
    var fileSpeedData by remember { mutableStateOf(FileSpeedData()) }

    val downloadedFormatted by remember {
        derivedStateOf {
            "Progress : ${
                EmptyFormat.toFileSize(context, current)
            } / ${
                EmptyFormat.toFileSize(context, total)
            } (${
                EmptyFormat.toFileSize(context, fileSpeedData.speed)
            }/s)"
        }
    }

    val etaFormatted by remember {
        derivedStateOf {
            "ETA : ${
                EmptyFormat.duration(
                    durationValue = fileSpeedData.eta,
                    unit = DurationUnit.SECONDS,
                    pattern = EmptyFormat.DurationPattern.AUTO
                )
            }"
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = { current / total.toFloat() }
            )

            Text(text = downloadedFormatted)

            Text(text = etaFormatted)

            Button(
                onClick = {

                    coroutineScope.coroutineContext.cancelChildren()

                    coroutineScope.launch {

                        current = 0L
                        previous = 0L
                        fileSpeedData = FileSpeedData()

                        while (current <= total) {

                            val increment = (100000..500000).randomOrNull() ?: 0

                            previous = current
                            current = (current + increment).coerceAtMost(total)

                            fileSpeedData = fileSpeedMeter(
                                total = total,
                                current = current,
                                previous = previous
                            )

                            delay(duration = 1000.milliseconds)
                        }
                    }
                }
            ) {

                Text(text = "Start Download")
            }
        }
    }
}