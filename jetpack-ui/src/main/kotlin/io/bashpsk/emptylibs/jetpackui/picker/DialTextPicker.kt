package io.bashpsk.emptylibs.jetpackui.picker

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

/**
 * A composable function that displays a dial text picker.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state The state of the dial text picker.
 * @param textStyle The text style for the items in the picker.
 * @param selectedTextStyle The text style for the selected item in the picker.
 * @param textBoxPadding The padding around the selected text box.
 * @param textBoxThickness The thickness of the selected text box border.
 * @param textBoxColor The color of the selected text box border.
 * @param T The type of items in the picker.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun <T> DialTextPicker(
    modifier: Modifier = Modifier,
    state: DialTextPickerState<T>,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    selectedTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    textBoxPadding: Dp = 2.dp,
    textBoxThickness: Dp = 2.dp,
    textBoxColor: Color = MaterialTheme.colorScheme.primary
) {

    val textMeasurer = rememberTextMeasurer()

    val dialTextBoxSize by remember(state.selectedText) {
        derivedStateOf {
            textMeasurer.measure(
                text = "${state.selectedText ?: state.textList.firstOrNull()}",
                style = selectedTextStyle
            ).size.toSize()
        }
    }

    val pointerInputModifier = Modifier.pointerInput(Unit) {

        detectDragGestures(
            onDragStart = { position ->

                state.onDialStart(Offset(size.center.y - position.y, size.center.x - position.x))
            },
            onDragEnd = state::onDialEnd,
            onDragCancel = state::onDialEnd,
            onDrag = { change, _ ->

                change.consume()

                val position = Offset(
                    x = size.center.y - change.position.y,
                    y = size.center.x - change.position.x
                )

                state.onDialDrag(position = position)
            }
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(ratio = 1.0F)
            .then(pointerInputModifier),
        contentAlignment = Alignment.Center
    ) {

        val canvasWidth = constraints.maxWidth.toFloat()
        val canvasHeight = constraints.maxHeight.toFloat()
        val center = Offset(canvasWidth / 2, canvasHeight / 2)
        val dialRadius = (minOf(canvasWidth, canvasHeight) / 2F)

        Canvas(
            modifier = Modifier.fillMaxSize(),
            contentDescription = "Dial Text Picker"
        ) {

            rotate(degrees = state.currentAngle, pivot = center) {

                val angleStep = 360F / state.textList.size

                state.textList.forEachIndexed { index, item ->

                    val itemAngleDegrees = index * angleStep
                    val isSelected = item == state.selectedText

                    val textLayoutResult = textMeasurer.measure(
                        text = "$item",
                        style = if (isSelected) selectedTextStyle else textStyle
                    )

                    rotate(degrees = itemAngleDegrees, pivot = center) {

                        drawText(
                            topLeft = Offset(
                                x = center.x + dialRadius - textLayoutResult.size.width,
                                y = center.y - textLayoutResult.size.height / 2F
                            ),
                            textLayoutResult = textLayoutResult
                        )
                    }
                }
            }

            val rectSize = Size(
                dialTextBoxSize.width + (textBoxPadding.toPx() * 2),
                dialTextBoxSize.height + (textBoxPadding.toPx() * 2)
            )

            drawDialSelectedBox(
                end = Offset(
                    x = size.width + textBoxPadding.toPx(),
                    y = size.center.y + rectSize.height / 2
                ),
                rectSize = rectSize,
                width = textBoxThickness,
                color = textBoxColor
            )
        }
    }
}