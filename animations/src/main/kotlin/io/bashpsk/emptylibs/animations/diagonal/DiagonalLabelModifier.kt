package io.bashpsk.emptylibs.animations.diagonal

import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextMeasurer

/**
 * Adds a diagonal text label to the modifier, typically used for badges or banners
 * (e.g., "NEW", "OFFER").
 *
 * The label is drawn as a diagonal ribbon at one of the corners of the content.
 *
 * @param text The text to be displayed on the diagonal label.
 * @param textMeasurer A [TextMeasurer] to measure the text dimensions.
 * @param alignment The corner where the label should be placed.
 * Supported values: [Alignment.TopStart], [Alignment.TopEnd], [Alignment.BottomStart],
 * [Alignment.BottomEnd].
 * @param properties The visual and animation properties for the label.
 */
@Stable
fun Modifier.diagonalLabel(
    text: String,
    textMeasurer: TextMeasurer,
    alignment: Alignment = Alignment.TopEnd,
    properties: DiagonalLabelProperties = DiagonalLabelProperties()
): Modifier {

    return this then DiagonalLabelElement(
        text = text,
        alignment = alignment,
        properties = properties,
        textMeasurer = textMeasurer
    )
}