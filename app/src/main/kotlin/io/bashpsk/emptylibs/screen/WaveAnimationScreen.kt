package io.bashpsk.emptylibs.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.animations.wave.waveAnimation
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun WaveAnimationScreen() {

    val infiniteTransition = rememberInfiniteTransition(label = "Wave Animation")

    var progressLevel by rememberSaveable { mutableFloatStateOf(0.0F) }

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 1F,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Wave Offset"
    )

    val waveOffset2 by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 1F,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Wave Offset"
    )

    LaunchedEffect(Unit) {

        while (isActive && progressLevel < 1.0F) {

            progressLevel += 0.0025F
            delay(100.milliseconds)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.CenterVertically
            )
        ) {

            Column(
                modifier = Modifier
                    .border(width = 2.dp, color = Color.Red)
                    .waveAnimation(
                        progress = 0.69F,
                        waveOffset = waveOffset,
                        waveColor = Color.Blue
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(text = "Bash PSK", style = MaterialTheme.typography.displayLarge)

                Text(text = "Bash PSK", style = MaterialTheme.typography.displayLarge)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(width = 2.dp, color = Color.Red)
                    .waveAnimation(
                        progress = 0.69F,
                        waveOffset = waveOffset,
                        waveColor = Color.Magenta
                    )
            )

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .border(width = 2.dp, color = Color.Red)
                    .waveAnimation(
                        progress = progressLevel,
                        waveOffset = waveOffset2,
                        waveColor = Color.Green
                    )
            )
        }
    }
}