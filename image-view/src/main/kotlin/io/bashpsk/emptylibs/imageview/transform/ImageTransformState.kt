package io.bashpsk.emptylibs.imageview.transform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import io.bashpsk.emptylibs.composeutils.offset.OffsetData
import io.bashpsk.emptylibs.composeutils.offset.toOffsetData

/**
 * Remembers and creates an [ImageTransformState] with the given [config]
 * that survives configuration changes.
 *
 * This function is a composable function that uses [rememberSaveable] to ensure that the
 * [ImageTransformState] is preserved across recompositions and configuration changes.
 *
 * @return An [ImageTransformState] instance.
 */
@Composable
fun rememberImageTransformState(): ImageTransformState {

    return rememberSaveable(saver = ImageTransformState.StateSaver()) {
        ImageTransformState()
    }
}

/**
 * A state object that can be hoisted to control and observe image transformations.
 *
 * This class holds the current zoom, rotation, and position of the image.
 * It provides methods to update these values and reset them to their defaults.
 *
 * @param config The configuration for image transformations.
 */
class ImageTransformState() {

    /**
     * The current zoom level of the image.
     * The default value is 1.0F, indicating no zoom.
     * This property can be observed for changes.
     */
    var zoom by mutableFloatStateOf(1.0F)

    /**
     * The current rotation of the image in degrees.
     * The value is an integer, typically representing discrete rotation.
     * This property can be observed for changes.
     * methods.
     */
    var rotation by mutableFloatStateOf(0F)

    /**
     * The current position offset of the image.
     * This value represents the translation of the image from its original position.
     */
    var position by mutableStateOf(Offset.Zero)

    /**
     * Resets all transformation values (zoom, rotation, and position) to their default states.
     * Zoom is set to 1.0F, rotation to 0, and position to Offset.Zero.
     */
    fun resetAllValues() {

        resetZoom()
        resetRotation()
        resetPosition()
    }

    /**
     * Resets the zoom level to its default value (1.0F).
     */
    fun resetZoom() {

        zoom = 1.0F
    }

    /**
     * Resets the rotation of the image to its default value (0 degrees).
     */
    fun resetRotation() {

        rotation = 0F
    }

    /**
     * Resets the position of the image to the default (0, 0) offset.
     */
    fun resetPosition() {

        position = Offset.Zero
    }

    companion object {

        private const val KEY_ZOOM = "IMAGE-TRANSFORM-ZOOM"
        private const val KEY_ROTATION = "IMAGE-TRANSFORM-ROTATION"
        private const val KEY_POSITION = "IMAGE-TRANSFORM-POSITION"

        fun StateSaver(): Saver<ImageTransformState, Any> = mapSaver(
            save = { state ->

                mapOf(
                    KEY_ZOOM to state.zoom,
                    KEY_ROTATION to state.rotation,
                    KEY_POSITION to state.position.toOffsetData()
                )
            },
            restore = { elements ->

                ImageTransformState().apply {

                    zoom = elements.getOrElse(KEY_ZOOM) { 1.0F } as Float
                    rotation = elements.getOrElse(KEY_ROTATION) { 0F } as Float

                    position = (elements.getOrElse(
                        KEY_POSITION
                    ) { Offset.Zero.toOffsetData() } as OffsetData).toOffset()
                }
            }
        )
    }
}