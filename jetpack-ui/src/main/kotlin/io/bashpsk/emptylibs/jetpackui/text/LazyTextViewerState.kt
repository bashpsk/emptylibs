package io.bashpsk.emptylibs.jetpackui.text

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.bashpsk.emptylibs.jetpackui.utils.setDebug
import io.bashpsk.emptylibs.lrucachemanager.manager.EmptyCacheManager
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

/**
 * Creates and remembers a [LazyTextViewerState] for the given [source].
 *
 * @param source The source of the text to be displayed.
 * @return A [LazyTextViewerState] instance.
 */
@Composable
fun rememberLazyTextViewerState(source: TextSource): LazyTextViewerState {

    val coroutineScope = rememberCoroutineScope()

    val state = retain(coroutineScope, source) {
        LazyTextViewerState(coroutineScope = coroutineScope, source = source)
    }

    DisposableEffect(Unit) {

        onDispose { state.textCacheManager.evictAll() }
    }

    return state
}

/**
 * State object for [LazyTextViewer] that manages text loading and line counting.
 *
 * This class handles the logic of determining the number of lines in the source
 * and reading specific lines on demand. For file-based sources, it uses streams to avoid loading
 * the entire file into memory.
 *
 * @property coroutineScope The scope used for background loading operations.
 * @property source The source of the text.
 */
@Stable
class LazyTextViewerState(
    internal val coroutineScope: CoroutineScope,
    private val source: TextSource
) {

    /**
     * Manager responsible for caching recently read lines of text to improve performance
     * and reduce redundant I/O operations when scrolling or re-composing.
     *
     * Uses an LRU (Least Recently Used) strategy with a maximum capacity of 40 lines.
     */
    internal val textCacheManager = EmptyCacheManager<String>(maxSize = 40)

    /**
     * The total number of lines in the current [source].
     */
    var totalLines by mutableIntStateOf(0)
        private set

    /**
     * Whether the source is currently being loaded (e.g., counting lines).
     */
    var isSourceLoading by mutableStateOf(false)
        private set

    /**
     * The background job responsible for loading the source.
     */
    private var sourceLoadJob by mutableStateOf<Job?>(null)

    /**
     * A persistent list of file pointers (offsets) used for random access optimization.
     *
     * Each entry at index `i` stores the byte position (offset) in the file where line `i` starts.
     * This allows the viewer to use [RandomAccessFile.seek] to jump directly to the start of a
     * specific line instead of reading the file sequentially from the beginning.
     */
    internal var linePointerList by mutableStateOf(persistentListOf<Long>())

    init {

        setReloadTextSource()
    }

    /**
     * Reloads the text source, re-calculating the line count.
     */
    fun setReloadTextSource() {

        clearState()

        sourceLoadJob = coroutineScope.launch {

            isSourceLoading = true

            totalLines = try {

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

                is TextSource.RawString -> readLineContent(content = source.content, index = index)

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
     * @param content The raw string content.
     * @param index The 0-based index of the line to read.
     * @return The [TextContentResult] for the line.
     * @throws NullPointerException if the line is not found.
     */
    @Throws(NullPointerException::class)
    suspend fun readLineContent(
        content: String?,
        index: Int
    ): TextContentResult = withContext(Dispatchers.Default) {

        return@withContext textCacheManager.get(index.toString())?.let { lineText ->

            TextContentResult.Content(text = lineText)
        } ?: content?.lines()?.getOrNull(index)?.let { lineText ->

            textCacheManager.add(index.toString(), lineText)
            TextContentResult.Content(text = lineText)
        } ?: throw NullPointerException("Line not found.")
    }

    /**
     * Reads the content of a specific line from the text source.
     *
     * This function determines the type of the [source] (raw string, file path, or file object)
     * and delegates the reading process to the appropriate specialized method. It includes
     * error handling to catch and log exceptions, ensuring a [TextContentResult.Error] is
     * returned instead of crashing.
     *
     * @param index The 0-based index of the line to retrieve.
     * @return A [TextContentResult] containing either the line text or an error message.
     */
    @Throws(NullPointerException::class, IOException::class, IndexOutOfBoundsException::class)
    suspend fun readLineContent(
        file: File?,
        index: Int
    ): TextContentResult = withContext(Dispatchers.IO) {

        if (index !in 0..totalLines) throw IndexOutOfBoundsException("Index out of bounds.")

        return@withContext textCacheManager.get(index.toString())?.let { lineText ->

            TextContentResult.Content(text = lineText)
        } ?: file?.takeIf { sourceFile -> sourceFile.exists() }?.let { sourceFile ->

            RandomAccessFile(sourceFile, "r").use { randomFile ->

                randomFile.seek(linePointerList.getOrElse(index) { 0L })
                val lineText = randomFile.readLine() ?: throw NullPointerException(
                    "Line not found."
                )

                textCacheManager.add(index.toString(), lineText)
                TextContentResult.Content(text = lineText)
            }
        } ?: throw NullPointerException("Path is null.")
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
     * Counts the total number of lines in a file and populates the [linePointerList] with byte
     * offsets.
     *
     * This function performs a sequential read of the file using a [RandomAccessFile] to determine
     * the total line count. As it iterates, it stores the file pointer (byte offset) of every line
     * in [linePointerList] to facilitate fast random access seeking later.
     *
     * @param file The file to be processed.
     * @return The total number of lines found in the file, or 0 if the file is null or an error
     * occurs.
     */
    private suspend fun getFileLinesCount(file: File?): Int = withContext(Dispatchers.IO) {

        return@withContext try {

            var linesCount = 0

            RandomAccessFile(file, "r").use { randomFile ->

                while (currentCoroutineContext().isActive) {

                    linePointerList = linePointerList.add(randomFile.filePointer)
                    randomFile.readLine() ?: break
                    linesCount++
                    if (linesCount > 0 && linesCount % 10000 == 0) "LINES: $linesCount".setDebug()
                }
            }

            linesCount
        } catch (exception: Exception) {

            currentCoroutineContext().ensureActive()
            Log.e("LazyTextViewer", exception.message, exception)
            0
        }
    }

    /**
     * Resets the internal state of the viewer to its initial values.
     *
     * This function clears the line cache, cancels any active source-loading background jobs,
     * resets the line count and loading status, and re-initializes the sparse list used
     * for random access file seek points.
     */
    internal fun clearState() {

        textCacheManager.evictAll()
        sourceLoadJob?.cancel()
        totalLines = 0
        isSourceLoading = false
        linePointerList = persistentListOf()
    }
}