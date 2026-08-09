package io.bashpsk.emptylibs.jetpackui.sevensegment

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

/**
 * A composable that displays a string of characters as a series of 7-segment displays.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param data The string to be displayed.
 * @param colors The colors to be used for the active and inactive segments.
 * @param properties The properties of the segments, such as thickness and spacing.
 * @param model A map of characters to their corresponding 7-segment data.
 */
@Composable
fun SevenSegmentDisplay(
    modifier: Modifier = Modifier,
    data: String,
    colors: SevenSegmentColors = SevenSegmentDefault.colors(),
    properties: SevenSegmentProperties = SevenSegmentDefault.properties(),
    model: ImmutableMap<Char, SevenSegmentData> = SevenSegmentDefault.SegmentDataModel
) {

    val segmentData by remember(data, model) {
        derivedStateOf { data.findSegmentData(model = model) }
    }

    SevenSegmentDisplay(
        modifier = modifier,
        data = segmentData,
        colors = colors,
        properties = properties
    )
}

/**
 * A composable that displays a single character as a 7-segment display.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param data The character to be displayed.
 * @param colors The colors to be used for the active and inactive segments.
 * @param properties The properties of the segments, such as thickness and spacing.
 * @param model A map of characters to their corresponding 7-segment data.
 */
@Composable
fun SevenSegmentDisplay(
    modifier: Modifier = Modifier,
    data: Char?,
    colors: SevenSegmentColors = SevenSegmentDefault.colors(),
    properties: SevenSegmentProperties = SevenSegmentDefault.properties(),
    model: ImmutableMap<Char, SevenSegmentData> = SevenSegmentDefault.SegmentDataModel
) {

    val segmentData by remember(data, model) {
        derivedStateOf { data.findSegmentData(model = model) }
    }

    val aspectRatio by remember(properties, segmentData) {
        derivedStateOf {
            val ratio = properties.aspectRatio / 2F
            if (segmentData.hasDotOrColon()) ratio / 3.5F else ratio
        }
    }

    Canvas(
        modifier = modifier
            .aspectRatio(ratio = aspectRatio)
            .clipToBounds(),
        contentDescription = "7 Segment Display"
    ) {

        when (segmentData.hasDotOrColon()) {

            true -> drawDotElements(
                data = segmentData,
                colors = colors,
                properties = properties
            )

            false -> drawSegmentElements(
                data = segmentData,
                colors = colors,
                properties = properties
            )
        }
    }
}

/**
 * A composable that displays a list of 7-segment data as a series of 7-segment displays.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param data The list of 7-segment data to be displayed.
 * @param colors The colors to be used for the active and inactive segments.
 * @param properties The properties of the segments, such as thickness and spacing.
 */
@Composable
private fun SevenSegmentDisplay(
    modifier: Modifier = Modifier,
    data: ImmutableList<SevenSegmentData>,
    colors: SevenSegmentColors = SevenSegmentDefault.colors(),
    properties: SevenSegmentProperties = SevenSegmentDefault.properties()
) {

    val itemSize = properties.width
    val itemSpace = properties.itemSpace
    val itemWidth = itemSize * (properties.aspectRatio / 2F)

    val totalContentWidth by remember(data, properties) {
        derivedStateOf {
            data.fold(0.dp) { acc, segment ->

                acc + if (segment.hasDotOrColon()) itemWidth / 3.5F else itemWidth
            } + (itemSpace * (data.size - 1).coerceAtLeast(0))
        }
    }

    Canvas(
        modifier = modifier
            .size(width = totalContentWidth, height = itemSize)
            .clipToBounds(),
        contentDescription = "7 Segment Display"
    ) {

        var horizontalOffset = 0F
        val itemSpacePx = itemSpace.toPx()

        data.forEach { segment ->

            val segmentWidth = (if (segment.hasDotOrColon()) itemWidth / 3.5F else itemWidth).toPx()

            inset(
                left = horizontalOffset,
                top = 0F,
                right = size.width - (horizontalOffset + segmentWidth),
                bottom = size.height - itemSize.toPx()
            ) {

                when (segment.hasDotOrColon()) {

                    true -> drawDotElements(
                        data = segment,
                        colors = colors,
                        properties = properties
                    )

                    else -> drawSegmentElements(
                        data = segment,
                        colors = colors,
                        properties = properties
                    )
                }
            }

            horizontalOffset += segmentWidth + itemSpacePx
        }
    }
}