package io.bashpsk.emptylibs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.extension.size
import io.bashpsk.emptylibs.imageedit.extension.eraseImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageEditScreen() {

    val context = LocalContext.current
    val bitmapCoroutineScope = rememberCoroutineScope()

    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper01)

    val rect = Rect(Offset.Zero, imageBitmap.size / 2F)

    val path = remember(imageBitmap) {

        Path().apply {

            val center = Offset(x = imageBitmap.width / 2F, y = imageBitmap.height / 2F)
            val radius = minOf(imageBitmap.width, imageBitmap.height) / 2F

            addOval(oval = Rect(center = center, radius = radius))
        }
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

            Image(
                modifier = Modifier.fillMaxWidth(),
                bitmap = imageBitmap.eraseImage(area = rect),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )
        }
    }
}