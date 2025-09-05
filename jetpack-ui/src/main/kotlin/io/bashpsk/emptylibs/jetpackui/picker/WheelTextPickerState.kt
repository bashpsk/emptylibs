package io.bashpsk.emptylibs.jetpackui.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableList

/**
 * Creates a [WheelTextPickerState] that is remembered across compositions.
 *
 * @param textList The list of items to display in the picker.
 * @param initial The initially selected item. Defaults to the first item in [textList] or null if
 * the list is empty.
 * @return A [WheelTextPickerState] instance.
 */
@Composable
fun <T> rememberWheelTextPickerState(
    textList: ImmutableList<T>,
    initial: T? = textList.firstOrNull()
): WheelTextPickerState<T> {

    return rememberSaveable(
        textList,
        initial,
        saver = WheelTextPickerState.Saver(textList = textList, initial = initial)
    ) {
        WheelTextPickerState(textList = textList, initial = initial)
    }
}

/**
 * A state object that can be hoisted to control and observe the `WheelTextPicker`.
 *
 * In most cases, this will be created via [rememberWheelTextPickerState].
 *
 * @param textList The list of texts to be displayed in the picker.
 * @param initial The initially selected text. If null, the first text in `textList` will be
 * selected.
 */
class WheelTextPickerState<T>(
    val textList: ImmutableList<T>,
    val initial: T?,
) {

    /**
     * The currently selected text in the picker.
     * This property is observable and can be used to react to changes in the selected text.
     * It is initialized with the [initial] value provided to the constructor.
     * The setter is private to ensure that updates to the selected text are done through
     * the [updateSelectedText] or [updateSelectedTextFromIndex] methods,
     * which perform necessary validation.
     */
    var selectedText by mutableStateOf(initial)
        private set

    /**
     * Updates the selected text.
     *
     * This function checks if the `newText` is present in the `textList` and is different
     * from the currently `selectedText`. If both conditions are true, it updates the
     * `selectedText` to the `newText`.
     *
     * @param newText The new text to be selected.
     */
    fun updateSelectedText(newText: T) {

        newText.takeIf { textItem ->

            textList.contains(element = textItem) && selectedText != textItem
        }?.let { textItem -> selectedText = textItem }
    }

    /**
     * Updates the [selectedText] based on the provided [index].
     *
     * If the [index] is valid (within the bounds of [textList]) and the text at that index
     * is different from the current [selectedText], then [selectedText] will be updated.
     * If the [index] is out of bounds, [selectedText] will be set to `null`.
     *
     * @param index The index of the text item in [textList] to select.
     */
    internal fun updateSelectedTextFromIndex(index: Int) {

        textList.getOrNull(index = index)?.takeIf { textItem ->

            selectedText != textItem
        }.let { textItem -> selectedText = textItem }
    }

    companion object {

        private const val KEY_TEXT = "WHEEL-TEXT-PICKER-TEXT"

        @Suppress("UNCHECKED_CAST")
        fun <T> Saver(
            textList: ImmutableList<T>,
            initial: T?
        ): Saver<WheelTextPickerState<T>, Any> = mapSaver(
            save = { state ->

                mapOf(KEY_TEXT to state.selectedText)
            },
            restore = { elements ->

                WheelTextPickerState(textList = textList, initial = initial).apply {

                    selectedText = elements.getOrElse(KEY_TEXT) { initial } as T?
                }
            }
        )
    }
}