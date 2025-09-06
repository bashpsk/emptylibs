package io.bashpsk.emptylibs

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SaveAs
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import io.bashpsk.emptylibs.imagekolor.color.ImageKolorLayout
import io.bashpsk.emptylibs.imagekolor.color.rememberImageKolorState
import io.bashpsk.emptylibs.screen.imageedit.saveAsFile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageKolorScreen() {

    val context = LocalContext.current
    val bitmapCoroutineScope = rememberCoroutineScope()

    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper01)
    val kolorState = rememberImageKolorState(imageBitmap = imageBitmap)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            TopAppBar(
                title = {

                    Text("Kolor Filter")
                },
                actions = {

                    IconButton(
                        onClick = {

                            bitmapCoroutineScope.launch {

                                kolorState.getColorImage()?.saveAsFile(
                                    context,
                                    name = "PSK-Custom-Colored"
                                ).let { file ->

                                    Toast.makeText(
                                        context,
                                        if (file?.exists() == true) "Image Saved" else "Failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Filled.SaveAs,
                            contentDescription = "Save As File"
                        )
                    }

                    IconButton(
                        onClick = {

                            kolorState.resetAllValues()
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Filled.Restore,
                            contentDescription = "Reset All Values"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        ImageKolorLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = kolorState,
        )
    }
}