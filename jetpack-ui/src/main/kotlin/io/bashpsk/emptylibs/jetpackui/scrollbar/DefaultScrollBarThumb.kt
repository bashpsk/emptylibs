package io.bashpsk.emptylibs.jetpackui.scrollbar

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * The default thumb used by [LazyScrollBar].
 */
@PublishedApi
@Composable
internal fun DefaultScrollBarThumb() {

    Icon(
        modifier = Modifier.size(size = 28.dp),
        imageVector = Icons.Filled.DragIndicator,
        contentDescription = "Drag Indicator"
    )
}