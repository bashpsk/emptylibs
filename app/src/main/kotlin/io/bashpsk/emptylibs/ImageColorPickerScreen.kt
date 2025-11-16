package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.kolorpicker.color.ImageKolorPicker

@Composable
fun ImageColorPickerScreen() {

    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.wallpaper02)

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        ImageKolorPicker(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            imageBitmap = imageBitmap
        )
    }
}