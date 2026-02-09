package io.bashpsk.emptylibs.gestureui.transform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center

/**
 * Creates and remembers a [TransformableGesturesState] instance.
 *
 * @param initialZoom The initial zoom level. Defaults to 1.0F.
 * @param enableZoom Whether to enable zoom gestures. Defaults to true.
 * @param enableDoubleTapZoom Whether to enable double-tap to zoom. Defaults to true.
 * @param enablePan Whether to enable pan gestures. Defaults to true.
 * @param enableRotation Whether to enable rotation gestures. Defaults to false.
 * @param zoomRange The allowed range for zoom levels. Defaults to 0.5F..4.0F.
 * @return A new [TransformableGesturesState] instance.
 */
@Composable
fun rememberTransformableGesturesState(
    initialZoom: Float = 1.0F,
    enableZoom: Boolean = true,
    enableDoubleTapZoom: Boolean = true,
    enablePan: Boolean = true,
    enableRotation: Boolean = false,
    zoomRange: ClosedFloatingPointRange<Float> = 0.5F..4.0F
): TransformableGesturesState {

    val state = retain(initialZoom, zoomRange) {
        TransformableGesturesState(initialZoom = initialZoom, zoomRange = zoomRange)
    }

    LaunchedEffect(enableZoom) {

        state.enableZoom = enableZoom
    }

    LaunchedEffect(enableDoubleTapZoom) {

        state.enableDoubleTapZoom = enableDoubleTapZoom
    }

    LaunchedEffect(enablePan) {

        state.enablePan = enablePan
    }

    LaunchedEffect(enableRotation) {

        state.enableRotation = enableRotation
    }

    RetainedEffect(Unit) {

        onRetire { state.resetState() }
    }

    return state
}

/**
 * A state object that manages the state of transformable gestures.
 *
 * @param initialZoom The initial zoom level.
 * @param zoomRange The allowed range for zoom levels.
 */
@Stable
class TransformableGesturesState(
    val initialZoom: Float,
    val zoomRange: ClosedFloatingPointRange<Float>
) {

    /**
     * Whether to enable zoom gestures.
     */
    var enableZoom by mutableStateOf(true)

    /**
     * Whether to enable double-tap to zoom.
     */
    var enableDoubleTapZoom by mutableStateOf(true)

    /**
     * Whether to enable rotation gestures.
     */
    var enableRotation by mutableStateOf(false)

    /**
     * Whether to enable pan gestures.
     */
    var enablePan by mutableStateOf(true)

    /**
     * The current zoom level.
     */
    var zoom by mutableFloatStateOf(initialZoom)
        internal set

    /**
     * The current position offset.
     */
    var position by mutableStateOf(Offset.Zero)
        internal set

    /**
     * The current rotation angle in degrees.
     */
    var rotation by mutableFloatStateOf(0F)
        internal set

    /**
     * The number of touch pointers currently on the screen.
     */
    var touchCount by mutableIntStateOf(0)
        internal set

    /**
     * The size of the composable that the transformable gestures are applied to.
     */
    var boundSize by mutableStateOf(Size.Unspecified)
        internal set

    /**
     * Returns true if a transform gesture is currently in progress.
     */
    fun hasTransform(): Boolean {

        return (touchCount == 2 && (enableZoom || enableRotation))
                || (touchCount == 2 && zoom in ZoomedOut)
                || (touchCount == 1 && zoom in ZoomedIn)
                || (touchCount == 2 && rotation in RotationIn)
    }

    /**
     * Handles the double-tap gesture to zoom in or out.
     * The zoom level cycles through a set of predefined values.
     * If the zoom is reset to 1.0F, the position is also reset.
     *
     * @param tapPosition The position of the tap.
     */
    internal fun onDoubleTap(tapPosition: Offset) {

        if (enableDoubleTapZoom) {

            val intermediateZoom = zoomRange.endInclusive / 2

            val newZoom = when {

                zoom < initialZoom -> initialZoom
                zoom < intermediateZoom -> intermediateZoom
                zoom < zoomRange.endInclusive -> zoomRange.endInclusive
                else -> initialZoom
            }.coerceIn(range = zoomRange)

            if (newZoom > 1.0F) {

                val newPosition = (boundSize.center - tapPosition) * (newZoom / zoom - 1) + position

                position = getCoercedPosition(
                    position = newPosition,
                    zoom = newZoom,
                    boundSize = boundSize
                )

                zoom = newZoom
            } else resetAllValues()
        }
    }

    /**
     * Handles the transformation gesture, updating the zoom, pan, and rotation based on the gesture
     * changes. Zoom and rotation are only applied for two-finger gestures.
     * Panning is applied for one-finger drag when zoomed in.
     *
     * @param zoomChange The change in zoom factor.
     * @param panChange The change in pan offset.
     * @param rotationChange The change in rotation degrees.
     */
    internal fun onTransformation(zoomChange: Float, panChange: Offset, rotationChange: Float) {

        if (enableZoom && touchCount == 2) zoom = (zoom * zoomChange).coerceIn(zoomRange)

        if (enableRotation && touchCount == 2) {
            rotation = (rotation + rotationChange) % RotationIn.endInclusive
        }

        if (enablePan && zoom in ZoomedIn) {

            val newPosition = position + panChange

            position = getCoercedPosition(
                position = newPosition,
                zoom = zoom,
                boundSize = boundSize
            )
        } else resetPosition()
    }

    /**
     * Resets the zoom, rotation, and position to their initial values.
     */
    fun resetAllValues() {

        resetZoom()
        resetRotation()
        resetPosition()
    }

    /**
     * Resets the zoom to its initial value.
     */
    fun resetZoom() {

        zoom = initialZoom
    }

    /**
     * Resets the rotation to 0.
     */
    fun resetRotation() {

        rotation = 0F
    }

    /**
     * Resets the position to the center.
     */
    fun resetPosition() {

        position = Offset.Zero
    }

    /**
     * Resets the bounds of the composable to `Size.Unspecified`.
     */
    internal fun resetBounds() {

        boundSize = Size.Unspecified
    }

    /**
     * Resets the entire state of the transformable gestures, including zoom, rotation, position
     * and bounds.
     */
    internal fun resetState() {

        resetAllValues()
        resetBounds()
    }
}