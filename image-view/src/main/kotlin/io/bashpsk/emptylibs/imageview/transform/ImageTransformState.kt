package io.bashpsk.emptylibs.imageview.transform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

/**
 * Remembers and creates an [ImageTransformState] with the given [zoomRange] and [config]
 * that survives configuration changes.
 *
 * This function is a composable function that uses [rememberSaveable] to ensure that the
 * [ImageTransformState] is preserved across recompositions and configuration changes.
 *
 * @param zoomRange The range of allowed zoom values. Defaults to 0.4F..8.0F.
 * @param config The configuration for image transformation. Defaults to a default
 * [TransformImageConfig].
 * @return An [ImageTransformState] instance.
 */
@Composable
fun rememberImageTransformState(
    zoomRange: ClosedFloatingPointRange<Float> = 0.4F..8.0F,
    config: TransformImageConfig = TransformImageConfig()
): ImageTransformState {

    return rememberSaveable(zoomRange, config, saver = ImageTransformState.StateSaver) {
        ImageTransformState(
            zoomMin = zoomRange.start,
            zoomMax = zoomRange.endInclusive,
            config = config
        )
    }
}

/**
 * A state object that can be hoisted to control and observe image transformations.
 *
 * This class holds the current zoom, rotation, and position of the image.
 * It provides methods to update these values and reset them to their defaults.
 *
 * @param zoomMin The allowable range for zoom values.
 * @param config The configuration for image transformations.
 */
class ImageTransformState(
    val zoomMin: Float,
    val zoomMax: Float,
    val config: TransformImageConfig
) {

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
    var rotation by mutableIntStateOf(0)

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

        zoom = 1.0F
        rotation = 0
        position = Offset.Zero
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

        rotation = 0
    }

    /**
     * Resets the position of the image to the default (0, 0) offset.
     */
    fun resetPosition() {

        position = Offset.Zero
    }

    companion object {

        val StateSaver: Saver<ImageTransformState, List<Any?>> = Saver(
            save = { state ->

                listOf(
                    state.zoomMin,
                    state.zoomMax,
                    state.config,
                    state.zoom,
                    state.rotation,
                    state.position
                )
            },
            restore = { elements ->

                val savedZoomMin = elements.getOrNull(0) as? Float ?: 0.4F
                val savedZoomMax = elements.getOrNull(1) as? Float ?: 8.0F

                val savedConfig = elements.getOrNull(2) as? TransformImageConfig
                    ?: TransformImageConfig()

                val savedZoom = elements.getOrNull(3) as? Float ?: 1.0F
                val savedRotation = elements.getOrNull(4) as? Int ?: 0
                val savedPosition = elements.getOrNull(5) as? Offset ?: Offset.Zero

                ImageTransformState(
                    zoomMin = savedZoomMin,
                    zoomMax = savedZoomMax,
                    config = savedConfig
                ).apply {

                    zoom = savedZoom
                    rotation = savedRotation
                    position = savedPosition
                }
            }
        )
    }
}