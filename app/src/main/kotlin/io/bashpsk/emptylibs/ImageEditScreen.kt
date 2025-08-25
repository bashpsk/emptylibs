package io.bashpsk.emptylibs

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.imageedit.edit.ImageEdit
import io.bashpsk.emptylibs.imageedit.edit.rememberImageEditState
import io.bashpsk.emptylibs.imageutils.shape.ImageShape
import io.bashpsk.emptylibs.screen.imageedit.saveAsFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ImageEditScreen() {

    val context = LocalContext.current
    val density = LocalDensity.current
    val bitmapCoroutineScope = rememberCoroutineScope()

    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper01)
    val imageBitmap2 = ImageBitmap.imageResource(R.drawable.wallpaper02)
    val imageEditState = rememberImageEditState(imageBitmap = imageBitmap)

    val textStyle = MaterialTheme.typography.displayMedium.copy(
        color = MaterialTheme.colorScheme.secondary
    )

    LaunchedEffect(imageEditState) {

        imageEditState.updateTextStyle(textStyle)
        imageEditState.updateShape(shape = ImageShape.CutCorner(radius = 0.10F))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 4.dp)
        ) {

            ImageEdit(
                modifier = Modifier.fillMaxSize(),
                state = imageEditState,
                onBitmapSelect = {

                    imageEditState.updateBitmap(bitmap = imageBitmap2)
                },
                onDoneClick = {

                    bitmapCoroutineScope.launch(Dispatchers.IO) {

                        imageEditState.getEditedImageBitmap(
                            density = density
                        )?.saveAsFile(
                            context,
                            "PSK-Edited"
                        ).let { file ->

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