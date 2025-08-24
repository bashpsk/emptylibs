package io.bashpsk.emptylibs

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import io.bashpsk.emptylibs.canvasslate.slate.CanvasSlate
import io.bashpsk.emptylibs.canvasslate.slate.rememberCanvasSlateState
import io.bashpsk.emptylibs.screen.imageedit.saveAsFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CanvasSlateScreen() {

    val context = LocalContext.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    val canvasSlateState = rememberCanvasSlateState(
        background = MaterialTheme.colorScheme.background,
        initial = MaterialTheme.colorScheme.onSurface
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            CanvasSlate(
                modifier = Modifier.fillMaxSize(),
                state = canvasSlateState,
                onDoneClick = {

                    coroutineScope.launch(Dispatchers.IO) {

                        canvasSlateState.getImageBitmap(
                            density = density
                        )?.saveAsFile(context,"PSK-Drawing").let { file ->

                            launch(Dispatchers.Main) {

                                Toast.makeText(
                                    context,
                                    if (file?.exists() == true) "Image Saved" else "Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            )
        }
    }
}