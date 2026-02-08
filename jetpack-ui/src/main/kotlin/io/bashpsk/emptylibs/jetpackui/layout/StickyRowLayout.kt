package io.bashpsk.emptylibs.jetpackui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth

/**
 * A custom row layout that keeps its first child "sticky" at a specified horizontal scroll
 * position.
 *
 * This layout is specifically designed for scenarios like code editors or log viewers where line
 * numbers (the first child) should remain visible while the text content (the remaining children)
 * scrolls horizontally.
 *
 * The layout implements dynamic clipping to ensure that subsequent children do not bleed under the
 * sticky first child when their backgrounds are transparent. It also ensures the row is wide enough
 * to maintain the sticky element's visibility even for short content.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param horizontalScroll The current horizontal scroll offset in pixels, typically obtained
 * from a [androidx.compose.foundation.ScrollState].
 * @param horizontalArrangement The horizontal arrangement of the children.
 * Defaults to [Arrangement.Start].
 * @param verticalAlignment The vertical alignment of the children.
 * Defaults to [Alignment.Top].
 * @param content The children composables. The first child in the [content] lambda will be treated
 * as the sticky element.
 */
@Composable
inline fun StickyRowLayout(
    modifier: Modifier = Modifier,
    horizontalScroll: Int = 0,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    crossinline content: @Composable () -> Unit = {}
) {

    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        if (measurables.isEmpty()) return@Layout layout(
            width = constraints.minWidth,
            height = constraints.minHeight
        ) {}

        val horizontalSpace = horizontalArrangement.spacing.roundToPx()
        val stickyPlaceable = measurables[0].measure(constraints.copy(minWidth = 0))
        val remainingWidth = when {
            constraints.hasBoundedWidth -> (constraints.maxWidth - stickyPlaceable.width
                    - horizontalSpace).coerceAtLeast(0)

            else -> Constraints.Infinity
        }
        val otherPlaceables = measurables.drop(1).map { measurable ->
            measurable.measure(constraints.copy(minWidth = 0, maxWidth = remainingWidth))
        }
        val allPlaceables = listOf(stickyPlaceable) + otherPlaceables
        val totalChildrenWidth = allPlaceables.sumOf { placeable ->
            placeable.width
        } + (allPlaceables.size - 1).coerceAtLeast(0) * horizontalSpace
        val minimumStickyWidth = horizontalScroll + stickyPlaceable.width
        val width = constraints.constrainWidth(maxOf(totalChildrenWidth, minimumStickyWidth))
        val height = constraints.constrainHeight(
            allPlaceables.maxOfOrNull { placeable -> placeable.height } ?: 0
        )

        layout(width = width, height = height) {

            val sizes = IntArray(allPlaceables.size) { index -> allPlaceables[index].width }
            val positions = IntArray(allPlaceables.size)

            with(horizontalArrangement) {
                arrange(
                    totalSize = width,
                    sizes = sizes,
                    layoutDirection = layoutDirection,
                    outPositions = positions
                )
            }

            val stickyWidth = stickyPlaceable.width
            val stickyEndBoundary = horizontalScroll + stickyWidth + horizontalSpace

            (1 until allPlaceables.size).forEach { itemIndex ->

                val placeable = allPlaceables[itemIndex]
                val x = positions[itemIndex]
                val y = verticalAlignment.align(placeable.height, height)

                (stickyEndBoundary - x).coerceIn(0..placeable.width).takeIf { overlap ->

                    overlap < placeable.width
                }?.let { overlap ->

                    when (overlap > 0) {

                        true -> placeable.placeRelativeWithLayer(x = x, y = y) {

                            val clipShape = object : Shape {

                                override fun createOutline(
                                    size: Size,
                                    layoutDirection: LayoutDirection,
                                    density: Density
                                ): Outline {

                                    return Outline.Rectangle(
                                        rect = Rect(
                                            left = overlap.toFloat(),
                                            top = 0F,
                                            right = size.width,
                                            bottom = size.height
                                        )
                                    )
                                }
                            }

                            clip = true
                            shape = clipShape
                        }

                        else -> placeable.placeRelative(x = x, y = y)
                    }
                }
            }

            val stickyX = horizontalScroll.coerceIn(
                positions[0]..(width - stickyWidth).coerceAtLeast(positions[0])
            )
            val stickyY = verticalAlignment.align(stickyPlaceable.height, height)

            stickyPlaceable.placeRelative(x = stickyX, y = stickyY)
        }
    }
}