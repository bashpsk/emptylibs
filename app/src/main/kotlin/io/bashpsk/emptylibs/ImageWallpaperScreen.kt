package io.bashpsk.emptylibs

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.imagekrop.crop.KropConfig
import io.bashpsk.emptylibs.imagewallpaper.wallpaper.ImageWallpaper
import io.bashpsk.emptylibs.utils.setDebug

@Composable
fun ImageWallpaperScreen() {

    val activity = LocalActivity.current

    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper01)

    val handleColor = MaterialTheme.colorScheme.onSurface
    val targetColor = MaterialTheme.colorScheme.surfaceTint
    val borderColor = MaterialTheme.colorScheme.errorContainer
    val overlayColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5F)

    val kropConfig by remember(
        handleColor,
        targetColor,
        borderColor,
        overlayColor
    ) {
        derivedStateOf {
            KropConfig(
                minimumCropSize = 40.dp,
                handleColor = handleColor,
                targetColor = targetColor,
                borderColor = borderColor,
                overlayColor = overlayColor
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        ImageWallpaper(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            imageBitmap = imageBitmap,
            config = kropConfig,
            dialogContainerColor = AlertDialogDefaults.containerColor.copy(alpha = 0.75F),
            onWallpaperResult = { type, result ->

                "${type.label} Wallpaper set result: $result".setDebug()
            },
            onNavigateBack = {

                activity?.finishAfterTransition()
            }
        )
    }
}