package io.bashpsk.emptylibs.storage.storage

import android.content.Context
import android.os.storage.StorageManager
import android.util.Log
import io.bashpsk.emptylibs.storage.storage.StorageExt.getDirectoryData
import io.bashpsk.emptylibs.storage.storage.StorageExt.getDirectoryFileFlow
import io.bashpsk.emptylibs.storage.storage.StorageExt.getFileSize
import io.bashpsk.emptylibs.storage.storage.StorageExt.getFreeMemory
import io.bashpsk.emptylibs.storage.storage.StorageExt.getStorageVolumeFlow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Provides utility functions for interacting with device storage.
 *
 * This object includes methods for:
 * - Retrieving information about available storage volumes.
 * - Listing files and directories.
 * - Getting metadata for files and directories (name, path, size, type, etc.).
 * - Creating new files and folders.
 * - Calculating storage space (total, free, used).
 *
 * Many operations are performed on the [Dispatchers.IO] thread to avoid blocking the main thread.
 * Error handling is implemented, with relevant messages logged for debugging.
 */
@Suppress("unused")
object StorageExt {

    private val emptyStorage: EmptyStorage = EmptyStorageImpl()

    /**
     * Retrieves a list of available storage volumes on the device.
     *
     * This function uses the [StorageManager] to get information about the storage volumes.
     *
     * Each storage volume is represented by a [StorageVolumeData].
     *
     * The function returns a [Flow] that emits an [ImmutableList] of [StorageVolumeData].
     * If an error occurs during the process, it logs a warning and emits an empty list.
     * The operation is performed on the [Dispatchers.IO] thread.
     *
     * @param context The application context.
     * @return A [Flow] emitting an [ImmutableList] of [StorageVolumeData] representing the
     * available storage volumes.
     * Emits an empty list if an error occurs or no volumes are found.
     */
    fun getStorageVolumeFlow(context: Context): Flow<ImmutableList<StorageVolumeData>> {

        return flow {

            emit(value = emptyStorage.getStorageVolumeList(context = context))
        }.flowOn(context = Dispatchers.IO)
    }

    /**
     * Retrieves a list of available storage volumes on the device.
     *
     * This function is a synchronous call that fetches information about the device's storage
     * volumes using the [StorageManager]. For a non-blocking, asynchronous alternative,
     * see [getStorageVolumeFlow].
     *
     * It wraps the underlying storage access logic and returns the result directly.
     * If an error occurs during the process, it will be handled by the underlying
     * implementation, which typically logs a warning and returns an empty list.
     *
     * Each storage volume is represented by a [StorageVolumeData] object, which contains
     * details like its path, description, state, and storage capacity.
     *
     * @param context The application context, used to access system services like [StorageManager].
     * @return An [ImmutableList] of [StorageVolumeData] representing the available storage volumes.
     * Returns an empty list if no volumes are found or an error occurs.
     */
    suspend fun getStorageVolumeList(context: Context): ImmutableList<StorageVolumeData> {

        return emptyStorage.getStorageVolumeList(context = context)
    }

    /**
     * Retrieves a [Flow] of [DirectoryFileData] for the specified directory path.
     *
     * This function lists the files and subdirectories within the given path,
     * gathers their information, and emits a [DirectoryFileData].
     *
     * The operation is performed on the [Dispatchers.IO] thread.
     * In case of an exception during the process, an empty [DirectoryFileData] object is emitted,
     * and a warning is logged.
     *
     * @param context The application [Context].
     * @param path The absolute path of the directory to list files from.
     * @return A [Flow] that emits a single [DirectoryFileData] object.
     */
    fun getDirectoryFileFlow(context: Context, path: String): Flow<DirectoryFileData> {

        return flow {

            emit(value = emptyStorage.getDirectoryFileData(context = context, path = path))
        }.flowOn(context = Dispatchers.IO)
    }

    /**
     * Retrieves information about the contents of a specified directory.
     *
     * This function lists the files and subdirectories within the given `path`,
     * gathers their metadata (such as name, path, size, and type), and returns
     * the result in a [DirectoryFileData] object.
     *
     * The operation is performed synchronously on the calling thread. For a non-blocking
     * version that returns a [Flow] and operates on [Dispatchers.IO], see [getDirectoryFileFlow].
     *
     * In case of an exception (e.g., the path does not exist or is not a directory),
     * an empty [DirectoryFileData] object is returned, and a warning is logged.
     *
     * @param context The application [Context].
     * @param path The absolute path of the directory to analyze.
     * @return A [DirectoryFileData] object containing lists of files and subdirectories.
     * Returns an empty object on error.
     */
    suspend fun getDirectoryFileData(context: Context, path: String): DirectoryFileData {

        return emptyStorage.getDirectoryFileData(context = context, path = path)
    }

    /**
     * Finds the [StorageVolumeData] for the given file path from a list of storage volumes.
     *
     * @param path The absolute path of the file or directory.
     * @param storageVolumes A list of [StorageVolumeData] to search within. Defaults to an empty
     * persistent list.
     * @return The [StorageVolumeData] that contains the given path, or null if not found.
     */
    fun findStorageVolumeData(
        path: String,
        storageVolumes: Iterable<StorageVolumeData> = persistentListOf()
    ): StorageVolumeData? {

        return emptyStorage.findStorageVolumeData(path = path, storageVolumes = storageVolumes)
    }

    /**
     * Finds the [StorageVolumeData] that contains the given [file].
     *
     * @param file The file to find the storage volume for.
     * @param storageVolumes A list of available [StorageVolumeData] to search within.
     * Defaults to an empty persistent list.
     * @return The [StorageVolumeData] that contains the file, or null if not found.
     */
    fun findStorageVolumeData(
        file: File,
        storageVolumes: Iterable<StorageVolumeData> = persistentListOf()
    ): StorageVolumeData? {

        return emptyStorage.findStorageVolumeData(path = file.path, storageVolumes = storageVolumes)
    }

    /**
     * Retrieves data for a specific directory.
     *
     * This function is an overload of [getDirectoryData] that takes a file path as a string.
     * It creates a [File] object from the given path and then calls the primary [getDirectoryData]
     * function.
     *
     * @param path The absolute path to the directory.
     * @param storageVolumes An optional immutable list of [StorageVolumeData] objects.
     * If provided, it's used to determine the storage volume the directory belongs to.
     * Defaults to an empty persistent list.
     * @return A [DirectoryData] object containing information about the directory,
     * or null if an error occurs (e.g., the path does not exist or is not a directory).
     */
    suspend fun getDirectoryData(
        path: String,
        storageVolumes: Iterable<StorageVolumeData> = persistentListOf()
    ): DirectoryData? {

        return emptyStorage.getDirectoryData(path = path, storageVolumes = storageVolumes)
    }

    /**
     * Retrieves data for a given directory.
     *
     * This function takes a [File] object representing a directory and an optional list of
     * [StorageVolumeData] objects. It attempts to gather information about the directory,
     * such as its name, path, URI, visibility, number of subfolders and files,
     * last modified date, and the storage volume it belongs to.
     *
     * @param file The [File] object representing the directory.
     * @param storageVolumes An optional immutable list of [StorageVolumeData] objects.
     * This list is used to determine the storage volume the directory resides on.
     * Defaults to an empty persistent list.
     * @return A [DirectoryData] object containing the information about the directory if
     * successful, or `null` if an exception occurs during the process.
     */
    suspend fun getDirectoryData(
        file: File,
        storageVolumes: Iterable<StorageVolumeData> = persistentListOf()
    ): DirectoryData? {

        return emptyStorage.getDirectoryData(path = file.path, storageVolumes = storageVolumes)
    }

    /**
     * Retrieves metadata for a file at the specified path.
     *
     * This function constructs a [FileData] object containing information such as the file's name,
     * path, URI, extension, visibility, type, size, and last modified date.
     * It also attempts to determine the storage volume the file belongs to.
     *
     * @param path The absolute path to the file.
     * @param storageVolumes An optional list of [StorageVolumeData] to help identify the storage
     * volume. Defaults to an empty persistent list.
     * @return A [FileData] object if the file exists and its information can be retrieved,
     * otherwise `null`.
     */
    suspend fun getFileData(
        path: String,
        storageVolumes: Iterable<StorageVolumeData> = persistentListOf()
    ): FileData? {

        return emptyStorage.getFileData(path = path, storageVolumes = storageVolumes)
    }

    /**
     * Retrieves metadata for a given file.
     *
     * This function attempts to gather various details about the specified file,
     * including its name, path, URI, extension, visibility, type, size,
     * last modified date, and the storage volume it belongs to.
     *
     * @param file The [File] object for which to retrieve data.
     * @param storageVolumes An optional immutable list of [StorageVolumeData] objects.
     * If provided, it's used to determine the storage volume of the file.
     * Defaults to an empty list.
     * @return A [FileData] object containing the file's metadata if successful,
     * or `null` if an error occurs during the process (e.g., file not found, permission issues).
     */
    suspend fun getFileData(
        file: File,
        storageVolumes: Iterable<StorageVolumeData> = persistentListOf()
    ): FileData? {

        return emptyStorage.getFileData(path = file.path, storageVolumes = storageVolumes)
    }

    /**
     * Recursively scans a directory and its subdirectories to find files with specified extensions.
     *
     * This function traverses the file system starting from the given `path`, collects all files
     * that match any of the provided `extensions`, and emits the results as a `Flow` of
     * `ImmutableList<FileData>`. The scan is performed on the [Dispatchers.IO] thread.
     *
     * The search is case-insensitive for file extensions. For example, providing "jpg" will match
     * ".jpg", ".JPG", and ".jPg".
     *
     * This operation can be resource-intensive for large directories. The `Flow` allows for
     * handling the results as they are found without blocking the UI.
     *
     * @param path The absolute path of the root directory to start the scan from.
     * @param extensions A list of file extensions to search for (e.g., "txt", "jpg", "pdf").
     * The leading dot should be omitted.
     * @return A [Flow] that emits an [ImmutableList] of [FileData] objects for the matching files
     * found. If an error occurs or no files are found, it may emit an empty list.
     */
    fun getFileListByExtensionsFlow(
        context: Context,
        path: String,
        extensions: Iterable<String>
    ): Flow<ImmutableList<FileData>> {

        return flow {

            val fileList = emptyStorage.getFileListByExtensions(
                context = context,
                path = path,
                extensions = extensions
            )

            emit(value = fileList)
        }.flowOn(context = Dispatchers.IO)
    }

    /**
     * Searches for files with specific extensions within a given directory and its subdirectories.
     *
     * This function traverses the file system starting from the given `path`.
     * It collects all files that match any of the provided `extensions` and emits them as an
     * immutable list of [FileData] objects.
     *
     * The search is performed asynchronously on the [Dispatchers.IO] thread to avoid blocking
     * the main thread.
     *
     * If an error occurs (e.g., the path is invalid, not a directory, or inaccessible),
     * it logs the error and emits an empty list.
     *
     * @param path The absolute path of the root directory to start the search from.
     * @param extensions An immutable list of file extensions to search for (e.g., "txt", "jpg").
     * The matching is case-insensitive.
     * @return A [ImmutableList] of [FileData] for all matching files found.
     * Emits an empty list if no files are found or an error occurs.
     */
    suspend fun getFileListByExtensions(
        context: Context,
        path: String,
        extensions: Iterable<String>
    ): ImmutableList<FileData> {

        return emptyStorage.getFileListByExtensions(
            context = context,
            path = path,
            extensions = extensions
        )
    }

    /**
     * Recursively searches for files and directories within a specified path that match a query.
     *
     * This function performs a top-down traversal of the file system starting from [path].
     * It filters for files and directories whose names contain the [query] string,
     * performing a case-insensitive search. The results are collected into a
     * [DirectoryFileData] object, which contains separate lists for folders and files.
     *
     * Note: The starting directory [path] is excluded from the resulting folders list
     * even if its name matches the query.
     *
     * @param context The Android context used to retrieve storage volume information.
     * @param path The absolute path of the directory to start the recursive search from.
     * @param query The search term to match against file and directory names.
     * @param includeFolders Whether to include matching directories in the results.
     * Defaults to `true`.
     * @param extensions An optional list of file extensions (e.g., "png", "txt") to filter files.
     * If null, all file types are considered.
     * @return A [DirectoryFileData] containing the matched [FileData] and [DirectoryData] items.
     * Returns an empty [DirectoryFileData] if an error occurs or the path is inaccessible.
     */
    suspend fun getSearchDirectoryFileData(
        context: Context,
        path: String,
        query: String,
        includeFolders: Boolean = true,
        extensions: Iterable<String>? = null
    ): DirectorySearchData {

        return emptyStorage.getSearchDirectoryFileData(
            context = context,
            paths = persistentListOf(path),
            query = query,
            includeFolders = includeFolders,
            extensions = extensions
        )
    }

    /**
     * Recursively searches for files and directories across all available storage volumes that
     * match a query.
     *
     * This function identifies all available storage volumes (e.g., internal storage, SD cards)
     * and performs a top-down, case-insensitive search for files and directories whose names
     * contain the [query] string.
     *
     * The results from all volumes are aggregated into a single [DirectorySearchData] object,
     * which categorizes the matches into separate lists for folders and files.
     *
     * @param context The Android context used to retrieve the list of storage volumes.
     * @param query The search term to match against file and directory names.
     * @param includeFolders Whether to include matching directories in the results.
     * Defaults to `true`.
     * @param extensions An optional list of file extensions (e.g., "png", "txt") to filter files.
     * If null, all file types are considered.
     * @return A [DirectorySearchData] containing the aggregated [FileData] and [DirectoryData]
     * items from all storage volumes.
     * Returns an empty result if no matches are found or an error occurs.
     */
    suspend fun getSearchDirectoryFileData(
        context: Context,
        query: String,
        includeFolders: Boolean = true,
        extensions: Iterable<String>? = null
    ): DirectorySearchData {

        return emptyStorage.getSearchDirectoryFileData(
            context = context,
            paths = getStorageVolumeList(context = context).map { volumeData -> volumeData.path },
            query = query,
            includeFolders = includeFolders,
            extensions = extensions
        )
    }

    /**
     * Retrieves the total memory space of a storage volume.
     *
     * @param path The path to the storage volume.
     * @return The total memory space in bytes, or 0L if an error occurs.
     */
    fun getTotalMemory(path: String): Long {

        return emptyStorage.getTotalMemory(path = path)
    }

    /**
     * Retrieves the total memory space of the storage volume associated with the given file.
     *
     * @param file The file representing the storage volume.
     * @return The total memory space in bytes, or 0L if an error occurs.
     */
    fun getTotalMemory(file: File): Long {

        return emptyStorage.getTotalMemory(path = file.path)
    }

    /**
     * Retrieves the free memory space available at the specified path.
     *
     * This function is a convenience wrapper around [getFreeMemory] that accepts a [File] object.
     * It creates a [File] object from the given [path] and then calls the other overload.
     *
     * @param path The absolute path to the storage volume or directory.
     * @return The free memory space in bytes. Returns 0L if an error occurs or the path is invalid.
     */
    fun getFreeMemory(path: String): Long {

        return emptyStorage.getFreeMemory(path = path)
    }

    /**
     * Retrieves the free memory space of a storage volume.
     *
     * @param file The [File] object representing the storage volume.
     * @return The free memory space in bytes. Returns 0L if an error occurs.
     */
    fun getFreeMemory(file: File): Long {

        return emptyStorage.getFreeMemory(path = file.path)
    }

    /**
     * Retrieves the used memory size of a storage volume or directory specified by its path.
     *
     * @param path The path of the storage volume or directory.
     * @return The used memory size in bytes, or 0L if an error occurs.
     */
    fun getUsedMemory(path: String): Long {

        return emptyStorage.getUsedMemory(path = path)
    }

    /**
     * Calculates the used memory space for a given file or directory.
     *
     * This function subtracts the free space from the total space of the file system
     * where the given [file] resides to determine the used space.
     *
     * @param file The [File] object representing the file or directory.
     * @return The used memory space in bytes. Returns 0L if an error occurs
     * (e.g., if the file does not exist or if there are permission issues).
     */
    fun getUsedMemory(file: File): Long {

        return emptyStorage.getUsedMemory(path = file.path)
    }

    /**
     * Calculates the total size of a file or directory at the given path.
     *
     * This function is a suspend function and should be called from a coroutine.
     * It internally calls [getFileSize] with a list containing only the provided path.
     *
     * @param path The absolute path to the file or directory.
     * @return The total size of the file or directory in bytes. Returns 0L if an error occurs or
     * the path does not exist.
     */
    suspend fun getFileSize(path: String): Long {

        return emptyStorage.getFileSize(paths = persistentListOf(path))
    }

    /**
     * Calculates the total size of files and folders at the given paths.
     *
     * This function operates on the [Dispatchers.IO] context.
     * This function is a suspending function, meaning it can perform long-running operations
     * without blocking the main thread.
     *
     * It iterates through the provided list of paths:
     * - For each path that represents a directory, it recursively walks through the directory
     *   to find all files and sums their sizes.
     * - For each path that represents a file, it directly gets its size.
     *
     * The function then returns the sum of all calculated file and folder sizes.
     *
     * If any error occurs during the process (e.g., a file or folder is not accessible),
     * it logs a warning and returns 0L.
     *
     * @param paths An immutable list of strings, where each string is an absolute path
     * to a file or directory.
     * @return The total size in bytes of all files and files within directories specified by the
     * paths.
     * Returns 0L if an error occurs or if no files are found.
     */
    suspend fun getFileSize(paths: Iterable<String>): Long {

        return emptyStorage.getFileSize(paths = paths)
    }

    /**
     * Creates a folder or file at the specified path.
     *
     * This function operates on the [Dispatchers.IO] context.
     * It handles potential exceptions and logs errors.
     *
     * @param parentPath The path to the parent directory where the new folder or file will be
     * created.
     * @param name The name of the new folder or file. Leading dots ('.') will be trimmed from the
     * name unless the `visibleType` is [FileVisibleType.Hidden].
     * @param isFolder `true` to create a folder, `false` to create a file.
     * @param visibleType Specifies the visibility of the file or folder. Defaults to
     * [FileVisibleType.Public].
     * If [FileVisibleType.Hidden], a '.' will be prepended to the `name`.
     * [FileVisibleType.Unknown] is treated the same as [FileVisibleType.Public].
     * @return A [MakeFileResult] indicating the outcome of the operation:
     * - [MakeFileResult.Success] if the folder or file was created successfully.
     * - [MakeFileResult.Exist] if a folder or file with the same name already exists at the
     * location.
     * - [MakeFileResult.Failed] if an error occurred during the creation process.
     */
    suspend fun makeFolderOrFile(
        parentPath: String,
        name: String,
        isFolder: Boolean,
        visibleType: FileVisibleType = FileVisibleType.Public
    ): MakeFileResult {

        return withContext(context = Dispatchers.IO) {

            try {

                val fileName = name.trimStart { it == '.' }

                val sourceFile = when (visibleType) {

                    FileVisibleType.Public -> File(parentPath, fileName)
                    FileVisibleType.Hidden -> File(parentPath, ".$fileName")
                    FileVisibleType.Unknown -> File(parentPath, fileName)
                }

                emptyStorage.makeFolderOrFile(destination = sourceFile.path, isFolder = isFolder)
            } catch (exception: Exception) {

                coroutineContext.ensureActive()
                Log.e("StorageExt", exception.message, exception)
                MakeFileResult.Failed(message = exception.message ?: "Unknown Error")
            }
        }
    }

    /**
     * Creates a new folder or file at the specified destination.
     *
     * This function attempts to create a new folder or file at the given `destination` path.
     * If a file or folder already exists at the destination, it returns `MakeFileResult.Exist`.
     * If the creation is successful, it returns `MakeFileResult.Success`.
     * If the creation fails for any reason (e.g., permission issues, invalid path),
     * it returns `MakeFileResult.Failed`.
     *
     * This function operates on the [Dispatchers.IO] context.
     *
     * @param destination The absolute path where the folder or file should be created.
     * @param isFolder `true` to create a folder, `false` to create a file.
     * @return A [MakeFileResult] indicating the outcome of the operation:
     *   - [MakeFileResult.Success] if the folder/file was created successfully.
     *   - [MakeFileResult.Exist] if a folder/file already exists at the destination.
     *   - [MakeFileResult.Failed] if the creation failed.
     */
    suspend fun makeFolderOrFile(destination: String, isFolder: Boolean): MakeFileResult {

        return emptyStorage.makeFolderOrFile(destination = destination, isFolder = isFolder)
    }
}