package io.bashpsk.emptylibs.animations.diagonal

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import io.bashpsk.emptylibs.animations.shimmer.ShimmerEffectNodeDelegate
import kotlin.math.max
import kotlin.math.sqrt

/**
 * A [Modifier.Node] that implements [DrawModifierNode] to draw a diagonal ribbon with text.
 *
 * @property text The text to be displayed.
 * @property alignment The corner where the label is drawn.
 * @property properties The visual properties for the label.
 * @property textMeasurer The measurer for the text.
 */
internal class DiagonalLabelNode(
    var text: String,
    var alignment: Alignment,
    var properties: DiagonalLabelProperties,
    var textMeasurer: TextMeasurer
) : Modifier.Node(), DrawModifierNode {

    /**
     * Delegate responsible for managing the shimmer effect's animation state and providing the
     * brush used to render the shimmer over the diagonal label.
     */
    private var shimmerDelegate: ShimmerEffectNodeDelegate? = null

    override fun onAttach() {
        super.onAttach()

        setLaunchAnimation()
    }

    /**
     * Initializes and starts the shimmer animation effect.
     *
     * This function checks if shimmer properties are defined in the current [properties]. If they
     * exist, it initializes the [shimmerDelegate] to manage the animation
     */
    private fun setLaunchAnimation() {

        val shimmerProperties = properties.shimmerProperties ?: return

        shimmerDelegate = ShimmerEffectNodeDelegate(
            properties = shimmerProperties,
            coroutineScope = coroutineScope,
            onInvalidate = { invalidateDraw() }
        )
    }

    override fun ContentDrawScope.draw() {

        drawContent()

        val textLayoutResult = textMeasurer.measure(
            text = text,
            style = properties.labelStyle.copy(color = properties.labelColor)
        )
        val textWidth = textLayoutResult.size.width
        val textHeight = textLayoutResult.size.height

        val verticalPaddingPx = properties.padding.calculateTopPadding()
            .toPx() + properties.padding.calculateBottomPadding().toPx()
        val ribbonHeightPx = textHeight + verticalPaddingPx

        val axisOffsetPx = when (alignment) {

            Alignment.TopStart -> max(
                properties.padding.calculateStartPadding(layoutDirection).toPx(),
                properties.padding.calculateTopPadding().toPx()
            )

            Alignment.TopEnd -> max(
                properties.padding.calculateEndPadding(layoutDirection).toPx(),
                properties.padding.calculateTopPadding().toPx()
            )

            Alignment.BottomStart -> max(
                properties.padding.calculateStartPadding(layoutDirection).toPx(),
                properties.padding.calculateBottomPadding().toPx()
            )

            Alignment.BottomEnd -> max(
                properties.padding.calculateEndPadding(layoutDirection).toPx(),
                properties.padding.calculateBottomPadding().toPx()
            )

            else -> 0F
        }

        val safeMargin = textHeight / sqrt(2F) + 24F
        val requiredCenterDist = (textWidth / sqrt(2F)) + safeMargin

        val centerDist = max(axisOffsetPx + (ribbonHeightPx * sqrt(2F)) / 2F, requiredCenterDist)
        val axisRibbonHeight = ribbonHeightPx * sqrt(2F)

        val d1 = centerDist - axisRibbonHeight / 2F
        val d2 = centerDist + axisRibbonHeight / 2F

        val rotationDegrees: Float
        val centerX: Float
        val centerY: Float

        val path = Path().apply {

            when (alignment) {

                Alignment.TopEnd -> {

                    moveTo(size.width - d2, 0F)
                    lineTo(size.width - d1, 0F)
                    lineTo(size.width, d1)
                    lineTo(size.width, d2)
                    close()

                    rotationDegrees = 45F
                    centerX = size.width - centerDist / 2F
                    centerY = centerDist / 2F
                }

                Alignment.TopStart -> {

                    moveTo(d2, 0F)
                    lineTo(d1, 0F)
                    lineTo(0F, d1)
                    lineTo(0F, d2)
                    close()

                    rotationDegrees = -45F
                    centerX = centerDist / 2F
                    centerY = centerDist / 2F
                }

                Alignment.BottomEnd -> {

                    moveTo(size.width - d2, size.height)
                    lineTo(size.width - d1, size.height)
                    lineTo(size.width, size.height - d1)
                    lineTo(size.width, size.height - d2)
                    close()

                    rotationDegrees = -45F
                    centerX = size.width - centerDist / 2F
                    centerY = size.height - centerDist / 2F
                }

                Alignment.BottomStart -> {

                    moveTo(d2, size.height)
                    lineTo(d1, size.height)
                    lineTo(0F, size.height - d1)
                    lineTo(0F, size.height - d2)
                    close()

                    rotationDegrees = 45F
                    centerX = centerDist / 2F
                    centerY = size.height - centerDist / 2F
                }

                else -> return
            }
        }

        val ribbonWidth = centerDist * sqrt(2F) + ribbonHeightPx
        val ribbonSize = Size(width = ribbonWidth, height = ribbonHeightPx)

        drawPath(path = path, brush = SolidColor(properties.containerColor))

        shimmerDelegate?.getBrush(size = ribbonSize)?.let { brush ->

            clipPath(path = path) {

                rotate(degrees = rotationDegrees, pivot = Offset(x = centerX, y = centerY)) {

                    translate(
                        left = centerX - (ribbonWidth / 2F),
                        top = centerY - (ribbonHeightPx / 2F)
                    ) {

                        drawRect(brush = brush, size = ribbonSize)
                    }
                }
            }
        }

        rotate(degrees = rotationDegrees, pivot = Offset(x = centerX, y = centerY)) {

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = centerX - (textWidth / 2F),
                    y = centerY - (textHeight / 2F)
                )
            )
        }
    }

    /**
     * Updates the node's state with new parameters, manages the shimmer effect lifecycle, and
     * triggers a redraw of the component.
     *
     * @param text The new text to be displayed.
     * @param alignment The new corner alignment for the label.
     * @param properties The updated visual and animation properties.
     * @param textMeasurer The measurer for calculating text layout.
     */
    fun update(
        text: String,
        alignment: Alignment,
        properties: DiagonalLabelProperties,
        textMeasurer: TextMeasurer
    ) {


        val oldShimmer = this.properties.shimmerProperties

        this.text = text
        this.alignment = alignment
        this.properties = properties
        this.textMeasurer = textMeasurer

        val newShimmer = properties.shimmerProperties

        if (oldShimmer != newShimmer) if (newShimmer == null) {

            shimmerDelegate = null
        } else shimmerDelegate?.update(properties = newShimmer) ?: setLaunchAnimation()

        invalidateDraw()
    }
}