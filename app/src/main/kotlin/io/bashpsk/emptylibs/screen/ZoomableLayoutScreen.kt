package io.bashpsk.emptylibs.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import io.bashpsk.emptylibs.R
import io.bashpsk.emptylibs.formatter.format.findAspectRatio
import io.bashpsk.emptylibs.gestureui.transform.rememberTransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.transformableGestures
import io.bashpsk.emptylibs.layouts.zoomable.ZoomableLayout

@Composable
fun ZoomableLayoutScreen() {

    val sampleImage = ImageBitmap.imageResource(R.drawable.wallpaper01)

    val aspectRatio by remember(sampleImage) {
        derivedStateOf { findAspectRatio(width = sampleImage.width, height = sampleImage.height) }
    }

    val transformableState = rememberTransformableGesturesState(
        initialZoom = 1.0F,
        enableZoom = true,
        enablePan = true,
        enableDoubleTapZoom = true
    )

    val isScrollEnabled by remember(transformableState) {
        derivedStateOf { transformableState.touchCount == 1 }
    }

    val layoutPosition by remember(transformableState) {
        derivedStateOf { transformableState.position.round().copy(y = 0) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .transformableGestures(state = transformableState),
            userScrollEnabled = isScrollEnabled,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {

            items(7) {

                ZoomableLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { layoutPosition }
                        .border(width = 2.dp, color = Color.Red),
                    zoomScale = transformableState.zoom
                ) {

                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(aspectRatio)
                            .border(width = 2.dp, color = Color.DarkGray),
                        bitmap = sampleImage,
                        contentScale = ContentScale.Fit,
                        contentDescription = null
                    )
                }
            }
        }
    }
}