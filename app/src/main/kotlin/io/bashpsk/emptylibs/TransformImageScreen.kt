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
import io.bashpsk.emptylibs.imageview.transform.TransformImageView
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun TransformImageScreen() {

    val simpleList = persistentListOf(
        R.drawable.wallpaper01,
        R.drawable.wallpaper02,
        R.drawable.empty_layer,
        333
    )

    val tooLongList = (0..333).map { simpleList }.flatten().toImmutableList()

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
                imageModelList = simpleList,
                initialImage = R.drawable.wallpaper02,
                enableControls = true
            )
        }
    }
}