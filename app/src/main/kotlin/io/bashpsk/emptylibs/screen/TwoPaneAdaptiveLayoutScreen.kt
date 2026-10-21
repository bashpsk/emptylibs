package io.bashpsk.emptylibs.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.R
import io.bashpsk.emptylibs.layouts.twopane.TwoPaneAdaptiveLayout

@Composable
fun TwoPaneAdaptiveLayoutScreen() {

    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper01)
//    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper02)

    val description = "End the day with a beautiful sunset as warm colors fill the sky " +
            "and slowly fade beyond the horizon. Shades of orange, pink, and gold " +
            "create a peaceful scene that is perfect for slowing down and reflecting. " +
            "Watch the final moments of daylight disappear while the landscape becomes " +
            "quiet and calm, creating a memorable end to the day.".trimIndent()

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        TwoPaneAdaptiveLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            firstPaneRatio = 0.50F,
            firstPane = {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, color = Color.Blue),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        bitmap = imageBitmap,
                        contentDescription = null
                    )
                }
            }
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, color = Color.Magenta)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(text = description)
            }
        }
    }
}