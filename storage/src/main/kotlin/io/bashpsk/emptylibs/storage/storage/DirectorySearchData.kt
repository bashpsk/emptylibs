package io.bashpsk.emptylibs.storage.storage

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Represents the result of a directory search operation, encapsulating lists of subfolders
 * and files found within a specific path.
 *
 * This class is designed to be immutable and parcelable, making it suitable for state management
 * in Jetpack Compose and data transfer between Android components.
 *
 * @property folders An immutable list of [DirectoryData] representing the subfolders found.
 * Defaults to an empty persistent list.
 * @property files An immutable list of [FileData] representing the files found.
 * Defaults to an empty persistent list.
 */
@Immutable
@Parcelize
@Serializable
data class DirectorySearchData(
    val folders: ImmutableList<DirectoryData> = persistentListOf(),
    val files: ImmutableList<FileData> = persistentListOf()
) : Parcelable