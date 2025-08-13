package io.bashpsk.emptylibs.imageview.transform

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Configuration for image transformation capabilities.
 *
 * This data class defines the settings for enabling or disabling various image manipulation
 * features such as zooming, rotation, panning, and swiping. It is designed to be immutable,
 * parcelable for Android, and serializable for data persistence or transfer.
 *
 * @property enableZoom Whether zooming the image is allowed. Defaults to `true`.
 * @property enableRotation Whether rotating the image is allowed. Defaults to `true`.
 * @property enablePan Whether panning (moving) the image is allowed. Defaults to `true`.
 * @property enableSwipe Whether swiping gestures on the image are allowed. Defaults to `false`.
 */
@Immutable
@Parcelize
@Serializable
data class TransformImageConfig(
    val enableZoom: Boolean = true,
    val enableRotation: Boolean = true,
    val enablePan: Boolean = true,
    val enableSwipe: Boolean = false
) : Parcelable