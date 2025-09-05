package io.bashpsk.emptylibs.jetpackui.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.floor

@Composable
fun <T> rememberDialTextPickerState(
    textList: ImmutableList<T>,
    initial: T? = textList.firstOrNull()
): DialTextPickerState<T> {

    return remember(textList, initial) {
        DialTextPickerState(textList = textList, initial = initial)
    }
}

@Stable
class DialTextPickerState<T>(
    val textList: ImmutableList<T>,
    val initial: T?,
) {

    var currentRotationAngle by mutableFloatStateOf(0F)
        private set

    var selectedItem by mutableStateOf(initial)
        private set

    internal var previousAngle by mutableFloatStateOf(0F)

    init {

        setInitialAngle()
    }

    fun updateRotation(newAngle: Float) {

        textList.takeIf { items -> items.isNotEmpty() }?.let { items ->

            currentRotationAngle = newAngle

            val anglePerItem = 360F / items.size
            val normalizedAngle = (-currentRotationAngle % 360F + 360F) % 360F
            val centeredAngle = (normalizedAngle + anglePerItem / 2F) % 360F

            (floor(centeredAngle / anglePerItem).toInt() % items.size).takeIf { itemIndex ->

                itemIndex in items.indices
            }?.let { itemIndex ->

                selectedItem = items[itemIndex]
            }
        }
    }

    internal fun onDialStart(position: Offset) {

        previousAngle = atan2(position.x, position.y) * (180F / PI.toFloat())
    }

    internal fun onDialEnd() {

        textList.takeIf { items -> items.isNotEmpty() }?.let { items ->

            val anglePerItem = 360F / items.size
            val normalizedAngle = (-currentRotationAngle % 360F + 360F) % 360F
            val centeredAngle = (normalizedAngle + anglePerItem / 2F) % 360F
            val currentSelectedIndex = (floor(centeredAngle / anglePerItem).toInt() % items.size)
            var targetRotation = -(currentSelectedIndex.toFloat() * anglePerItem)
            val angleDifference = (targetRotation - currentRotationAngle) % 360F

            targetRotation = currentRotationAngle + when {

                angleDifference > 180F -> angleDifference - 360F
                angleDifference < -180F -> angleDifference + 360F
                else -> angleDifference
            }

            updateRotation(targetRotation)
        }

        previousAngle = 0F
    }

    internal fun onDialDrag(position: Offset) {

        val currentAngle = atan2(position.x, position.y) * (180F / PI.toFloat())
        val angleChange = currentAngle - previousAngle

        updateRotation(currentRotationAngle + angleChange)
        previousAngle = currentAngle
    }

    private fun setInitialAngle() {

        initial?.let { item ->

            textList.indexOf(item).takeIf { index -> index != -1 }?.let { index ->

                currentRotationAngle = -(index.toFloat() / textList.size) * 360F
            }
        }
    }
}