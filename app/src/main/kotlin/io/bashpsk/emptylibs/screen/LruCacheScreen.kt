package io.bashpsk.emptylibs.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
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
import io.bashpsk.emptylibs.R
import io.bashpsk.emptylibs.lrucachemanager.manager.EmptyCacheManager

val bitmapCacheManager = EmptyCacheManager<String, Bitmap>(maxSize = 3)

@Composable
fun LruCacheScreen() {

    val bitmap1 = ImageBitmap.imageResource(id = R.drawable.wallpaper01)
    val bitmap2 = ImageBitmap.imageResource(id = R.drawable.wallpaper02)
    val bitmap3 = ImageBitmap.imageResource(id = R.drawable.empty_layer)

    var isRefresh by rememberSaveable { mutableStateOf(false) }

    val cachedBitmap1 by remember(isRefresh) {
        derivedStateOf { bitmapCacheManager["image1"] }
    }

    val cachedBitmap2 by remember(isRefresh) {
        derivedStateOf { bitmapCacheManager["image2"] }
    }

    val cachedBitmap3 by remember(isRefresh) {
        derivedStateOf { bitmapCacheManager["image3"] }
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

                FlowRow(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                ) {

                    Button(
                        onClick = {

                            bitmapCacheManager["image1"] = bitmap1.asAndroidBitmap()
                            isRefresh = !isRefresh
                        }
                    ) {

                        Text(text = "Add Image 1")
                    }

                    Button(
                        onClick = {

                            bitmapCacheManager["image2"] = bitmap2.asAndroidBitmap()
                            isRefresh = !isRefresh
                        }
                    ) {

                        Text(text = "Add Image 2")
                    }

                    Button(
                        onClick = {

                            bitmapCacheManager["image3"] = bitmap3.asAndroidBitmap()
                            isRefresh = !isRefresh
                        }
                    ) {

                        Text(text = "Add Image 3")
                    }

                    Button(
                        onClick = {

                            bitmapCacheManager.evictAll()
                            isRefresh = !isRefresh
                        }
                    ) {

                        Text(text = "Clear Cache")
                    }

                    Button(
                        onClick = {

                            bitmapCacheManager.remove("image1")
                            isRefresh = !isRefresh
                        }
                    ) {

                        Text(text = "Remove Image 1")
                    }

                    Button(
                        onClick = {

                            bitmapCacheManager.remove("image2")
                            isRefresh = !isRefresh
                        }
                    ) {

                        Text(text = "Remove Image 2")
                    }

                    Button(
                        onClick = {

                            bitmapCacheManager.remove("image3")
                            isRefresh = !isRefresh
                        }
                    ) {

                        Text(text = "Remove Image 3")
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