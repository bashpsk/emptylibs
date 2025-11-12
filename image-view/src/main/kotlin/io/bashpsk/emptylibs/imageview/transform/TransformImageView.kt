package io.bashpsk.emptylibs.imageview.transform

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

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
        onLongClick = onLongClick
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
    onLongClick: (offset: Offset) -> Unit = {}
) {

    val pagerCoroutineScope = rememberCoroutineScope()

    val initialPage by remember(imageModelList, initialImage) {
        derivedStateOf { imageModelList.indexOf(initialImage).coerceIn(imageModelList.indices) }
    }

    val pagerState = rememberPagerState(initialPage = initialPage) { imageModelList.size }

    var touchCount by rememberSaveable { mutableIntStateOf(0) }
    val isOneTouch by remember(touchCount) { derivedStateOf { touchCount == 1 } }
    val isTwoTouch by remember(touchCount) { derivedStateOf { touchCount == 2 } }

    val isCanSwipe by remember(state) {
        derivedStateOf { state.zoom == 1.0F && enableSwipe }
    }

    val touchModifier = Modifier.pointerInput(Unit) {

        awaitEachGesture {

            do {

                val event = awaitPointerEvent()

                touchCount = event.changes.size
            } while (event.changes.any { change -> change.pressed })
        }
    }

    val tapPointerInput = Modifier.pointerInput(Unit) {

        detectTapGestures(
            onDoubleTap = { position ->

                if (enableDoubleTapZoom) {

                    val zoomFactor = when (state.zoom) {

                        in 0.80F..1.40F -> 2.0F
                        in 1.80F..2.40F -> 3.0F
                        in 2.80F..3.40F -> 4.0F
                        else -> 1.0F
                    }.coerceIn(range = zoomRange)

                    state.apply {

                        resetAllValues()
                        zoom = zoomFactor
                    }
                }
            },
            onTap = onClick,
            onLongPress = onLongClick
        )
    }

    val transformableState = rememberTransformableState { zoomChange, panChange, rotationChange ->

        when {

            isTwoTouch -> state.apply {

                if (enableZoom) zoom = (state.zoom * zoomChange).coerceIn(zoomRange)
                if (enablePan) position = state.position + panChange
                if (enableRotation) rotation = state.rotation + rotationChange
            }

            isOneTouch && state.zoom != 1.0F && enablePan -> state.position += panChange
            else -> return@rememberTransformableState
        }
    }

    Box(
        modifier = modifier
            .then(touchModifier)
            .transformable(state = transformableState)
            .then(if (enableZoom) tapPointerInput else Modifier),
        contentAlignment = Alignment.Center
    ) {

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
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

                    Column(
                        modifier = Modifier.matchParentSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator()
                    }
                },
                error = {

                    Column(
                        modifier = Modifier.matchParentSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            modifier = Modifier.fillMaxSize(fraction = 0.4F),
                            imageVector = Icons.Filled.BrokenImage,
                            contentDescription = "Image View"
                        )
                    }
                },
                contentDescription = "Image View"
            )
        }

        if (enableControls) TransformImageControls(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .offset(y = (-32).dp),
            onPreviousImage = {

                pagerCoroutineScope.launch {

                    pagerState.animateScrollToPage(
                        page = (pagerState.currentPage - 1).coerceAtLeast(0),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
            },
            onNextImage = {

                pagerCoroutineScope.launch {

                    pagerState.animateScrollToPage(
                        page = (pagerState.currentPage + 1).coerceAtMost(pagerState.pageCount - 1),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
            },
            onResetTransform = {

                state.resetAllValues()
            }
        )
    }
}