package io.bashpsk.emptylibs.storage.extension

import android.util.Log
import io.bashpsk.emptylibs.storage.utils.LOG_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Returns the length of this file in bytes, or `null` if the file does not exist or an
 * I/O error occurs.
 *
 * This function is a safe alternative to [java.io.File.length], handling exceptions
 * and non-existent files gracefully.
 *
 * @return The file length as a [Long], or `null` if the file doesn't exist or an error occurs.
 * @see fileLength
 */
fun File.fileLengthOrNull(): Long? {

    return try {

        length()
    } catch (exception: Exception) {

        null
    }
}

/**
 * Returns the length of this file in bytes, or `0L` if the file doesn't exist or an I/O error
 * occurs.
 * This is a convenience wrapper around [fileLengthOrNull] that provides a non-nullable return
 * value.
 *
 * @return The length of the file in bytes, or `0L` on failure.
 * @see fileLengthOrNull
 */
fun File.fileLength(): Long {

    return fileLengthOrNull() ?: 0L
}

suspend fun File.folderCount(): Int = withContext(context = Dispatchers.IO) {

    return@withContext try {

        listFiles { file -> file.isDirectory }?.count() ?: 0
    } catch (exception: Exception) {

        currentCoroutineContext().ensureActive()
        Log.w(LOG_TAG, exception.message, exception)
        0
    }
}

suspend fun File.fileCount(): Int = withContext(context = Dispatchers.IO) {

    return@withContext try {

        listFiles { file -> file.isFile }?.count() ?: 0
    } catch (exception: Exception) {

        currentCoroutineContext().ensureActive()
        Log.w(LOG_TAG, exception.message, exception)
        0
    }
}