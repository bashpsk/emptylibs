package io.bashpsk.emptylibs.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.R
import io.bashpsk.emptylibs.composeutils.shape.PathShape
import io.bashpsk.emptylibs.imageutils.shape.bitmapMask

@Composable
fun ImageUtilsShapeScreen() {

//    val imageShape = PathShape.Circle
//    val imageShape = PathShape.Triangle
//    val imageShape = PathShape.Polygon(sides = 5)
//    val imageShape = PathShape.Polygon(sides = 6)
//    val imageShape = PathShape.Rectangle(radius = 0.10F)
//    val imageShape = PathShape.CutCorner(radius = 0.15F)
    val imageShape = PathShape.Star(edges = 5, distance = 2.5F)
    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper01)

    val imageBitmapMasked by remember {
        derivedStateOf { imageShape.bitmapMask(imageBitmap) }
    }

    val imageBitmapView = @Composable { bitmap: ImageBitmap ->

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16F / 9F),
            bitmap = bitmap,
            contentScale = ContentScale.Fit,
            contentDescription = null
        )
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            )
        ) {

            Text("Original")

            imageBitmapView(imageBitmap)

            Spacer(modifier = Modifier.height(height = 8.dp))

            Text("Shaped")

            imageBitmapView(imageBitmapMasked)
        }
    }
}