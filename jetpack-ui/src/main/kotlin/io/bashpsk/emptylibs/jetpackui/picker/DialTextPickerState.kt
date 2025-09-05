package io.bashpsk.emptylibs.jetpackui.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.floor

/**
 * Creates and remembers a [DialTextPickerState] for a dial text picker.
 *
 * This function is a composable that provides a convenient way to create and manage the state
 * of a dial text picker. It handles the creation and remembering of the [DialTextPickerState]
 * instance, ensuring that the state is preserved across recompositions.
 *
 * The [DialTextPickerState] holds the current state of the picker, including the list of items,
 * the currently selected item, and the rotation angle of the dial.
 *
 * @param T The type of items in the text list.
 * @param textList An immutable list of items to be displayed in the picker.
 * @param initial The initially selected item. If null, the first item in [textList] will be
 * selected. Defaults to the first item in [textList] or null if the list is empty.
 * @return A [DialTextPickerState] instance that can be used to control and observe the picker's
 * state.
 *
 * @see DialTextPickerState
 */
@Composable
fun <T> rememberDialTextPickerState(
    textList: ImmutableList<T>,
    initial: T? = textList.firstOrNull()
): DialTextPickerState<T> {

    return rememberSaveable(
        textList,
        initial,
        saver = DialTextPickerState.Saver(textList = textList, initial = initial)
    ) {
        DialTextPickerState(textList = textList, initial = initial)
    }
}

/**
 * A state object that can be hoisted to control and observe the [DialTextPicker].
 *
 * This class holds the current state of the picker, including the list of items,
 * the initially selected item, the current rotation angle, and the currently selected item.
 * It provides methods to update the rotation, handle dial interactions (start, end, drag),
 * and set the initial angle based on the initial item.
 *
 * @param T The type of items in the picker.
 * @property textList The immutable list of items to display in the picker.
 * @property initial The initially selected item. If null, the first item in [textList] will be
 * selected, or no item if [textList] is empty.
 * @property currentAngle The current rotation angle of the dial in degrees.
 * This value is updated as the user interacts with the dial.
 * @property selectedText The currently selected item from the [textList].
 * This is updated when the [currentAngle] changes.
 * @property previousAngle Internal state used to calculate the change in angle during drag gesture.
 */
@Stable
class DialTextPickerState<T>(
    val textList: ImmutableList<T>,
    val initial: T?,
) {

    /**
     * The currently selected text in the picker.
     * This value is updated as the user interacts with the dial.
     * It is initialized with the `initial` value provided to the state.
     */
    var selectedText by mutableStateOf(initial)
        private set

    /**
     * The current rotation angle of the dial in degrees.
     * This value is updated as the user interacts with the dial.
     * It is read-only from outside the class.
     */
    internal var currentAngle by mutableFloatStateOf(0F)

    /**
     * Stores the angle of the previous drag event.
     * This is used to calculate the change in angle during a drag gesture.
     * It is initialized to 0F and updated in [onDialStart] and [onDialDrag].
     * It is reset to 0F in [onDialEnd].
     */
    internal var previousAngle by mutableFloatStateOf(0F)

    init {

        setInitialAngle()
    }

    /**
     * Updates the rotation angle of the dial and selects the corresponding item.
     *
     * This function calculates the new rotation angle and determines the item
     * at that position in the `textList`. If the `textList` is not empty,
     * it updates the `currentAngle` and `selectedItem`.
     *
     * The selection logic involves:
     * 1. Calculating the angle allocated to each item (`anglePerItem`).
     * 2. Normalizing the `newAngle` to be within the 0-360 degree range (`normalizedAngle`).
     * 3. Adjusting the normalized angle to be centered within an item's segment (`centeredAngle`).
     * 4. Determining the index of the item corresponding to the `centeredAngle`.
     * 5. If the calculated index is valid, the `selectedItem` is updated.
     *
     * @param newAngle The new rotation angle in degrees.
     */
    internal fun updateRotation(newAngle: Float) {

        textList.takeIf { items -> items.isNotEmpty() }?.let { items ->

            currentAngle = newAngle

            val anglePerItem = 360F / items.size
            val normalizedAngle = (-currentAngle % 360F + 360F) % 360F
            val centeredAngle = (normalizedAngle + anglePerItem / 2F) % 360F

            (floor(centeredAngle / anglePerItem).toInt() % items.size).takeIf { itemIndex ->

                itemIndex in items.indices
            }?.let { itemIndex ->

                selectedText = items[itemIndex]
            }
        }
    }

    /**
     * Called when the dial interaction starts.
     *
     * This function calculates the initial angle based on the touch position
     * and stores it in [previousAngle] to be used for subsequent drag calculations.
     *
     * @param position The [Offset] representing the starting position of the touch event on the
     * dial.
     */
    internal fun onDialStart(position: Offset) {

        previousAngle = atan2(position.x, position.y) * (180F / PI.toFloat())
    }

    /**
     * Called when the dial interaction ends.
     *
     * This function calculates the target rotation angle to snap the dial to the nearest item
     * based on the current rotation and updates the rotation. It also resets the previous angle.
     */
    internal fun onDialEnd() {

        textList.takeIf { items -> items.isNotEmpty() }?.let { items ->

            val anglePerItem = 360F / items.size
            val normalizedAngle = (-currentAngle % 360F + 360F) % 360F
            val centeredAngle = (normalizedAngle + anglePerItem / 2F) % 360F
            val currentSelectedIndex = (floor(centeredAngle / anglePerItem).toInt() % items.size)
            var targetRotation = -(currentSelectedIndex.toFloat() * anglePerItem)
            val angleDifference = (targetRotation - currentAngle) % 360F

            targetRotation = currentAngle + when {

                angleDifference > 180F -> angleDifference - 360F
                angleDifference < -180F -> angleDifference + 360F
                else -> angleDifference
            }

            updateRotation(targetRotation)
        }

        previousAngle = 0F
    }

    /**
     * Handles the drag event on the dial.
     *
     * This function is called when the user drags their finger on the dial.
     * It calculates the change in angle based on the drag position and updates the
     * dial's rotation accordingly.
     *
     * @param position The current position of the drag event.
     */
    internal fun onDialDrag(position: Offset) {

        val currentAngle = atan2(position.x, position.y) * (180F / PI.toFloat())
        val angleChange = currentAngle - previousAngle

        updateRotation(this@DialTextPickerState.currentAngle + angleChange)
        previousAngle = currentAngle
    }

    /**
     * Sets the initial rotation angle of the dial based on the provided initial item.
     * If an initial item is specified and found in the list, the dial will be rotated
     * so that this item is initially selected. If no initial item is provided or
     * if the provided item is not in the list, the dial will start at its default
     * position (usually the first item).
     */
    private fun setInitialAngle() {

        initial?.let { item ->

            textList.indexOf(item).takeIf { index -> index != -1 }?.let { index ->

                currentAngle = -(index.toFloat() / textList.size) * 360F
            }
        }
    }

    companion object {

        private const val KEY_TEXT = "DIAL-TEXT-PICKER-TEXT"
        private const val KEY_CURRENT_ANGLE = "DIAL-TEXT-PICKER-CURRENT-ANGLE"
        private const val KEY_PREVIOUS_ANGLE = "DIAL-TEXT-PICKER-PREVIOUS-ANGLE"

        @Suppress("UNCHECKED_CAST")
        fun <T> Saver(
            textList: ImmutableList<T>,
            initial: T?
        ): Saver<DialTextPickerState<T>, Any> = mapSaver(
            save = { state ->

                mapOf(
                    KEY_TEXT to state.selectedText,
                    KEY_CURRENT_ANGLE to state.currentAngle,
                    KEY_PREVIOUS_ANGLE to state.previousAngle
                )
            },
            restore = { elements ->

                DialTextPickerState(textList = textList, initial = initial).apply {

                    selectedText = elements.getOrElse(KEY_TEXT) { initial } as T?
                    currentAngle = elements.getOrElse(KEY_CURRENT_ANGLE) { 0F } as Float
                    previousAngle = elements.getOrElse(KEY_PREVIOUS_ANGLE) { 0F } as Float
                }
            }
        )
    }
}