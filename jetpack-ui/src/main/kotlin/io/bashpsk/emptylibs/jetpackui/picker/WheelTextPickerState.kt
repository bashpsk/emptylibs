package io.bashpsk.emptylibs.jetpackui.picker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.splineBasedDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    return retain(density, coroutineScope, textList, initial) {
        WheelTextPickerState(
            density = density,
            coroutineScope = coroutineScope,
            textList = textList,
            initial = initial
        )
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
@Stable
class WheelTextPickerState<T>(
    private val density: Density,
    private val coroutineScope: CoroutineScope,
    val textList: ImmutableList<T>,
    val initial: T?
) {

    /**
     * The currently selected text in the picker.
     * This property is observable and can be used to react to changes in the selected text.
     * It is initialized with the [initial] value provided to the constructor.
     * The setter is private to ensure that updates to the selected text are done through
     * the [updateSelectedText] methods,
     * which perform necessary validation.
     */
    var selectedText by mutableStateOf(initial)
        private set

    /**
     * An [Animatable] instance used to handle the scrolling and flinging animations
     * of the picker. It stores the current scroll offset of the picker.
     * The initial value is 0F, representing no initial offset.
     * This property is internal to the class and is used by various methods
     * to update and animate the picker's scroll position.
     */
    internal val animatable = Animatable(0F)

    /**
     * Updates the [selectedText] based on the current scroll position and item height.
     *
     * This function calculates the index of the item that is currently centered in the picker
     * and updates the [selectedText] accordingly.
     *
     * @param itemHeight The height of each item in the picker.
     * @param pickerCenterY The Y-coordinate of the center of the picker.
     */
    internal fun updateSelectedText(itemHeight: Float, pickerCenterY: Float) {

        textList.takeIf { itemsList ->

            itemsList.isNotEmpty() && itemHeight > 0
        }?.let { itemsList ->

            itemsList.getOrNull(
                ((pickerCenterY + animatable.value - itemHeight / 2F) / itemHeight).toInt()
                    .coerceIn(itemsList.indices)
            )?.let { newSelection ->

                selectedText = newSelection
            }
        } ?: run {

            selectedText = null
        }
    }

    /**
     * Sets the initial scroll position of the picker.
     * This function is called when the picker is first composed or when the [initial] value
     * changes. It calculates the scroll offset needed to center the [initial] item in the picker
     * and snaps the [animatable] to that offset.
     *
     * If [textList] is empty or [itemHeight] is not positive, this function does nothing.
     * If [initial] is null or not found in [textList], the picker will scroll to the first item.
     *
     * @param itemHeight The height of each item in the picker.
     * @param pickerCenterY The Y-coordinate of the center of the picker view.
     */
    internal fun setInitialScroll(itemHeight: Float, pickerCenterY: Float) {

        coroutineScope.launch {

            textList.takeIf { itemsList ->

                itemsList.isNotEmpty() && itemHeight > 0
            }?.let { itemsList ->

                val initialIndex = initial?.let { itemsList.indexOf(it) }?.takeIf { it != -1 } ?: 0

                animatable.snapTo((initialIndex * itemHeight + itemHeight / 2F) - pickerCenterY)
            }
        }
    }

    /**
     * Handles the scroll event and updates the animatable value.
     *
     * This function is called when the user scrolls the picker. It adjusts the current
     * animation value by the given delta, effectively moving the items in the picker.
     *
     * @param delta The change in scroll position. A positive value indicates scrolling down,
     * and a negative value indicates scrolling up.
     */
    internal fun onScroll(delta: Float) {

        coroutineScope.launch { animatable.snapTo(animatable.value - delta) }
    }

    /**
     * Handles the fling gesture on the picker.
     * It initiates an animation to decay the scroll velocity and then snaps to the nearest item.
     *
     * @param velocity The velocity of the fling gesture.
     * @param itemHeight The height of each item in the picker.
     * @param pickerCenterY The Y-coordinate of the center of the picker.
     */
    internal fun onFling(velocity: Float, itemHeight: Float, pickerCenterY: Float) {

        coroutineScope.launch {

            animatable.animateDecay(
                initialVelocity = -velocity,
                animationSpec = splineBasedDecay(density)
            ) {

                launch {

                    snapToNearestItem(itemHeight = itemHeight, pickerCenterY = pickerCenterY)
                }
            }
        }
    }

    /**
     * Snaps the picker to the nearest item after a scroll or fling gesture.
     *
     * This function calculates the index of the item closest to the center of the picker
     * and animates the scroll position to align that item with the center.
     *
     * @param itemHeight The height of a single item in the picker.
     * @param pickerCenterY The Y-coordinate of the center of the picker.
     */
    internal fun snapToNearestItem(itemHeight: Float, pickerCenterY: Float) {

        coroutineScope.launch {

            textList.takeIf { itemsList ->

                itemsList.isNotEmpty() && itemHeight > 0
            }?.let { itemsList ->

                val index = ((pickerCenterY + animatable.value - itemHeight / 2F) / itemHeight)
                    .toInt().coerceIn(itemsList.indices)

                animatable.animateTo((index * itemHeight + itemHeight / 2F) - pickerCenterY)
            }
        }
    }
}