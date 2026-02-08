package io.bashpsk.emptylibs.imagekolor.color

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.bashpsk.emptylibs.formatter.format.findAspectRatio
import io.bashpsk.emptylibs.jetpackui.layout.TwoPaneAdaptiveLayout

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

    val imageAspectRatio by remember(state.imageBitmap) {
        derivedStateOf {
            findAspectRatio(
                width = state.imageBitmap?.width ?: 16,
                height = state.imageBitmap?.height ?: 9
            )
        }
    }

    TwoPaneAdaptiveLayout(
        modifier = modifier,
        aspectRatio = imageAspectRatio,
        firstPane = imageContent,
        secondPane = kolorContent
    )
}