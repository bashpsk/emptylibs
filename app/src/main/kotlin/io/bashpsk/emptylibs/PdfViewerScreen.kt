package io.bashpsk.emptylibs

import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.bashpsk.emptylibs.imagekolor.filter.ImageFilterType
import io.bashpsk.emptylibs.pdfviewer.pdf.PdfLazyColumn
import io.bashpsk.emptylibs.pdfviewer.pdf.PdfSource
import io.bashpsk.emptylibs.pdfviewer.pdf.rememberPdfLazyColumnState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PdfViewerScreen() {

    val fileCoroutineScope = rememberCoroutineScope()

    var pdfSource by retain { mutableStateOf<PdfSource>(PdfSource.Empty) }

    val pdfLazyColumnState = rememberPdfLazyColumnState(source = pdfSource)

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { resultUri ->

            if (resultUri != null) pdfSource = PdfSource.URI(resultUri)
        }
    )

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

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

            else -> PdfLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = pdfLazyColumnState,
                contentPadding = paddingValues,
                colorFilter = ImageFilterType.Original.colorFilter
            )
        }
    }
}