package io.bashpsk.emptylibs.screen

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.R
import io.bashpsk.emptylibs.animations.diagonal.DiagonalLabelProperties
import io.bashpsk.emptylibs.animations.diagonal.diagonalLabel
import io.bashpsk.emptylibs.animations.shimmer.ShimmerEffectDefault

@Composable
fun DiagonalLabelScreen() {

    val textMeasurer = rememberTextMeasurer()

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 6.dp,
                alignment = Alignment.CenterVertically
            )
        ) {

            item {

                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16F / 9F)
                        .diagonalLabel(
                            text = "69% discount",
                            textMeasurer = textMeasurer,
                            alignment = Alignment.TopEnd,
                            properties = DiagonalLabelProperties(
                                containerColor = Color.Red,
                                labelColor = Color.Yellow,
                                labelStyle = MaterialTheme.typography.bodyMedium,
                                shimmerProperties = ShimmerEffectDefault.properties(
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(3000),
                                        repeatMode = RepeatMode.Restart
                                    )
                                )
                            )
                        ),
                    painter = painterResource(R.drawable.thumbnail01),
                    contentDescription = null
                )
            }

            item {

                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16F / 9F)
                        .diagonalLabel(
                            text = "OFFER",
                            textMeasurer = textMeasurer,
                            alignment = Alignment.TopStart,
                            properties = DiagonalLabelProperties(
                                containerColor = Color.Blue,
                                labelColor = Color.Green,
                                labelStyle = MaterialTheme.typography.titleSmall,
                                padding = PaddingValues(top = 16.dp),
                                shimmerProperties = ShimmerEffectDefault.properties(
                                    angle = 90.0F,
                                    widthRatio = 0.35F,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(2500),
                                        repeatMode = RepeatMode.Reverse
                                    )
                                )
                            )
                        ),
                    painter = painterResource(R.drawable.thumbnail01),
                    contentDescription = null
                )
            }
        }
    }
}