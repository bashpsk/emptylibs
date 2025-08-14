package io.bashpsk.emptylibs.jetpackui.textpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableList

@Composable
fun <T> rememberTextPickerState(
    textList: ImmutableList<T>,
    initial: T? = textList.firstOrNull()
): TextPickerState<T> {

    return rememberSaveable(textList, initial, saver = TextPickerState.Saver()) {
        TextPickerState(textList = textList, initial = initial)
    }
}

class TextPickerState<T>(
    val textList: ImmutableList<T>,
    val initial: T?,
) {

    var selectedText by mutableStateOf(initial)
        private set

    fun updateSelectedText(newText: T) {

        newText.takeIf { textItem ->

            textList.contains(element = textItem) && selectedText != textItem
        }?.let { textItem -> selectedText = textItem }
    }

    internal fun updateSelectedTextFromIndex(index: Int) {

        textList.getOrNull(index = index)?.takeIf { textItem ->

            selectedText != textItem
        }.let { textItem ->

            selectedText = textItem
        }
    }

    companion object {

        fun <T> Saver(): Saver<TextPickerState<T>, List<Any?>> = Saver(
            save = { state ->

                listOf(
                    state.textList,
                    state.initial,
                    state.selectedText
                )
            },
            restore = { elements ->

                val savedTextList = elements[0] as ImmutableList<T>
                val savedInitial = elements[1] as T
                val savedSelectedText = elements[2] as? T

                TextPickerState(textList = savedTextList, initial = savedInitial).apply {

                    selectedText = savedSelectedText
                }
            }
        )
    }
}