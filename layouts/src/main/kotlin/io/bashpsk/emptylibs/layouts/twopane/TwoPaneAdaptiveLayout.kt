package io.bashpsk.emptylibs.layouts.twopane

import androidx.activity.compose.LocalActivity
import androidx.annotation.FloatRange
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

/**
 * A layout that arranges two panes adaptively based on the available width and a fraction ratio.
 *
 * - In compact width, it places the `secondPane` below the `firstPane` using `paneRatio` for height
 * splitting.
 * - In medium or expanded width, it places the `secondPane` to the side of the `firstPane` using
 * `paneRatio` for width splitting.
 *
 * This layout is useful for master-detail screens or any UI that needs to adapt its
 * two-pane structure to different screen sizes.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param firstPaneRatio The fraction of available space (width in side-by-side, height in stacked)
 * assigned to the `firstPane` (between 0.0 and 1.0).
 * @param firstPane The content of the first pane.
 * @param secondPane The content of the second pane.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun TwoPaneAdaptiveLayout(
    modifier: Modifier = Modifier,
    @FloatRange(0.0, 1.0)
    firstPaneRatio: Float = 0.5F,
    firstPane: @Composable () -> Unit,
    secondPane: @Composable () -> Unit
) {

    val activity = LocalActivity.current
    val windowSizeClass = activity?.let { calculateWindowSizeClass(activity = it) }
    val windowWidthSize by remember(windowSizeClass) {
        derivedStateOf { windowSizeClass?.widthSizeClass ?: WindowWidthSizeClass.Compact }
    }

    val boundedRatio = firstPaneRatio.coerceIn(0.0F..1.0F)

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

                val firstPaneHeight = (constraints.maxHeight * boundedRatio).toInt()
                val secondPaneHeight = (constraints.maxHeight - firstPaneHeight).coerceAtLeast(0)

                firstPlaceable = firstMeasurable.measure(
                    Constraints(
                        minWidth = constraints.minWidth,
                        maxWidth = constraints.maxWidth,
                        minHeight = firstPaneHeight,
                        maxHeight = firstPaneHeight
                    )
                )

                secondPlaceable = secondMeasurable.measure(
                    Constraints(
                        minWidth = constraints.minWidth,
                        maxWidth = constraints.maxWidth,
                        minHeight = secondPaneHeight,
                        maxHeight = secondPaneHeight
                    )
                )

                IntOffset(x = 0, y = 0) to IntOffset(x = 0, y = firstPaneHeight)
            }

            else -> {

                val firstPaneWidth = (constraints.maxWidth * boundedRatio).toInt()
                val secondPaneWidth = (constraints.maxWidth - firstPaneWidth).coerceAtLeast(0)

                firstPlaceable = firstMeasurable.measure(
                    Constraints(
                        minWidth = firstPaneWidth,
                        maxWidth = firstPaneWidth,
                        minHeight = constraints.minHeight,
                        maxHeight = constraints.maxHeight
                    )
                )

                secondPlaceable = secondMeasurable.measure(
                    Constraints(
                        minWidth = secondPaneWidth,
                        maxWidth = secondPaneWidth,
                        minHeight = constraints.minHeight,
                        maxHeight = constraints.maxHeight
                    )
                )

                IntOffset(x = 0, y = 0) to IntOffset(x = firstPaneWidth, y = 0)
            }
        }

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {

            firstPlaceable.placeRelative(position = firstPlacementOffset)
            secondPlaceable.placeRelative(position = secondPlacementOffset)
        }
    }
}
