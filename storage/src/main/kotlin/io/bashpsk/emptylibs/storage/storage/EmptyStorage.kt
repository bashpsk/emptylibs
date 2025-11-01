package io.bashpsk.emptylibs.storage.storage

import android.content.Context
import kotlinx.collections.immutable.ImmutableList

internal interface EmptyStorage {

    suspend fun getStorageVolumeList(
        context: Context
    ): ImmutableList<StorageVolumeData>

    suspend fun getDirectoryFileData(
        context: Context,
        path: String
    ): DirectoryFileData

    fun findStorageVolumeData(
        path: String,
        storageVolumes: ImmutableList<StorageVolumeData>
    ): StorageVolumeData?

    suspend fun getDirectoryData(
        path: String,
        storageVolumes: ImmutableList<StorageVolumeData>
    ): DirectoryData?

    suspend fun getFileData(
        path: String,
        storageVolumes: ImmutableList<StorageVolumeData>
    ): FileData?

    suspend fun getFileListByExtensions(
        context: Context,
        path: String,
        extensions: ImmutableList<String>
    ): ImmutableList<FileData>

    fun getTotalMemory(path: String): Long

    fun getFreeMemory(path: String): Long

    fun getUsedMemory(path: String): Long

    suspend fun getFileSize(paths: ImmutableList<String>): Long

    suspend fun makeFolderOrFile(destination: String, isFolder: Boolean): MakeFileResult
}