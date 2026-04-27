package io.bashpsk.emptylibs.jetpackui.picker

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.times
import kotlin.math.abs
import kotlin.math.floor

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
    itemSpace: Dp = 12.dp,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    textScaleLevel: Float = 0.4F,
    textAlphaLevel: Float = 0.7F,
    dividerFraction: Float = 0.4F,
    dividerColor: Color = MaterialTheme.colorScheme.primary,
    dividerThickness: Dp = 3.dp
) {

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val itemHeight by remember(density, textStyle, itemSpace) {
        derivedStateOf {
            with(density) {
                (textStyle.lineHeight.takeIf { textUnit ->

                    textUnit.isSpecified
                }?.toDp() ?: (textStyle.fontSize.toDp() * 1.6F)) + (2 * itemSpace)
            }
        }
    }

    val itemHeightPx by remember(density, itemHeight) {
        derivedStateOf { with(density) { itemHeight.toPx() } }
    }

    val pickerTotalHeight by remember(density, itemHeight, visibleCount) {
        derivedStateOf { with(density) { (itemHeight * visibleCount).toPx() } }
    }

    val pickerCenterY by remember(pickerTotalHeight) {
        derivedStateOf { pickerTotalHeight / 2F }
    }

    LaunchedEffect(state, itemHeightPx, pickerCenterY) {

        state.setInitialScroll(itemHeight = itemHeightPx, pickerCenterY = pickerCenterY)
    }

    LaunchedEffect(state.animatable.value, itemHeightPx, pickerCenterY) {

        state.updateSelectedText(itemHeight = itemHeightPx, pickerCenterY = pickerCenterY)
    }

    val draggableState = rememberDraggableState { delta ->

        state.onScroll(delta = delta)
    }

    val draggableModifier = Modifier.draggable(
        state = draggableState,
        orientation = Orientation.Vertical,
        onDragStopped = { velocity ->

            state.onFling(
                velocity = velocity,
                itemHeight = itemHeightPx,
                pickerCenterY = pickerCenterY
            )
        }
    )

    BoxWithConstraints(
        modifier = modifier
            .height(itemHeight * visibleCount)
            .then(draggableModifier),
        contentAlignment = Alignment.Center
    ) {

        val canvasWidth = constraints.maxWidth.toFloat()

        Canvas(
            modifier = Modifier
                .matchParentSize()
                .clipToBounds(),
            contentDescription = "Wheel Text Picker"
        ) {

            state.textList.takeIf { itemsList -> itemsList.isNotEmpty() }?.let { itemsList ->

                val firstVisibleIndex = floor(
                    (state.animatable.value - itemHeightPx) / itemHeightPx
                ).toInt().coerceAtLeast(0)

                val lastVisibleIndex = floor(
                    (pickerTotalHeight + state.animatable.value + itemHeightPx) / itemHeightPx
                ).toInt().coerceAtMost(itemsList.lastIndex)

                (firstVisibleIndex..lastVisibleIndex).forEach { index ->

                    val itemCenterY = index * itemHeightPx + itemHeightPx / 2f
                    val itemDistance = (itemCenterY - state.animatable.value) - pickerCenterY
                    val distanceNormalized = abs(itemDistance / itemHeightPx).coerceIn(
                        0.0F..(visibleCount / 2f) + 0.5f
                    )

                    val scale = (1.2F - textScaleLevel * distanceNormalized).coerceAtLeast(0.1f)
                    val alpha = (1.0F - textAlphaLevel * distanceNormalized).coerceAtLeast(0.1f)

                    val textLayoutResult = textMeasurer.measure(
                        text = "${itemsList[index]}",
                        style = textStyle.copy(color = textStyle.color.copy(alpha = alpha))
                    )

                    val textPosition = Offset(
                        (canvasWidth - textLayoutResult.size.width) / 2f,
                        (index * itemHeightPx) - state.animatable.value + itemSpace.toPx()
                    )

                    withTransform(
                        transformBlock = {
                            translate(
                                left = textPosition.x + textLayoutResult.size.width / 2f,
                                top = textPosition.y + textLayoutResult.size.height / 2f
                            )
                            scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
                            translate(
                                left = -(textPosition.x + textLayoutResult.size.width / 2f),
                                top = -(textPosition.y + textLayoutResult.size.height / 2f)
                            )
                        }
                    ) {

                        drawText(topLeft = textPosition, textLayoutResult = textLayoutResult)
                    }
                }
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
 * A composable function that displays two horizontal dividers with rounded corners.
 * These dividers are used to highlight the selected item in the WheelTextPicker.
 *
 * @param modifier The modifier to be applied to the Column.
 * @param dividerFraction The fraction of the width that the dividers should occupy.
 * @param dividerColor The color of the dividers.
 * @param dividerThickness The thickness of the dividers.
 */
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