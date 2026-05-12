package io.bashpsk.emptylibs

import android.os.Environment
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.formatter.format.DurationPattern
import io.bashpsk.emptylibs.formatter.format.findPercentage
import io.bashpsk.emptylibs.formatter.format.formattedDuration
import io.bashpsk.emptylibs.formatter.format.toFileSize
import io.bashpsk.emptylibs.formatter.meter.FileSpeedData
import io.bashpsk.emptylibs.formatter.meter.fileSpeedMeter
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun FileCopySpeedScreen() {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val sourceDestinationFileList by remember {
        derivedStateOf {
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).listFiles()?.filter { file ->

                file.exists() && file.isFile
            }?.map { file ->

                file to File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS
                    ),
                    file.name
                )
            }?.toImmutableList() ?: persistentListOf()
        }
    }

    var fileSpeedData by remember { mutableStateOf(FileSpeedData()) }
    var currentFile by remember { mutableStateOf<File?>(null) }
    var progressMonitorJob by remember { mutableStateOf<Job?>(null) }

    val copyingFormatted by remember(fileSpeedData) {
        derivedStateOf {
            "Progress : ${
                fileSpeedData.current.toFileSize(context)
            } / ${
                fileSpeedData.total.toFileSize(context)
            } (${
                fileSpeedData.speed.toFileSize(context)
            }/s)"
        }
    }

    val etaFormatted by remember(fileSpeedData) {
        derivedStateOf {
            "ETA : ${fileSpeedData.eta.seconds.formattedDuration(pattern = DurationPattern.TimeLabel())}"
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
                progress = {

                    findPercentage(
                        total = fileSpeedData.total,
                        obtained = fileSpeedData.current
                    ) / 100F
                }
            )

            Text(text = copyingFormatted)

            Text(text = etaFormatted)

            Text(text = currentFile?.name.toString())

            Button(
                onClick = {

                    val bufferSize = DEFAULT_BUFFER_SIZE

                    coroutineScope.coroutineContext.cancelChildren()

                    coroutineScope.launch(context = Dispatchers.IO) {

                        sourceDestinationFileList.forEach { fileItem ->

                            fileSpeedData = FileSpeedData()
                            currentFile = fileItem.first

                            progressMonitorJob = launch(context = Dispatchers.IO) {

                                fileSpeedMeter(
                                    source = fileItem.first,
                                    destination = fileItem.second,
                                    interval = 1.seconds
                                ) { fileSpeedDataLatest ->

                                    fileSpeedData = fileSpeedDataLatest ?: FileSpeedData()
                                }
                            }

                            fileItem.first.inputStream().use { inputStream ->

                                fileItem.second.outputStream().use { outputStream ->

                                    var bytesCopied: Long = 0
                                    val buffer = ByteArray(bufferSize)
                                    var bytes = inputStream.read(buffer)

                                    while (bytes >= 0) {

                                        outputStream.write(buffer, 0, bytes)
                                        bytesCopied += bytes
                                        bytes = inputStream.read(buffer)

                                        delay(duration = 2.milliseconds)
                                        currentCoroutineContext().ensureActive()
                                    }
                                }
                            }

                            progressMonitorJob?.cancel()
                            fileSpeedData = FileSpeedData()
                        }
                    }
                }
            ) {

                Text(text = "Start Copy")
            }
        }
    }
}