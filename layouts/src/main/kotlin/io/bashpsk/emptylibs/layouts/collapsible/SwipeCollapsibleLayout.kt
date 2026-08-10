package io.bashpsk.emptylibs.layouts.collapsible

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.util.lerp

/**
 * A layout that provides a collapsible and swipable content sheet that can overlay a main content
 * area. The layout supports three main states: [CollapsibleLayoutProgress.Expanded],
 * [CollapsibleLayoutProgress.Collapsed] and [CollapsibleLayoutProgress.Dismissed].
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state The state of the collapsible layout.
 * @param collapsedContentWidthRatio The width ratio of the primary content in the collapsed state.
 * @param primaryContentHeightRatio The aspect ratio of the primary content.
 * @param backgroundLayer The background layer of the collapsible sheet.
 * @param primaryContent The primary content of the collapsible sheet.
 * @param secondaryContent The content shown next to the primary content when collapsed.
 * @param tertiaryContent The content shown below the primary content when expanded.
 * @param content The main content over which the collapsible sheet will be displayed.
 */
@Composable
fun SwipeCollapsibleLayout(
    modifier: Modifier = Modifier,
    state: SwipeCollapsibleLayoutState = rememberSwipeCollapsibleLayoutState(),
    @FloatRange(0.0, 1.0)
    collapsedContentWidthRatio: Float = SwipeCollapsibleLayoutDefault.PrimaryContentWidthRatio,
    @FloatRange(0.0, 1.0)
    primaryContentHeightRatio: Float = SwipeCollapsibleLayoutDefault.PrimaryContentHeightRatio,
    backgroundLayer: @Composable BoxScope.() -> Unit = {

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(color = MaterialTheme.colorScheme.background)
        )
    },
    primaryContent: @Composable BoxScope.() -> Unit = {},
    secondaryContent: @Composable BoxScope.() -> Unit = {},
    tertiaryContent: @Composable BoxScope.() -> Unit = {},
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {

    val density = LocalDensity.current

    val collapsedHeight by remember {
        derivedStateOf {
            val width = state.layoutSize.width
            val height = state.layoutSize.height
            if (width == 0 || height == 0) 0f else {
                (minOf(width, height) * collapsedContentWidthRatio) * primaryContentHeightRatio
            }
        }
    }

    val paddingValues by remember {
        derivedStateOf {
            PaddingValues(
                bottom = with(density) { (if (state.isVisible) collapsedHeight else 0f).toDp() }
            )
        }
    }

    RetainedEffect(state.layoutSize, collapsedHeight) {

        if (collapsedHeight > 0) {

            val newAnchors = DraggableAnchors {

                CollapsibleLayoutProgress.Expanded at 0F
                CollapsibleLayoutProgress.Collapsed at (state.layoutSize.height - collapsedHeight)
            }

            state.anchoredDraggableState.updateAnchors(newAnchors = newAnchors)
        }

        onRetire { }
    }

    Layout(
        modifier = modifier.onSizeChanged { size -> state.layoutSize = size },
        content = {

            content(paddingValues)

            if (state.isVisible) Layout(
                modifier = Modifier
                    .fillMaxWidth()
                    .anchoredDraggable(
                        state = state.anchoredDraggableState,
                        orientation = Orientation.Vertical,
                        flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                            state = state.anchoredDraggableState,
                            animationSpec = state.animationSpec
                        )
                    ),
                content = {

                    Box(
                        modifier = Modifier.layoutId(BackgroundContentId),
                        content = backgroundLayer
                    )

                    Box(
                        modifier = Modifier.layoutId(PrimaryContentId),
                        content = primaryContent
                    )

                    Box(
                        modifier = Modifier.layoutId(SecondaryContentId),
                        content = secondaryContent
                    )

                    Box(
                        modifier = Modifier.layoutId(TertiaryContentId),
                        content = tertiaryContent
                    )
                }
            ) { measurables, constraints ->

                val offset = state.anchoredDraggableState.offset
                val progress = if (offset.isNaN() || state.layoutSize.height <= collapsedHeight) {
                    0F
                } else {
                    1F - (offset / (state.layoutSize.height - collapsedHeight))
                        .coerceIn(0F..1F)
                }

                val currentCollapsedWidthPx = minOf(
                    a = state.layoutSize.width,
                    b = state.layoutSize.height
                ) * collapsedContentWidthRatio

                val currentCollapsedHeightPx = currentCollapsedWidthPx * primaryContentHeightRatio

                val interpolatedWidth = lerp(
                    start = currentCollapsedWidthPx,
                    stop = constraints.maxWidth.toFloat(),
                    fraction = progress
                ).toInt()

                val interpolatedHeight = lerp(
                    start = currentCollapsedHeightPx,
                    stop = constraints.maxWidth * primaryContentHeightRatio,
                    fraction = progress
                ).toInt()

                val secondaryWidth = (constraints.maxWidth - interpolatedWidth).coerceAtLeast(0)
                val secondaryHeight = currentCollapsedHeightPx.toInt()

                val tertiaryWidth = constraints.maxWidth
                val tertiaryHeight = (constraints.maxHeight - interpolatedHeight).coerceAtLeast(0)

                val backgroundPlaceable = measurables.first { it.layoutId == BackgroundContentId }
                    .measure(Constraints.fixed(constraints.maxWidth, constraints.maxHeight))

                val primaryPlaceable = measurables.first { it.layoutId == PrimaryContentId }
                    .measure(Constraints.fixed(interpolatedWidth, interpolatedHeight))

                val secondaryPlaceable = measurables.first { it.layoutId == SecondaryContentId }
                    .measure(Constraints.fixed(secondaryWidth, secondaryHeight))

                val tertiaryPlaceable = measurables.first { it.layoutId == TertiaryContentId }
                    .measure(Constraints.fixed(tertiaryWidth, tertiaryHeight))

                layout(width = constraints.maxWidth, height = constraints.maxHeight) {

                    backgroundPlaceable.place(x = 0, y = 0)

                    primaryPlaceable.place(x = 0, y = 0)

                    secondaryPlaceable.placeWithLayer(x = interpolatedWidth, y = 0) {

                        alpha = 1F - progress
                    }

                    tertiaryPlaceable.placeWithLayer(x = 0, y = interpolatedHeight) {

                        alpha = progress
                    }
                }
            }
        }
    ) { measurables, constraints ->

        val mainPlaceable = measurables[0].measure(constraints)

        val sheetPlaceable = if (measurables.size > 1) {

            val offset = state.anchoredDraggableState.offset
            val sheetHeight = if (offset.isNaN()) 0 else {
                (constraints.maxHeight - offset).toInt().coerceAtLeast(0)
            }
            measurables[1].measure(Constraints.fixed(constraints.maxWidth, sheetHeight))
        } else null

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {

            val sheetY = if (state.anchoredDraggableState.offset.isNaN()) {
                constraints.maxHeight
            } else state.anchoredDraggableState.offset.toInt()

            mainPlaceable.place(x = 0, y = 0)
            sheetPlaceable?.place(x = 0, y = sheetY)
        }
    }
}