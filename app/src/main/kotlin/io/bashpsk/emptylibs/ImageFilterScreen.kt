package io.bashpsk.emptylibs

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.imagekolor.filter.ImageFilter
import io.bashpsk.emptylibs.imagekolor.filter.rememberImageFilterState
import io.bashpsk.emptylibs.screen.imageedit.saveAsFile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageFilterScreen() {

    val context = LocalContext.current
    val bitmapCoroutineScope = rememberCoroutineScope()

    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper02)
    val imageFilterState = rememberImageFilterState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            TopAppBar(
                title = {

                    Text(text = "Image Kolor")
                },
                actions = {

                    IconButton(
                        onClick = {

                            bitmapCoroutineScope.launch {

                                imageFilterState.getFilterImage(
                                    imageBitmap
                                ).saveAsFile(context,name = "PSK-Colored").let { file ->

                                    Toast.makeText(
                                        context,
                                        if (file?.exists() == true) "Image Saved" else "Failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = "Done"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(space = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16F / 9F),
                bitmap = imageBitmap,
                contentScale = ContentScale.Fit,
                colorFilter = imageFilterState.selectedFilter.colorFilter,
                contentDescription = "Image Color Filter"
            )

            ImageFilter(
                modifier = Modifier.weight(weight = 1.0F),
                state = imageFilterState
            )
        }
    }
}