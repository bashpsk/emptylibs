package io.bashpsk.emptylibs.jetpackui.text

import androidx.compose.runtime.Stable
import java.io.File

/**
 * Defines the source from which the [LazyTextViewer] reads its content.
 */
@Stable
sealed interface TextSource {

    /**
     * Source provided as a raw [String].
     *
     * **Warning:** For very large texts, this can lead to OutOfMemory errors as the entire string
     * must be held in memory. Use [TextFile] or [FilePath] for large content.
     *
     * @property content The raw string content.
     */
    data class RawString(val content: String?): TextSource

    /**
     * Source provided as a file path string. Content is read line-by-line using a stream.
     *
     * @property content The absolute path to the text file.
     */
    data class FilePath(val content: String?): TextSource

    /**
     * Source provided as a [File] object. Content is read line-by-line using a stream.
     *
     * @property content The [File] object pointing to the text file.
     */
    data class TextFile(val content: File?): TextSource
}