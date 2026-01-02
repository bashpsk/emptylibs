package io.bashpsk.emptylibs.jetpackui.sevensegment

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Dp
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
 * @param itemSize The size of each 7-segment display item.
 * @param itemSpace The space between each 7-segment display item.
 * @param radius The radius of dot/colon.
 */
@Composable
fun SevenSegmentDisplay(
    modifier: Modifier = Modifier,
    data: String,
    colors: SevenSegmentColors = SevenSegmentDefault.colors(),
    properties: SevenSegmentProperties = SevenSegmentDefault.properties(),
    model: ImmutableMap<Char, SevenSegmentData> = SevenSegmentDefault.SegmentDataModel,
    itemSize: Dp = 64.dp,
    itemSpace: Dp = 4.dp,
    @FloatRange(from = 0.0, to = 1.0)
    radius: Float = 0.0F
) {

    val segmentData by remember(data, model) {
        derivedStateOf { data.findSegmentData(model = model) }
    }

    SevenSegmentDisplay(
        modifier = modifier,
        data = segmentData,
        colors = colors,
        properties = properties,
        itemSize = itemSize,
        itemSpace = itemSpace,
        radius = radius
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
 * @param radius The radius of dot/colon.
 */
@Composable
fun SevenSegmentDisplay(
    modifier: Modifier = Modifier,
    data: Char?,
    colors: SevenSegmentColors = SevenSegmentDefault.colors(),
    properties: SevenSegmentProperties = SevenSegmentDefault.properties(),
    model: ImmutableMap<Char, SevenSegmentData> = SevenSegmentDefault.SegmentDataModel,
    @FloatRange(from = 0.0, to = 1.0)
    radius: Float = 0.0F
) {

    val segmentData by remember(data, model) {
        derivedStateOf { data.findSegmentData(model = model) }
    }

    segmentData.takeIf { segment -> segment.hasDotOrColon() }?.let { segment ->

        SevenSegmentDot(
            modifier = modifier,
            data = segment,
            colors = colors,
            properties = properties,
            radius = radius
        )
    } ?: run {

        SevenSegmentNumber(
            modifier = modifier,
            data = segmentData,
            colors = colors,
            properties = properties
        )
    }
}

/**
 * A composable that displays a list of 7-segment data as a series of 7-segment displays.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param data The list of 7-segment data to be displayed.
 * @param colors The colors to be used for the active and inactive segments.
 * @param properties The properties of the segments, such as thickness and spacing.
 * @param itemSize The size of each 7-segment display item.
 * @param itemSpace The space between each 7-segment display item.
 * @param radius The radius of dot/colon.
 */
@Composable
private fun SevenSegmentDisplay(
    modifier: Modifier = Modifier,
    data: ImmutableList<SevenSegmentData>,
    colors: SevenSegmentColors = SevenSegmentDefault.colors(),
    properties: SevenSegmentProperties = SevenSegmentDefault.properties(),
    itemSize: Dp = 64.dp,
    itemSpace: Dp = 4.dp,
    @FloatRange(from = 0.0, to = 1.0)
    radius: Float = 0.0F
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = itemSpace,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        data.forEach { segmentData ->

            segmentData.takeIf { segment -> segment.hasDotOrColon() }?.let { segment ->

                SevenSegmentDot(
                    modifier = Modifier.size(width = itemSize / 4, height = itemSize),
                    data = segment,
                    colors = colors,
                    properties = properties,
                    radius = radius
                )
            } ?: run {

                SevenSegmentNumber(
                    modifier = Modifier.size(width = itemSize / 2, height = itemSize),
                    data = segmentData,
                    colors = colors,
                    properties = properties
                )
            }
        }
    }
}

/**
 * A composable that displays a single 7-segment data.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param data The 7-segment data to be displayed.
 * @param colors The colors to be used for the active and inactive segments.
 * @param properties The properties of the segments, such as thickness and spacing.
 */
@Composable
private fun SevenSegmentNumber(
    modifier: Modifier = Modifier,
    data: SevenSegmentData = SevenSegmentData.Empty,
    colors: SevenSegmentColors = SevenSegmentDefault.colors(),
    properties: SevenSegmentProperties = SevenSegmentDefault.properties()
) {

    Canvas(
        modifier = modifier
            .aspectRatio(ratio = 0.5F)
            .clipToBounds(),
        contentDescription = "7 Segment Display"
    ) {

        drawSegmentElements(data = data, colors = colors, properties = properties)
    }
}

/**
 * A composable that displays a dot or a colon for a 7-segment display.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param data The 7-segment data to be displayed, which should be a dot or a colon.
 * @param colors The colors to be used for the active and inactive segments.
 * @param properties The properties of the segments, such as thickness.
 * @param radius The corner radius of the dots, as a fraction of the dot size.
 */
@Composable
private fun SevenSegmentDot(
    modifier: Modifier = Modifier,
    data: SevenSegmentData = SevenSegmentData.Empty,
    colors: SevenSegmentColors = SevenSegmentDefault.colors(),
    properties: SevenSegmentProperties = SevenSegmentDefault.properties(),
    @FloatRange(from = 0.0, to = 1.0)
    radius: Float = 0.0F
) {

    Canvas(
        modifier = modifier
            .aspectRatio(ratio = 0.25F)
            .clipToBounds(),
        contentDescription = "7 Segment Dot"
    ) {

        drawDotElements(data = data, colors = colors, properties = properties, radius = radius)
    }
}