package io.bashpsk.emptylibs.imagekrop.crop

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Represents the result of a Krop operation.
 *
 * This sealed interface defines the possible outcomes of an image cropping operation:
 *  - [Init]: The initial state before any cropping has occurred.
 *  - [Failed]: Indicates that the cropping operation failed.
 *  - [Success]: Indicates that the cropping operation was successful.
 */
sealed interface KropResult {

    /**
     * Initial state of the KropResult, indicating that no cropping operation has been performed
     * yet.
     */
    data object Init : KropResult

    /**
     * Represents a failed crop operation.
     *
     * @property message A message describing the reason for the failure.
     */
    data class Failed(val message: String) : KropResult

    /**
     * Represents a successful crop operation.
     *
     * @property bitmap The resulting cropped image.
     */
    data class Success(val bitmap: ImageBitmap) : KropResult
}