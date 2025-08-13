package io.bashpsk.emptylibs.storage.storage

/**
 * Represents the result of a file creation operation.
 *
 * This sealed interface defines the possible outcomes when attempting to create a file.
 * It can either be a success, indicate that the file already exists, or represent a failure.
 */
sealed interface MakeFileResult {

    /**
     * Represents a failed attempt to create a file.
     *
     * @property message A message describing the reason for the failure.
     */
    data class Failed(val message: String) : MakeFileResult

    /**
     * Represents the result when a file already exists.
     *
     * @property path The absolute path to the existing file.
     * @property name The name of the existing file.
     */
    data class Exist(val path: String, val name: String) : MakeFileResult

    /**
     * Represents a successful file creation operation.
     *
     * @property path The absolute path of the newly created file.
     * @property name The name of the newly created file.
     */
    data class Success(val path: String, val name: String) : MakeFileResult
}