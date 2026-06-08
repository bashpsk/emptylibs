package io.bashpsk.emptylibs.formatter.format

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import io.bashpsk.emptylibs.formatter.resolution.ResolutionType
import kotlin.math.roundToInt

/**
 * Finds a human-readable resolution label (e.g., "1080p HD", "4K UHD") for a given width and
 * height.
 *
 * This function maps common video and display resolutions to their standard labels, such as
 * SD, HD, Full HD (FHD), Quad HD (QHD), and various Ultra HD (UHD) standards up to 16K.
 * It prioritizes the vertical dimension (height) for labeling, which is a common convention
 * (e.g., 1080p refers to 1080 pixels of vertical resolution).
 *
 * If a resolution does not match any predefined standard, the function returns a string
 * representing the raw dimensions in the format `"width x height"`.
 *
 * @param width The width of the resolution in pixels.
 * @param height The height of the resolution in pixels.
 * @return A [String] containing the standard resolution label (e.g., "1080p FHD") or the
 * raw dimensions (e.g., "1366x768").
 *
 * Example usage:
 * ```kotlin
 * findResolutionLabel(1920, 1080) // returns "1080p FHD"
 * findResolutionLabel(3840, 2160) // returns "4K UHD"
 * findResolutionLabel(1366, 768)  // returns "1366x768"
 * ```
 */
@Stable
fun findResolutionLabel(width: Int, height: Int): String {

    return findResolutionLabelOrNull(width = width, height = height) ?: "${width}x${height}"
}

/**
 * Finds a human-readable resolution label (e.g., "1080p HD", "4K UHD") for a given [Size].
 *
 * This function maps common video and display resolutions to their standard labels, such as
 * SD, HD, Full HD (FHD), Quad HD (QHD), and various Ultra HD (UHD) standards up to 16K.
 * It prioritizes the vertical dimension (height) for labeling, which is a common convention
 * (e.g., 1080p refers to 1080 pixels of vertical resolution).
 *
 * If a resolution does not match any predefined standard, the function returns a string
 * representing the raw dimensions in the format `"width x height"`.
 *
 * @return A [String] containing the standard resolution label (e.g., "1080p FHD") or the
 * raw dimensions (e.g., "1366x768").
 *
 * Example usage:
 * ```kotlin
 * Size(1920, 1080).findResolutionLabel() // returns "1080p FHD"
 * Size(3840, 2160).findResolutionLabel() // returns "4K UHD"
 * Size(1366, 768).findResolutionLabel()  // returns "1366x768"
 * ```
 */
@Stable
fun Size.findResolutionLabel(): String {

    return this.findResolutionLabelOrNull() ?: "${width.roundToInt()}x${height.roundToInt()}"
}

/**
 * Finds a human-readable resolution label (e.g., "1080p HD", "4K UHD") for a given [IntSize].
 *
 * This function maps common video and display resolutions to their standard labels, such as
 * SD, HD, Full HD (FHD), Quad HD (QHD), and various Ultra HD (UHD) standards up to 16K.
 * It prioritizes the vertical dimension (height) for labeling, which is a common convention
 * (e.g., 1080p refers to 1080 pixels of vertical resolution).
 *
 * If a resolution does not match any predefined standard, the function returns a string
 * representing the raw dimensions in the format `"width x height"`.
 *
 * @return A [String] containing the standard resolution label (e.g., "1080p FHD") or the
 * raw dimensions (e.g., "1366x768").
 *
 * Example usage:
 * ```kotlin
 * IntSize(1920, 1080).findResolutionLabel() // returns "1080p FHD"
 * IntSize(3840, 2160).findResolutionLabel() // returns "4K UHD"
 * IntSize(1366, 768).findResolutionLabel()  // returns "1366x768"
 * ```
 */
@Stable
fun IntSize.findResolutionLabel(): String {

    return this.findResolutionLabelOrNull() ?: "${width}x${height}"
}

/**
 * Finds a human-readable resolution label (e.g., "1080p HD", "4K UHD") for a given width and
 * height, returning `null` if no standard match is found.
 *
 * This function maps common video and display resolutions to their standard labels, such as
 * SD, HD, Full HD (FHD), Quad HD (QHD), and various Ultra HD (UHD) standards up to 16K.
 * It prioritizes the vertical dimension (height) for labeling, which is a common convention
 * (e.g., 1080p refers to 1080 pixels of vertical resolution).
 *
 * Unlike [findResolutionLabel], this function returns `null` if the resolution does not match
 * any predefined standard, allowing for custom fallback handling.
 *
 * @param width The width of the resolution in pixels.
 * @param height The height of the resolution in pixels.
 * @return A [String] containing the standard resolution label (e.g., "1080p FHD"), or `null`
 * if the resolution is not a recognized standard.
 *
 * Example usage:
 * ```kotlin
 * findResolutionLabelOrNull(1920, 1080) // returns "1080p FHD"
 * findResolutionLabelOrNull(3840, 2160) // returns "4K UHD"
 * findResolutionLabelOrNull(1366, 768)  // returns null
 * ```
 * @see findResolutionLabel
 */
@Stable
fun findResolutionLabelOrNull(width: Int, height: Int): String? {

    return ResolutionType.findOrNull(width = width, height = height)?.label
}

/**
 * Finds a human-readable resolution label (e.g., "1080p HD", "4K UHD") for a given [Size],
 * returning `null` if no standard match is found.
 *
 * This function maps common video and display resolutions to their standard labels, such as
 * SD, HD, Full HD (FHD), Quad HD (QHD), and various Ultra HD (UHD) standards up to 16K.
 * It prioritizes the vertical dimension (height) for labeling, which is a common convention
 * (e.g., 1080p refers to 1080 pixels of vertical resolution).
 *
 * Unlike [findResolutionLabel], this function returns `null` if the resolution does not match
 * any predefined standard, allowing for custom fallback handling.
 *
 * @return A [String] containing the standard resolution label (e.g., "1080p FHD"), or `null`
 * if the resolution is not a recognized standard.
 *
 * Example usage:
 * ```kotlin
 * Size(1920, 1080).findResolutionLabel() // returns "1080p FHD"
 * Size(3840, 2160).findResolutionLabel() // returns "4K UHD"
 * Size(1366, 768).findResolutionLabel()  // returns null
 * ```
 * @see findResolutionLabel
 */
@Stable
fun Size.findResolutionLabelOrNull(): String? {

    return findResolutionLabelOrNull(width = width.roundToInt(), height = height.roundToInt())
}

/**
 * Finds a human-readable resolution label (e.g., "1080p HD", "4K UHD") for a given [IntSize],
 * returning `null` if no standard match is found.
 *
 * This function maps common video and display resolutions to their standard labels, such as
 * SD, HD, Full HD (FHD), Quad HD (QHD), and various Ultra HD (UHD) standards up to 16K.
 * It prioritizes the vertical dimension (height) for labeling, which is a common convention
 * (e.g., 1080p refers to 1080 pixels of vertical resolution).
 *
 * Unlike [findResolutionLabel], this function returns `null` if the resolution does not match
 * any predefined standard, allowing for custom fallback handling.
 *
 * @return A [String] containing the standard resolution label (e.g., "1080p FHD"), or `null`
 * if the resolution is not a recognized standard.
 *
 * Example usage:
 * ```kotlin
 * IntSize(1920, 1080).findResolutionLabel() // returns "1080p FHD"
 * IntSize(3840, 2160).findResolutionLabel() // returns "4K UHD"
 * IntSize(1366, 768).findResolutionLabel()  // returns null
 * ```
 * @see findResolutionLabel
 */
@Stable
fun IntSize.findResolutionLabelOrNull(): String? {

    return findResolutionLabelOrNull(width = width, height = height)
}