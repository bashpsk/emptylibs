package io.bashpsk.emptylibs.layouts.twopane

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import kotlin.math.min

/**
 * A layout that arranges two panes adaptively based on the available width.
 *
 * - In compact width, it places the `secondPane` below the `firstPane`.
 * - In medium or expanded width, it places the `secondPane` to the side of the `firstPane`.
 *
 * This layout is useful for master-detail screens or any UI that needs to adapt its
 * two-pane structure to different screen sizes. The `firstPane`'s aspect ratio is maintained.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param firstPane The content of the first pane.
 * @param secondPane The content of the second pane.
 * @param aspectRatio The aspect ratio to be maintained for the `firstPane`.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
inline fun TwoPaneAdaptiveLayout(
    modifier: Modifier = Modifier,
    aspectRatio: Float = 1.0F,
    crossinline firstPane: @Composable () -> Unit,
    crossinline secondPane: @Composable () -> Unit
) {

    val activity = LocalActivity.current
    val windowSizeClass = activity?.let { calculateWindowSizeClass(activity = it) }
    val windowWidthSize by remember(windowSizeClass) {
        derivedStateOf { windowSizeClass?.widthSizeClass ?: WindowWidthSizeClass.Compact }
    }

    Layout(
        modifier = modifier,
        content = {

            firstPane()
            secondPane()
        }
    ) { measurables, constraints ->

        require(measurables.size == 2) { "TwoPaneAdaptiveLayout requires exactly two children." }

        val (firstMeasurable, secondMeasurable) = measurables

        val firstPlaceable: Placeable
        val secondPlaceable: Placeable

        val (firstPlacementOffset, secondPlacementOffset) = when (windowWidthSize) {

            WindowWidthSizeClass.Compact -> {

                val firstPaneMaxHeight = (constraints.maxHeight * aspectRatio).toInt()
                val initialFirstPaneHeight = (constraints.maxWidth / aspectRatio).toInt()
                val firstPaneRenderHeight = min(initialFirstPaneHeight, firstPaneMaxHeight)
                val firstPaneRenderWidth = (firstPaneRenderHeight * aspectRatio).toInt()
                    .coerceIn(0, constraints.maxWidth)
                val finalFirstPaneHeight = (firstPaneRenderWidth / aspectRatio).toInt()
                    .coerceIn(0, firstPaneMaxHeight)

                firstPlaceable = firstMeasurable.measure(
                    Constraints.fixed(width = firstPaneRenderWidth, height = finalFirstPaneHeight)
                )

                secondPlaceable = secondMeasurable.measure(
                    Constraints.fixed(
                        width = constraints.maxWidth,
                        height = (constraints.maxHeight - finalFirstPaneHeight).coerceAtLeast(0)
                    )
                )

                IntOffset(
                    x = ((constraints.maxWidth - firstPaneRenderWidth) / 2).coerceAtLeast(0),
                    y = 0
                ) to IntOffset(x = 0, y = finalFirstPaneHeight)
            }

            else -> {

                val firstPaneMaxWidth = (constraints.maxWidth * aspectRatio).toInt()
                val initialFirstPaneWidth = (constraints.maxHeight * aspectRatio).toInt()
                val firstPaneRenderWidth = min(initialFirstPaneWidth, firstPaneMaxWidth)
                val firstPaneRenderHeight = (firstPaneRenderWidth / aspectRatio).toInt()
                    .coerceIn(0, constraints.maxHeight)
                val finalFirstPaneWidth = (firstPaneRenderHeight * aspectRatio).toInt()
                    .coerceIn(0, firstPaneMaxWidth)

                firstPlaceable = firstMeasurable.measure(
                    Constraints.fixed(width = finalFirstPaneWidth, height = firstPaneRenderHeight)
                )

                secondPlaceable = secondMeasurable.measure(
                    Constraints.fixed(
                        width = (constraints.maxWidth - finalFirstPaneWidth).coerceAtLeast(0),
                        height = constraints.maxHeight
                    )
                )

                IntOffset(
                    x = 0,
                    y = ((constraints.maxHeight - firstPaneRenderHeight) / 2).coerceAtLeast(0)
                ) to IntOffset(x = finalFirstPaneWidth, y = 0)
            }
        }

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {

            firstPlaceable.placeRelative(position = firstPlacementOffset)
            secondPlaceable.placeRelative(position = secondPlacementOffset)
        }
    }
}