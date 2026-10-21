package io.bashpsk.emptylibs.layouts.collapsible

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class CollapsibleLayoutShape(
    val collapsedShape: Shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
    val expandedShape: Shape = RectangleShape
)