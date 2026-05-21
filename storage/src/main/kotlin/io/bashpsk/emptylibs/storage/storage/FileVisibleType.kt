package io.bashpsk.emptylibs.storage.storage

import android.util.Log
import io.bashpsk.emptylibs.storage.utils.LOG_TAG
import java.io.File

/**
 * Represents the visibility type of file.
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
enum class FileVisibleType {

    /**
     * File is visible in public.
     */
    Public,

    /**
     * Represents a hidden file.
     * Hidden files are typically not displayed by default in file explorers.
     */
    Hidden,

    /**
     * File visibility type is unknown.
     * This may occur if an error happened while checking file visibility.
     */
    Unknown;

    companion object {

        /**
         * Determines the visibility type of given file.
         *
         * This function checks if a file is hidden or public.
         * If an error occurs during the check, it defaults to [Unknown].
         *
         * @return [FileVisibleType.Hidden] if the file is hidden,
         * [FileVisibleType.Public] if the file is not hidden,
         * or [FileVisibleType.Unknown] if an exception occurs during the check.
         */
        fun File.getFileVisibleType(): FileVisibleType {

            return try {

                if (isHidden) Hidden else Public
            } catch (exception: Exception) {

                Log.w(LOG_TAG, exception.message, exception)
                Unknown
            }
        }
    }
}