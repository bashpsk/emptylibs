package io.bashpsk.emptylibs.datastoreui.font

import androidx.annotation.FontRes
import androidx.compose.runtime.Stable

/**
 * Represents a single selectable font option within a preference UI.
 *
 * This interface defines the necessary metadata to display a font choice to the user
 * and retrieve the underlying Android font resource.
 *
 * @property label The human-readable name of the font to be displayed in the UI.
 * @property resId The Android font resource identifier associated with this preference item.
 */
@Stable
interface FontPreferenceItem {

    val label: String

    @get:FontRes
    val resId: Int
}