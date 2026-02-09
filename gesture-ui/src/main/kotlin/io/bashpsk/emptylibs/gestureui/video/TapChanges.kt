package io.bashpsk.emptylibs.gestureui.video

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset

/**
 * Represents the different types of tap gestures that can be detected.
 * This sealed interface is used to categorize tap events based on their nature
 * and the position on the screen where they occurred.
 *
 * Possible tap changes include:
 * - [Unknown]: Represents an unrecognized or indeterminate tap state.
 * - [SingleTap]: Represents a standard single tap gesture.
 * - [ForwardTap]: Represents a tap gesture that is typically interpreted as a "forward" action,
 *   often associated with tapping on the right side of a view.
 * - [BackwardTap]: Represents a tap gesture that is typically interpreted as a "backward" action,
 *   often associated with tapping on the left side of a view.
 */
@Stable
sealed interface TapChanges {

    /**
     * Represents an unknown or unclassified tap change.
     * This can be used as a default or initial state.
     */
    data object Unknown : TapChanges

    /**
     * Represents a single tap gesture.
     *
     * @property position The [Offset] where the tap occurred.
     */
    data class SingleTap(val position: Offset) : TapChanges

    /**
     * Represents a forward tap gesture, often associated with actions like fast-forward.
     *
     * @property position The [Offset] coordinates where the forward tap occurred.
     */
    data class ForwardTap(val position: Offset) : TapChanges

    /**
     * Represents a backward tap gesture, often associated with actions like fast-backward.
     *
     * @property position The [Offset] coordinates where the backward tap occurred.
     */
    data class BackwardTap(val position: Offset) : TapChanges
}