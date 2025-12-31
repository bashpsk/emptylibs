package io.bashpsk.emptylibs.formatter.meter

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Represents a snapshot of file transfer or processing speed data.
 *
 * This data class is immutable and holds information about the progress, speed,
 * and estimated time of completion for a file-related operation. It is designed
 * to be used in UI layers, particularly with Jetpack Compose, as indicated by
 * the `@Immutable` annotation. It is also `Parcelable` for easy transport between
 * Android components and `Serializable` for persistence or network transfer.
 *
 * @property total The total size of the file or data being processed, in bytes.
 * @property current The amount of data that has been processed so far, in bytes.
 * @property remaining The amount of data remaining to be processed, in bytes.
 * @property speed The current transfer or processing speed, in bytes per second.
 * @property eta The estimated time of arrival (ETA) until completion, in seconds.
 */
@Immutable
@Parcelize
@Serializable
data class FileSpeedData(
    val total: Long = 0,
    val current: Long = 0,
    val remaining: Long = 0,
    val speed: Long = 0,
    val eta: Long = 0
) : Parcelable