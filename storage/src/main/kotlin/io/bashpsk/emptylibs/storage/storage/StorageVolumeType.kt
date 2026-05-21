package io.bashpsk.emptylibs.storage.storage

import android.os.storage.StorageVolume

/**
 * Represents the different types of storage volumes available on an Android device.
 * Each type has a human-readable label.
 *
 * @property label The human-readable label for the storage volume type.
 */
enum class StorageVolumeType(val label: String = "") {

    /**
     * Represents the internal storage of the device.
     */
    Internal(label = "Internal"),

    /**
     * Represents an SD card storage volume.
     */
    SdCard(label = "SD Card"),

    /**
     * OTG (On-The-Go) storage, typically a USB drive connected to the device.
     */
    Otg(label = "OTG"),

    /**
     * Represents an unknown storage volume type.
     * This is typically used as a fallback or when the volume type cannot be definitively
     * determined.
     */
    Unknown(label = "Unknown");

    companion object {

        /**
         * Determines the [StorageVolumeType] of this [StorageVolume].
         */
        fun StorageVolume.getVolumeType(): StorageVolumeType {

            return when {

                isPrimary -> Internal
                isRemovable -> SdCard
                else -> Otg
            }
        }
    }
}