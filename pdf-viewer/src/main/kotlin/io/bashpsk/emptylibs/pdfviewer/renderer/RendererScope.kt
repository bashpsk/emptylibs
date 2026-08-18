package io.bashpsk.emptylibs.pdfviewer.renderer

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Manages the lifecycle and thread-safe access to a [PdfRenderer].
 *
 * @property onCreate Lambda to create a new [PdfRenderer] instance.
 * @property fileDescriptor The file descriptor for the PDF document.
 */
internal class RendererScope(
    private val onCreate: () -> PdfRenderer,
    private val fileDescriptor: ParcelFileDescriptor
) {

    /**
     * Mutex to ensure thread-safe access to the renderer.
     */
    private val mutex = Mutex()

    /**
     * The underlying [PdfRenderer] instance.
     */
    private var renderer by mutableStateOf<PdfRenderer?>(null)

    /**
     * Executes a block of code using the [PdfRenderer], initializing it if necessary.
     *
     * @param block The block to execute.
     * @return The result of the block.
     */
    suspend fun <T> use(block: suspend (PdfRenderer) -> T): T = mutex.withLock {

        if (renderer == null) renderer = onCreate()
        block(renderer!!)
    }

    /**
     * Closes the renderer and the associated file descriptor.
     */
    fun close() {

        renderer?.close()
        fileDescriptor.close()
        renderer = null
    }
}