package io.bashpsk.emptylibs.jetpackui.layout

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect

/**
 * Receiver scope for the content of a zoomable layout.
 *
 * This scope provides information about the current state of the zoomable area,
 * such as the visible portion of the content.
 */
@Stable
interface ZoomableLayoutScope {

    val viewport: Rect
}

/**
 * Implementation of [ZoomableLayoutScope] that manages the current viewport state.
 *
 * This class tracks the visible area of the zoomable layout using a reactive [viewport]
 * property, allowing UI components to respond to changes in position and scale.
 */
@Stable
internal class ZoomableLayoutScopeImpl : ZoomableLayoutScope {

    override var viewport: Rect by mutableStateOf(Rect.Zero)
}