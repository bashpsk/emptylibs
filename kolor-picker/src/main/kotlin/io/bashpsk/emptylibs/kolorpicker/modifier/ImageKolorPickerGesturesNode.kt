package io.bashpsk.emptylibs.kolorpicker.modifier

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode

internal class ImageKolorPickerGesturesNode(
    private var onColorSelection: (position: Offset) -> Unit
) : BaseKolorPickerGesturesNode() {

    override val tapNode = delegate(
        SuspendingPointerInputModifierNode {

            detectTapGestures(
                onPress = { offset ->

                    onColorSelection(offset)
                }
            )
        }
    )

    override val dragNode = delegate(
        SuspendingPointerInputModifierNode {

            detectDragGestures { change, _ ->

                onColorSelection(change.position)
                change.consume()
            }
        }
    )

    fun update(onColorSelection: (position: Offset) -> Unit) {

        this.onColorSelection = onColorSelection
    }
}