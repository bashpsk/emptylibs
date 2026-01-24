package io.bashpsk.emptylibs

import android.os.Environment
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.formatter.format.EmptyFormat
import io.bashpsk.emptylibs.jetpackui.text.LazyTextViewer
import io.bashpsk.emptylibs.jetpackui.text.LazyTextViewerDefaults
import io.bashpsk.emptylibs.jetpackui.text.TextSource
import io.bashpsk.emptylibs.jetpackui.text.rememberLazyTextViewerState
import io.bashpsk.emptylibs.storage.extension.fileLength
import io.bashpsk.emptylibs.utils.setDebug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyTextViewerScreen() {

    val coroutineScope = rememberCoroutineScope()

    val lineText = "A full-featured cropping UI with support for predefined aspect ratios," +
            " custom crop shapes(circles, stars), image flipping, and a live preview.\n"

    val largeText by remember(lineText) { derivedStateOf { lineText.repeat(400) } }

    val textFile by remember {
        derivedStateOf {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//                "Empty Libs.txt"
                "EmptyLayer.kt"
            )
        }
    }

    var oomText by remember { mutableStateOf("") }

    val textViewerState = rememberLazyTextViewerState(
//        source = TextSource.RawString(content = largeText)
        source = TextSource.TextFile(content = textFile)
//        source = TextSource.FilePath(content = textFile.path)
//        source = TextSource.RawString(content = oomText) /*Simulate Error*/
    )

    val properties = LazyTextViewerDefaults.properties(
        contentStyle = MaterialTheme.typography.bodyMedium,
        numberStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65F)
        ),
        softWrapEnabled = false
    )

    LaunchedEffect(textFile) {

//        val isOomText = true
        val isOomText = false

        if (isOomText) coroutineScope.launch(Dispatchers.IO) {

            textFile.bufferedReader().use { reader -> oomText = reader.readText() }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            TopAppBar(
                title = {

                    Text("Lazy Text Viewer")
                },
                actions = {

                    Button(
                        onClick = {

                            coroutineScope.launch(Dispatchers.IO) {

                                try {

                                    textFile.bufferedWriter().use { writer ->

                                        repeat(5_000_000) { index ->

                                            writer.write("${index + 1}: $lineText")
                                            if (index % 100_000 == 0) "WRITE: $index".setDebug()
                                        }
                                    }

                                    "COMPLETED: ${
                                        EmptyFormat.toFileSize(textFile.fileLength())
                                    }".setDebug()
                                } catch (exception: Exception) {

                                    "ERROR: ${exception.message}".setDebug()
                                }
                            }
                        }
                    ) {

                        Text("Write Text File")
                    }
                }
            )
        }
    ) { paddingValues ->

        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            isRefreshing = textViewerState.isSourceLoading,
            onRefresh = textViewerState::setReloadTextSource
        ) {

            LazyTextViewer(
                modifier = Modifier.fillMaxSize(),
                state = textViewerState,
                properties = properties,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
            )
        }
    }
}