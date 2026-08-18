package io.bashpsk.emptylibs.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.R
import io.bashpsk.emptylibs.gestureui.transform.rememberTransformableGesturesState
import io.bashpsk.emptylibs.imagekolor.filter.ImageFilterType
import io.bashpsk.emptylibs.imageview.tile.TileImageView
import io.bashpsk.emptylibs.layouts.zoomable.ZoomableLayout

@Composable
fun TileImageViewScreen() {

    val largeImage = ImageBitmap.imageResource(R.drawable.wallpaper_large)

    val transformableState = rememberTransformableGesturesState()

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
                    .fillMaxWidth()
                    .clipToBounds()
                    .border(width = 2.dp, Color.Red),
                state = transformableState
            ) {

                TileImageView(
                    modifier = Modifier.fillMaxWidth(),
                    imageBitmap = largeImage,
                    tileSize = 128,
                    colorFilter = ImageFilterType.Original.colorFilter
                )
            }
        }
    }
}