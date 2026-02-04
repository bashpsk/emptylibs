package io.bashpsk.emptylibs.pdftemplate.sheet

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable

@Immutable
data class SheetMargin(
    @param:FloatRange(0.0, 0.45)
    val left: Float = 0F,
    @param:FloatRange(0.0, 0.45)
    val top: Float = 0F,
    @param:FloatRange(0.0, 0.45)
    val right: Float = 0F,
    @param:FloatRange(0.0, 0.45)
    val bottom: Float = 0F
) {

    companion object {

        val Default = SheetMargin(left = 0.05F, top = 0.05F, right = 0.05F, bottom = 0.05F)
    }
}