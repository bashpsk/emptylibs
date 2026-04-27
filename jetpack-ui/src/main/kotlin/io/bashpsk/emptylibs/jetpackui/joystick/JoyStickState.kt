package io.bashpsk.emptylibs.jetpackui.joystick

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.isActive
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Creates and remembers a [JoyStickState].
 *
 * @param properties The configuration properties for the joystick.
 * @return A [JoyStickState] instance.
 */
@Composable
fun rememberJoyStickState(
    properties: JoyStickProperties = JoyStickDefaults.properties()
): JoyStickState {

    val density = LocalDensity.current

    val state = retain(properties) { JoyStickState(properties = properties) }

    LaunchedEffect(state, state.properties) {

        with(density) { state.properties.speed.toPx() }.takeIf { speed ->
            speed > 0F
        }?.let { speed ->

            while (isActive) {

                withFrameNanos { state.tick(speed = speed) }
            }
        }
    }

    return state
}

/**
 * State object for [JoyStick].
 *
 * @property properties The configuration properties for the joystick.
 */
@Stable
class JoyStickState(val properties: JoyStickProperties) {

    /**
     * The maximum distance in pixels that the joystick thumb can move from the center.
     */
    internal var boundRadius by mutableFloatStateOf(0F)

    /**
     * The current changes and movement data of the joystick.
     */
    var changes by mutableStateOf(JoyStickChanges())
        private set

    /**
     * Whether the joystick is currently being pressed.
     */
    var isPressed by mutableStateOf(false)
        private set

    /**
     * The current offset of the thumb from the center.
     */
    internal var thumbPosition by mutableStateOf(Offset.Zero)
        private set

    /**
     * Updates the cumulative motion based on the current input and provided speed.
     * Usually called within a frame-bound loop.
     *
     * @param speed The speed in pixels per frame.
     */
    fun tick(speed: Float) {

        changes.takeIf { current ->

            current.input.x != 0F || current.input.y != 0F
        }?.let { current ->

            changes = current.copy(motion = current.motion + current.input * speed)
        }
    }

    /**
     * Handles the initial touch-down event.
     *
     * @param newPosition The initial position of the touch relative to the center.
     */
    fun onDown(newPosition: Offset) {

        isPressed = true
        updatePosition(newPosition = newPosition)
    }

    /**
     * Handles the touch up or release event.
     * Resets input values and applies rotation reset if configured.
     */
    fun onUp() {

        isPressed = false
        thumbPosition = Offset.Zero

        changes = changes.copy(
            input = Offset.Zero,
            displacement = 0F,
            angle = 0F,
            rotation = if (properties.faceToDirection) changes.rotation else 0F
        )
    }

    /**
     * Handles movement/drag events.
     *
     * @param amount The drag offset since the last event.
     */
    fun onDrag(amount: Offset) {

        updatePosition(newPosition = thumbPosition + amount)
    }

    /**
     * Calculates and updates the joystick's internal state based on a new touch position.
     *
     * This function handles position clamping within the [boundRadius], calculates the
     * displacement ratio, updates the thumb's visual position, and derives movement data
     * such as angle and rotation for the [changes] object.
     *
     * @param newPosition The raw offset of the touch input relative to the joystick center.
     */
    private fun updatePosition(newPosition: Offset) {

        sqrt(newPosition.x * newPosition.x + newPosition.y * newPosition.y).takeIf { distance ->

            distance > 0
        }?.let { distance ->

            val clampedDistance = min(distance, boundRadius)
            val ratio = clampedDistance / distance
            val constrainedPosition = newPosition * ratio
            val displacement = constrainedPosition / boundRadius
            val totalDisplacement = sqrt(
                displacement.x * displacement.x + displacement.y * displacement.y
            )
            val radian = atan2(constrainedPosition.y, constrainedPosition.x)
            val angle = Math.toDegrees(radian.toDouble()).toFloat().let { degree ->
                if (degree < 0) degree + 360 else degree
            }

            thumbPosition = constrainedPosition

            changes = changes.copy(
                input = displacement,
                displacement = totalDisplacement,
                angle = angle,
                rotation = if (properties.faceToDirection) angle else changes.rotation
            )
        } ?: run { onUp() }
    }
}