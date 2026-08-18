package io.bashpsk.emptylibs.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.R
import io.bashpsk.emptylibs.component.image.ImageBitmapView
import io.bashpsk.emptylibs.jetpackui.scrollbar.LazyListScrollBar
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollBarLazyListScreen() {

    val barLazyListState = rememberLazyListState()

    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper01)
    val imageBitmap2 = ImageBitmap.imageResource(R.drawable.wallpaper02)
    val imageList by remember {
        derivedStateOf {
            (0..33).map { index ->

                if (index % 2 == 1) imageBitmap else imageBitmap2
            }.toImmutableList()
        }
    }
    var isLazyColumn by rememberSaveable { mutableStateOf<Boolean?>(null) }

    val scrollBarContent: @Composable BoxWithConstraintsScope.() -> Unit = {

        LazyListScrollBar(
            modifier = Modifier,
            state = barLazyListState,
            label = { index, visibleItemsCount, itemsCount ->

                val barLabel by remember(index, itemsCount, visibleItemsCount) {
                    derivedStateOf {
                        when (visibleItemsCount) {

                            0 -> "$index/$itemsCount"
                            1 -> "${index + 1}/$itemsCount"
                            else -> "${index + 1}-${index + visibleItemsCount}/$itemsCount"
                        }
                    }
                }

                Text(
                    text = barLabel,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis
                )
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            TopAppBar(
                title = { Text(text = "Scroll Bar") },
                navigationIcon = {

                    IconButton(onClick = { isLazyColumn = null }) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues),
            contentAlignment = Alignment.Center
        ) {

            when (isLazyColumn) {

                true -> {

                    LazyColumn(
                        modifier = Modifier.matchParentSize(),
                        state = barLazyListState,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {

                        itemsIndexed(items = imageList) { index, image ->

                            ImageBitmapView(
                                modifier = Modifier.fillMaxWidth(),
                                imageBitmap = image,
                                index = index,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    scrollBarContent()
                }

                false -> {

                    LazyRow(
                        modifier = Modifier.matchParentSize(),
                        state = barLazyListState,
                        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        itemsIndexed(items = imageList) { index, image ->

                            ImageBitmapView(
                                modifier = Modifier.fillMaxHeight(),
                                imageBitmap = image,
                                index = index,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    scrollBarContent()
                }

                null -> Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Button(onClick = { isLazyColumn = true }) {

                        Text("Lazy Column")
                    }

                    Button(onClick = { isLazyColumn = false }) {

                        Text("Lazy Row")
                    }
                }
            }
        }
    }
}