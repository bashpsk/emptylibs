package io.bashpsk.emptylibs.formatter.extension

import java.io.File

internal fun File.fileLengthOrNull(): Long? {

    return try {

        length().takeIf { exists() }
    } catch (exception: Exception) {

        null
    }
}

internal fun File.fileLength(): Long {

    return fileLengthOrNull() ?: 0L
}