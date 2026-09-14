package io.bashpsk.emptylibs.component.jetpackui

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
interface OptionBarData {

    val label: String

    val icon: ImageVector

    val enabled: Boolean

    companion object {

        fun OptionBarData.copy(
            label: String = this.label,
            icon: ImageVector = this.icon,
            enabled: Boolean = this.enabled
        ): OptionBarData = object : OptionBarData {

            override val label: String = label

            override val icon: ImageVector = icon

            override val enabled: Boolean = enabled
        }
    }
}