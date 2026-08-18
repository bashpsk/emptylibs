package io.bashpsk.emptylibs.screen

import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import io.bashpsk.emptylibs.imagekolor.filter.ImageFilterType
import io.bashpsk.emptylibs.pdfviewer.layout.PdfLazyColumn
import io.bashpsk.emptylibs.pdfviewer.layout.PdfLazyRow
import io.bashpsk.emptylibs.pdfviewer.layout.rememberPdfViewerState
import io.bashpsk.emptylibs.pdfviewer.source.PdfSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PdfViewerScreen() {

    val fileCoroutineScope = rememberCoroutineScope()

    var pdfSource by retain { mutableStateOf<PdfSource>(PdfSource.Empty) }
    val pdfViewerState = rememberPdfViewerState(source = pdfSource)

    var pdfLayoutType by rememberSaveable { mutableIntStateOf(0) }
    var isTopBarVisible by rememberSaveable { mutableStateOf(false) }
    val colorFilter = ImageFilterType.Original.colorFilter

    val onClick = remember { { offset: Offset -> isTopBarVisible = !isTopBarVisible } }

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { resultUri ->

            if (resultUri != null) pdfSource = PdfSource.URI(resultUri)
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            if (pdfSource != PdfSource.Empty && isTopBarVisible) SecondaryScrollableTabRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets),
                selectedTabIndex = pdfLayoutType
            ) {

                Button(onClick = { pdfLayoutType = 0 }) { Text("LazyColumn") }

                Button(onClick = { pdfLayoutType = 1 }) { Text("LazyRow") }

                Button(onClick = { pdfLayoutType = 2 }) { Text("LazyVerticalGrid") }
            }
        }
    ) { paddingValues ->

        when (pdfSource) {

            PdfSource.Empty -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Button(onClick = { pdfPicker.launch("application/pdf") }) {

                    Text(text = "Pick PDF Uri")
                }

                Button(
                    onClick = {

                        fileCoroutineScope.launch(context = Dispatchers.IO) {

                            val pdfFile = File(
                                Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_DOWNLOADS
                                ),
                                "Empty Libs.pdf"
                            )

                            pdfSource = PdfSource.Path(pdfFile.path)
                        }
                    }
                ) {

                    Text(text = "Pick PDF Path")
                }
            }

            else -> when (pdfLayoutType) {

                0 -> PdfLazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    state = pdfViewerState,
                    colorFilter = colorFilter,
                    onClick = onClick
                )

                1 -> PdfLazyRow(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    state = pdfViewerState,
                    colorFilter = colorFilter,
                    onClick = onClick
                )
            }
        }
    }
}