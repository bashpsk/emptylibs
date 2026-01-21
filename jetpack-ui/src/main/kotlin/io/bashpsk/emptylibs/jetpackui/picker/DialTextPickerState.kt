package io.bashpsk.emptylibs.jetpackui.picker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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

    val coroutineScope = rememberCoroutineScope()

    return rememberSaveable(
        textList,
        initial,
        coroutineScope,
        saver = DialTextPickerState.Saver(
            textList = textList,
            initial = initial,
            coroutineScope = coroutineScope
        )
    ) {
        DialTextPickerState(
            textList = textList,
            initial = initial,
            coroutineScope = coroutineScope
        )
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
 * @property coroutineScope The coroutine scope used for animations.
 * @property selectedText The currently selected item from the [textList].
 * @property selectedIndex The index of the currently selected item in the [textList].
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
    val coroutineScope: CoroutineScope
) {

    var selectedText by mutableStateOf(initial)
        private set

    var selectedIndex by mutableIntStateOf(textList.indexOf(initial))
        private set

    internal val currentAngle = Animatable(0F)

    internal var previousAngle by mutableFloatStateOf(0F)

    init {

        updateSelectedText(newValue = initial, animationSpec = null)
    }

    /**
     * Updates the currently selected text and adjusts the dial's angle accordingly.
     *
     * If the `newValue` is not null and exists in the `textList`, this function
     * will find the index of `newValue` in the list. If found, it calculates
     * the corresponding angle to center that item on the dial and updates
     * the `currentAngle`. This effectively makes `newValue` the selected item
     * by rotating the dial.
     *
     * If `newValue` is null or not found in `textList`, the `currentAngle`
     * and `selectedText` remain unchanged.
     *
     * @param newValue The new item to be selected.
     */
    fun updateSelectedText(
        newValue: T?,
        animationSpec: TweenSpec<Float>? = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        )
    ) {

        newValue?.let { item ->

            textList.indexOf(item).takeIf { index -> index != -1 }?.let { index ->

                updateRotation(
                    newAngle = -(index.toFloat() / textList.size) * 360F,
                    animationSpec = animationSpec
                )
            }
        }
    }

    /**
     * Updates the selected text based on the provided index and rotates the dial accordingly.
     *
     * This function is useful for programmatically selecting an item in the `textList`
     * by its index. If the `newIndex` is a valid index within the `textList`,
     * this function calculates the required rotation angle to center the item at that
     * index on the dial and then calls `updateRotation` to apply the change.
     *
     * If the `newIndex` is out of bounds for the `textList`, this function does nothing.
     *
     * @param newIndex The index of the item in `textList` to be selected.
     */
    fun updateSelectedTextFromIndex(
        newIndex: Int,
        animationSpec: TweenSpec<Float>? = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        )
    ) {

        newIndex.takeIf { index -> index in textList.indices }?.let { index ->

            updateRotation(
                newAngle = -(index.toFloat() / textList.size) * 360F,
                animationSpec = animationSpec
            )
        }
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
    private fun updateRotation(newAngle: Float, animationSpec: TweenSpec<Float>?) {

        coroutineScope.launch {

            currentAngle.apply {

                when (animationSpec) {

                    null -> snapTo(targetValue = newAngle)

                    else -> animateTo(targetValue = newAngle, animationSpec = animationSpec) {

                        updateSelectionFromAngle(value)
                    }
                }
            }

            updateSelectionFromAngle(currentAngle.value)
        }
    }

    /**
     * Updates the selected text and index based on the current dial angle.
     *
     * This internal function is responsible for determining which item in the `textList`
     * should be considered selected given a specific rotation `angle` of the dial.
     * It performs the following steps:
     * 1. Checks if `textList` is not empty.
     * 2. Calculates `anglePerItem`, which is the angular space each item occupies on the dial.
     * 3. Normalizes the input `angle` to be within the 0-360 degree range. The angle is negated
     *    because a positive drag typically results in a counter-clockwise rotation, which
     *    corresponds to decreasing item indices.
     * 4. Calculates `centeredAngle` by shifting the `normalizedAngle` by half of `anglePerItem`.
     *    This adjustment helps in correctly identifying the item whose segment the angle falls
     *    into, effectively treating the center of each item's segment as the selection point.
     * 5. Determines `itemIndex` by dividing `centeredAngle` by `anglePerItem`, taking the floor,
     *    and then using the modulo operator with the number of items to ensure the index wraps
     *    around.
     * 6. If the calculated `itemIndex` is valid (within the bounds of `textList`), it updates
     *    `selectedText` to the item at that index and `selectedIndex` to the index itself.
     *
     * This function is typically called during or after a rotation animation or drag gesture
     * to reflect the change in selection.
     *
     * @param angle The current rotation angle of the dial in degrees.
     */
    private fun updateSelectionFromAngle(angle: Float) {

        textList.takeIf { items -> items.isNotEmpty() }?.let { items ->

            val anglePerItem = 360F / items.size
            val normalizedAngle = (-angle % 360F + 360F) % 360F
            val centeredAngle = (normalizedAngle + anglePerItem / 2F) % 360F

            (floor(centeredAngle / anglePerItem).toInt() % items.size).takeIf { itemIndex ->

                itemIndex in items.indices
            }?.let { itemIndex ->

                selectedText = items[itemIndex]
                selectedIndex = itemIndex
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

        previousAngle = (atan2(position.y, position.x) * (180.0 / PI)).toFloat()
    }

    /**
     * Called when the dial interaction ends.
     *
     * This function calculates the target rotation angle to snap the dial to the nearest item
     * based on the current rotation and updates the rotation. It also resets the previous angle.
     */
    internal fun onDialEnd() {

        textList.takeIf { items -> items.isNotEmpty() }?.let { items ->

            val animationSpec = tween<Float>(
                durationMillis = 250,
                easing = FastOutSlowInEasing
            )

            val anglePerItem = 360F / items.size
            val normalizedAngle = (-currentAngle.value % 360F + 360F) % 360F
            val centeredAngle = (normalizedAngle + anglePerItem / 2F) % 360F
            val currentSelectedIndex = (floor(centeredAngle / anglePerItem).toInt() % items.size)
            val targetRotation = -(currentSelectedIndex.toFloat() * anglePerItem)
            val angleDifference = (targetRotation - currentAngle.value) % 360F

            val shortestPath = when {

                angleDifference > 180F -> angleDifference - 360F
                angleDifference < -180F -> angleDifference + 360F
                else -> angleDifference
            }

            updateRotation(
                newAngle = currentAngle.value + shortestPath,
                animationSpec = animationSpec
            )
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

        val newAngle = (atan2(position.y, position.x) * (180.0 / PI)).toFloat()
        var angleChange = newAngle - previousAngle

        when {

            angleChange > 180f -> angleChange -= 360f
            angleChange < -180f -> angleChange += 360f
        }

        coroutineScope.launch {

            currentAngle.snapTo(currentAngle.value + angleChange)
            updateSelectionFromAngle(currentAngle.value)
        }

        previousAngle = newAngle
    }

    companion object {

        private const val KEY_TEXT = "DIAL-TEXT-PICKER-TEXT"
        private const val KEY_INDEX = "DIAL-TEXT-PICKER-INDEX"
        private const val KEY_CURRENT_ANGLE = "DIAL-TEXT-PICKER-CURRENT-ANGLE"
        private const val KEY_PREVIOUS_ANGLE = "DIAL-TEXT-PICKER-PREVIOUS-ANGLE"

        @Suppress("UNCHECKED_CAST")
        internal fun <T> Saver(
            textList: ImmutableList<T>,
            initial: T?,
            coroutineScope: CoroutineScope
        ): Saver<DialTextPickerState<T>, Any> = mapSaver(
            save = { state ->

                mapOf(
                    KEY_TEXT to state.selectedText,
                    KEY_INDEX to state.selectedIndex,
                    KEY_CURRENT_ANGLE to state.currentAngle.value,
                    KEY_PREVIOUS_ANGLE to state.previousAngle
                )
            },
            restore = { elements ->

                DialTextPickerState(
                    textList = textList,
                    initial = initial,
                    coroutineScope = coroutineScope
                ).apply {

                    selectedText = elements.getOrElse(KEY_TEXT) { initial } as T?
                    selectedIndex = elements.getOrElse(KEY_INDEX) { initial } as Int
                    previousAngle = elements.getOrElse(KEY_PREVIOUS_ANGLE) { 0F } as Float
                    val savedAngle = elements.getOrElse(KEY_CURRENT_ANGLE) { 0F } as Float

                    coroutineScope.launch { currentAngle.snapTo(savedAngle) }
                }
            }
        )
    }
}