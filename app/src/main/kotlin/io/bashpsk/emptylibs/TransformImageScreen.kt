package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import io.bashpsk.emptylibs.imageview.transform.TransformImageView
import io.bashpsk.emptylibs.imageview.transform.rememberImageTransformState

@Composable
fun TransformImageScreen() {

    val imageBitmap = ImageBitmap.imageResource(R.drawable.empty_layer)

    val imageTransformState = rememberImageTransformState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TransformImageView(
                modifier = Modifier.fillMaxWidth(),
                imageModel = R.drawable.empty_layer,
                state = imageTransformState
            )
        }
    }
}