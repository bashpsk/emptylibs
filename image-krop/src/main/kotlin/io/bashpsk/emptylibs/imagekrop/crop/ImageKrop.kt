package io.bashpsk.emptylibs.imagekrop.crop

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.layout.ContentScale
import io.bashpsk.emptylibs.imagekrop.modifier.imageKropModifier
import kotlinx.collections.immutable.ImmutableList

/**
 * A Composable function that provides an image cropping interface.
 *
 * This function allows users to crop an image with various configurations,
 * including aspect ratios and crop shapes. It provides a visual interface
 * for selecting the crop area and applying the crop.
 *
 * @param modifier Optional [Modifier] for the root Composable.
 * @param state The [ImageKropState] that holds the current state of the cropping UI,
 * including the image bitmap, configuration, and crop selection.
 * @param aspectList A list of [KropAspectRatio] options to be displayed to the user.
 * Defaults to [KropAspectRatio.Basic].
 * @param topBar An optional Composable lambda to replace the default top bar.
 * If null, [ImageKropTopBar] will be used.
 * @param bottomBar An optional Composable lambda to replace the default bottom bar.
 * If null, [ImageKropBottomBar] will be used.
 * @param onKropFinished A callback function that is invoked when the user finishes the
 * cropping process, typically by pressing a confirmation button.
 * @param onNavigateBack A callback function that is invoked when the user initiates a back
 * navigation action, typically by pressing a back button in the UI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ImageKrop(
    modifier: Modifier = Modifier,
    state: ImageKropState,
    aspectList: ImmutableList<KropAspectRatio> = KropAspectRatio.Basic,
    topBar: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    onKropFinished: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {

    val imagePreviewSheetState = rememberModalBottomSheetState()

    RetainedEffect(
        state.canvasSize,
        state.originalImage,
        state.kropAspectRatio,
        state.isAspectLocked
    ) {

        if (state.canvasSize.isSpecified) state.onKropRectInitialized()

        onRetire { }
    }

    KropImagePreview(
        sheetState = imagePreviewSheetState,
        state = state
    )

    KropShapeCustomizationDialog(state = state)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        topBar?.invoke() ?: run {

            ImageKropTopBar(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                imagePreviewSheetState = imagePreviewSheetState,
                onKropFinished = onKropFinished,
                onNavigateBack = onNavigateBack
            )
        }

        BoxWithConstraints(
            modifier = Modifier.weight(weight = 1.0F),
            contentAlignment = Alignment.Center
        ) {

            Image(
                modifier = Modifier.imageKropModifier(state = state),
                bitmap = state.originalImage,
                contentScale = ContentScale.Fit,
                contentDescription = "Image View"
            )
        }

        bottomBar?.invoke() ?: run {

            ImageKropBottomBar(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                aspectList = aspectList
            )
        }
    }
}