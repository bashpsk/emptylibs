package io.bashpsk.emptylibs.storage.extension

import android.webkit.MimeTypeMap
import java.io.File

fun findMimeType(extension: String) : String {

    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
}

fun File.findMimeType(): String {

    return findMimeType(extension = this.extension)
}