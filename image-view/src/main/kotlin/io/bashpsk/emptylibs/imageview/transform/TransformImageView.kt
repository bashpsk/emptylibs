package io.bashpsk.emptylibs.imageview.transform

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import coil3.compose.SubcomposeAsyncImage
import io.bashpsk.emptylibs.gestureui.transform.TransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.rememberTransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.transformableGestures
import io.bashpsk.emptylibs.imageview.R
import io.bashpsk.emptylibs.jetpackui.layout.ZoomableLayout
import kotlinx.collections.immutable.ImmutableList

/**
 * A Composable that displays an image with support for transformations like
 * zoom, pan, and rotation. It also handles click and long-click events.
 *
 * This view integrates with Coil for image loading and allows users to
 * interact with the image using gestures:
 * - **Pinch-to-zoom:** Use two fingers to zoom in and out.
 * - **Double-tap to zoom:** Quickly zoom to predefined levels.
 * - **Pan:** Drag with one finger to move around a zoomed-in image.
 * - **Rotate:** Twist with two fingers to rotate the image.
 *
 * This is a convenience overload for displaying a single image. For displaying a
 * swipeable gallery, use the overload that accepts an `imageModelList`.
 *
 * @param modifier The [Modifier] to be applied to the composable.
 * @param state The [TransformableGesturesState] that holds and manages the current transformation
 *   state (zoom, pan, rotation). A default state is remembered if not provided.
 * @param imageModel The image model to be displayed. This can be a URL, a local
 *   file path, or any other type supported by the image loading library (Coil).
 * @param contentScale The scaling to be applied to the image to fit within the composable's
 *   bounds. Defaults to [ContentScale.Fit].
 */
@Composable
fun TransformImageView(
    modifier: Modifier = Modifier,
    state: TransformableGesturesState = rememberTransformableGesturesState(),
    imageModel: Any?,
    contentScale: ContentScale = ContentScale.Fit,
    onClick: (offset: Offset) -> Unit = {},
    onLongClick: (offset: Offset) -> Unit = {},
    loadingIndicator: (@Composable () -> Unit)? = {

        CircularProgressIndicator()
    },
    errorIndicator: (@Composable () -> Unit)? = {

        Image(
            modifier = Modifier.fillMaxSize(fraction = 0.65F),
            painter = painterResource(id = R.drawable.image_broken),
            contentDescription = "Image Load Failed"
        )
    }
) {

    LaunchedEffect(imageModel) {

        state.resetAllValues()
    }

    TransformImageViewLayout(
        modifier = modifier,
        state = state,
        onClick = onClick,
        onLongClick = onLongClick
    ) {

        ImageView(
            modifier = Modifier.fillMaxSize(),
            state = state,
            model = imageModel,
            contentScale = contentScale,
            loadingIndicator = loadingIndicator,
            errorIndicator = errorIndicator
        )
    }
}

/**
 * A Composable that displays a swipeable gallery of images with support for
 * transformations like zoom, pan, and rotation.
 *
 * This view integrates with Coil for image loading and uses a `HorizontalPager`
 * to display a list of images. It allows users to interact with each image
 * using gestures:
 * - **Pinch-to-zoom:** Use two fingers to zoom in and out.
 * - **Double-tap to zoom:** Quickly zoom to predefined levels.
 * - **Pan:** Drag with one finger to move around a zoomed-in image.
 * - **Rotate:** Twist with two fingers to rotate the image.
 * - **Swipe:** Swipe horizontally to navigate between images when not zoomed in.
 *
 * This overload is suitable for displaying a gallery of multiple images.
 *
 * @param modifier The [Modifier] to be applied to the layout.
 * @param state The [TransformableGesturesState] which holds and manages the current state of zoom,
 * pan, and rotation for the active image. A default state is remembered by default.
 * @param imageModelList An immutable list of image models to be displayed in the pager.
 * Models can be URLs, local file paths, or any other type supported by Coil.
 * @param initialImage The specific image model from [imageModelList] that should be displayed
 * initially. If not found or null, the first image is shown.
 * @param contentScale The scaling to be applied to the image to fit within the bounds of the
 * composable. Defaults to [ContentScale.Fit].
 */
@Composable
fun TransformImageView(
    modifier: Modifier = Modifier,
    state: TransformableGesturesState = rememberTransformableGesturesState(),
    imageModelList: ImmutableList<Any?>,
    initialImage: Any? = null,
    contentScale: ContentScale = ContentScale.Fit,
    enableSwipe: Boolean = true,
    onClick: (offset: Offset) -> Unit = {},
    onLongClick: (offset: Offset) -> Unit = {},
    loadingIndicator: (@Composable () -> Unit)? = {

        CircularProgressIndicator()
    },
    errorIndicator: (@Composable () -> Unit)? = {

        Image(
            modifier = Modifier.fillMaxSize(fraction = 0.65F),
            painter = painterResource(id = R.drawable.image_broken),
            contentDescription = "Image Load Failed"
        )
    },
    content: (@Composable (pagerState: PagerState) -> Unit)? = null
) {

    val initialPage by remember(imageModelList, initialImage) {
        derivedStateOf { imageModelList.indexOf(initialImage).takeIf { index -> index >= 0 } ?: 0 }
    }

    val pagerState = rememberPagerState(initialPage = initialPage) { imageModelList.size }

    val isTransforming by remember(state.touchCount, state.zoom, state.rotation) {
        derivedStateOf { state.hasTransform() }
    }

    val isSwipeEnabled by remember(enableSwipe, isTransforming) {
        derivedStateOf { enableSwipe && isTransforming.not() }
    }

    LaunchedEffect(pagerState.currentPage) {

        state.resetAllValues()
    }

    TransformImageViewLayout(
        modifier = modifier,
        state = state,
        onClick = onClick,
        onLongClick = onLongClick
    ) {

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            userScrollEnabled = isSwipeEnabled,
            verticalAlignment = Alignment.CenterVertically
        ) { page ->

            ImageView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                model = imageModelList[page],
                contentScale = contentScale,
                loadingIndicator = loadingIndicator,
                errorIndicator = errorIndicator
            )
        }

        content?.invoke(pagerState)
    }
}

/**
 * A private layout composable that provides the core transformation logic and gesture
 * handling for the `TransformImageView`. It wraps the provided `content` (usually
 * an image or a pager of images) with the necessary modifiers for zooming, panning,
 * rotation, and tap detection.
 *
 * This function is responsible for:
 * - Setting up `pointerInput` to detect tap gestures (double-tap, single-tap, long-press).
 * - Setting up `transformable` to handle multi-touch gestures (pinch-to-zoom, pan, rotate).
 * - Applying the transformations(pan, zoom & etc.) from the [TransformableGesturesState] to the
 * content.
 * - Coercing pan and zoom values to stay within valid bounds.
 * - Clipping the content to the layout's bounds to hide overflow.
 *
 * @param modifier The [Modifier] to be applied to the layout.
 * @param state The [TransformableGesturesState] that holds and manages the current transformation
 * state.
 * @param onClick A lambda to be invoked when a single tap is detected.
 * @param onLongClick A lambda to be invoked when a long press is detected.
 * @param content The composable content to be displayed and transformed, typically the image
 * itself.
 */
@Composable
private fun TransformImageViewLayout(
    modifier: Modifier = Modifier,
    state: TransformableGesturesState = rememberTransformableGesturesState(),
    onClick: (offset: Offset) -> Unit = {},
    onLongClick: (offset: Offset) -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {

    Box(
        modifier = modifier.transformableGestures(
            state = state,
            onClick = onClick,
            onLongClick = onLongClick
        ),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * A private composable that wraps the `SubcomposeAsyncImage` to render the image
 * with the applied transformations.
 *
 * This composable is responsible for displaying the image itself and handling
 * its loading and error states. It uses the provided [TransformableGesturesState] to
 * apply zoom, pan, and rotation transformations.
 *
 * @param modifier The [Modifier] to be applied to this composable.
 * @param state The [TransformableGesturesState] that holds the current transformation values.
 * @param model The image model to be loaded by Coil.
 * @param contentScale The scaling to be applied to the image.
 * @param loadingIndicator A composable to be displayed while the image is loading.
 * @param errorIndicator A composable to be displayed if the image fails to load.
 */
@Composable
private fun ImageView(
    modifier: Modifier = Modifier,
    state: TransformableGesturesState,
    model: Any?,
    contentScale: ContentScale = ContentScale.Fit,
    loadingIndicator: (@Composable () -> Unit)? = {

        CircularProgressIndicator()
    },
    errorIndicator: (@Composable () -> Unit)? = {

        Image(
            modifier = Modifier.fillMaxSize(fraction = 0.65F),
            painter = painterResource(id = R.drawable.image_broken),
            contentDescription = "Image Load Failed"
        )
    }
) {

    val layoutPosition by remember(state.position) {
        derivedStateOf { IntOffset(state.position.x.toInt(), state.position.y.toInt()) }
    }

    ZoomableLayout(
        modifier = modifier
            .fillMaxWidth()
            .offset { layoutPosition }
            .rotate(degrees = state.rotation),
        zoomScale = state.zoom
    ) {

        SubcomposeAsyncImage(
            modifier = Modifier.fillMaxWidth(),
            model = model,
            contentScale = contentScale,
            loading = {

                loadingIndicator?.let { content ->

                    Column(
                        modifier = Modifier.matchParentSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        content()
                    }
                }
            },
            error = {

                errorIndicator?.let { content ->

                    Column(
                        modifier = Modifier.matchParentSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        content()
                    }
                }
            },
            contentDescription = "Image View"
        )
    }
}