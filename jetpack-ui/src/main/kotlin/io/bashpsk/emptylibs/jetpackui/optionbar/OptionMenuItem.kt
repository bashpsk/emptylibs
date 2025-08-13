package io.bashpsk.jetpackui.optionbar

import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * A composable function that displays a single option item within a dropdown menu.
 *
 * This item typically consists of an icon, a label, and an action to be performed on click.
 * It's designed to be used as part of an `OptionBar` or similar dropdown structure.
 *
 * @param modifier Optional [Modifier] to be applied to the `DropdownMenuItem`.
 * @param optionData The [OptionBarData] containing the information for this menu item,
 *   such as its label, icon, and enabled state. Defaults to an empty [OptionBarData].
 * @param onOptionClick A lambda function that will be invoked when this menu item is clicked.
 *   It receives the `optionData` of the clicked item as a parameter. Defaults to an empty lambda.
 */
@Composable
internal fun OptionMenuItem(
    modifier: Modifier = Modifier,
    optionData: OptionBarData = OptionBarData(),
    onOptionClick: (option: OptionBarData) -> Unit = {}
) {

    DropdownMenuItem(
        modifier = modifier,
        enabled = optionData.isEnable,
        text = {

            Text(
                text = optionData.label,
                textAlign = TextAlign.Start,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {

            Icon(
                modifier = Modifier.size(size = 20.dp),
                imageVector = optionData.icon,
                contentDescription = optionData.label
            )
        },
        onClick = {

            onOptionClick(optionData)
        }
    )
}