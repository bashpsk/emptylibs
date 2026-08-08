package io.bashpsk.emptylibs.kolorpicker.modifier

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

internal data class ImageKolorPickerGesturesElement(
    private val onColorSelection: (position: Offset) -> Unit
) : ModifierNodeElement<ImageKolorPickerGesturesNode>() {

    override fun create(): ImageKolorPickerGesturesNode {

        return ImageKolorPickerGesturesNode(onColorSelection = onColorSelection)
    }

    override fun update(node: ImageKolorPickerGesturesNode) {

        node.update(onColorSelection = onColorSelection)
    }

    override fun InspectorInfo.inspectableProperties() {

        name = "imageKolorPickerGestures"
        properties["onColorSelection"] = onColorSelection
    }
}