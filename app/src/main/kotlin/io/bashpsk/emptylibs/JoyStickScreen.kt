package io.bashpsk.emptylibs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import io.bashpsk.emptylibs.jetpackui.joystick.JoyStick
import io.bashpsk.emptylibs.jetpackui.joystick.JoyStickDefaults
import io.bashpsk.emptylibs.jetpackui.joystick.JoyStickType
import io.bashpsk.emptylibs.jetpackui.joystick.rememberJoyStickState

@Composable
fun JoyStickScreen() {

    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper01)

    val properties = JoyStickDefaults.properties(
        speed = 0.5.dp,
        faceToDirection = true,
        type = JoyStickType._03
    )

    val colors = JoyStickDefaults.colors(
        thumbColor = Color.Gray.copy(alpha = 0.85F),
        borderColor = Color.Gray.copy(alpha = 0.35F)
    )

    val joyStickState = rememberJoyStickState(properties = properties)

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {

            Image(
                modifier = Modifier
                    .width(100.dp)
                    .align(Alignment.Center)
                    .offset { joyStickState.changes.motion.round() }
                    .rotate(joyStickState.changes.rotation),
                bitmap = imageBitmap,
                contentScale = ContentScale.Fit,
                contentDescription = "Movable Image"
            )

            JoyStick(
                modifier = Modifier
                    .padding(40.dp)
                    .width(width = 100.dp)
                    .align(Alignment.BottomStart),
                state = joyStickState,
                colors = colors
            )
        }
    }
}