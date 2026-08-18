package io.bashpsk.emptylibs.component.jetpackui

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

object ListData {

    val FILE_OPERATION_LIST = persistentListOf(
        FileOperation.Share,
        FileOperation.Info,
        FileOperation.Copy,
        FileOperation.Move,
        FileOperation.Rename,
        FileOperation.Delete,
        FileOperation.SelectAll,
        FileOperation.SelectNone,
        FileOperation.SelectInvert,
        FileOperation.SelectFolders,
        FileOperation.SelectFiles,
    ).toImmutableList()
}