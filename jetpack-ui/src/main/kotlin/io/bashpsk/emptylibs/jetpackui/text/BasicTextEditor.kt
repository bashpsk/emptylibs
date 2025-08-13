package io.bashpsk.emptylibs.jetpackui.text

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun BasicTextEditor(
    modifier: Modifier = Modifier,
    inputContent: String,
    onContentChange: (content: String) -> Unit = {},
    textStyle: TextStyle = TextStyle.Default,
    cursorBrush: Brush = SolidColor(value = Color.DarkGray),
) {

    val horizontalScrollState = rememberScrollState()

    LaunchedEffect(inputContent.lines()) {

        horizontalScrollState.animateScrollTo(value = 0)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {

            inputContent.lines().indices.forEach { lineNumber ->

                Text(text = "${lineNumber + 1}.", style = textStyle, maxLines = 1)
            }
        }

        VerticalDivider(modifier = Modifier.wrapContentHeight())

        BasicTextField(
            modifier = Modifier
                .weight(weight = 1.0F)
                .horizontalScroll(state = horizontalScrollState),
            value = inputContent,
            onValueChange = onContentChange,
            singleLine = false,
            textStyle = textStyle,
            cursorBrush = cursorBrush
        )
    }
}