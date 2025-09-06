package io.bashpsk.emptylibs.imagekolor.color

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import io.bashpsk.emptylibs.formatter.format.EmptyFormat

/**
 * A layout composable that arranges an image and color adjustment controls.
 *
 * This layout adapts its arrangement based on the window size:
 * - **Compact width:** The image is displayed at the top, and the color adjustment controls
 *   are displayed below it. The image maintains its aspect ratio.
 * - **Medium or Expanded width:** The image is displayed on the left, and the color adjustment
 *   controls are displayed on the right. The image maintains its aspect ratio.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state The [ImageKolorState] that holds the current image and color adjustment values.
 * @param imageContent A composable lambda that defines the content for displaying the image.
 * By default, it uses [KolorImageView].
 * @param kolorContent A composable lambda that defines the content for the color adjustment
 * controls. By default, it uses [KolorAdjustmentSliders].
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ImageKolorLayout(
    modifier: Modifier = Modifier,
    state: ImageKolorState,
    imageContent: @Composable () -> Unit = {

        KolorImageView(modifier = Modifier.fillMaxSize(), state = state)
    },
    kolorContent: @Composable () -> Unit = {

        KolorAdjustmentSliders(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            state = state
        )
    }
) {

    val activity = LocalActivity.current
    val windowSizeClass = activity?.let { calculateWindowSizeClass(activity = it) }

    val imageAspectRatio by remember(state.imageBitmap) {
        derivedStateOf {
            EmptyFormat.findAspectRatio(
                width = state.imageBitmap?.width ?: 16,
                height = state.imageBitmap?.height ?: 9
            )
        }
    }

    Layout(
        modifier = modifier,
        content = {

            imageContent()
            kolorContent()
        }
    ) { measurables, constraints ->

        require(measurables.size == 2) { "ImageFilterLayout requires exactly two children." }

        val (imageMeasurable, filterMeasurable) = measurables

        val imagePlaceable: Placeable
        val filterPlaceable: Placeable

        val (imagePlacementOffset, filterPlacementOffset) = when (windowSizeClass?.widthSizeClass) {

            WindowWidthSizeClass.Compact -> {

                val imageAreaMaxHeight = (constraints.maxHeight * imageAspectRatio).toInt()
                val initialImageHeight = (constraints.maxWidth / imageAspectRatio).toInt()

                val imageRenderHeight = minOf(initialImageHeight, imageAreaMaxHeight)
                val imageRenderWidth = (imageRenderHeight * imageAspectRatio).toInt()
                    .coerceIn(0..constraints.maxWidth)

                val finalImageRenderHeight = (imageRenderWidth / imageAspectRatio).toInt()
                    .coerceIn(0..imageAreaMaxHeight)

                imagePlaceable = imageMeasurable.measure(
                    Constraints.fixed(width = imageRenderWidth, height = finalImageRenderHeight)
                )

                filterPlaceable = filterMeasurable.measure(
                    Constraints.fixed(
                        width = constraints.maxWidth,
                        height = (constraints.maxHeight - finalImageRenderHeight).coerceAtLeast(0)
                    )
                )

                IntOffset(
                    x = ((constraints.maxWidth - imageRenderWidth) / 2).coerceAtLeast(0),
                    y = 0
                ) to IntOffset(x = 0, y = finalImageRenderHeight)
            }

            else -> {

                val imageAreaMaxWidth = (constraints.maxWidth * imageAspectRatio).toInt()
                val initialImageWidth = (constraints.maxHeight * imageAspectRatio).toInt()

                val imageRenderWidth = minOf(initialImageWidth, imageAreaMaxWidth)
                val imageRenderHeight = (imageRenderWidth / imageAspectRatio).toInt()
                    .coerceIn(0..constraints.maxHeight)

                val finalImageRenderWidth = (imageRenderHeight * imageAspectRatio).toInt()
                    .coerceIn(0, imageAreaMaxWidth)

                imagePlaceable = imageMeasurable.measure(
                    Constraints.fixed(width = finalImageRenderWidth, height = imageRenderHeight)
                )

                filterPlaceable = filterMeasurable.measure(
                    Constraints.fixed(
                        width = (constraints.maxWidth - finalImageRenderWidth).coerceAtLeast(0),
                        height = constraints.maxHeight
                    )
                )

                IntOffset(
                    x = 0,
                    y = ((constraints.maxHeight - imageRenderHeight) / 2).coerceAtLeast(0)
                ) to IntOffset(x = finalImageRenderWidth, y = 0)
            }
        }

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {

            imagePlaceable.placeRelative(imagePlacementOffset)
            filterPlaceable.placeRelative(filterPlacementOffset)
        }
    }
}