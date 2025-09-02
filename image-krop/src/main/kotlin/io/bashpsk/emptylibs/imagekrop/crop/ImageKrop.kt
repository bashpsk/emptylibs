package io.bashpsk.emptylibs.imagekrop.crop

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.toSize
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
    onKropFinished: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {

    val imagePreviewSheetState = rememberModalBottomSheetState()

    val imagePlacedModifier = Modifier.onPlaced { layoutCoordinates ->

        val imageWidth = layoutCoordinates.size.width.toFloat()
        val imageHeight = layoutCoordinates.size.height.toFloat()

        state.apply {

            kropRectPosition = Offset(imageWidth * 0.05F, imageHeight * 0.05F)
            kropRectSize = Size(width = imageWidth * 0.90F, height = imageHeight * 0.90F)
            canvasSize = layoutCoordinates.size.toSize()
        }
    }

    val pointerInputModifier = Modifier.pointerInput(Unit) {

        detectDragGestures(
            onDragStart = state::onKropStart,
            onDragEnd = state::onKropEnd,
            onDragCancel = state::onKropEnd,
            onDrag = { change, dragAmount ->

                change.consume()
                state.onKropChanges(position = change.position, amount = dragAmount)
            }
        )
    }

    val cropCanvasModifier = Modifier.drawWithContent {

        drawContent()

        drawKropHandle(
            kropShape = state.kropShape,
            topLeft = state.kropRectPosition,
            rectSize = state.kropRectSize,
            config = state.config
        )
    }

    LaunchedEffect(
        state.canvasSize,
        state.originalImage,
        state.kropAspectRatio,
        state.isAspectLocked
    ) {

        state.onKropRectInitialized()
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

        ImageKropTopBar(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            imagePreviewSheetState = imagePreviewSheetState,
            onKropFinished = onKropFinished,
            onNavigateBack = onNavigateBack
        )

        BoxWithConstraints(
            modifier = Modifier.weight(weight = 1.0F),
            contentAlignment = Alignment.Center
        ) {

            Image(
                modifier = Modifier
                    .then(imagePlacedModifier)
                    .then(pointerInputModifier)
                    .then(cropCanvasModifier),
                bitmap = state.originalImage,
                contentScale = ContentScale.Fit,
                contentDescription = "Image View"
            )
        }

        ImageKropBottomBar(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            aspectList = aspectList
        )
    }
}