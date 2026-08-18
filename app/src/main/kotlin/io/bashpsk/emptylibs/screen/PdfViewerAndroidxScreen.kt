package io.bashpsk.emptylibs.screen

import android.net.Uri
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.pdf.PdfDocument
import androidx.pdf.SandboxedPdfLoader
import androidx.pdf.compose.PdfViewer
import androidx.pdf.compose.PdfViewerState

@Composable
fun PdfViewerAndroidxScreen() {

    val context = LocalContext.current

    var pdfUri by retain { mutableStateOf<Uri?>(null) }
    val pdfLoader = retain { SandboxedPdfLoader(context) }
    val pdfViewerState = retain { PdfViewerState() }

    val pdfDocument by produceState<PdfDocument?>(initialValue = null, pdfUri) {
        value = pdfUri?.let { uri ->
            try {
                pdfLoader.openDocument(uri)
            } catch (e: Exception) {
                null
            }
        }
    }

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { resultUri -> pdfUri = resultUri }
    )

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        when (pdfUri) {

            null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Button(onClick = { pdfPicker.launch("application/pdf") }) {

                    Text(text = "Pick PDF Uri")
                }
            }

            else -> PdfViewer(
                modifier = Modifier.fillMaxSize(),
                pdfDocument = pdfDocument,
                state = pdfViewerState,
                contentPadding = paddingValues
            )
        }
    }
}