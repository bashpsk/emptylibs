package io.bashpsk.emptylibs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.lrucachemanager.manager.BitmapCacheManager

val bitmapCacheManager = BitmapCacheManager(3)

@Composable
fun LruCacheScreen() {

    val bitmap1 = ImageBitmap.imageResource(id = R.drawable.wallpaper01)
    val bitmap2 = ImageBitmap.imageResource(id = R.drawable.wallpaper02)
    val bitmap3 = ImageBitmap.imageResource(id = R.drawable.empty_layer)

    var isRefresh by rememberSaveable { mutableStateOf(false) }

    val cachedBitmap1 by remember(isRefresh) {
        derivedStateOf { bitmapCacheManager.getBitmap("image1") }
    }

    val cachedBitmap2 by remember(isRefresh) {
        derivedStateOf { bitmapCacheManager.getBitmap("image2") }
    }

    val cachedBitmap3 by remember(isRefresh) {
        derivedStateOf { bitmapCacheManager.getBitmap("image3") }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {

            item {

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(
                        onClick = {

                            bitmapCacheManager.addBitmap("image1", bitmap1.asAndroidBitmap())
                            isRefresh = !isRefresh
                        }
                    ) {

                        Text(text = "Image 1")
                    }

                    Button(
                        onClick = {

                            bitmapCacheManager.addBitmap("image2", bitmap2.asAndroidBitmap())
                            isRefresh = !isRefresh
                        }
                    ) {

                        Text(text = "Image 2")
                    }

                    Button(
                        onClick = {

                            bitmapCacheManager.addBitmap("image3", bitmap3.asAndroidBitmap())
                            isRefresh = !isRefresh
                        }
                    ) {

                        Text(text = "Image 3")
                    }
                }
            }

            item {

                cachedBitmap1?.asImageBitmap()?.let { imageBitmap ->

                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        bitmap = imageBitmap,
                        contentDescription = "Image 1"
                    )
                }
            }

            item {

                cachedBitmap2?.asImageBitmap()?.let { imageBitmap ->

                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        bitmap = imageBitmap,
                        contentDescription = "Image 2"
                    )
                }
            }

            item {

                cachedBitmap3?.asImageBitmap()?.let { imageBitmap ->

                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        bitmap = imageBitmap,
                        contentDescription = "Image 3"
                    )
                }
            }
        }
    }
}