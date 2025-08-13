package io.bashpsk.emptylibs.storage.storage

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents data for a storage volume.
 *
 * This data class holds information about a storage volume, such as its unique identifier,
 * title, path, total size, available size, used size, and volume type.
 *
 * It is designed to be immutable, parcelable (for Android), and serializable
 * (for data persistence).
 *
 * @property uuid A unique identifier for the storage volume, generated randomly by default.
 * @property title The title or display name of the storage volume.
 * @property path The file system path to the root of the storage volume.
 * @property totalSize The total capacity of the storage volume in bytes.
 * @property availableSize The available free space on the storage volume in bytes.
 * @property usedSize The amount of space currently used on the storage volume in bytes.
 * @property volumeType The type of the storage volume (e.g., INTERNAL, SD_CARD, OTG).
 */
@OptIn(ExperimentalUuidApi::class)
@Immutable
@Parcelize
@Serializable
data class StorageVolumeData(
    val uuid: String = Uuid.random().toString(),
    val title: String = "",
    val path: String = "",
    val totalSize: Long = 0L,
    val availableSize: Long = 0L,
    val usedSize: Long = 0L,
    val volumeType: StorageVolumeType = StorageVolumeType.UNKNOWN
) : Parcelable