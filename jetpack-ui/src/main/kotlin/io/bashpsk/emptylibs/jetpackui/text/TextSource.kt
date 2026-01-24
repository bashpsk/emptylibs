package io.bashpsk.emptylibs.jetpackui.text

import android.net.Uri
import androidx.compose.runtime.Stable

/**
 * Defines the source from which the [LazyTextViewer] reads its content.
 */
@Stable
sealed interface TextSource {

    data object Empty : TextSource

    /**
     * Source provided as a raw [String].
     *
     * **Warning:** For very large texts, this can lead to OutOfMemory errors as the entire string
     * must be held in memory. Use [URI] or [Path] for large content.
     *
     * @property content The raw string content.
     */
    data class RawString(val content: String?) : TextSource

    /**
     * Source provided as a file path string. Content is read line-by-line using a stream.
     *
     * @property content The absolute path to the text file.
     */
    data class Path(val content: String?) : TextSource

    /**
     * Source provided as an Android [Uri]. Content is read lazily using a stream via a
     * ContentResolver.
     *
     * @property content The URI pointing to the text content.
     */
    data class URI(val content: Uri?) : TextSource
}