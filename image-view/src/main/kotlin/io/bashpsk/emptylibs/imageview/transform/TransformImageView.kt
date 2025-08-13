package io.bashpsk.emptylibs.imageview.transform

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil3.compose.SubcomposeAsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

/**
 * A Composable that displays an image with support for various transformations like
 * zoom, pan, rotation, and swipe gestures.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param imageModel A lambda that returns the image model to be displayed. This can be a URL,
 * a local file path, or any other model supported by Coil.
 * @param state The [ImageTransformState] which holds the current state of zoom, pan, and rotation.
 * Defaults to a remembered [ImageTransformState] instance.
 * @param onLeftSwipe A lambda that is invoked when a left swipe gesture is detected.
 * @param onRightSwipe A lambda that is invoked when a right swipe gesture is detected.
 * @param onClick A lambda that is invoked when the image is clicked. It receives the [Offset]
 * of the click.
 * @param onLongClick A lambda that is invoked when the image is long-clicked. It receives the
 * [Offset] of the long click.
 * @param contentScale The scaling to be applied to the image when displayed.
 * Defaults to [ContentScale.Fit].
 */
@Composable
fun TransformImageView(
    modifier: Modifier = Modifier,
    imageModel: () -> Any?,
    state: ImageTransformState = rememberImageTransformState(),
    onLeftSwipe: () -> Unit = {},
    onRightSwipe: () -> Unit = {},
    onClick: (offset: Offset) -> Unit = {},
    onLongClick: (offset: Offset) -> Unit = {},
    contentScale: ContentScale = ContentScale.Fit,
) {

    val gestureCoroutineScope = rememberCoroutineScope()

    var resetImageGestureJob by remember { mutableStateOf<Job?>(null) }
    var imageGesture by remember { mutableStateOf<TransformImageGesture?>(null) }
    var dragSwipeMinimum by remember { mutableFloatStateOf(0.0F) }
    var touchCount by remember { mutableIntStateOf(0) }
    val isOneTouch by remember(touchCount) { derivedStateOf { touchCount == 1 } }
    val isTwoTouch by remember(touchCount) { derivedStateOf { touchCount == 2 } }
    val isZoomed by remember(state) { derivedStateOf { state.zoom != 1.0F } }

    val isCanSwipe by remember(state) {
        derivedStateOf { state.zoom == 1.0F && state.config.enableSwipe }
    }

    fun resetDragGestureAction() {

        resetImageGestureJob?.cancel()
        resetImageGestureJob = gestureCoroutineScope.launch(context = Dispatchers.Default) {

            delay(duration = 1000.milliseconds)
            imageGesture = null
        }
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

                val zoomFactor = when (state.zoom) {

                    in 0.80F..1.40F -> 2.0F
                    in 1.80F..2.40F -> 3.0F
                    in 2.80F..3.40F -> 4.0F
                    else -> 1.0F
                }.coerceIn(range = state.zoomMin..state.zoomMax)

                state.zoom = zoomFactor
            },
            onTap = onClick,
            onLongPress = onLongClick
        )
    }

    val dragPointerInput = Modifier.pointerInput(Unit) {

        detectHorizontalDragGestures(
            onDragCancel = {

                resetDragGestureAction()
            },
            onDragEnd = {

                resetDragGestureAction()
            }
        ) { change, dragAmount ->

            change.consume()
            dragSwipeMinimum += dragAmount

            when {

                isTwoTouch -> {

                    dragSwipeMinimum = 0.0F
                    imageGesture = TransformImageGesture.INIT
                    change.changedToUp()
                    return@detectHorizontalDragGestures
                }

                isOneTouch && abs(x = dragSwipeMinimum) > 75.0F && isCanSwipe -> {

                    imageGesture = when (imageGesture) {

                        TransformImageGesture.INIT -> when {

                            dragSwipeMinimum > 0 -> TransformImageGesture.RIGHT_SWIPE
                            dragSwipeMinimum < 0 -> TransformImageGesture.LEFT_SWIPE
                            else -> TransformImageGesture.INIT
                        }

                        else -> imageGesture
                    }

                    when (imageGesture) {

                        TransformImageGesture.LEFT_SWIPE -> {

                            onLeftSwipe()
                            change.changedToUp()
                        }

                        TransformImageGesture.RIGHT_SWIPE -> {

                            onRightSwipe()
                            change.changedToUp()
                        }

                        else -> {}
                    }

                    dragSwipeMinimum = 0.0F
                }

                else -> {

                    dragSwipeMinimum = 0.0F
                    imageGesture = TransformImageGesture.INIT
                    change.changedToUp()
                    return@detectHorizontalDragGestures
                }
            }
        }
    }

    val transformableState = rememberTransformableState { zoomChange, panChange, rotationChange ->

        when {

            isTwoTouch -> when (imageGesture) {

                TransformImageGesture.INIT -> when {

                    abs(rotationChange) >= 1.0F -> if (state.config.enableRotation) {

                        imageGesture = TransformImageGesture.ROTATION
                    }

                    zoomChange >= 0.10F -> if (state.config.enableZoom) {

                        imageGesture = TransformImageGesture.ZOOM
                    }
                }

                TransformImageGesture.ZOOM -> {

                    val newZoom = (state.zoom * zoomChange).coerceIn(
                        range = state.zoomMin..state.zoomMax
                    )

                    val newPan = Offset(
                        x = state.position.x,
                        y = state.position.y
                    ) + panChange

                    state.zoom = newZoom
                    state.position = newPan
                    resetDragGestureAction()
                }

                TransformImageGesture.ROTATION -> {

                    val newPan = Offset(
                        x = state.position.x,
                        y = state.position.y
                    ) + panChange

                    val newRotation = (state.rotation + rotationChange).toInt().coerceIn(
                        range = 0..360
                    )

                    state.position = newPan
                    state.rotation = newRotation
                    resetDragGestureAction()
                }

                TransformImageGesture.RIGHT_SWIPE, TransformImageGesture.LEFT_SWIPE -> {

                    return@rememberTransformableState
                }

                else -> {

                    imageGesture = TransformImageGesture.INIT
                    return@rememberTransformableState
                }
            }

            isOneTouch && isZoomed -> when (imageGesture) {

                TransformImageGesture.INIT -> if (state.config.enablePan) {

                    imageGesture = TransformImageGesture.PAN
                }

                TransformImageGesture.PAN -> {

                    val newPan = Offset(
                        x = state.position.x,
                        y = state.position.y
                    ) + panChange

                    state.position = newPan
                    resetDragGestureAction()
                }

                TransformImageGesture.RIGHT_SWIPE, TransformImageGesture.LEFT_SWIPE -> {

                    return@rememberTransformableState
                }

                else -> {

                    imageGesture = TransformImageGesture.INIT
                    return@rememberTransformableState
                }
            }

            isCanSwipe -> return@rememberTransformableState

            else -> {

                imageGesture = TransformImageGesture.INIT
                return@rememberTransformableState
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .then(touchModifier)
            .transformable(state = transformableState)
            .then(tapPointerInput)
            .then(dragPointerInput),
        contentAlignment = Alignment.Center
    ) {

        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = state.zoom.coerceIn(range = state.zoomMin..state.zoomMax),
                    scaleY = state.zoom.coerceIn(range = state.zoomMin..state.zoomMax),
                    translationX = state.position.x,
                    translationY = state.position.y,
                    rotationZ = state.rotation.toFloat()
                ),
            model = imageModel(),
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
                        modifier = Modifier.size(size = this@BoxWithConstraints.maxWidth / 4),
                        imageVector = Icons.Filled.BrokenImage,
                        contentDescription = "Image View"
                    )
                }
            },
            contentDescription = "Image View"
        )
    }
}

/**
 * Enum class representing the possible gestures for transforming an image.
 */
private enum class TransformImageGesture {

    INIT,
    PAN,
    ZOOM,
    ROTATION,
    LEFT_SWIPE,
    RIGHT_SWIPE;
}