package io.bashpsk.emptylibs.animations.shimmer

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A delegate class that handles the animation logic and brush generation for the shimmer effect.
 *
 * @property properties The [ShimmerEffectProperties] to be applied.
 * @property coroutineScope The [CoroutineScope] in which to run the animation.
 * @property onInvalidate A callback to be invoked when the drawing should be invalidated.
 */
class ShimmerEffectNodeDelegate(
    var properties: ShimmerEffectProperties,
    private val coroutineScope: CoroutineScope,
    private val onInvalidate: () -> Unit
) {

    /**
     * The animation state representing the progress of the shimmer effect, typically ranging from
     * 0.0 to 1.0.
     */
    private val progress = Animatable(0F)

    init {

        setLaunchAnimation()
    }

    /**
     * Launches the shimmer animation by animating the progress value from 0 to 1.
     *
     * This function uses the animation specification defined in [properties] and executes the
     * animation within the delegate's [coroutineScope].
     */
    private fun setLaunchAnimation() {

        coroutineScope.launch {

            progress.animateTo(targetValue = 1F, animationSpec = properties.animationSpec)
        }
    }

    /**
     * Updates the properties of the shimmer effect.
     *
     * @param properties The new [ShimmerEffectProperties] to be applied.
     */
    fun update(properties: ShimmerEffectProperties) {

        val oldSpec = this.properties.animationSpec

        this.properties = properties
        if (oldSpec != properties.animationSpec) setLaunchAnimation()
        onInvalidate()
    }

    /**
     * Generates a [Brush] for the current frame of the shimmer animation.
     *
     * @param size The [Size] of the drawing area.
     * @return The [Brush] to be used for drawing the shimmer.
     */
    fun getBrush(size: Size): Brush {


        val angleRadians = (properties.angle * PI / 180F).toFloat()
        val directionX = cos(angleRadians)
        val directionY = sin(angleRadians)

        val p1 = 0F
        val p2 = size.width * directionX
        val p3 = size.height * directionY
        val p4 = size.width * directionX + size.height * directionY

        val minP = minOf(p1, minOf(p2, minOf(p3, p4)))
        val maxP = maxOf(p1, maxOf(p2, maxOf(p3, p4)))

        val widthPx = size.width * properties.widthRatio
        val totalTravel = (maxP - minP) + widthPx
        val sweepPosition = minP - widthPx + totalTravel * progress.value

        val start = Offset(
            x = sweepPosition * directionX,
            y = sweepPosition * directionY
        )

        val end = Offset(
            x = (sweepPosition + widthPx) * directionX,
            y = (sweepPosition + widthPx) * directionY
        )

        if (progress.isRunning) onInvalidate()

        return Brush.linearGradient(colors = properties.colors, start = start, end = end)
    }
}