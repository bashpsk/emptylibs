package io.bashpsk.emptylibs

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.gestureui.video.DragChanges
import io.bashpsk.emptylibs.gestureui.video.TapChanges
import io.bashpsk.emptylibs.gestureui.video.ValueChange
import io.bashpsk.emptylibs.gestureui.video.VideoGestureBox
import io.bashpsk.emptylibs.utils.setDebug
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VideoGestureScreen() {

    val context = LocalContext.current
    val activity = LocalActivity.current
    val window = activity?.window

    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    var currentVolume by rememberSaveable {
        mutableIntStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
    }

    val maxVolume by remember {
        derivedStateOf { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }
    }

    var brightness by rememberSaveable(window) {
        mutableFloatStateOf(window?.attributes?.screenBrightness?.coerceIn(0.0F..1.0F) ?: 0.0F)
    }

    var boostedFinish by rememberSaveable { mutableFloatStateOf(0.0F) }
    var imageViewScale by rememberSaveable { mutableFloatStateOf(1.0F) }
    var imageViewOffset by remember { mutableStateOf(Offset.Zero) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        VideoGestureBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            onTapChanges = { changes ->

                when (changes) {

                    is TapChanges.BackwardTap -> "BackwardTap : ${changes.position}".setDebug()

                    is TapChanges.ForwardTap -> "ForwardTap : ${changes.position}".setDebug()

                    is TapChanges.SingleTap -> "SingleTap : ${changes.position}".setDebug()

                    is TapChanges.Unknown -> "Unknown".setDebug()
                }
            },
            onDragChanges = { changes ->

                when (changes) {

                    is DragChanges.VerticalLeftChanges -> {

                        when (changes.changes) {

                            ValueChange.Increased -> when (brightness >= 1.00F) {

                                true -> brightness = 1.00F
                                false -> brightness += 0.02F
                            }

                            ValueChange.Decreased -> when (brightness <= 0.00F) {

                                true -> brightness = 0.00F
                                false -> brightness -= 0.02F
                            }

                            else -> {}
                        }

                        window?.attributes = window.attributes?.apply {

                            screenBrightness = brightness.coerceIn(0.0F..1.0F)
                            "Brightness : $screenBrightness".setDebug()
                        }
                    }

                    is DragChanges.DragCanceled -> "DragCanceled".setDebug()

                    is DragChanges.DragEnded -> "DragEnded".setDebug()

                    is DragChanges.DragStart -> "DragStart : ${changes.position}".setDebug()

                    is DragChanges.HorizontalTopStart -> "HorizontalTopStart".setDebug()

                    is DragChanges.HorizontalBottomStart -> "HorizontalBottomStart".setDebug()

                    is DragChanges.HorizontalTopChanges -> {

                        "HorizontalTopChanges : ${changes.amount}[${changes.changes}]".setDebug()
                    }

                    is DragChanges.HorizontalBottomChanges -> {

                        "HorizontalBottomChanges : ${changes.amount}[${changes.changes}]".setDebug()
                    }

                    is DragChanges.HorizontalTopEnd -> {

                        "HorizontalTopEnd : ${changes.amount}".setDebug()
                    }

                    is DragChanges.HorizontalBottomEnd -> {

                        "HorizontalBottomEnd : ${changes.amount}".setDebug()
                    }

                    is DragChanges.Unknown -> "Unknown".setDebug()

                    is DragChanges.VerticalRightChanges -> {

                        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

                        when (changes.changes) {

                            ValueChange.Increased -> {

                                val isMaxVolume = currentVolume == maxVolume
                                val isBoost = boostedFinish <= 30.0F
                                val isBoostLeast = boostedFinish == 0.0F

                                when {

                                    isMaxVolume && isBoost -> {

                                        boostedFinish = (boostedFinish + 1.0F).coerceIn(
                                            range = 0.0F..30.0F
                                        )

                                        "Volume Boosted : $boostedFinish".setDebug()
                                    }

                                    isMaxVolume && isBoostLeast -> {

                                        boostedFinish = (boostedFinish + 1.0F).coerceIn(
                                            range = 0.0F..30.0F
                                        )

                                        "Volume Boosted : $boostedFinish".setDebug()
                                    }

                                    isMaxVolume.not() -> {

                                        audioManager.adjustStreamVolume(
                                            AudioManager.STREAM_MUSIC,
                                            AudioManager.ADJUST_RAISE,
                                            AudioManager.FLAG_PLAY_SOUND
                                        )

                                        currentVolume = audioManager.getStreamVolume(
                                            AudioManager.STREAM_MUSIC
                                        )

                                        "Volume Normal : $currentVolume".setDebug()
                                    }

                                    else -> {

                                        audioManager.adjustStreamVolume(
                                            AudioManager.STREAM_MUSIC,
                                            AudioManager.ADJUST_RAISE,
                                            AudioManager.FLAG_PLAY_SOUND
                                        )

                                        currentVolume = audioManager.getStreamVolume(
                                            AudioManager.STREAM_MUSIC
                                        )

                                        "Volume Normal : $currentVolume".setDebug()
                                    }
                                }
                            }

                            ValueChange.Decreased -> {

                                val isMaxVolume = currentVolume == maxVolume
                                val isBoost = boostedFinish >= 1.0F
                                val isBoostLeast = boostedFinish == 1.0F

                                when {

                                    isMaxVolume && isBoost -> {

                                        boostedFinish = (boostedFinish - 1.0F).coerceIn(
                                            range = 0.0F..30.0F
                                        )

                                        "Volume Boosted : $boostedFinish".setDebug()
                                    }

                                    isMaxVolume && isBoostLeast -> {

                                        boostedFinish = (boostedFinish - 1.0F).coerceIn(
                                            range = 0.0F..30.0F
                                        )

                                        "Volume Boosted : $boostedFinish".setDebug()
                                    }

                                    isMaxVolume.not() -> {

                                        audioManager.adjustStreamVolume(
                                            AudioManager.STREAM_MUSIC,
                                            AudioManager.ADJUST_LOWER,
                                            AudioManager.FLAG_PLAY_SOUND
                                        )

                                        currentVolume = audioManager.getStreamVolume(
                                            AudioManager.STREAM_MUSIC
                                        )

                                        "Volume Normal : $currentVolume".setDebug()
                                    }

                                    else -> {

                                        audioManager.adjustStreamVolume(
                                            AudioManager.STREAM_MUSIC,
                                            AudioManager.ADJUST_LOWER,
                                            AudioManager.FLAG_PLAY_SOUND
                                        )

                                        currentVolume = audioManager.getStreamVolume(
                                            AudioManager.STREAM_MUSIC
                                        )

                                        "Volume Normal : $currentVolume".setDebug()
                                    }
                                }
                            }

                            else -> {}
                        }
                    }

                    is DragChanges.TransformChanges -> {

                        imageViewScale *= changes.zoom
                        imageViewOffset += changes.pan
                    }
                }
            }
        ) {

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        scaleX = imageViewScale.coerceIn(range = 0.1F..5.0F),
                        scaleY = imageViewScale.coerceIn(range = 0.1F..5.0F),
                        translationX = imageViewOffset.x,
                        translationY = imageViewOffset.y
                    ),
                painter = painterResource(R.drawable.wallpaper01),
                contentScale = ContentScale.Fit,
                contentDescription = "Image"
            )

            GesturePreview(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.5F)
            )

            GesturePreview(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.5F)
                    .rotate(90.0F)
            )
        }
    }
}

@Composable
private fun GesturePreview(modifier: Modifier = Modifier) {

    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(space = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        GestureColorBox(
            modifier = Modifier
                .weight(weight = 0.05F)
                .fillMaxHeight(),
            color = Color.Gray
        )

        GestureColorBox(
            modifier = Modifier
                .weight(weight = 0.85F / 2)
                .fillMaxHeight(),
            color = Color.Green
        )

        GestureColorBox(
            modifier = Modifier
                .weight(weight = 0.05F)
                .fillMaxHeight(),
            color = Color.Gray
        )

        GestureColorBox(
            modifier = Modifier
                .weight(weight = 0.85F / 2)
                .fillMaxHeight(),
            color = Color.Yellow
        )

        GestureColorBox(
            modifier = Modifier
                .weight(weight = 0.05F)
                .fillMaxHeight(),
            color = Color.Gray
        )
    }
}

@Composable
private fun GestureColorBox(modifier: Modifier = Modifier, color: Color) {

    Box(
        modifier = modifier.background(color = color)
    )
}