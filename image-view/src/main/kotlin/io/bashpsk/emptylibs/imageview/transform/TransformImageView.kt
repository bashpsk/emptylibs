package io.bashpsk.emptylibs.imageview.transform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.round
import coil3.compose.SubcomposeAsyncImage
import io.bashpsk.emptylibs.gestureui.transform.TransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.rememberTransformableGesturesState
import io.bashpsk.emptylibs.gestureui.transform.transformableGestures
import io.bashpsk.emptylibs.imageview.tile.TileImageView
import io.bashpsk.emptylibs.layouts.zoomable.ZoomableLayout
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
 * state (zoom, pan, rotation). A default state is remembered if not provided.
 * @param imageModel The image model to be displayed. This can be a URL, a local
 * file path, or any other type supported by the image loading library (Coil).
 * @param contentScale The scaling to be applied to the image to fit within the composable's
 * bounds. Defaults to [ContentScale.Fit].
 */
@Composable
fun <T> TransformImageView(
    modifier: Modifier = Modifier,
    state: TransformableGesturesState = rememberTransformableGesturesState(),
    imageModel: T?,
    contentScale: ContentScale = ContentScale.Fit,
    onClick: (offset: Offset) -> Unit = {},
    onLongClick: (offset: Offset) -> Unit = {},
    loadingIndicator: (@Composable () -> Unit)? = {

        TransformImageViewDefault.LoadingIndicator()
    },
    errorIndicator: (@Composable () -> Unit)? = {

        TransformImageViewDefault.ErrorIndicator()
    }
) {

    RetainedEffect(imageModel) {

        onRetire { state.resetAllValues() }
    }

    TransformImageViewLayout(
        modifier = modifier,
        state = state,
        onClick = onClick,
        onLongClick = onLongClick
    ) {

        ImageView(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            model = imageModel,
            contentScale = contentScale,
            loadingIndicator = loadingIndicator,
            errorIndicator = errorIndicator
        )
    }
}

/**
 * A Composable that displays an [ImageBitmap] with support for transformations like
 * zoom, pan, and rotation. This version uses a tiled rendering approach via [TileImageView]
 * for optimized performance when handling large high-resolution bitmaps.
 *
 * This view allows users to interact with the bitmap using gestures:
 * - **Pinch-to-zoom:** Use two fingers to zoom in and out.
 * - **Double-tap to zoom:** Quickly zoom to predefined levels.
 * - **Pan:** Drag with one finger to move around a zoomed-in image.
 * - **Rotate:** Twist with two fingers to rotate the image.
 *
 * This is a specialized overload for local [ImageBitmap] objects. For loading remote
 * URLs or other media types, use the overloads that accept `imageModel` or `imageModelList`.
 *
 * @param modifier The [Modifier] to be applied to the composable.
 * @param state The [TransformableGesturesState] that holds and manages the current transformation
 * state (zoom, pan, rotation). A default state is remembered if not provided.
 * @param imageModel The [ImageBitmap] to be displayed.
 * @param contentScale The scaling to be applied to the image to fit within the composable's
 * bounds. Defaults to [ContentScale.Fit].
 * @param tileSize The size (in pixels) of the individual tiles used to render the image.
 * Larger tiles use more memory but may result in fewer draw calls. Defaults to 512.
 * @param onClick A lambda to be invoked when a single tap is detected on the image.
 * @param onLongClick A lambda to be invoked when a long press is detected on the image.
 */
@Composable
fun TransformImageView(
    modifier: Modifier = Modifier,
    state: TransformableGesturesState = rememberTransformableGesturesState(),
    imageModel: ImageBitmap,
    contentScale: ContentScale = ContentScale.Fit,
    tileSize: Int = 512,
    onClick: (offset: Offset) -> Unit = {},
    onLongClick: (offset: Offset) -> Unit = {}
) {

    RetainedEffect(imageModel) {

        onRetire { state.resetAllValues() }
    }

    TransformImageViewLayout(
        modifier = modifier,
        state = state,
        onClick = onClick,
        onLongClick = onLongClick
    ) {

        ImageView(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            model = imageModel,
            contentScale = contentScale,
            tileSize = tileSize
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
 * @param onImageChanges A lambda to be invoked when the currently active image changes.
 * @param contentScale The scaling to be applied to the image to fit within the bounds of the
 * composable. Defaults to [ContentScale.Fit].
 * @param enableSwipe A flag indicating whether swiping between images is enabled.
 * @param onClick A lambda to be invoked when a single tap is detected on the image.
 * @param onLongClick A lambda to be invoked when a long press is detected on the image.
 * @param loadingIndicator A composable to be displayed while the image is loading.
 * @param errorIndicator A composable to be displayed if the image fails to load.
 * @param content A composable to be displayed on top of the image view.
 */
@Composable
fun <T> TransformImageView(
    modifier: Modifier = Modifier,
    state: TransformableGesturesState = rememberTransformableGesturesState(),
    imageModelList: ImmutableList<T>,
    initialImage: T? = null,
    onImageChanges: (image: T?) -> Unit = {},
    contentScale: ContentScale = ContentScale.Fit,
    enableSwipe: Boolean = true,
    onClick: (offset: Offset) -> Unit = {},
    onLongClick: (offset: Offset) -> Unit = {},
    loadingIndicator: (@Composable () -> Unit)? = {

        TransformImageViewDefault.LoadingIndicator()
    },
    errorIndicator: (@Composable () -> Unit)? = {

        TransformImageViewDefault.ErrorIndicator()
    },
    content: @Composable BoxScope.(pagerState: PagerState) -> Unit = {}
) {

    val pagerState = rememberPagerState { imageModelList.size }

    var isInitialImageLoaded by rememberSaveable { mutableStateOf(false) }

    val isTransforming by remember(state.touchCount, state.zoom, state.rotation) {
        derivedStateOf { state.hasTransform() }
    }

    val isSwipeEnabled by remember(enableSwipe, isTransforming) {
        derivedStateOf { enableSwipe && isTransforming.not() }
    }

    RetainedEffect(initialImage, imageModelList) {

        if (!isInitialImageLoaded) imageModelList.indexOf(initialImage).takeIf { index ->

            index in imageModelList.indices
        }?.let { index ->

            pagerState.requestScrollToPage(index)
            isInitialImageLoaded = true
        }

        onRetire { }
    }

    RetainedEffect(pagerState.settledPage) {

        onImageChanges(imageModelList.getOrNull(pagerState.settledPage))

        onRetire { state.resetAllValues() }
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
                modifier = Modifier.fillMaxWidth(),
                state = state,
                model = imageModelList.getOrNull(page),
                contentScale = contentScale,
                loadingIndicator = loadingIndicator,
                errorIndicator = errorIndicator
            )
        }

        content(pagerState)
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
private fun <T> ImageView(
    modifier: Modifier = Modifier,
    state: TransformableGesturesState,
    model: T?,
    contentScale: ContentScale = ContentScale.Fit,
    loadingIndicator: (@Composable () -> Unit)? = {

        TransformImageViewDefault.LoadingIndicator()
    },
    errorIndicator: (@Composable () -> Unit)? = {

        TransformImageViewDefault.ErrorIndicator()
    }
) {

    val layoutPosition by remember(state.position) { derivedStateOf { state.position.round() } }

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

                loadingIndicator?.invoke()
            },
            error = {

                errorIndicator?.invoke()
            },
            contentDescription = "Image View"
        )
    }
}

/**
 * A private composable that renders a [ImageBitmap] using a tiled approach for optimized
 * performance at high zoom levels.
 *
 * This version of `ImageView` is specifically designed for local [ImageBitmap] resources
 * and utilizes [TileImageView] to handle the rendering of image segments. It applies
 * transformations such as zoom, pan, and rotation managed by the provided
 * [TransformableGesturesState].
 *
 * @param modifier The [Modifier] to be applied to this composable.
 * @param state The [TransformableGesturesState] that holds the current transformation values
 * (zoom, position, rotation).
 * @param model The [ImageBitmap] to be displayed.
 * @param contentScale The scaling to be applied to the image within its bounds.
 * Defaults to [ContentScale.Fit].
 * @param tileSize The size (in pixels) of the individual tiles used to render the image.
 * Defaults to 512.
 */
@Composable
private fun ImageView(
    modifier: Modifier = Modifier,
    state: TransformableGesturesState,
    model: ImageBitmap,
    contentScale: ContentScale = ContentScale.Fit,
    tileSize: Int = 512
) {

    val layoutPosition by remember(state.position) { derivedStateOf { state.position.round() } }

    ZoomableLayout(
        modifier = modifier
            .fillMaxWidth()
            .offset { layoutPosition }
            .rotate(degrees = state.rotation),
        zoomScale = state.zoom
    ) {

        TileImageView(
            modifier = Modifier.fillMaxWidth(),
            imageBitmap = model,
            contentScale = contentScale,
            tileSize = tileSize
        )
    }
}