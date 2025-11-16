package io.bashpsk.emptylibs

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.imageedit.edit.ImageEdit
import io.bashpsk.emptylibs.imageedit.edit.rememberImageEditState
import io.bashpsk.emptylibs.imageview.transform.TransformImageView
import io.bashpsk.emptylibs.screen.imageedit.saveAsFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ImageEditScreen() {

    val context = LocalContext.current
    val bitmapCoroutineScope = rememberCoroutineScope()

    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper02)

    var isImageEdit by rememberSaveable { mutableStateOf(false) }
    var finalImage by remember { mutableStateOf<ImageBitmap?>(null) }

    val imageEditState = rememberImageEditState(imageBitmap = finalImage)

    LaunchedEffect(Unit) {

        delay(3.seconds)
        finalImage = imageBitmap
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

            AnimatedVisibility(
                visible = isImageEdit,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {

                ImageEdit(
                    modifier = Modifier.weight(1F),
                    state = imageEditState,
                    onDoneClick = {

                        bitmapCoroutineScope.launch(Dispatchers.IO) {

                            imageEditState.getEditedImageBitmap()?.let { bitmap ->

                                finalImage = bitmap

                                bitmap.saveAsFile(
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

                                    isImageEdit = false
                                }
                            }
                        }
                    },
                    onNavigateBack = {

                        isImageEdit = false
                    }
                )
            }

            AnimatedVisibility(
                visible = isImageEdit.not(),
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    TransformImageView(
                        modifier = Modifier.weight(weight = 1.0F),
                        imageModel = finalImage?.asAndroidBitmap()
                    )

                    Button(
                        onClick = {

                            isImageEdit = true
                        }
                    ) {

                        Text("Edit Image")
                    }
                }
            }
        }
    }
}