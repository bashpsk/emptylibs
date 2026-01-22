package io.bashpsk.emptylibs.jetpackui.text

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/**
 * Creates and remembers a [LazyTextViewerState] for the given [source].
 *
 * @param source The source of the text to be displayed.
 * @return A [LazyTextViewerState] instance.
 */
@Composable
fun rememberLazyTextViewerState(source: TextSource): LazyTextViewerState {

    val coroutineScope = rememberCoroutineScope()

    return retain(coroutineScope, source) {
        LazyTextViewerState(coroutineScope = coroutineScope, source = source)
    }
}

/**
 * State object for [LazyTextViewer] that manages text loading and line counting.
 *
 * This class handles the logic of determining the number of lines in the source
 * and reading specific lines on demand. For file-based sources, it uses streams
 * to avoid loading the entire file into memory.
 *
 * @property coroutineScope The scope used for background loading operations.
 * @property source The source of the text.
 * @property lineCount The total number of lines in the current [source].
 * @property isSourceLoading Whether the source is currently being loaded (e.g., counting lines).
 * @property sourceLoadJob The background job responsible for loading the source.
 */
@Stable
class LazyTextViewerState(
    internal val coroutineScope: CoroutineScope,
    private val source: TextSource
) {

    var lineCount by mutableIntStateOf(0)
        private set

    var isSourceLoading by mutableStateOf(false)
        private set

    private var sourceLoadJob by mutableStateOf<Job?>(null)

    init {

        setReloadTextSource()
    }

    /**
     * Reloads the text source, re-calculating the line count.
     */
    fun setReloadTextSource() {

        sourceLoadJob?.cancel()

        sourceLoadJob = coroutineScope.launch {

            isSourceLoading = true

            lineCount = try {

                when (source) {

                    is TextSource.RawString -> source.content?.lines()?.size ?: 0

                    is TextSource.FilePath -> getFileLinesCount(
                        file = source.content?.let { path -> File(path) }
                    )

                    is TextSource.TextFile -> getFileLinesCount(file = source.content)
                }
            } catch (exception: Exception) {

                currentCoroutineContext().ensureActive()
                Log.e("LazyTextViewer", exception.message, exception)
                0
            } finally {

                isSourceLoading = false
            }
        }
    }

    /**
     * Reads the content of a specific line index from the source.
     *
     * @param index The 0-based index of the line to read.
     * @return A [TextContentResult] containing the line text or an error.
     */
    suspend fun readLineContent(index: Int): TextContentResult {

        return try {

            when (source) {

                is TextSource.RawString -> readLineContent(text = source.content, index = index)

                is TextSource.FilePath -> readLineContent(
                    file = source.content?.let { path -> File(path) },
                    index = index
                )

                is TextSource.TextFile -> readLineContent(file = source.content, index = index)
            }
        } catch (exception: Exception) {

            currentCoroutineContext().ensureActive()
            Log.e("LazyTextViewer", exception.message, exception)
            TextContentResult.Error(exception.message ?: "Unknown error.")
        }
    }

    /**
     * Reads a line from a raw string.
     *
     * @param text The raw string content.
     * @param index The 0-based index of the line to read.
     * @return The [TextContentResult] for the line.
     * @throws NullPointerException if the line is not found.
     */
    @Throws(NullPointerException::class)
    suspend fun readLineContent(
        text: String?,
        index: Int
    ): TextContentResult = withContext(Dispatchers.Default) {

        return@withContext text?.lines()?.drop(index)?.firstOrNull()?.let { lineText ->

            TextContentResult.Content(lineText)
        } ?: throw NullPointerException("Line not found.")
    }

    /**
     * Reads a line from a [File] efficiently using [File.useLines].
     *
     * @param file The file to read from.
     * @param index The 0-based index of the line to read.
     * @return The [TextContentResult] for the line.
     * @throws NullPointerException if the file or line is not found.
     * @throws IOException if an I/O error occurs.
     */
    @Throws(NullPointerException::class, IOException::class)
    suspend fun readLineContent(
        file: File?,
        index: Int
    ): TextContentResult = withContext(Dispatchers.IO) {

        return@withContext (file ?: throw NullPointerException("Path is null.")).useLines { lines ->

            TextContentResult.Content(
                lines.drop(index).firstOrNull() ?: throw NullPointerException("Line not found.")
            )
        }
    }

    /**
     * Formats the line number for display.
     *
     * @param index The 0-based index of the line.
     * @return A string representation of the 1-based line number.
     */
    internal fun getFormattedLineNumber(index: Int): String {

        return "${index + 1}"
    }

    /**
     * Efficiently counts the number of lines in a file.
     *
     * @param file The file to count lines in.
     * @return The number of lines, or 0 if an error occurs.
     */
    private suspend fun getFileLinesCount(file: File?): Int = withContext(Dispatchers.IO) {

        return@withContext try {

            file?.useLines { lines -> lines.count() } ?: 0
        } catch (exception: Exception) {

            currentCoroutineContext().ensureActive()
            Log.e("LazyTextViewer", exception.message, exception)
            0
        }
    }
}