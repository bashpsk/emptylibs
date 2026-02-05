package io.bashpsk.emptylibs.imagewallpaper.wallpaper

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import io.bashpsk.emptylibs.formatter.format.findAspectRatio
import io.bashpsk.emptylibs.imagekrop.crop.ImageKrop
import io.bashpsk.emptylibs.imagekrop.crop.KropAspectRatio
import io.bashpsk.emptylibs.imagekrop.crop.KropConfig
import io.bashpsk.emptylibs.imagekrop.crop.rememberImageKropState

/**
 * A composable that provides an interface for cropping an image to fit the device's screen
 * dimensions and then setting it as the wallpaper. It utilizes [ImageKrop] for the cropping
 * functionality and presents a [WallpaperTypeDialog] to choose the wallpaper type
 * (e.g., home screen, lock screen, or both) after cropping is complete.
 *
 * The aspect ratio for cropping is automatically determined based on the device's screen size
 * and orientation.
 *
 * @param modifier The [Modifier] to be applied to the [ImageKrop] composable.
 * @param imageBitmap The [ImageBitmap] to be cropped and set as wallpaper.
 * @param config The [KropConfig] to customize the behavior and appearance of the cropping view.
 * Defaults to [KropConfig.surfaceBased].
 * @param dialogContainerColor The background color for the wallpaper type selection dialog.
 * Defaults to [AlertDialogDefaults.containerColor].
 * @param onWallpaperResult A callback function to be invoked when the wallpaper setting is
 * completed. It takes a [Boolean] parameter indicating whether the setting was successful.
 * @param onNavigateBack A callback invoked when the user requests to navigate back from the
 * cropping screen, for example, by pressing the back button.
 */
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ImageWallpaper(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    config: KropConfig = KropConfig.surfaceBased(),
    dialogContainerColor: Color = AlertDialogDefaults.containerColor,
    onWallpaperResult: ((type: WallpaperType, result: Boolean) -> Unit)? = null,
    onNavigateBack: () -> Unit = {}
) {

    val windowInfo = LocalWindowInfo.current
    val configuration = LocalConfiguration.current
    val imageKropState = rememberImageKropState(imageBitmap = imageBitmap, config = config)
    val wallpaperTypeDialogVisibleState = remember { MutableTransitionState(false) }

    val wallpaperAspectRatio by remember(configuration.orientation, windowInfo.containerSize) {
        derivedStateOf {
            findAspectRatio(
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
            imageBitmap = bitmap,
            containerColor = dialogContainerColor,
            onWallpaperResult = onWallpaperResult
        )
    } ?: run {

        wallpaperTypeDialogVisibleState.targetState = false
    }

    LaunchedEffect(imageKropState, wallpaperAspectRatio) {

        imageKropState.apply {

            updateAspectRatio(aspect = KropAspectRatio(ratio = wallpaperAspectRatio))
            imageKropState.updateAspectLocked(locked = true)
        }
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