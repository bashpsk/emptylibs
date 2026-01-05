package io.bashpsk.emptylibs

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.bashpsk.emptylibs.pdfviewer.pdf.PdfLazyColumn
import io.bashpsk.emptylibs.pdfviewer.pdf.rememberPdfLazyColumnState

@Composable
fun PdfViewerScreen() {

    var uri by remember { mutableStateOf<Uri?>(null) }

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { resultUri ->

            if (resultUri != null) uri = resultUri
        }
    )

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        when (uri) {

            null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Button(onClick = { pdfPicker.launch("application/pdf") }) {

                    Text(text = "Pick PDF")
                }
            }

            else -> PdfLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = rememberPdfLazyColumnState(uri = uri)
            )
        }
    }
}