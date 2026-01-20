package io.bashpsk.emptylibs.screen.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import io.bashpsk.emptylibs.formatter.format.EmptyFormat

@Composable
fun ImageBitmapView(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    index: Int,
    contentScale: ContentScale = ContentScale.Fit,
) {

    val aspectRatio by remember(imageBitmap) {
        derivedStateOf {
            EmptyFormat.findAspectRatio(width = imageBitmap.width, height = imageBitmap.height)
//            1F
        }
    }

    Box(
        modifier = modifier.aspectRatio(aspectRatio),
        contentAlignment = Alignment.TopCenter
    ) {

        Image(
            modifier = Modifier.matchParentSize(),
            bitmap = imageBitmap,
            contentScale = contentScale,
            contentDescription = null
        )

        Text(
            text = "${index + 1}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}