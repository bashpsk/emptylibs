package io.bashpsk.emptylibs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.layouts.collapsible.SwipeCollapsibleLayout
import io.bashpsk.emptylibs.layouts.collapsible.rememberSwipeCollapsibleLayoutState
import io.bashpsk.emptylibs.screen.layouts.VideoData
import io.bashpsk.emptylibs.screen.layouts.dummyVideoList
import kotlinx.coroutines.launch

@Composable
fun SwipeCollapsibleLayoutScreen() {

    val coroutineScope = rememberCoroutineScope()
    val state = rememberSwipeCollapsibleLayoutState()

    var selectedEntry by retain { mutableStateOf<VideoData?>(null) }

    val imageItemView = @Composable { imageItem: VideoData ->

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            onClick = {
                coroutineScope.launch { state.expand() }
                selectedEntry = imageItem
            }
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16F / 9F),
                    painter = painterResource(imageItem.thumbnail),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = Alignment.CenterVertically
                    )
                ) {

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = imageItem.title,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.75F),
                        text = "3.33K views  •  333 likes  •  3 years ago",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }

    val recommendationItemView = @Composable { imageItem: VideoData ->

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            onClick = {
                coroutineScope.launch { state.expand() }
                selectedEntry = imageItem
            }
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    modifier = Modifier
                        .fillMaxWidth(0.5F)
                        .aspectRatio(16F / 9F),
                    painter = painterResource(imageItem.thumbnail),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )

                Column(
                    modifier = Modifier
                        .weight(1.0F)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = Alignment.CenterVertically
                    )
                ) {

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = imageItem.title,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.75F),
                        text = "3.33K views  •  333 likes  •  3 years ago",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
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

                    selectedEntry?.let { imageItem ->

                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16F / 9F),
                            painter = painterResource(imageItem.thumbnail),
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
                        text = selectedEntry?.title ?: "Not selected!"
                    )

                    IconButton(
                        onClick = {
                            selectedEntry = dummyVideoList[
                                dummyVideoList.indexOf(selectedEntry) - 1
                            ]
                        }
                    ) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = null)
                    }

                    IconButton(
                        onClick = {
                            selectedEntry = dummyVideoList[
                                dummyVideoList.indexOf(selectedEntry) + 1
                            ]
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
                    contentPadding = PaddingValues(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterVertically
                    )
                ) {

                    item { HorizontalDivider(thickness = 1.6.dp) }

                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    item {
                        Text(
                            text = "Description: ${selectedEntry?.description ?: ""}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    item { HorizontalDivider(thickness = 1.6.dp) }

                    items(dummyVideoList) { imageItem ->

                        recommendationItemView(imageItem)
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
                    space = 8.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {

                items(dummyVideoList) { imageItem ->

                    imageItemView(imageItem)
                }
            }
        }
    }
}