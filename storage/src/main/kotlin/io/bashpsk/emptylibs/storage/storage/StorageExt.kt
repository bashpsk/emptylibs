package io.bashpsk.emptylibs.storage.storage

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import io.bashpsk.emptylibs.storage.storage.StorageExt.getDirectoryData
import io.bashpsk.emptylibs.storage.storage.StorageExt.getFileSize
import io.bashpsk.emptylibs.storage.storage.StorageExt.getFreeMemory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.coroutineContext

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
object StorageExt {

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
    fun getStorageVolumes(context: Context): Flow<ImmutableList<StorageVolumeData>> {

        return flow {

            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

            val storageVolumeList = try {

                when {

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {

                        storageManager.storageVolumes.map { volume ->

                            val path = volume.directory?.path ?: ""

                            StorageVolumeData(
                                title = volume.getDescription(context),
                                path = path,
                                totalSize = getTotalMemory(path = path),
                                availableSize = getFreeMemory(path = path),
                                usedSize = getUsedMemory(path = path),
                                volumeType = StorageVolumeType.getVolumeType(volume = volume)
                            )
                        }.toImmutableList()
                    }

                    else -> {

                        getStorageDirectories(context = context).map { path ->

                            storageManager.getStorageVolume(File(path))?.let { volume ->

                                StorageVolumeData(
                                    title = volume.getDescription(context),
                                    path = path,
                                    totalSize = getTotalMemory(path = path),
                                    availableSize = getFreeMemory(path = path),
                                    usedSize = getUsedMemory(path = path),
                                    volumeType = StorageVolumeType.getVolumeType(volume = volume)
                                )
                            } ?: StorageVolumeData()
                        }.toImmutableList()
                    }
                }.filter { volumeData -> volumeData.path.isNotEmpty() }.toImmutableList()
            } catch (exception: Exception) {

                coroutineContext.ensureActive()
                Log.w("StorageExt", exception.message, exception)
                persistentListOf()
            }

            emit(value = storageVolumeList)
        }.flowOn(context = Dispatchers.IO)
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
    fun getDirectoryFiles(context: Context, path: String): Flow<DirectoryFileData> {

        return flow {

            val folderList = MutableStateFlow(value = persistentListOf<DirectoryData>())
            val fileList = MutableStateFlow(value = persistentListOf<FileData>())

            try {

                val storageVolumeList = getStorageVolumes(
                    context = context
                ).toList().flatten().distinctBy { storage -> storage.path }.toImmutableList()

                File(path).listFiles()?.forEach { fileItem ->

                    when (fileItem.isFile) {

                        true -> getFileData(
                            file = fileItem,
                            storageVolumes = storageVolumeList
                        )?.let { newFileData ->

                            fileList.update { filesOld -> filesOld.add(element = newFileData) }
                        }

                        false -> getDirectoryData(
                            file = fileItem,
                            storageVolumes = storageVolumeList
                        )?.let { newDirectoryData ->

                            folderList.update { foldersOld -> foldersOld.add(newDirectoryData) }
                        }
                    }
                }

                val directoryData = getDirectoryData(
                    path = path,
                    storageVolumes = storageVolumeList
                ) ?: DirectoryData()

                val storageVolume = findStorageVolumeData(
                    path = path,
                    storageVolumes = storageVolumeList
                ) ?: StorageVolumeData()

                val newDirectoryFileData = DirectoryFileData(
                    folders = folderList.value.toImmutableList(),
                    files = fileList.value.toImmutableList(),
                    storage = storageVolume,
                    directory = directoryData
                )

                emit(value = newDirectoryFileData)
            } catch (exception: Exception) {

                val newDirectoryFileData = DirectoryFileData()

                coroutineContext.ensureActive()
                Log.w("StorageExt", exception.message, exception)
                emit(value = newDirectoryFileData)
            }
        }.flowOn(context = Dispatchers.IO)
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
        storageVolumes: ImmutableList<StorageVolumeData> = persistentListOf()
    ): StorageVolumeData? {

        return findStorageVolumeData(file = File(path), storageVolumes = storageVolumes)
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
        storageVolumes: ImmutableList<StorageVolumeData> = persistentListOf()
    ): StorageVolumeData? {

        return storageVolumes.find { storage -> file.path.startsWith(storage.path) }
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
    fun getDirectoryData(
        path: String,
        storageVolumes: ImmutableList<StorageVolumeData> = persistentListOf()
    ): DirectoryData? {

        return getDirectoryData(file = File(path), storageVolumes = storageVolumes)
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
    fun getDirectoryData(
        file: File,
        storageVolumes: ImmutableList<StorageVolumeData> = persistentListOf()
    ): DirectoryData? {

        return try {

            val storageVolume = findStorageVolumeData(
                file = file,
                storageVolumes = storageVolumes
            ) ?: StorageVolumeData()

            val folders = file.listFiles()?.count { folder -> folder.isDirectory } ?: 0
            val files = file.listFiles()?.count { file -> file.isFile } ?: 0

            DirectoryData(
                title = file.name,
                path = file.path,
                uri = file.toUri().toString(),
                visibleType = FileVisibleType.Companion.getFileVisibleType(file = file),
                folders = folders,
                files = files,
                modifiedDate = file.lastModified(),
                storage = storageVolume
            )
        } catch (exception: Exception) {

            Log.w("StorageExt", exception.message, exception)
            null
        }
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
    fun getFileData(
        path: String,
        storageVolumes: ImmutableList<StorageVolumeData> = persistentListOf()
    ): FileData? {

        return getFileData(file = File(path), storageVolumes = storageVolumes)
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
    fun getFileData(
        file: File,
        storageVolumes: ImmutableList<StorageVolumeData> = persistentListOf()
    ): FileData? {

        return try {

            val storageVolume = findStorageVolumeData(
                file = file,
                storageVolumes = storageVolumes
            ) ?: StorageVolumeData()

            FileData(
                title = file.name,
                path = file.path,
                uri = file.toUri().toString(),
                extension = file.extension,
                visibleType = FileVisibleType.Companion.getFileVisibleType(file = file),
                fileType = FileType.Companion.getFileType(extension = file.extension),
                size = file.length(),
                modifiedDate = file.lastModified(),
                storage = storageVolume
            )
        } catch (exception: Exception) {

            Log.w("StorageExt", exception.message, exception)
            null
        }
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
     * name unless the `visibleType` is [FileVisibleType.HIDDEN].
     * @param isFolder `true` to create a folder, `false` to create a file.
     * @param visibleType Specifies the visibility of the file or folder. Defaults to
     * [FileVisibleType.PUBLIC].
     * If [FileVisibleType.HIDDEN], a '.' will be prepended to the `name`.
     * [FileVisibleType.UNKNOWN] is treated the same as [FileVisibleType.PUBLIC].
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
        visibleType: FileVisibleType = FileVisibleType.PUBLIC
    ): MakeFileResult {

        return withContext(context = Dispatchers.IO) {

            try {

                val fileName = name.trimStart { it == '.' }

                val sourceFile = when (visibleType) {

                    FileVisibleType.PUBLIC -> File(parentPath, fileName)
                    FileVisibleType.HIDDEN -> File(parentPath, ".$fileName")
                    FileVisibleType.UNKNOWN -> File(parentPath, fileName)
                }

                makeFolderOrFile(destination = sourceFile.path, isFolder = isFolder)
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

        return withContext(context = Dispatchers.IO) {

            try {

                val sourceFile = File(destination)

                when (sourceFile.exists()) {

                    true -> MakeFileResult.Exist(path = sourceFile.path, name = sourceFile.name)

                    false -> {

                        val result = when (isFolder) {

                            true -> sourceFile.mkdirs()
                            false -> sourceFile.createNewFile()
                        }

                        when (result) {

                            true -> MakeFileResult.Success(sourceFile.path, sourceFile.name)
                            false -> MakeFileResult.Failed("Directory File Does Not Created!")
                        }
                    }
                }
            } catch (exception: Exception) {

                coroutineContext.ensureActive()
                Log.e("StorageExt", exception.message, exception)
                MakeFileResult.Failed(message = exception.message ?: "Unknown Error")
            }
        }
    }

    /**
     * Retrieves a list of storage directories available on the device.
     *
     * This function attempts to identify both primary and secondary storage locations.
     * It checks for emulated storage and external storage (like SD cards).
     *
     * @param context The application context.
     * @return An [ImmutableList] of strings, where each string is the absolute path to a storage
     * directory.
     * Returns an empty list if an error occurs or no storage directories are found.
     */
    private fun getStorageDirectories(context: Context): ImmutableList<String> {

        return try {

            val emulatedStorage = System.getenv("EMULATED_STORAGE_TARGET")
            val availableDirectories = hashSetOf<String>()

            when (emulatedStorage?.isNotEmpty() == true) {

                true -> getEmulatedStorageTarget()?.let(availableDirectories::add)
                false -> availableDirectories.addAll(elements = getExternalStorage(context))
            }

            availableDirectories.addAll(elements = getAllSecondaryStorages().toList())
            availableDirectories.toImmutableList()
        } catch (exception: Exception) {

            Log.w("StorageExt", exception.message, exception)
            persistentListOf()
        }
    }

    /**
     * Retrieves a list of external storage paths.
     *
     * This function calls `getExternalFilesDirs` to get a list of application-specific
     * directories on all shared/external storage devices. It then extracts the root path
     * of each storage device by removing the "Android/data" suffix from the absolute path.
     *
     * @param context The application context.
     * @return An immutable list of strings, where each string is the root path of an external
     * storage device. Returns an empty list if no external storage is found or an error occurs.
     */
    private fun getExternalStorage(context: Context): ImmutableList<String> {

        return getExternalFilesDirs(context = context).map { file ->

            file.absolutePath.substring(0, file.absolutePath.indexOf(string = "Android/data"))
        }.toImmutableList()
    }

    /**
     * Retrieves the emulated storage target path.
     *
     * This function attempts to determine the path for emulated storage. It checks the
     * "EMULATED_STORAGE_TARGET" environment variable.
     *
     * It also analyzes the path of the primary external storage directory. If the last segment
     * of this path is a digit (indicating a user ID for multi-user setups), it appends
     * this ID to the "EMULATED_STORAGE_TARGET" path.
     *
     * @return The emulated storage target path as a [String], or `null` if it cannot be determined
     * or an error occurs.
     */
    private fun getEmulatedStorageTarget(): String? {

        return try {

            val emulatedStorage = System.getenv("EMULATED_STORAGE_TARGET")
            val path = Environment.getExternalStorageDirectory().absolutePath
            val folders = path.split(File.separator)
            val lastSegment = folders.lastOrNull()
            val isNotEmpty = lastSegment?.isNotEmpty() == true
            val isDigit = lastSegment?.isDigitsOnly() == true
            var rawStorageId = ""

            when {

                isNotEmpty && isDigit -> rawStorageId = lastSegment
            }

            when (rawStorageId.isEmpty()) {

                true -> emulatedStorage
                else -> "$emulatedStorage/$rawStorageId"
            }
        } catch (exception: Exception) {

            Log.w("StorageExt", exception.message, exception)
            null
        }
    }

    /**
     * Retrieves a list of all secondary storage paths.
     *
     * This function attempts to read the "SECONDARY_STORAGE" environment variable.
     * If the variable is set and not empty, it splits the value by the file path separator
     * and returns an immutable list of the resulting paths.
     * If the environment variable is not set, empty, or an exception occurs during the process,
     * an empty immutable list is returned.
     *
     * @return An [ImmutableList] of [String] objects, where each string is a path to a secondary
     * storage.
     * Returns an empty list if no secondary storage is found or an error occurs.
     */
    private fun getAllSecondaryStorages(): ImmutableList<String> {

        return try {

            val secondaryStorage = System.getenv("SECONDARY_STORAGE")

            when (secondaryStorage?.isNotEmpty() == true) {

                true -> secondaryStorage.split(File.pathSeparator).toImmutableList()
                else -> persistentListOf()
            }
        } catch (exception: Exception) {

            Log.w("StorageExt", exception.message, exception)
            persistentListOf()
        }
    }

    /**
     * Returns absolute paths to application-specific directories on all shared/external storage
     * devices where the application can place persistent files it owns.
     *
     * These directories are located under Android/data/package_name/files.
     *
     * @param context The context to use.
     * @return A list of [File] objects representing the directories. Returns an empty list if no
     * such directories are found.
     */
    private fun getExternalFilesDirs(context: Context): ImmutableList<File> {

        return context.getExternalFilesDirs(null).toList().toImmutableList()
    }

    /**
     * Retrieves the total memory space of a storage volume.
     *
     * @param path The path to the storage volume.
     * @return The total memory space in bytes, or 0L if an error occurs.
     */
    fun getTotalMemory(path: String): Long {

        return getTotalMemory(file = File(path))
    }

    /**
     * Retrieves the total memory space of the storage volume associated with the given file.
     *
     * @param file The file representing the storage volume.
     * @return The total memory space in bytes, or 0L if an error occurs.
     */
    fun getTotalMemory(file: File): Long {

        return try {

            file.totalSpace
        } catch (exception: Exception) {

            Log.w("StorageExt", exception.message, exception)
            0L
        }
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

        return getFreeMemory(file = File(path))
    }

    /**
     * Retrieves the free memory space of a storage volume.
     *
     * @param file The [File] object representing the storage volume.
     * @return The free memory space in bytes. Returns 0L if an error occurs.
     */
    fun getFreeMemory(file: File): Long {

        return try {

            file.freeSpace
        } catch (exception: Exception) {

            Log.w("StorageExt", exception.message, exception)
            0L
        }
    }

    /**
     * Retrieves the used memory size of a storage volume or directory specified by its path.
     *
     * @param path The path of the storage volume or directory.
     * @return The used memory size in bytes, or 0L if an error occurs.
     */
    fun getUsedMemory(path: String): Long {

        return getUsedMemory(file = File(path))
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

        return try {

            file.totalSpace - file.freeSpace
        } catch (exception: Exception) {

            Log.w("StorageExt", exception.message, exception)
            0L
        }
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

        return getFileSize(paths = persistentListOf(path))
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
    suspend fun getFileSize(paths: ImmutableList<String>): Long {

        return withContext(context = Dispatchers.IO) {

            try {

                val files = paths.map { path -> File(path) }

                val foldersFileSize = files.filter { folder ->

                    folder.isDirectory
                }.map { folder ->

                    folder.walkTopDown().filter { file ->

                        file.isFile
                    }.map { file ->

                        file.length()
                    }.toImmutableList()
                }.flatten().sum()

                val fileSize = files.filter { file -> file.isFile }.sumOf { file -> file.length() }

                foldersFileSize + fileSize
            } catch (exception: Exception) {

                Log.w("StorageExt", exception.message, exception)
                0L
            }
        }
    }
}