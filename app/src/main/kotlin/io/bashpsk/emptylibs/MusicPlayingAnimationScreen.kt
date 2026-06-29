package io.bashpsk.emptylibs

import androidx.compose.animation.core.EaseInOutBounce
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.animations.music.MusicPlayingAnimation
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun MusicPlayingAnimationScreen() {

    var isPlaying1 by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        delay(3.seconds)
        isPlaying1 = true
        delay(3.seconds)
        delay(10.seconds)
        isPlaying1 = false
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 12.dp)
        ) {

            MusicPlayingAnimation(
                modifier = Modifier
                    .size(size = 40.dp)
                    .border(width = 1.dp, color = Color.Red),
                isPlaying = isPlaying1,
                barCount = 3,
                boxCount = 4,
                easing = EaseOutBounce
            )

            MusicPlayingAnimation(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F)
                    .border(width = 1.dp, color = Color.Red),
                isPlaying = true,
                barCount = 5,
                boxCount = 5,
                easing = EaseInOutBounce
            )
        }
    }
}