package io.bashpsk.emptylibs.jetpackui.text

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BasicTextEditor(
    modifier: Modifier = Modifier,
    inputContent: String,
    onContentChange: (content: String) -> Unit,
    textStyle: TextStyle = TextStyle.Default,
    numberColor: Color = MaterialTheme.colorScheme.onSurface,
    cursorBrush: Brush = SolidColor(Color.DarkGray),
    highlightColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50F),
    itemSpace: Dp = 4.dp,
    dividerContent: @Composable () -> Unit = {}
) {

    var textFieldValue by remember { mutableStateOf(TextFieldValue(inputContent)) }

    LaunchedEffect(inputContent) {

        inputContent.takeIf { text -> text != textFieldValue.text }?.let { text ->

            textFieldValue = textFieldValue.copy(text = text)
        }
    }

    BasicTextEditor(
        modifier = modifier,
        inputContent = textFieldValue,
        onContentChange = { newValue ->

            textFieldValue = newValue
            onContentChange(newValue.text)
        },
        textStyle = textStyle,
        numberColor = numberColor,
        cursorBrush = cursorBrush,
        highlightColor = highlightColor,
        itemSpace = itemSpace,
        dividerContent = dividerContent
    )
}

@Composable
fun BasicTextEditor(
    modifier: Modifier = Modifier,
    inputContent: TextFieldValue,
    onContentChange: (content: TextFieldValue) -> Unit,
    textStyle: TextStyle = TextStyle.Default,
    numberColor: Color = MaterialTheme.colorScheme.onSurface,
    cursorBrush: Brush = SolidColor(Color.DarkGray),
    highlightColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50F),
    itemSpace: Dp = 4.dp,
    dividerContent: @Composable () -> Unit = {}
) {

    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val currentLine by remember(inputContent.selection, textLayoutResult) {
        derivedStateOf { textLayoutResult?.getLineForOffset(inputContent.selection.start) }
    }

    val drawLineHighlighter = Modifier.drawBehind {

        textLayoutResult?.let { layoutResult ->

            currentLine?.let { line ->

                val lineTop = layoutResult.getLineTop(line)
                val lineBottom = layoutResult.getLineBottom(line)

                drawRect(
                    topLeft = Offset(0F, lineTop),
                    size = size.copy(height = lineBottom - lineTop),
                    color = highlightColor
                )
            }
        }
    }

    LaunchedEffect(inputContent.text.lines()) {

        horizontalScrollState.animateScrollTo(value = 0)
    }

    Row(
        modifier = modifier
            .verticalScroll(state = verticalScrollState)
            .height(intrinsicSize = IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = itemSpace)
    ) {

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {

            inputContent.text.lines().indices.forEach { line ->

                val numberStyle by remember(currentLine, textStyle, numberColor, line) {
                    derivedStateOf {
                        if (line == currentLine) textStyle else textStyle.copy(color = numberColor)
                    }
                }

                Text(
                    text = "${line + 1}",
                    textAlign = TextAlign.End,
                    style = numberStyle,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis
                )
            }
        }

        dividerContent()

        BasicTextField(
            modifier = Modifier
                .fillMaxHeight()
                .weight(weight = 1.0F)
                .horizontalScroll(state = horizontalScrollState)
                .then(drawLineHighlighter),
            value = inputContent,
            onValueChange = onContentChange,
            singleLine = false,
            textStyle = textStyle,
            cursorBrush = cursorBrush,
            onTextLayout = { result ->

                textLayoutResult = result
            }
        )
    }
}