package io.bashpsk.emptylibs.component.layouts

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import io.bashpsk.emptylibs.R

@Immutable
data class VideoData(
    val title: String = "",
    val description: String = "",
    @DrawableRes
    val thumbnail: Int = R.drawable.thumbnail01
)