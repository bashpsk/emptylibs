package io.bashpsk.emptylibs.imagekolor.filter

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import io.bashpsk.emptylibs.formatter.format.findAspectRatio
import io.bashpsk.emptylibs.layouts.twopane.TwoPaneAdaptiveLayout

/**
 * A composable function that lays out an image and a filter selection UI.
 *
 * This layout adapts to different screen sizes. On compact screens, the filter UI is placed below
 * the image.
 * On larger screens, the filter UI is placed to the side of the image.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param imageBitmap The [ImageBitmap] to be displayed.
 * @param state The [ImageFilterState] that holds the current filter selection.
 * @param imageContent A composable lambda that defines the content for displaying the image.
 * By default, it uses an [Image] composable with [ContentScale.Fit] and
 * applies the selected color filter from the [state].
 * @param filterContent A composable lambda that defines the content for the filter selection UI.
 * By default, it uses the [ImageFilter] composable.
 *
 * @throws IllegalArgumentException if `imageContent` and `filterContent` do not result in exactly
 * two measurable children.
 */
@Composable
inline fun ImageFilterLayout(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    state: ImageFilterState,
    crossinline imageContent: @Composable () -> Unit = {

        Image(
            modifier = Modifier.fillMaxSize(),
            bitmap = imageBitmap,
            contentScale = ContentScale.Fit,
            colorFilter = state.selectedFilter.colorFilter,
            contentDescription = "Image Color Filter"
        )
    },
    crossinline filterContent: @Composable () -> Unit = {

        ImageFilter(modifier = Modifier.fillMaxSize(), state = state)
    }
) {

    val imageAspectRatio by remember(imageBitmap) {
        derivedStateOf { findAspectRatio(width = imageBitmap.width, height = imageBitmap.height) }
    }

    TwoPaneAdaptiveLayout(
        modifier = modifier,
        aspectRatio = imageAspectRatio,
        firstPane = imageContent,
        secondPane = filterContent
    )
}