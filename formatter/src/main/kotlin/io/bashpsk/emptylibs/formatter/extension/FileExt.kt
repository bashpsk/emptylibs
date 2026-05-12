package io.bashpsk.emptylibs.formatter.extension

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
internal fun File.fileLengthOrNull(): Long? {

    return try {

        length()
    } catch (exception: Exception) {

        null
    }
}

/**
 * Returns the length of this file in bytes, or `0L` if the file doesn't exist or an I/O error
 * occurs. This is a convenience wrapper around [fileLengthOrNull] that provides a non-nullable
 * return value.
 *
 * @return The length of the file in bytes, or `0L` on failure.
 * @see fileLengthOrNull
 */
internal fun File.fileLength(): Long {

    return fileLengthOrNull() ?: 0L
}