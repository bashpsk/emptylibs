package io.bashpsk.emptylibs.imagekolor.color

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Configuration class for image color adjustments.
 *
 * This data class defines which color adjustment features are enabled.
 * Each property corresponds to a specific color adjustment type.
 * By default, all adjustments are enabled.
 *
 * @property enableBrightness Whether brightness adjustment is enabled.
 * @property enableExposure Whether exposure adjustment is enabled.
 * @property enableContrast Whether contrast adjustment is enabled.
 * @property enableHighlights Whether highlights adjustment is enabled.
 * @property enableShadows Whether shadows adjustment is enabled.
 * @property enableSaturation Whether saturation adjustment is enabled.
 * @property enableWarmth Whether warmth adjustment is enabled.
 * @property enableTint Whether tint adjustment is enabled.
 */
@Immutable
@Parcelize
@Serializable
data class ImageKolorConfig(
    val enableBrightness: Boolean = true,
    val enableExposure: Boolean = true,
    val enableContrast: Boolean = true,
    val enableHighlights: Boolean = true,
    val enableShadows: Boolean = true,
    val enableSaturation: Boolean = true,
    val enableWarmth: Boolean = true,
    val enableTint: Boolean = true
) : Parcelable