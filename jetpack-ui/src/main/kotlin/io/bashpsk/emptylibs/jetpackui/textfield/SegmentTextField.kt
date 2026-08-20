package io.bashpsk.emptylibs.jetpackui.textfield

import android.view.KeyEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList

/**
 * A segmented text field that allows users to enter a single character in each segment.
 *
 * This is useful for scenarios like OTP (One-Time Password) entry or any other input that requires
 * a fixed number of individual characters.
 *
 * @param modifier The [Modifier] to be applied to the layout.
 * @param value A [PersistentList] of [Char] representing the values in each segment. A `null` value
 * indicates an empty segment.
 * @param onValueChange Callback that is triggered when the values in the segments change.
 * @param enabled Controls the enabled state of the text field. When `false`, this component will
 * not respond to user input, and it will appear visually disabled.
 * @param readOnly Controls the editable state of the text field. When `true`, the text field cannot
 * be modified, however, a user can focus it and copy text from it. Read-only text fields are
 * usually used to display text that the user cannot edit.
 * @param isError Indicates if the text field's current value is in error. If set to `true`, the
 * label, cursor and indicator color will be displayed in error color.
 * @param textStyle The style to be applied to the input text.
 * @param placeholder The content to be displayed when a segment is empty.
 * @param textFieldSize The size of each segment text field.
 * @param shape The shape of the text field's container and border.
 * @param colors [TextFieldColors] that will be used to resolve color of the text field in different
 * states.
 * @param visualTransformation The visual transformation filter for the input text.
 * @param keyboardOptions Software keyboard options that contains configuration such as
 * [KeyboardType] and [ImeAction].
 * @param keyboardActions When the software keyboard emits an IME action, the corresponding callback
 * is called.
 * @param horizontalArrangement The horizontal arrangement of the segments.
 * @param verticalArrangement The vertical arrangement of the segments.
 * @param itemVerticalAlignment The vertical alignment of the segments.
 * @param maxItemsInEachRow The maximum number of segments in each row.
 * @param maxLines The maximum number of lines for the layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentTextField(
    modifier: Modifier = Modifier,
    value: PersistentList<Char?>,
    onValueChange: (newValues: PersistentList<Char?>) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: @Composable (() -> Unit)? = {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            Text(text = "-")
        }
    },
    textFieldSize: DpSize = DpSize(width = 56.dp, height = 56.dp),
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    itemVerticalAlignment: Alignment.Vertical = Alignment.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    maxLines: Int = Int.MAX_VALUE
) {

    require(value.isNotEmpty()) { "SegmentTextField value must not empty." }

    val state = rememberSegmentTextFieldState(segmentCount = value.size)

    LaunchedEffect(state.focusedIndex) {

        state.focusedIndex?.let { index ->

            state.focusRequesterList.getOrNull(index)?.requestFocus()
        }
    }

    FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        itemVerticalAlignment = itemVerticalAlignment,
        maxItemsInEachRow = maxItemsInEachRow,
        maxLines = maxLines
    ) {

        value.forEachIndexed { index, char ->

            val focusRequester = state.focusRequesterList[index]

            OutlinedTextField(
                modifier = Modifier
                    .size(textFieldSize)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->

                        state.onFocusChanged(index = index, isFocused = focusState.isFocused)
                    }
                    .onKeyEvent { event ->

                        val isDelete = event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL

                        if (isDelete && char == null) {

                            state.handleBackspace(
                                index = index,
                                values = value,
                                onValueChange = onValueChange
                            )

                            true
                        } else false
                    },
                value = char?.toString() ?: "",
                onValueChange = { newText ->

                    state.onTextChange(
                        index = index,
                        text = newText,
                        values = value,
                        onValueChange = onValueChange
                    )
                },
                enabled = enabled,
                readOnly = readOnly,
                singleLine = true,
                isError = isError,
                textStyle = textStyle.copy(textAlign = TextAlign.Center),
                shape = shape,
                colors = colors,
                placeholder = placeholder,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions.copy(
                    imeAction = if (index == value.lastIndex) {
                        keyboardOptions.imeAction
                    } else ImeAction.Next
                ),
                keyboardActions = if (index == value.lastIndex) keyboardActions else {
                    KeyboardActions(onNext = { state.onFocusChanged(index + 1, true) })
                }
            )
        }
    }
}