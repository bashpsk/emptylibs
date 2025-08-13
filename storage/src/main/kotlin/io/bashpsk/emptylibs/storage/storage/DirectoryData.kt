package io.bashpsk.emptylibs.storage.storage

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents data associated with a directory.
 * This class is designed to be immutable, parcelable for Android IPC, and serializable.
 *
 * @property uuid A unique identifier for the directory, generated using [Uuid.random].
 * @property title The display name or title of the directory. Defaults to an empty string.
 * @property path The absolute file system path of the directory. Defaults to an empty string.
 * @property uri The content URI of the directory, if applicable (e.g., for SAF). Defaults to an
 * empty string.
 * @property visibleType The visibility type of the directory (e.g., public, private). Defaults to
 * [FileVisibleType.PUBLIC].
 * @property folders The number of subfolders contained within this directory. Defaults to 0.
 * @property files The number of files (excluding subfolders) contained within this directory.
 * Defaults to 0.
 * @property modifiedDate The last modified timestamp of the directory, in milliseconds since the
 * epoch. Defaults to 0L.
 * @property storage Information about the storage volume where this directory resides. Defaults to
 * a default [StorageVolumeData] instance.
 */
@OptIn(ExperimentalUuidApi::class)
@Immutable
@Parcelize
@Serializable
data class DirectoryData(
    val uuid: String = Uuid.random().toString(),
    val title: String = "",
    val path: String = "",
    val uri: String = "",
    val visibleType: FileVisibleType = FileVisibleType.PUBLIC,
    val folders: Int = 0,
    val files: Int = 0,
    val modifiedDate: Long = 0L,
    val storage: StorageVolumeData = StorageVolumeData()
) : Parcelable