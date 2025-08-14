package io.bashpsk.emptylibs.jetpackui.textpicker

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.times
import kotlin.math.abs

/**
 * A composable function that creates a wheel-style text picker.
 *
 * This picker allows users to select an item from a list of text by scrolling through them in a
 * wheel-like interface.
 * The currently selected item is highlighted, and items further from the center are scaled down and
 * have reduced opacity.
 *
 * @param T The type of items in the text list.
 * @param modifier The modifier to be applied to the picker.
 * @param state The state object that holds the list of text items and the currently selected item.
 * See [WheelTextPickerState].
 * @param visibleCount The number of items visible in the wheel at any given time. Should be an odd
 * number for symmetrical appearance.
 * @param itemSpace The vertical spacing between items in the wheel.
 * @param textStyle The [TextStyle] to be applied to the text items.
 * @param textColor The color of the text items.
 * @param textWeight The [FontWeight] of the text items.
 * @param textScaleLevel A factor determining how much items are scaled down as they move away from
 * the center.
 * A value of 0 means no scaling, while a higher value means more pronounced scaling.
 * @param textAlphaLevel A factor determining how much the opacity of items is reduced as they move
 * away from the center.
 * A value of 0 means no alpha change, while a higher value means more pronounced fading.
 * @param dividerFraction The fraction of the picker's width that the highlight dividers will
 * occupy.
 * @param dividerColor The color of the highlight dividers.
 * @param dividerThickness The thickness of the highlight dividers.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun <T> WheelTextPicker(
    modifier: Modifier = Modifier,
    state: WheelTextPickerState<T>,
    visibleCount: Int = 3,
    itemSpace: Dp = 16.dp,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    textWeight: FontWeight = FontWeight.Normal,
    textScaleLevel: Float = 0.4F,
    textAlphaLevel: Float = 0.7F,
    dividerFraction: Float = 0.5F,
    dividerColor: Color = MaterialTheme.colorScheme.primary,
    dividerThickness: Dp = 3.dp
) {

    val density = LocalDensity.current
    val lazyListState = rememberLazyListState()

    val itemHeight = with(density) {
        (textStyle.lineHeight.takeIf { height -> height.isSpecified }?.toDp()
            ?: (textStyle.fontSize.toDp() * 1.6F)) + (2 * itemSpace)
    }

    val itemHeightPx = with(density) { itemHeight.toPx() }
    val centerIndex = visibleCount / 2

    val scrollPosition by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset
        }
    }

    val nearestIndex by remember {
        derivedStateOf {

            val layoutInfo = lazyListState.layoutInfo
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2

            layoutInfo.visibleItemsInfo.minByOrNull { item ->

                abs((item.offset + item.size / 2) - viewportCenter)
            }?.index?.coerceIn(0..state.textList.lastIndex)
        }
    }

    LaunchedEffect(nearestIndex) {

        nearestIndex?.let(block = state::updateSelectedTextFromIndex)
    }

    BoxWithConstraints(
        modifier = modifier.height(height = itemHeight * visibleCount),
        contentAlignment = Alignment.Center
    ) {

        LazyColumn(
            modifier = Modifier.matchParentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            state = lazyListState,
            contentPadding = PaddingValues(vertical = itemHeight * centerIndex),
            flingBehavior = rememberSnapFlingBehavior(lazyListState)
        ) {

            itemsIndexed(
                items = state.textList,
                key = { index, textItem -> "$index. $textItem" }
            ) { index, textItem ->

                val (firstIndex, firstOffset) = scrollPosition
                val offsetPx = (index - firstIndex) * itemHeightPx - firstOffset
                val distanceFromCenter = offsetPx / itemHeightPx
                val distanceNormalized = abs(distanceFromCenter).coerceIn(0.0F..1.0F)

                val scaleAnimation by animateFloatAsState(
                    targetValue = 1.2F - textScaleLevel * distanceNormalized,
                    animationSpec = tween(durationMillis = 50, easing = FastOutSlowInEasing),
                    label = "Scale Animation"
                )

                val alphaAnimation by animateFloatAsState(
                    targetValue = 1.0F - textAlphaLevel * distanceNormalized,
                    animationSpec = tween(durationMillis = 50, easing = FastOutSlowInEasing),
                    label = "Alpha Animation"
                )

                WheelItemView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .padding(horizontal = 4.dp, vertical = itemSpace)
                        .graphicsLayer(
                            scaleX = scaleAnimation,
                            scaleY = scaleAnimation,
                            rotationX = distanceFromCenter * -25.0F,
                            alpha = alphaAnimation
                        ),
                    text = textItem,
                    textStyle = textStyle,
                    textColor = textColor,
                    textWeight = textWeight
                )
            }
        }

        HighlightDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = itemHeight),
            dividerFraction = dividerFraction,
            dividerColor = dividerColor,
            dividerThickness = dividerThickness
        )
    }
}

/**
 * Composable function that represents a single item in the wheel picker.
 *
 * @param modifier Modifier for this composable.
 * @param text The text to display for this item.
 * @param textStyle The text style to apply to the text.
 * @param textColor The color of the text.
 * @param textWeight The font weight of the text.
 */
@Composable
private fun <T> WheelItemView(
    modifier: Modifier = Modifier,
    text: T,
    textStyle: TextStyle,
    textColor: Color,
    textWeight: FontWeight
) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "$text",
            textAlign = TextAlign.Center,
            style = textStyle,
            color = textColor,
            fontWeight = textWeight
        )
    }
}

/**
 * A composable function that displays two horizontal dividers with rounded corners.
 * These dividers are used to highlight the selected item in the WheelTextPicker.
 *
 * @param modifier The modifier to be applied to the Column.
 * @param dividerFraction The fraction of the width that the dividers should occupy.
 * @param dividerColor The color of the dividers.
 * @param dividerThickness The thickness of the dividers.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun HighlightDivider(
    modifier: Modifier = Modifier,
    dividerFraction: Float = 0.4F,
    dividerColor: Color = MaterialTheme.colorScheme.primary,
    dividerThickness: Dp = 3.dp
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(fraction = dividerFraction)
                .clip(shape = CircleShape),
            color = dividerColor,
            thickness = dividerThickness
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(fraction = dividerFraction)
                .clip(shape = CircleShape),
            color = dividerColor,
            thickness = dividerThickness
        )
    }
}