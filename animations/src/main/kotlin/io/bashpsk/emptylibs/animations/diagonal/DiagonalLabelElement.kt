package io.bashpsk.emptylibs.animations.diagonal

import androidx.compose.ui.Alignment
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.text.TextMeasurer

/**
 * A [ModifierNodeElement] that creates and updates [DiagonalLabelNode].
 *
 * @property text The text to be displayed.
 * @property alignment The alignment of the label.
 * @property properties The properties for the label.
 * @property textMeasurer The measurer for the text.
 */
internal data class DiagonalLabelElement(
    val text: String,
    val alignment: Alignment,
    val properties: DiagonalLabelProperties,
    val textMeasurer: TextMeasurer
) : ModifierNodeElement<DiagonalLabelNode>() {

    override fun create(): DiagonalLabelNode {

        return DiagonalLabelNode(
            text = text,
            alignment = alignment,
            properties = properties,
            textMeasurer = textMeasurer
        )
    }

    override fun update(node: DiagonalLabelNode) {

        node.update(
            text = text,
            alignment = alignment,
            properties = properties,
            textMeasurer = textMeasurer
        )
    }

    override fun InspectorInfo.inspectableProperties() {

        name = "diagonalLabel"
        properties["text"] = text
        properties["alignment"] = alignment
        properties["properties"] = properties
    }
}