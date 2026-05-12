package io.bashpsk.emptylibs.storage.storage

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents a file's metadata.
 *
 * This data class is used to store information about a file, such as its unique identifier,
 * title, path, URI, extension, visibility, type, size, modification date, and storage volume.
 *
 * It is designed to be immutable, parcelable (for Android), and serializable
 * (for data persistence).
 *
 * @property uuid A unique identifier for the file, generated randomly by default.
 * @property title The title or display name of the file.
 * @property path The absolute file system path to the file.
 * @property uri The content URI of the file, typically used for accessing files through content
 * providers.
 * @property extension The file extension (e.g., "txt", "jpg").
 * @property visibleType The visibility type of the file (e.g., public, private).
 * @property fileType The general type of the file (e.g., document, image, video).
 * @property size The size of the file in bytes.
 * @property modifiedDate The last modification timestamp of the file, in milliseconds since the
 * epoch.
 * @property storage Information about the storage volume where the file is located.
 */
@OptIn(ExperimentalUuidApi::class)
@Immutable
@Parcelize
@Serializable
data class FileData(
    val uuid: String = Uuid.random().toString(),
    val title: String = "",
    val path: String = "",
    val uri: String = "",
    val extension: String = "",
    val visibleType: FileVisibleType = FileVisibleType.Unknown,
    val fileType: FileType = FileType.UNKNOWN,
    val size: Long = 0L,
    val modifiedDate: Long = 0L,
    val storage: StorageVolumeData? = null
) : Parcelable