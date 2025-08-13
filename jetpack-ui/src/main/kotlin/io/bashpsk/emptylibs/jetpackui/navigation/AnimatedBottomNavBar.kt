package io.bashpsk.jetpackui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A composable function that creates an animated bottom navigation bar.
 *
 * This component displays a row of actions at the bottom of the screen, typically used for primary
 * navigation. It provides customization options for its appearance and behavior.
 *
 * @param modifier The modifier to be applied to the bottom navigation bar.
 * @param containerColor The background color of the bottom navigation bar.
 * @param contentColor The color of the content (icons and text) within the bottom navigation bar.
 * @param tonalElevation The tonal elevation of the bottom navigation bar. Defaults to `0.dp`.
 * @param actions A composable lambda that defines the content of the bottom navigation bar,
 * typically a row of `NavigationBarItem`s or custom action items. This lambda is invoked within a
 * `RowScope`, allowing direct use of `RowScope` modifiers.
 */
@Composable
fun AnimatedBottomNavBar(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    tonalElevation: Dp = 0.dp,
    actions: @Composable RowScope.() -> Unit = {},
) {

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(insets = WindowInsets.navigationBars),
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            actions()
        }
    }
}