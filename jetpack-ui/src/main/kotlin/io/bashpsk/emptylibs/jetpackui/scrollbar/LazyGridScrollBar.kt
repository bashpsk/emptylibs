package io.bashpsk.emptylibs.jetpackui.scrollbar

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A scrollbar for [LazyGridState] that appears when scrolling and fades out after a short delay.
 *
 * @param modifier The [Modifier] to be applied to the scrollbar.
 * @param state The [LazyGridState] to attach the scrollbar to.
 * @param orientation The [Orientation] of the scrollbar. Defaults to the orientation of the
 * [LazyGridState].
 * @param alignment The [Alignment] of the scrollbar. Defaults to [Alignment.TopEnd] for vertical
 * grids and [Alignment.BottomStart] for horizontal grids.
 * @param thumbColor The [Color] of the scrollbar thumb.
 * @param thumbNotchWidth The width of the notch on the scrollbar thumb.
 * @param label A composable that displays the current item index.
 * @param thumb The composable to be used as the scrollbar thumb.
 */
@Composable
inline fun BoxWithConstraintsScope.LazyGridScrollBar(
    modifier: Modifier = Modifier,
    state: LazyGridState,
    orientation: Orientation = state.layoutInfo.orientation,
    alignment: Alignment = when (orientation) {

        Orientation.Vertical -> Alignment.TopEnd
        Orientation.Horizontal -> Alignment.BottomStart
    },
    thumbColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    thumbNotchWidth: Dp = 12.dp,
    crossinline label: @Composable (
        firstVisibleItemIndex: Int,
        visibleItemsCount: Int,
        totalItemsCount: Int
    ) -> Unit = { _, _, _ -> },
    crossinline thumb: @Composable BoxScope.() -> Unit = {

        DefaultScrollBarThumb()
    }
) {

    val scrollBarState = rememberLazyGridScrollBarState(state = state)

    LazyScrollBar(
        state = scrollBarState,
        orientation = orientation,
        modifier = modifier,
        alignment = alignment,
        thumbColor = thumbColor,
        thumbNotchWidth = thumbNotchWidth,
        label = label,
        thumb = thumb
    )
}