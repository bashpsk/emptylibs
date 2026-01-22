package io.bashpsk.emptylibs.jetpackui.text

import androidx.compose.runtime.Stable

/**
 * Represents the result of an asynchronous operation to fetch a line of text.
 */
@Stable
sealed interface TextContentResult {

    /**
     * Initial state before the content has started loading or while it's in progress.
     */
    data object Init : TextContentResult

    /**
     * Successfully loaded content.
     *
     * @property text The string content of the requested line.
     */
    data class Content(val text: String) : TextContentResult

    /**
     * Represents a failure in loading the content.
     *
     * @property message A description of the error that occurred.
     */
    data class Error(val message: String) : TextContentResult
}