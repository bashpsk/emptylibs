package io.bashpsk.jetpackui.optionbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents the data for option bar.
 *
 * This data class holds the information needed to display and manage a single option
 * within an option bar UI component.
 *
 * @property label The text label displayed for the option.
 * @property icon The vector graphic icon displayed for the option..
 * @property isEnable A boolean indicating whether the option is currently enabled or disabled.
 * Disabled options are typically visually distinct and non-interactive.
 */
@Immutable
data class OptionBarData(
    val label: String = "Label",
    val icon: ImageVector = Icons.Filled.Info,
    val isEnable: Boolean = true
)