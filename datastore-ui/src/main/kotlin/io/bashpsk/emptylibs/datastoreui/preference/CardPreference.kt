package io.bashpsk.emptylibs.datastoreui.preference

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import io.bashpsk.emptylibs.datastoreui.component.PreferenceSummary

/**
 * A Composable function that displays a card-style preference item.
 *
 * This function utilizes the [ListItem] Composable to create a visually distinct preference item
 * with a title, an optional summary, optional leading and trailing content, and click handling.
 *
 * @param modifier Optional [Modifier] for theming and behavior of this preference.
 * @param title A lambda function that returns the string to be displayed as the main title of the
 * preference.
 * @param summary A lambda function that returns the string to be displayed as the summary or
 * description below the title. Defaults to an empty string if not provided.
 * @param leadingContent A Composable lambda function that defines the content to be displayed at
 * the beginning of the preference item (e.g., an icon). Defaults to an empty Composable.
 * @param trailingContent A Composable lambda function that defines the content to be displayed at
 * the end of the preference item (e.g., a switch or a chevron). Defaults to an empty Composable.
 * @param colors [ListItemColors] to be used for this list item.
 * @param tonalElevation The tonal elevation of this list item.
 * @param shadowElevation The shadow elevation of this list item.
 * @param onClick A lambda function to be executed when the preference item is clicked. Defaults to
 * an empty lambda.
 */
@Composable
fun CardPreference(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    summary: @Composable () -> Unit = { PreferenceSummary() },
    leadingContent: @Composable () -> Unit = {},
    trailingContent: @Composable () -> Unit = {},
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    onClick: () -> Unit = {}
) {

    ListItem(
        modifier = modifier.clickable(role = Role.Button, onClick = onClick),
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        headlineContent = title,
        supportingContent = summary
    )
}