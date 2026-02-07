package io.bashpsk.emptylibs.imagekolor.svg

import androidx.compose.runtime.Immutable

@Immutable
data class SvgKolorData(
    val id: Int = 0,
    val oldHex: String = "",
    val newHex: String = ""
)