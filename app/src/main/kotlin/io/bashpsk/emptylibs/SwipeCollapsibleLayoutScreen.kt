package io.bashpsk.emptylibs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.layouts.collapsible.SwipeCollapsibleLayout
import io.bashpsk.emptylibs.layouts.collapsible.rememberSwipeCollapsibleLayoutState
import kotlinx.coroutines.launch

@Composable
fun SwipeCollapsibleLayoutScreen() {

    val coroutineScope = rememberCoroutineScope()
    val state = rememberSwipeCollapsibleLayoutState()

    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper01)
    val imageBitmap2 = ImageBitmap.imageResource(R.drawable.wallpaper02)
    val imageBitmap3 = ImageBitmap.imageResource(R.drawable.empty_layer)

    val imageFeedList by remember {
        derivedStateOf {
            (1..33).associate { index ->

                "Video feed $index" to when (index % 3) {
                    1 -> imageBitmap
                    2 -> imageBitmap2
                    else -> imageBitmap3
                }
            }.toList()
        }
    }

    val imageRecommendationList by remember {
        derivedStateOf {
            (1..10).associate { index ->

                "Video recommendation $index" to when (index % 3) {
                    1 -> imageBitmap
                    2 -> imageBitmap2
                    else -> imageBitmap3
                }
            }.toList()
        }
    }

    var selectedEntry by retain { mutableStateOf<Pair<String, ImageBitmap>?>(null) }

    val imageItemView = @Composable { imageItem: Pair<String, ImageBitmap> ->

        ListItem(
            modifier = Modifier
                .padding(8.dp)
                .clickable(
                    onClick = {
                        coroutineScope.launch { state.expand() }
                        selectedEntry = imageItem
                    }
                ),
            headlineContent = { Text(imageItem.first) },
            leadingContent = {

                Box(
                    modifier = Modifier
                        .size(120.dp, 72.dp)
                        .background(Color.Gray)
                ) {

                    Image(
                        modifier = Modifier.fillMaxSize(),
                        bitmap = imageItem.second,
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                }
            }
        )
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        SwipeCollapsibleLayout(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            state = state,
            primaryContent = {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Blue)
                ) {

                    selectedEntry?.second?.let {

                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16F / 9F),
                            bitmap = it,
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )
                    }
                }
            },
            secondaryContent = {

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier = Modifier.weight(1f),
                        text = selectedEntry?.first ?: "Not selected!"
                    )

                    IconButton(
                        onClick = {
                            selectedEntry = imageFeedList[imageFeedList.indexOf(selectedEntry) - 1]
                        }
                    ) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = null)
                    }

                    IconButton(
                        onClick = {
                            selectedEntry = imageFeedList[imageFeedList.indexOf(selectedEntry) + 1]
                        }
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = null)
                    }

                    IconButton(onClick = { coroutineScope.launch { state.dismiss() } }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            tertiaryContent = {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = Alignment.CenterVertically
                    )
                ) {

                    items(imageRecommendationList) { imageItem ->

                        imageItemView(imageItem)
                    }
                }
            }
        ) { layoutPaddingValues ->

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 4.dp,
                    top = 4.dp,
                    end = 4.dp,
                    bottom = layoutPaddingValues.calculateBottomPadding()
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {

                items(imageFeedList) { imageItem ->

                    imageItemView(imageItem)
                }
            }
        }
    }
}