package io.bashpsk.emptylibs.screen.jetpackui

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

object ListData {

    val FILE_OPERATION_LIST = persistentListOf(
        FileOperation.SHARE,
        FileOperation.INFO,
        FileOperation.COPY,
        FileOperation.MOVE,
        FileOperation.RENAME,
        FileOperation.DELETE,
        FileOperation.SELECT_ALL,
        FileOperation.SELECT_NONE,
        FileOperation.SELECT_INVERT,
        FileOperation.SELECT_FOLDERS,
        FileOperation.SELECT_FILES,
    ).toImmutableList()
}