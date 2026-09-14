package io.bashpsk.emptylibs.imageview.transform

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import io.bashpsk.emptylibs.imageview.R

object TransformImageViewDefault {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun LoadingIndicator() {

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            ContainedLoadingIndicator()
        }
    }

    @Composable
    fun ErrorIndicator() {

        Image(
            modifier = Modifier.fillMaxWidth(fraction = 0.65F),
            painter = painterResource(id = R.drawable.image_broken),
            contentDescription = "Image Load Failed"
        )
    }
}