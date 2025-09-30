package io.bashpsk.emptylibs.imagewallpaper.wallpaper

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import io.bashpsk.emptylibs.formatter.format.EmptyFormat
import io.bashpsk.emptylibs.imagekrop.crop.ImageKrop
import io.bashpsk.emptylibs.imagekrop.crop.KropAspectRatio
import io.bashpsk.emptylibs.imagekrop.crop.KropConfig
import io.bashpsk.emptylibs.imagekrop.crop.rememberImageKropState

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ImageWallpaper(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    config: KropConfig = KropConfig.surfaceBased(),
    onNavigateBack: () -> Unit = {}
) {

    val windowInfo = LocalWindowInfo.current
    val configuration = LocalConfiguration.current
    val imageKropState = rememberImageKropState(imageBitmap = imageBitmap, config = config)
    val wallpaperTypeDialogVisibleState = remember { MutableTransitionState(false) }

    val wallpaperAspectRatio by remember(configuration.orientation, windowInfo.containerSize) {
        derivedStateOf {
            EmptyFormat.findAspectRatio(
                width = windowInfo.containerSize.width.takeIf {
                    configuration.orientation != Configuration.ORIENTATION_LANDSCAPE
                } ?: windowInfo.containerSize.height,
                height = windowInfo.containerSize.height.takeIf {
                    configuration.orientation != Configuration.ORIENTATION_LANDSCAPE
                } ?: windowInfo.containerSize.width
            )
        }
    }

    imageKropState.modifiedImage?.let { bitmap ->

        WallpaperTypeDialog(
            dialogVisibleState = wallpaperTypeDialogVisibleState,
            imageBitmap = bitmap
        )
    } ?: run {

        wallpaperTypeDialogVisibleState.targetState = false
    }

    LaunchedEffect(imageKropState, wallpaperAspectRatio) {

        imageKropState.updateAspectRatio(aspect = KropAspectRatio(ratio = wallpaperAspectRatio))
        imageKropState.updateAspectLocked(locked = true)
    }

    ImageKrop(
        modifier = modifier,
        state = imageKropState,
        bottomBar = {},
        onKropFinished = {

            wallpaperTypeDialogVisibleState.targetState = true
        },
        onNavigateBack = onNavigateBack
    )
}