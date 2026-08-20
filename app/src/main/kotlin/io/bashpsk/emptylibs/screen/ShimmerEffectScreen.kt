package io.bashpsk.emptylibs.screen

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.R
import io.bashpsk.emptylibs.animations.shimmer.ShimmerEffectDefault
import io.bashpsk.emptylibs.animations.shimmer.shimmerEffect
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun ShimmerEffectScreen() {

    var imageId by retain { mutableStateOf<Int?>(null) }

    val imageLoadingShimmerModifier = if (imageId == null) Modifier.shimmerEffect(
        ShimmerEffectDefault.properties(
            angle = 45.0F,
            animationSpec = infiniteRepeatable(
                animation = tween(3_000),
                repeatMode = RepeatMode.Reverse
            )
        )
    ) else Modifier

    LaunchedEffect(Unit) {

        delay(10.seconds)
        imageId = R.drawable.wallpaper01
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues(8.dp)),
            contentPadding = paddingValues,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            )
        ) {

            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3.0F)
                        .background(color = Color.DarkGray)
                        .shimmerEffect()
                )
            }

            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3.0F)
                        .background(color = Color.DarkGray)
                        .shimmerEffect(
                            ShimmerEffectDefault.properties(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Cyan.copy(alpha = 0.2F),
                                    Color.Cyan.copy(alpha = 0.5F),
                                    Color.Cyan.copy(alpha = 0.2F),
                                    Color.Transparent
                                ),
                                angle = 45.0F,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(3500),
                                    repeatMode = RepeatMode.Restart
                                )
                            )
                        )
                )
            }

            item {

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3.50F)
                        .then(imageLoadingShimmerModifier),
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 8.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        imageId?.let { bitmap ->

                            Image(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(16F / 9F),
                                painter = painterResource(bitmap),
                                contentDescription = null
                            )

                            Column(
                                modifier = Modifier.weight(1.0F),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(
                                    space = 8.dp,
                                    alignment = Alignment.CenterVertically
                                )
                            ) {

                                Text(
                                    text = "This is the image title content.",
                                    style = MaterialTheme.typography.titleSmall
                                )

                                Text(
                                    text = "Image description.",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        } ?: run {

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(16F / 9F)
                                    .background(color = Color.Gray)
                            )

                            Column(
                                modifier = Modifier.weight(1.0F),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(
                                    space = 8.dp,
                                    alignment = Alignment.CenterVertically
                                )
                            ) {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = 0.85F)
                                        .height(24.dp)
                                        .background(color = Color.Gray)
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = 0.65F)
                                        .height(24.dp)
                                        .background(color = Color.Gray)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}