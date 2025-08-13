package io.bashpsk.jetpackui.optionbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Composable function to display a single option item in the option bar.
 *
 * This function creates a visual representation of an option, typically including an icon and a
 * label. It's designed to be used within a larger option bar structure.
 *
 * @param modifier Optional [Modifier] to be applied to the composable.
 * @param optionData The [OptionBarData] object containing the icon, label, and enabled state for
 * this item.
 * @param onOptionClick A lambda function that will be invoked when this option item is clicked.
 * It receives the [OptionBarData] of the clicked item as a parameter.
 */
@Composable
internal fun OptionBarItem(
    modifier: Modifier = Modifier,
    optionData: OptionBarData = OptionBarData(),
    onOptionClick: (option: OptionBarData) -> Unit = {}
) {

    val cardColors = CardDefaults.cardColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.50F)
    )

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        enabled = optionData.isEnable,
        colors = cardColors,
        onClick = {

            onOptionClick(optionData)
        }
    ) {

        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                modifier = Modifier.size(size = 20.dp),
                imageVector = optionData.icon,
                contentDescription = optionData.label
            )

            Spacer(modifier = Modifier.height(height = 8.dp))

            Text(
                text = optionData.label,
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}