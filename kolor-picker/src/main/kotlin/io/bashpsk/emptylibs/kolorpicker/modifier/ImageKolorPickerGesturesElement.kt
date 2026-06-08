package io.bashpsk.emptylibs.kolorpicker.modifier

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

internal class ImageKolorPickerGesturesElement(
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

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other !is ImageKolorPickerGesturesElement) return false
        if (onColorSelection != other.onColorSelection) return false

        return true
    }

    override fun hashCode(): Int {

        return onColorSelection.hashCode()
    }
}