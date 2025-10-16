package io.bashpsk.emptylibs.formatter.meter

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

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