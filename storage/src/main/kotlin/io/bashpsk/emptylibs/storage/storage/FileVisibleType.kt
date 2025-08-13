package io.bashpsk.emptylibs.storage.storage

import android.util.Log
import java.io.File

/**
 * Represents the visibility type of a file.
 *
 * This enum class defines the possible visibility states for a file:
 * - `PUBLIC`: The file is not hidden and is generally accessible.
 * - `HIDDEN`: The file is marked as hidden by the operating system. Hidden files are typically not
 * displayed by default in file explorers.
 * - `UNKNOWN`: The visibility of the file could not be determined. This might occur due to an error
 * or an unsupported file system.
 *
 * Each visibility type has an associated `label` string for display purposes.
 *
 * The companion object provides a utility function `getFileVisibleType`
 * to determine the visibility of a given [File] object.
 */
enum class FileVisibleType(val label: String = "") {

    /**
     * File is visible in public.
     */
    PUBLIC(label = "Public"),

    /**
     * Represents a hidden file.
     * Hidden files are typically not displayed by default in file explorers.
     */
    HIDDEN(label = "Hidden"),

    /**
     * File visibility type is unknown.
     * This may occur if an error happened while checking file visibility.
     */
    UNKNOWN(label = "Unknown");

    companion object {

        /**
         * Determines the visibility type of a given file.
         *
         * This function checks if a file is hidden or public.
         * If an error occurs during the check, it defaults to [UNKNOWN].
         *
         * @param file The [File] object to check.
         * @return [FileVisibleType.HIDDEN] if the file is hidden,
         * [FileVisibleType.PUBLIC] if the file is not hidden,
         * or [FileVisibleType.UNKNOWN] if an exception occurs during the check.
         */
        fun getFileVisibleType(file: File): FileVisibleType {

            return try {

                if (file.isHidden) HIDDEN else PUBLIC
            } catch (exception: Exception) {

                Log.w("StorageExt", exception.message, exception)
                UNKNOWN
            }
        }
    }
}