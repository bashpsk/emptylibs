package io.bashpsk.emptylibs.jetpackui.textfield

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import kotlinx.collections.immutable.PersistentList

/**
 * Creates and remembers a [SegmentTextFieldState] for the given [segmentCount].
 *
 * @param segmentCount The number of segments in the text field.
 * @return A [SegmentTextFieldState] instance.
 */
@Composable
internal fun rememberSegmentTextFieldState(segmentCount: Int): SegmentTextFieldState {

    return retain(segmentCount) { SegmentTextFieldState(segmentCount = segmentCount) }
}

/**
 * State class for [SegmentTextField].
 *
 * Handles focus management, text changes, and backspace events for the segmented text field.
 *
 * @property segmentCount The number of segments in the text field.
 */
@Stable
internal class SegmentTextFieldState(val segmentCount: Int) {

    /**
     * A list of [FocusRequester]s, one for each segment.
     */
    val focusRequesterList = List(segmentCount) { FocusRequester() }

    /**
     * The index of the currently focused segment, or `null` if no segment is focused.
     */
    var focusedIndex by mutableStateOf<Int?>(null)
        private set

    /**
     * Handles text changes in a segment.
     *
     * @param index The index of the segment where the text changed.
     * @param text The new text in the segment.
     * @param values The current values of all segments.
     * @param onValueChange Callback to update the values.
     */
    fun onTextChange(
        index: Int,
        text: String,
        values: PersistentList<Char?>,
        onValueChange: (PersistentList<Char?>) -> Unit
    ) {

        if (text.isEmpty() && values[index] != null) {

            onValueChange(values.replacingAt(index = index, element = null))
            return
        }

        text.lastOrNull()?.let { char ->

            val newValues = values.replacingAt(index = index, element = char)

            onValueChange(newValues)

            getNextFocusedIndex(index = index, values = newValues)?.let { newIndex ->

                onFocusChanged(newIndex, true)
            }
        }
    }

    /**
     * Determines the index of the next segment to focus after a character is entered.
     *
     * @param index The index of the current segment.
     * @param values The current values of all segments.
     * @return The index of the next segment, or `null` if there is no next segment.
     */
    private fun getNextFocusedIndex(index: Int, values: PersistentList<Char?>): Int? {

        return (index + 1).takeIf { index != values.lastIndex && it in values.indices }
    }

    /**
     * Handles backspace events in a segment.
     *
     * If the current segment is empty, it moves focus to the previous segment and clears its value.
     * If the current segment is not empty, it clears its value.
     *
     * @param index The index of the segment where the backspace occurred.
     * @param values The current values of all segments.
     * @param onValueChange Callback to update the values.
     */
    fun handleBackspace(
        index: Int,
        values: PersistentList<Char?>,
        onValueChange: (PersistentList<Char?>) -> Unit
    ) {

        if (values[index] == null && index > 0) {

            val newIndex = index - 1

            onValueChange(values.replacingAt(index = newIndex, element = null))
            onFocusChanged(newIndex, true)
        } else if (values[index] != null) {

            onValueChange(values.replacingAt(index = index, element = null))
        }
    }

    /**
     * Updates the [focusedIndex] when a segment gains focus.
     *
     * @param index The index of the segment.
     * @param isFocused Whether the segment is focused.
     */
    fun onFocusChanged(index: Int, isFocused: Boolean) {

        if (isFocused) focusedIndex = index
    }
}