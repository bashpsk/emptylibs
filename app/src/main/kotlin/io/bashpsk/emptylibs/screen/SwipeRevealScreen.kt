package io.bashpsk.emptylibs.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.component.reveal.RevealData
import io.bashpsk.emptylibs.layouts.reveal.SwipeRevealItem
import io.bashpsk.emptylibs.layouts.reveal.rememberSwipeRevealState
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

@Composable
fun SwipeRevealScreen() {

    val coroutineScope = rememberCoroutineScope()

    var sampleContentList by remember {
        mutableStateOf(
            (1..10).map { item ->

                RevealData(
                    content = "$item. This is the sample item for swipe card.",
                    isRead = false
                )
            }.toPersistentList()
        )
    }

    val contentItemView = @Composable { content: RevealData ->

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraSmall,
            colors = if (content.isRead) CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) else CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            onClick = {}
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = content.content,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = content.content,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    val actionButtonView = @Composable { label: String,
                                         icon: ImageVector,
                                         colors: CardColors,
                                         onClick: () -> Unit ->

        ElevatedCard(
            modifier = Modifier,
            shape = MaterialTheme.shapes.extraSmall,
            colors = colors,
            onClick = onClick
        ) {

            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {

                Icon(imageVector = icon, contentDescription = label)

                Text(text = label, style = MaterialTheme.typography.labelMedium)
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = paddingValues,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            )
        ) {

            items(
                items = sampleContentList,
                key = { content -> content.content }
            ) { content ->

                val swipeRevealState = rememberSwipeRevealState()

                SwipeRevealItem(
                    modifier = Modifier.fillMaxWidth(),
                    state = swipeRevealState,
                    leftContent = {

                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 4.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            actionButtonView(
                                "Delete",
                                Icons.Filled.DeleteForever,
                                CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {

                                sampleContentList = sampleContentList.removing(content)
                                coroutineScope.launch { swipeRevealState.hide() }
                            }
                        }
                    },
                    rightContent = {

                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 4.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            actionButtonView(
                                "Archive",
                                Icons.Filled.Archive,
                                CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {

                                coroutineScope.launch { swipeRevealState.hide() }
                            }

                            actionButtonView(
                                "Read",
                                Icons.Filled.MarkEmailRead,
                                CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = MaterialTheme.colorScheme.onTertiary
                                )
                            ) {

                                val index = sampleContentList.indexOf(content)

                                if (index != -1) sampleContentList = sampleContentList.replacingAt(
                                    index,
                                    content.copy(isRead = true)
                                )

                                coroutineScope.launch { swipeRevealState.hide() }
                            }
                        }
                    }
                ) {

                    contentItemView(content)
                }
            }
        }
    }
}