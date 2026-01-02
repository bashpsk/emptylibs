package io.bashpsk.emptylibs.jetpackui.sevensegment

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
 */
@Composable
fun SevenSegmentDisplay(
    modifier: Modifier = Modifier,
    data: String,
    colors: SevenSegmentColors = SevenSegmentDefault.colors(),
    properties: SevenSegmentProperties = SevenSegmentDefault.properties(),
    model: ImmutableMap<Char, SevenSegmentData> = SevenSegmentDefault.SegmentDataModel,
    itemSize: Dp = 64.dp,
    itemSpace: Dp = 4.dp
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
        itemSpace = itemSpace
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

    SevenSegmentDisplay(
        modifier = modifier,
        data = segmentData,
        colors = colors,
        properties = properties
    )
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
 */
@Composable
fun SevenSegmentDisplay(
    modifier: Modifier = Modifier,
    data: ImmutableList<SevenSegmentData>,
    colors: SevenSegmentColors = SevenSegmentDefault.colors(),
    properties: SevenSegmentProperties = SevenSegmentDefault.properties(),
    itemSize: Dp = 64.dp,
    itemSpace: Dp = 4.dp
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

            SevenSegmentDisplay(
                modifier = Modifier.size(width = itemSize / 2, height = itemSize),
                data = segmentData,
                colors = colors,
                properties = properties
            )
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
fun SevenSegmentDisplay(
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

        drawSegmentElement(data = data, colors = colors, properties = properties)
    }
}