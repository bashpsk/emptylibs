package io.bashpsk.emptylibs.imagekolor.filter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Creates and remembers an [ImageFilterState] instance that survives configuration changes.
 *
 * This composable function is used to manage the state of image filters,
 * including the currently selected filter and the preview image.
 *
 * @param previewImage The optional [ImageBitmap] to be used as the preview image.
 * If `null`, a default image (R.drawable.flower_02) will be used.
 * @return An [ImageFilterState] instance that can be used to control and observe the image filter
 * state.
 */
@Composable
fun rememberImageFilterState(previewImage: ImageBitmap? = null): ImageFilterState {

    return rememberSaveable(
        previewImage,
        saver = ImageFilterState.StateSaver(previewImage = previewImage)
    ) {

        ImageFilterState(previewImage = previewImage)
    }
}

/**
 * Represents the state of the image filter, including the preview image and the selected filter.
 *
 * @param previewImage The image to be displayed and filtered image preview.
 */
@Stable
class ImageFilterState(val previewImage: ImageBitmap?) {

    /**
     * The currently selected image filter.
     *
     * This property holds the [ImageFilterType] that is currently applied to the image.
     * It is a mutable state, meaning that changes to this property will trigger recomposition
     * in composables that observe it.
     *
     * The `private set` modifier restricts modifications to this property from outside the
     * [ImageFilterState] class, ensuring that changes are made through the `onSelectFilter`
     * method.
     */
    var selectedFilter by mutableStateOf(ImageFilterType.Original)
        private set

    /**
     * Updates the currently selected image filter.
     *
     * This function is called when the user selects a new filter from the available options.
     *
     * @param filter The [ImageFilterType] to be applied to the image.
     */
    fun onSelectFilter(filter: ImageFilterType) {

        selectedFilter = filter
    }

    /**
     * Applies the currently selected filter to the given image and returns the filtered image.
     *
     * @param image The [ImageBitmap] to which the filter will be applied.
     * @return The filtered [ImageBitmap].
     */
    fun getFilterImage(image: ImageBitmap): ImageBitmap {

        return image.getKolorFilterBitmap(filter = selectedFilter)
    }

    companion object {

        const val KEY_SELECTED_FILTER = "IMAGE-FILTER-SELECTED-FILTER"

        fun StateSaver(
            previewImage: ImageBitmap?
        ): Saver<ImageFilterState, Any> = mapSaver(
            save = { state ->

                mapOf(KEY_SELECTED_FILTER to state.selectedFilter)
            },
            restore = { elements ->

                ImageFilterState(previewImage = previewImage).apply {

                    selectedFilter = elements.getOrElse(
                        KEY_SELECTED_FILTER
                    ) { ImageFilterType.Original} as ImageFilterType
                }
            }
        )
    }
}