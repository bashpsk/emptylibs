package io.bashpsk.emptylibs.formatter.format

import androidx.compose.runtime.Stable
import io.bashpsk.emptylibs.formatter.resolution.ResolutionType

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
 * @return A `String` containing the standard resolution label (e.g., "1080p FHD") or the
 * raw dimensions (e.g., "1366x768").
 *
 * @sample
 * ```
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
 * Finds a human-readable resolution label (e.g., "1080p HD", "4K UHD") for a given width and
 * height, returning null if no standard match is found.
 *
 * This function maps common video and display resolutions to their standard labels, such as
 * SD, HD, Full HD (FHD), Quad HD (QHD), and various Ultra HD (UHD) standards up to 16K.
 * It prioritizes the vertical dimension (height) for labeling, which is a common convention
 * (e.g., 1080p refers to 1080 pixels of vertical resolution).
 *
 * Unlike `findResolutionLabel`, this function returns `null` if the resolution does not match
 * any predefined standard, allowing for custom fallback handling.
 *
 * @param width The width of the resolution in pixels.
 * @param height The height of the resolution in pixels.
 * @return A `String` containing the standard resolution label (e.g., "1080p FHD"), or `null`
 * if the resolution is not a recognized standard.
 *
 * @sample
 * ```
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