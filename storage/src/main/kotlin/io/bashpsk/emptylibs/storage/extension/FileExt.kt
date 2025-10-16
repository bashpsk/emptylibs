package io.bashpsk.emptylibs.storage.extension

import java.io.File

fun File.fileLengthOrNull(): Long? {

    return try {

        length().takeIf { exists() }
    } catch (exception: Exception) {

        null
    }
}

fun File.fileLength(): Long {

    return fileLengthOrNull() ?: 0L
}