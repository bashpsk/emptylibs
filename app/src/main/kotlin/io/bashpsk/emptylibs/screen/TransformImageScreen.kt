package io.bashpsk.emptylibs.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import io.bashpsk.emptylibs.R
import io.bashpsk.emptylibs.gestureui.transform.rememberTransformableGesturesState
import io.bashpsk.emptylibs.imageview.transform.TransformImageView
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun TransformImageScreen() {

    val transformableGesturesState = rememberTransformableGesturesState(enableRotation = true)

    val imageList = remember {
        persistentListOf(
            R.drawable.wallpaper01,
            R.drawable.wallpaper02,
            R.drawable.empty_layer,
            333
        )
    }

    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper_large)
    var simpleList by remember { mutableStateOf(persistentListOf<Any?>()) }
    var initialImage by remember { mutableStateOf<Any?>(null) }

    val tooLongList = (0..333).flatMap { simpleList }.toImmutableList()

    LaunchedEffect(Unit) {

        delay(2.seconds)
        simpleList = imageList
        delay(2.seconds)
        initialImage = simpleList.getOrNull(1)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            TransformImageView(
                modifier = Modifier.weight(1F),
                state = transformableGesturesState,
                imageModelList = simpleList,
                initialImage = initialImage,
                onImageChanges = { image ->

                    image?.let { initialImage = it }
                }
            )

            /*TransformImageView(
                modifier = Modifier.fillMaxWidth(),
                state = transformableGesturesState,
                imageModel = imageBitmap,
                tileSize = 256
            )*/
        }
    }
}