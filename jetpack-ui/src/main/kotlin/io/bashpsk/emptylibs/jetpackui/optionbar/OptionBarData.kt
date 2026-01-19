package io.bashpsk.emptylibs.jetpackui.optionbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents the data for option bar.
 *
 * This data class holds the information needed to display and manage a single option
 * within an option bar UI component.
 *
 * @property label The text label displayed for the option.
 * @property icon The vector graphic icon displayed for the option..
 * @property enabled A boolean indicating whether the option is currently enabled or disabled.
 * Disabled options are typically visually distinct and non-interactive.
 */
@Stable
interface OptionBarData {

    val label: String

    val icon: ImageVector

    val enabled: Boolean

    companion object {

        /**
         * A default implementation of [OptionBarData] representing a "More" or overflow option.
         *
         * This object is typically used to trigger a menu or additional actions that do not fit
         * directly in the primary option bar space.
         */
        val More = object : OptionBarData {

            override val label: String = "More"

            override val icon: ImageVector = Icons.Filled.MoreVert

            override val enabled: Boolean = true
        }

        /**
         * Creates a copy of the [OptionBarData] with the specified changes.
         *
         * @param label The new text label. Defaults to the current label.
         * @param icon The new icon. Defaults to the current icon.
         * @param enabled The new enabled state. Defaults to the current state.
         * @return A new [OptionBarData] instance with the updated properties.
         */
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