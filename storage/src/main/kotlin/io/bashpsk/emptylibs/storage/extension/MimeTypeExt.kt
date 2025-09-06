package io.bashpsk.emptylibs.storage.extension

import android.webkit.MimeTypeMap
import java.io.File

/**
 * Finds the MIME type for a given file extension.
 *
 * This function uses the Android `MimeTypeMap` to look up the MIME type
 * associated with the provided file extension. If no specific MIME type
 * is found for the extension, it defaults to "*&#47;*", representing any
 * type of data.
 *
 * @param extension The file extension (e.g., "txt", "jpg", "pdf") for which
 *   to find the MIME type. It should not include the leading dot.
 * @return A string representing the MIME type (e.g., "text/plain", "image/jpeg").
 *   Returns "*&#47;*" if the MIME type cannot be determined.
 */
fun findMimeType(extension: String) : String {

    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
}

/**
 * Finds the MIME type for a given file extension.
 *
 * This function is an extension function for the `File` class. It extracts
 * the file's extension and then uses the `findMimeType(extension: String)`
 * function to determine the MIME type.
 *
 * @receiver The `File` object for which to find the MIME type.
 * @return A string representing the MIME type (e.g., "text/plain", "image/jpeg").
 *   Returns "*&#47;*" if the MIME type cannot be determined or if the file has no extension.
 */
fun File.findMimeType(): String {

    return findMimeType(extension = this.extension)
}