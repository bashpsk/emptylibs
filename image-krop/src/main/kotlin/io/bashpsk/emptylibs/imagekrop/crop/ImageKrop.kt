package io.bashpsk.emptylibs.imagekrop.crop

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.layout.ContentScale
import io.bashpsk.emptylibs.imagekrop.modifier.imageKropModifier

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
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ImageKrop(
    modifier: Modifier = Modifier,
    state: ImageKropState
) {

    RetainedEffect(
        state.canvasSize,
        state.originalImage,
        state.kropAspectRatio,
        state.isAspectLocked
    ) {

        if (state.canvasSize.isSpecified) state.onKropRectInitialized()

        onRetire { }
    }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Image(
            modifier = Modifier.imageKropModifier(state = state),
            bitmap = state.originalImage,
            contentScale = ContentScale.Fit,
            contentDescription = "Image View"
        )
    }
}