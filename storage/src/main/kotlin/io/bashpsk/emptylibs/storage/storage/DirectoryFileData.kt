package io.bashpsk.emptylibs.storage.storage

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Represents the data associated with a directory's contents, including its subfolders, files,
 * the storage volume it belongs to, and information about the directory itself.
 *
 * This class is designed to be immutable and parcelable, making it suitable for use in Android
 * applications, particularly with Jetpack Compose and data serialization.
 *
 * @property directory Information about the directory itself.
 * @property storage Information about the storage volume where this directory resides.
 * @property folders An immutable list of [DirectoryData] objects representing the subfolders
 * within this directory. Defaults to an empty persistent list.
 * @property files An immutable list of [FileData] objects representing the files
 * within this directory. Defaults to an empty persistent list.
 */
@Immutable
@Parcelize
@Serializable
data class DirectoryFileData(
    val directory: DirectoryData? = null,
    val storage: StorageVolumeData? = null,
    val folders: ImmutableList<DirectoryData> = persistentListOf(),
    val files: ImmutableList<FileData> = persistentListOf()
) : Parcelable