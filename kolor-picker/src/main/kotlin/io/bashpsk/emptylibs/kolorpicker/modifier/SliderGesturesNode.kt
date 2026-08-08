package io.bashpsk.emptylibs.kolorpicker.modifier

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode

internal class SliderGesturesNode(
    private var thumbRadiusPx: Float,
    private var range: ClosedFloatingPointRange<Float>,
    private var onValueChanged: (Float) -> Unit
) : BaseKolorPickerGesturesNode() {

    override val tapNode = delegate(
        SuspendingPointerInputModifierNode {

            detectTapGestures(
                onPress = { position ->

                    val panelWidth = panelSize.width.toFloat()

                    if (panelWidth > 0) {

                        val newX = position.x.coerceIn(
                            range = thumbRadiusPx..panelWidth - thumbRadiusPx
                        )
                        val sliderWidth = panelWidth - (2 * thumbRadiusPx)
                        val normalizedPosition = if (sliderWidth > 0) {
                            (newX - thumbRadiusPx) / sliderWidth
                        } else 0F
                        val newValue = range.start + (normalizedPosition * (
                                range.endInclusive - range.start)
                                )

                        onValueChanged(newValue.coerceIn(range = range))
                    }
                }
            )
        }
    )

    override val dragNode = delegate(
        SuspendingPointerInputModifierNode {

            detectDragGestures { change, _ ->

                val panelWidth = panelSize.width.toFloat()

                if (panelWidth > 0) {

                    val newX = change.position.x.coerceIn(
                        range = thumbRadiusPx..panelWidth - thumbRadiusPx
                    )
                    val sliderWidth = panelWidth - (2 * thumbRadiusPx)
                    val normalizedPosition = if (sliderWidth > 0) {
                        (newX - thumbRadiusPx) / sliderWidth
                    } else 0F
                    val newValue = range.start + (normalizedPosition * (
                            range.endInclusive - range.start)
                            )

                    onValueChanged(newValue.coerceIn(range = range))
                    change.consume()
                }
            }
        }
    )

    fun update(
        thumbRadiusPx: Float,
        range: ClosedFloatingPointRange<Float>,
        onValueChanged: (Float) -> Unit
    ) {

        this.thumbRadiusPx = thumbRadiusPx
        this.range = range
        this.onValueChanged = onValueChanged
    }
}