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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import io.bashpsk.emptylibs.imagekrop.crop.ImageKrop
import io.bashpsk.emptylibs.imagekrop.crop.KropConfig
import io.bashpsk.emptylibs.imagekrop.crop.rememberImageKropState
import io.bashpsk.emptylibs.imageview.transform.TransformImageView
import io.bashpsk.emptylibs.imageview.transform.rememberImageTransformState
import io.bashpsk.emptylibs.screen.imageedit.saveAsFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ImageCropScreen() {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val imageTransformState = rememberImageTransformState()

    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper01)

    var isImageEdit by rememberSaveable { mutableStateOf(false) }

    val handleColor = MaterialTheme.colorScheme.onSurface
    val targetColor = MaterialTheme.colorScheme.surfaceTint
    val borderColor = MaterialTheme.colorScheme.errorContainer
    val overlayColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5F)

    val kropConfig by remember(
        handleColor,
        targetColor,
        borderColor,
        overlayColor
    ) {
        derivedStateOf {
            KropConfig(
                minimumCropSize = 100.dp,
                handleColor = handleColor,
                targetColor = targetColor,
                borderColor = borderColor,
                overlayColor = overlayColor
            )
        }
    }

    val imageKropState = rememberImageKropState(imageBitmap = imageBitmap, config = kropConfig)

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        AnimatedVisibility(
            visible = isImageEdit,
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally() + fadeOut()
        ) {

            ImageKrop(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                state = imageKropState,
                onKropFinished = {

                    coroutineScope.launch(Dispatchers.IO) {

                        imageKropState.modifiedImage?.saveAsFile(
                            context,
                            "PSK-Cropped"
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
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                imageKropState.modifiedImage?.let { bitmap ->

                    TransformImageView(
                        modifier = Modifier.weight(weight = 1.0F),
                        imageModel = bitmap.asAndroidBitmap(),
                        state = imageTransformState
                    )
                } ?: TransformImageView(
                    modifier = Modifier.weight(weight = 1.0F),
                    imageModel = imageBitmap.asAndroidBitmap(),
                    state = imageTransformState
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