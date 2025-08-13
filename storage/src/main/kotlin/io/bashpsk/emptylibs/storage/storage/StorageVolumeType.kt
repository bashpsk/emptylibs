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
    INTERNAL(label = "Internal"),

    /**
     * Represents an SD card storage volume.
     */
    SD_CARD(label = "SD Card"),

    /**
     * OTG (On-The-Go) storage, typically a USB drive connected to the device.
     */
    OTG(label = "OTG"),

    /**
     * Represents an unknown storage volume type.
     * This is typically used as a fallback or when the volume type cannot be definitively
     * determined.
     */
    UNKNOWN(label = "Unknown");

    companion object {

        /**
         * Determines the [StorageVolumeType] of a given [StorageVolume].
         *
         * @param volume The [StorageVolume] to analyze.
         * @return The corresponding [StorageVolumeType].
         * - [INTERNAL] if the volume is the primary storage.
         * - [SD_CARD] if the volume is removable.
         * - [OTG] otherwise (assumed to be an On-The-Go device).
         */
        fun getVolumeType(volume: StorageVolume): StorageVolumeType {

            return when {

                volume.isPrimary -> INTERNAL
                volume.isRemovable -> SD_CARD
                else -> OTG
            }
        }
    }
}