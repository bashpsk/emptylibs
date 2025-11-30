package io.bashpsk.emptylibs.imageview.transform

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.toOffset
import coil3.compose.SubcomposeAsyncImage
import io.bashpsk.emptylibs.composeutils.offset.coerceIn
import io.bashpsk.emptylibs.imageview.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.math.abs

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
 * @param state The [ImageTransformState] that holds and manages the current transformation
 *   state (zoom, pan, rotation). A default state is remembered if not provided.
 * @param imageModel The image model to be displayed. This can be a URL, a local
 *   file path, or any other type supported by the image loading library (Coil).
 * @param contentScale The scaling to be applied to the image to fit within the composable's
 *   bounds. Defaults to [ContentScale.Fit].
 * @param zoomRange The allowed range for zooming. Defaults to `0.4F..8.0F`.
 * @param enableZoom Toggles whether pinch-to-zoom is enabled. Defaults to `true`.
 * @param enableDoubleTapZoom Toggles whether double-tap to zoom is enabled. Defaults to `true`.
 * @param enableRotation Toggles whether two-finger rotation is enabled. Defaults to `true`.
 */
@Composable
fun TransformImageView(
    modifier: Modifier = Modifier,
    state: ImageTransformState = rememberImageTransformState(),
    imageModel: Any?,
    contentScale: ContentScale = ContentScale.Fit,
    zoomRange: ClosedFloatingPointRange<Float> = 0.4F..8.0F,
    enableZoom: Boolean = true,
    enableDoubleTapZoom: Boolean = true,
    enableRotation: Boolean = true,
    enablePan: Boolean = true,
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

    TransformImageView(
        modifier = modifier,
        state = state,
        imageModelList = persistentListOf(imageModel),
        initialImage = imageModel,
        contentScale = contentScale,
        zoomRange = zoomRange,
        enableControls = false,
        enableZoom = enableZoom,
        enableDoubleTapZoom = enableDoubleTapZoom,
        enableRotation = enableRotation,
        enablePan = enablePan,
        onClick = onClick,
        onLongClick = onLongClick,
        loadingIndicator = loadingIndicator,
        errorIndicator = errorIndicator
    )
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
 * @param state The [ImageTransformState] which holds and manages the current state of zoom,
 *   pan, and rotation for the active image. A default state is remembered by default.
 * @param imageModelList An immutable list of image models to be displayed in the pager.
 *   Models can be URLs, local file paths, or any other type supported by Coil.
 * @param initialImage The specific image model from [imageModelList] that should be displayed
 *   initially. If not found or null, the first image is shown.
 * @param contentScale The scaling to be applied to the image to fit within the bounds of the
 *   composable. Defaults to [ContentScale.Fit].
 * @param zoomRange The allowed range for zooming. Defaults to `0.4F..8.0F`.
 * @param enableControls Toggles the visibility of built-in controls for navigation and
 *   resetting transformations. Defaults to `false`.
 */
@Composable
fun TransformImageView(
    modifier: Modifier = Modifier,
    state: ImageTransformState = rememberImageTransformState(),
    imageModelList: ImmutableList<Any?>,
    initialImage: Any? = null,
    contentScale: ContentScale = ContentScale.Fit,
    zoomRange: ClosedFloatingPointRange<Float> = 0.4F..8.0F,
    enableControls: Boolean = false,
    enableZoom: Boolean = true,
    enableDoubleTapZoom: Boolean = true,
    enableRotation: Boolean = true,
    enablePan: Boolean = true,
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
    }
) {

    val initialPage by remember(imageModelList, initialImage) {
        derivedStateOf { imageModelList.indexOf(initialImage).coerceIn(imageModelList.indices) }
    }

    val pagerState = rememberPagerState(initialPage = initialPage) { imageModelList.size }

    val isCanSwipe by remember(state) { derivedStateOf { state.zoom <= 1.0F && enableSwipe } }

    val tapPointerInput = Modifier.pointerInput(enableDoubleTapZoom) {

        detectTapGestures(
            onDoubleTap = { tapPosition ->

                if (enableDoubleTapZoom) {

                    state.apply {

                        when (zoom) {

                            in 0.80F..1.40F -> 2.0F
                            in 1.80F..2.40F -> 3.0F
                            in 2.80F..3.40F -> 4.0F
                            else -> 1.0F
                        }.coerceIn(range = zoomRange).takeIf { zoomFactor ->

                            zoomFactor > 1.0F
                        }?.let { zoomFactor ->

                            val boundCenter = boundSize.center.toOffset()
                            val maxPosition = boundCenter * (zoomFactor - 1.0F)

                            val newPosition = (tapPosition - boundCenter) * (1 - zoomFactor / zoom
                                    ) + position * (zoomFactor / zoom)

                            position = newPosition.coerceIn(
                                minimum = Offset(x = -abs(maxPosition.x), y = -abs(maxPosition.x)),
                                maximum = Offset(x = maxPosition.x, y = maxPosition.y)
                            )

                            zoom = zoomFactor
                        } ?: run {

                            resetAllValues()
                        }
                    }
                }
            },
            onTap = onClick,
            onLongPress = onLongClick
        )
    }

    val transformableState = rememberTransformableState { zoomChange, panChange, rotationChange ->

        state.apply {

            if (enableZoom) zoom = (zoom * zoomChange).coerceIn(zoomRange)
            if (enableRotation) rotation += rotationChange

            if (enablePan) if (zoom > 1.0F) {

                val newPosition = position + panChange
                val maxPosition = boundSize.center.toOffset() * (zoom - 1.0F)

                position = newPosition.coerceIn(
                    minimum = Offset(x = -abs(maxPosition.x), y = -abs(maxPosition.x)),
                    maximum = Offset(x = maxPosition.x, y = maxPosition.y)
                )
            } else resetPosition()
        }
    }

    LaunchedEffect(pagerState.currentPage) {

        state.resetAllValues()
    }

    DisposableEffect(Unit) {

        onDispose { state.resetState() }
    }

    Box(
        modifier = modifier
            .onSizeChanged { size -> state.boundSize = size }
            .transformable(state = transformableState)
            .then(tapPointerInput),
        contentAlignment = Alignment.Center
    ) {

        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            state = pagerState,
            userScrollEnabled = isCanSwipe,
            verticalAlignment = Alignment.CenterVertically
        ) { page ->

            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = state.zoom.coerceIn(range = zoomRange),
                        scaleY = state.zoom.coerceIn(range = zoomRange),
                        translationX = state.position.x,
                        translationY = state.position.y,
                        rotationZ = state.rotation
                    ),
                model = imageModelList.getOrNull(index = page),
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

        if (enableControls) DefaultImageControls(state = state, pagerState = pagerState)
    }
}