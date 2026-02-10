package io.bashpsk.emptylibs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import io.bashpsk.emptylibs.gestureui.transform.rememberTransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.transformableGestures
import io.bashpsk.emptylibs.imagekolor.filter.ImageFilterType
import io.bashpsk.emptylibs.imageview.tile.TileImageView
import io.bashpsk.emptylibs.jetpackui.layout.ZoomableLayout

@Composable
fun TileImageViewScreen() {

    val largeImage = ImageBitmap.imageResource(R.drawable.wallpaper_large)

    val transformableState = rememberTransformableGesturesState()

    val layoutPosition by remember(transformableState.position) {
        derivedStateOf { transformableState.position.round() }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            ZoomableLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
                    .transformableGestures(state = transformableState)
                    .offset { layoutPosition }
                    .border(width = 2.dp, Color.Red),
                zoomScale = transformableState.zoom
            ) {

                TileImageView(
                    modifier = Modifier.fillMaxWidth(),
                    imageBitmap = largeImage,
                    colorFilter = ImageFilterType.Original.colorFilter
                )
            }
        }
    }
}