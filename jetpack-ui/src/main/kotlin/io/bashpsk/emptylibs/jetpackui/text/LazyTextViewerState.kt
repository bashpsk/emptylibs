package io.bashpsk.emptylibs.jetpackui.text

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.bashpsk.emptylibs.formatter.extension.toMegabytes
import io.bashpsk.emptylibs.lrucachemanager.manager.EmptyCacheManager
import io.bashpsk.emptylibs.storage.extension.fileLengthOrNull
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.Channels

/**
 * Creates and remembers a [LazyTextViewerState] for the given [source].
 *
 * @param source The source of the text to be displayed.
 * @param cacheSize The maximum number of recently read lines to cache.
 * @return A [LazyTextViewerState] instance.
 */
@Composable
fun rememberLazyTextViewerState(
    source: TextSource,
    @IntRange(1, 70)
    cacheSize: Int = 40
): LazyTextViewerState {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val state = retain(coroutineScope, source) {
        LazyTextViewerState(context = context, coroutineScope = coroutineScope, source = source)
    }

    LaunchedEffect(cacheSize) {

        state.textCacheManager.resize(maxSize = cacheSize)
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
    private val context: Context,
    internal val coroutineScope: CoroutineScope,
    private val source: TextSource
) {

    /**
     * Manager responsible for caching recently read lines of text to improve performance
     * and reduce redundant I/O operations when scrolling or re-composing.
     *
     * Uses an LRU (Least Recently Used) strategy with a maximum capacity of 40 lines.
     */
    internal val textCacheManager = EmptyCacheManager<Int, String>(maxSize = 40)

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
     * The interval at which line offsets are stored in [linePointerList].
     * For large files, storing every line offset would consume too much memory.
     */
    private var sparseStep by mutableIntStateOf(250)

    /**
     * A sparse persistent list of file pointers (offsets) used for random access optimization.
     *
     * Stored offsets are separated by [sparseStep] lines. This allows the viewer to jump
     * to the nearest preceding stored offset and then read forward, balancing memory
     * usage and access speed.
     */
    private var linePointerList by mutableStateOf(persistentListOf<Long>())

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

                    is TextSource.Empty -> 0
                    is TextSource.RawString -> source.content?.lines()?.size ?: 0

                    is TextSource.Path -> getFileLinesCount(
                        file = source.content?.let { path -> File(path) }
                    )

                    is TextSource.URI -> getFileLinesCount(uri = source.content)
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

                is TextSource.Empty -> TextContentResult.Content("")

                is TextSource.RawString -> readLineContent(content = source.content, index = index)

                is TextSource.Path -> readLineContent(
                    file = source.content?.let { path -> File(path) },
                    index = index
                )

                is TextSource.URI -> readLineContent(file = source.content, index = index)
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

        return@withContext textCacheManager.get(index)?.let { lineText ->

            TextContentResult.Content(text = lineText)
        } ?: content?.lines()?.getOrNull(index)?.let { lineText ->

            textCacheManager.add(index, lineText)
            TextContentResult.Content(text = lineText)
        } ?: throw NullPointerException("Line not found.")
    }

    /**
     * Reads the content of a specific line from the text source.
     *
     * This function uses the sparse [linePointerList] to jump to the nearest stored offset
     * and then skips the remaining lines to reach the target [index].
     *
     * @param index The 0-based index of the line to retrieve.
     * @return A [TextContentResult] containing either the line text or an error message.
     * @throws NullPointerException if the file is null or an I/O error occurs.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    @Throws(NullPointerException::class, IOException::class, IndexOutOfBoundsException::class)
    suspend fun readLineContent(
        file: File?,
        index: Int
    ): TextContentResult = withContext(Dispatchers.IO) {

        if (index !in 0 until totalLines) throw IndexOutOfBoundsException(
            "Index out of bounds."
        )

        return@withContext textCacheManager.get(index)?.let { lineText ->

            TextContentResult.Content(text = lineText)
        } ?: file?.takeIf { sourceFile -> sourceFile.exists() }?.let { sourceFile ->

            RandomAccessFile(sourceFile, "r").use { randomFile ->

                val sparseIndex = index / sparseStep
                val linesToSkip = index % sparseStep

                randomFile.seek(linePointerList.getOrElse(index = sparseIndex) { 0L })

                Channels.newInputStream(randomFile.channel).buffered().use { inputStream ->

                    var linesSkipped = 0

                    while (linesSkipped < linesToSkip) {

                        currentCoroutineContext().ensureActive()

                        val bytes = inputStream.read()

                        if (bytes == -1) break

                        when (bytes) {

                            '\n'.code -> linesSkipped++

                            '\r'.code -> {

                                linesSkipped++
                                inputStream.mark(1)
                                if (inputStream.read() != '\n'.code) inputStream.reset()
                            }
                        }
                    }

                    ByteArrayOutputStream(1024).use { outputStream ->

                        while (currentCoroutineContext().isActive) {

                            val bytes = inputStream.read()

                            if (bytes == -1 || bytes == '\n'.code) break

                            if (bytes == '\r'.code) {
                                inputStream.mark(1)
                                if (inputStream.read() != '\n'.code) inputStream.reset()
                                break
                            }

                            outputStream.write(bytes)
                        }

                        val lineText = outputStream.toString()

                        textCacheManager.add(index, lineText)
                        TextContentResult.Content(text = lineText)
                    }
                }
            }
        } ?: throw NullPointerException("Path is null.")
    }

    /**
     * Reads the content of a specific line from a [Uri] source.
     *
     * This function utilizes the [linePointerList] to skip to the nearest pre-calculated
     * byte offset and then traverses the stream until the target line is reached.
     *
     * @param file The [Uri] pointing to the text resource.
     * @param index The 0-based index of the line to retrieve.
     * @return A [TextContentResult] containing the line text or an error.
     * @throws NullPointerException if the [file] URI is null.
     * @throws IndexOutOfBoundsException if the index is negative or exceeds [totalLines].
     * @throws IOException if the stream cannot be opened or read.
     */
    suspend fun readLineContent(
        file: Uri?,
        index: Int
    ): TextContentResult = withContext(Dispatchers.IO) {

        if (file == null) throw NullPointerException("URI is null")

        if (index !in 0 until totalLines) throw IndexOutOfBoundsException(
            "Index out of bounds"
        )

        return@withContext textCacheManager.get(index)?.let { lineText ->

            TextContentResult.Content(text = lineText)
        } ?: context.contentResolver.openInputStream(file)?.buffered()?.use { inputStream ->

            val sparseIndex = index / sparseStep
            val linesToSkip = index % sparseStep

            inputStream.skip(linePointerList.getOrElse(index = sparseIndex) { 0L })

            var linesSkipped = 0

            while (linesSkipped < linesToSkip) {

                currentCoroutineContext().ensureActive()

                val bytes = inputStream.read()

                if (bytes == -1) break

                when (bytes) {

                    '\n'.code -> linesSkipped++

                    '\r'.code -> {

                        linesSkipped++
                        inputStream.mark(1)
                        if (inputStream.read() != '\n'.code) inputStream.reset()
                    }
                }
            }

            ByteArrayOutputStream(1024).use { outputStream ->

                while (currentCoroutineContext().isActive) {

                    val bytes = inputStream.read()

                    if (bytes == -1 || bytes == '\n'.code) break

                    if (bytes == '\r'.code) {
                        inputStream.mark(1)
                        if (inputStream.read() != '\n'.code) inputStream.reset()
                        break
                    }

                    outputStream.write(bytes)
                }

                val lineText = outputStream.toString()

                textCacheManager.add(index, lineText)
                TextContentResult.Content(text = lineText)
            }
        } ?: throw IOException("Could not open URI stream")
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
     * Counts the total number of lines in the provided file and populates [linePointerList]
     * with sparse byte offsets for random access.
     *
     * This function reads the file byte-by-byte to accurately identify byte offsets for
     * line terminators (\n, \r, or \r\n).
     *
     * @param file The file to process.
     * @return The total number of lines found, or 0 if the file is null or an error occurs.
     */
    private suspend fun getFileLinesCount(file: File?): Int = withContext(Dispatchers.IO) {

        return@withContext try {

            val fileSize = file?.fileLengthOrNull() ?: throw NullPointerException("Path is null.")

            sparseStep = findSparseStep(length = fileSize)

            var linesFound = 1
            var bytePointer = 0L
            var totalReadBytes = 0
            var previousWasCR = false
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val lineOffsets = persistentListOf(0L).builder()

            file.inputStream().buffered().use { inputStream ->

                while (inputStream.read(buffer).also { bytes -> totalReadBytes = bytes } != -1) {

                    currentCoroutineContext().ensureActive()

                    (0 until totalReadBytes).forEach { index ->

                        bytePointer++

                        previousWasCR = when (buffer[index].toInt() and 0xFF) {

                            '\n'.code -> if (previousWasCR && (linesFound - 1) % sparseStep == 0) {
                                lineOffsets[lineOffsets.size - 1] = bytePointer
                                false
                            } else {
                                if (linesFound % sparseStep == 0) lineOffsets.add(bytePointer)
                                linesFound++
                                false
                            }

                            '\r'.code -> {
                                if (linesFound % sparseStep == 0) lineOffsets.add(bytePointer)
                                linesFound++
                                true
                            }

                            else -> false
                        }
                    }
                }
            }

            linePointerList = lineOffsets.build()
            linesFound
        } catch (exception: Exception) {

            currentCoroutineContext().ensureActive()
            Log.e("LazyTextViewer", exception.message, exception)
            0
        }
    }

    /**
     * Counts the total number of lines in the content pointed to by the provided [uri] and
     * populates [linePointerList] with sparse byte offsets for random access.
     *
     * This function uses the [context]'s content resolver to open an input stream, calculating
     * appropriate sparse steps based on the file size to balance memory usage and seek performance.
     * It handles various line terminators (\n, \r, or \r\n).
     *
     * @param uri The URI of the content to process.
     * @return The total number of lines found, or 0 if the URI is null or an error occurs.
     */
    private suspend fun getFileLinesCount(uri: Uri?): Int = withContext(Dispatchers.IO) {

        return@withContext try {

            val fileUri = uri ?: throw NullPointerException("URI is null.")

            context.contentResolver.openAssetFileDescriptor(fileUri, "r").use { descriptor ->

                sparseStep = findSparseStep(length = descriptor?.length ?: 0L)
            }

            var linesFound = 1
            var bytePointer = 0L
            var totalReadBytes = 0
            var previousWasCR = false
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val lineOffsets = persistentListOf(0L).builder()

            context.contentResolver.openInputStream(fileUri)?.buffered()?.use { inputStream ->

                while (inputStream.read(buffer).also { bytes -> totalReadBytes = bytes } != -1) {

                    currentCoroutineContext().ensureActive()

                    (0 until totalReadBytes).forEach { index ->

                        bytePointer++

                        previousWasCR = when (buffer[index].toInt() and 0xFF) {

                            '\n'.code -> if (previousWasCR && (linesFound - 1) % sparseStep == 0) {
                                lineOffsets[lineOffsets.size - 1] = bytePointer
                                false
                            } else {
                                if (linesFound % sparseStep == 0) lineOffsets.add(bytePointer)
                                linesFound++
                                false
                            }

                            '\r'.code -> {
                                if (linesFound % sparseStep == 0) lineOffsets.add(bytePointer)
                                linesFound++
                                true
                            }

                            else -> false
                        }
                    }
                }
            }

            linePointerList = lineOffsets.build()
            linesFound
        } catch (exception: Exception) {

            currentCoroutineContext().ensureActive()
            Log.e("LazyTextViewer", exception.message, exception)
            0
        }
    }

    /**
     * Determines the optimal [sparseStep] for storing line offsets based on the file size.
     *
     * A smaller step provides faster random access but consumes more memory by storing more offsets
     * in [linePointerList]. A larger step saves memory but requires more sequential reading when
     * jumping to a specific line.
     *
     * @param length The total length of the file in bytes.
     * @return The number of lines to skip between each stored offset.
     */
    private fun findSparseStep(length: Long): Int {

        val fileSizeInMB = length.toMegabytes()

        return when {

            fileSizeInMB <= 1.0 -> 1
            fileSizeInMB <= 10.0 -> 100
            fileSizeInMB <= 50.0 -> 250
            fileSizeInMB <= 100.0 -> 500
            else -> 1000
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
        sparseStep = 250
        totalLines = 0
        isSourceLoading = false
        linePointerList = persistentListOf()
    }
}