package io.bashpsk.emptylibs.jetpackui.layout

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

        val maxWidth = constraints.maxWidth
        val maxHeight = constraints.maxHeight

        val (firstPlacementOffset, secondPlacementOffset) = when (windowWidthSize) {

            WindowWidthSizeClass.Compact -> {

                var firstWidth = maxWidth
                var firstHeight = (firstWidth / aspectRatio).toInt()

                if (firstHeight > maxHeight) {
                    firstHeight = maxHeight
                    firstWidth = (firstHeight * aspectRatio).toInt()
                }

                firstPlaceable = firstMeasurable.measure(
                    Constraints.fixed(width = firstWidth, height = firstHeight)
                )

                secondPlaceable = secondMeasurable.measure(
                    Constraints.fixed(
                        width = maxWidth,
                        height = (maxHeight - firstHeight).coerceAtLeast(0)
                    )
                )

                IntOffset(
                    x = (maxWidth - firstWidth) / 2,
                    y = 0
                ) to IntOffset(x = 0, y = firstHeight)
            }

            else -> {

                var firstHeight = maxHeight
                var firstWidth = (firstHeight * aspectRatio).toInt()

                if (firstWidth > maxWidth) {
                    firstWidth = maxWidth
                    firstHeight = (firstWidth / aspectRatio).toInt()
                }

                firstPlaceable = firstMeasurable.measure(
                    Constraints.fixed(width = firstWidth, height = firstHeight)
                )

                secondPlaceable = secondMeasurable.measure(
                    Constraints.fixed(
                        width = (maxWidth - firstWidth).coerceAtLeast(0),
                        height = maxHeight
                    )
                )

                IntOffset(
                    x = 0,
                    y = (maxHeight - firstHeight) / 2
                ) to IntOffset(x = firstWidth, y = 0)
            }
        }

        layout(width = maxWidth, height = maxHeight) {

            firstPlaceable.placeRelative(position = firstPlacementOffset)
            secondPlaceable.placeRelative(position = secondPlacementOffset)
        }
    }
}