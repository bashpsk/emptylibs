package io.bashpsk.emptylibs.imagekolor.filter

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import io.bashpsk.emptylibs.formatter.format.EmptyFormat

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ImageFilterLayout(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    state: ImageFilterState,
    imageContent: @Composable () -> Unit = {

        Image(
            modifier = Modifier.fillMaxSize(),
            bitmap = imageBitmap,
            contentScale = ContentScale.Fit,
            colorFilter = state.selectedFilter.colorFilter,
            contentDescription = "Image Color Filter"
        )
    },
    filterContent: @Composable () -> Unit = {

        ImageFilter(modifier = Modifier.fillMaxSize(), state = state)
    }
) {

    val activity = LocalActivity.current
    val windowSizeClass = activity?.let { calculateWindowSizeClass(activity = it) }

    val imageAspectRatio by remember(imageBitmap) {
        derivedStateOf {
            EmptyFormat.findAspectRatio(width = imageBitmap.width, height = imageBitmap.height)
        }
    }

    Layout(
        modifier = modifier,
        content = {

            imageContent()
            filterContent()
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