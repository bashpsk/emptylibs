package io.bashpsk.emptylibs.storage.storage

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import io.bashpsk.emptylibs.storage.storage.FileVisibleType.Companion.getFileVisibleType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File

@Stable
internal class EmptyStorageImpl : EmptyStorage {

    override suspend fun getStorageVolumeList(
        context: Context
    ): ImmutableList<StorageVolumeData> = withContext(context = Dispatchers.IO) {

        return@withContext try {

            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

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

            currentCoroutineContext().ensureActive()
            Log.w("StorageExt", exception.message, exception)
            persistentListOf()
        }
    }

    override suspend fun getDirectoryFileData(
        context: Context,
        path: String
    ): DirectoryFileData = withContext(context = Dispatchers.IO) {

        return@withContext try {

            val folderList = MutableStateFlow(persistentListOf<DirectoryData>())
            val fileList = MutableStateFlow(persistentListOf<FileData>())

            val storageVolumes = getStorageVolumeList(context = context)

            File(path).listFiles()?.forEach { fileItem ->

                when (fileItem.isFile) {

                    true -> getFileData(
                        path = fileItem.path,
                        storageVolumes = storageVolumes
                    )?.let { newFileData ->

                        fileList.update { filesOld -> filesOld.add(element = newFileData) }
                    }

                    false -> getDirectoryData(
                        path = fileItem.path,
                        storageVolumes = storageVolumes
                    )?.let { newDirectoryData ->

                        folderList.update { foldersOld -> foldersOld.add(newDirectoryData) }
                    }
                }
            }

            val directoryData = getDirectoryData(
                path = path,
                storageVolumes = storageVolumes
            ) ?: DirectoryData()

            val storageVolume = findStorageVolumeData(
                path = path,
                storageVolumes = storageVolumes
            ) ?: StorageVolumeData()

            DirectoryFileData(
                folders = folderList.value.toImmutableList(),
                files = fileList.value.toImmutableList(),
                storage = storageVolume,
                directory = directoryData
            )
        } catch (exception: Exception) {

            currentCoroutineContext().ensureActive()
            Log.w("StorageExt", exception.message, exception)
            DirectoryFileData()
        }
    }

    override fun findStorageVolumeData(
        path: String,
        storageVolumes: Iterable<StorageVolumeData>
    ): StorageVolumeData? {

        return storageVolumes.find { storage -> path.startsWith(storage.path) }
    }

    override suspend fun getDirectoryData(
        path: String,
        storageVolumes: Iterable<StorageVolumeData>
    ): DirectoryData? = withContext(context = Dispatchers.IO) {

        return@withContext try {

            val file = File(path)

            val storageVolume = findStorageVolumeData(
                path = file.path,
                storageVolumes = storageVolumes
            ) ?: StorageVolumeData()

            val folders = file.listFiles()?.count { folder -> folder.isDirectory } ?: 0
            val files = file.listFiles()?.count { file -> file.isFile } ?: 0

            DirectoryData(
                title = file.name,
                path = file.path,
                uri = file.toUri().toString(),
                visibleType = file.getFileVisibleType(),
                folders = folders,
                files = files,
                modifiedDate = file.lastModified(),
                storage = storageVolume
            )
        } catch (exception: Exception) {

            currentCoroutineContext().ensureActive()
            Log.w("StorageExt", exception.message, exception)
            null
        }
    }

    override suspend fun getFileData(
        path: String,
        storageVolumes: Iterable<StorageVolumeData>
    ): FileData? = withContext(context = Dispatchers.IO) {

        return@withContext try {

            val file = File(path)

            val storageVolume = findStorageVolumeData(
                path = file.path,
                storageVolumes = storageVolumes
            ) ?: StorageVolumeData()

            FileData(
                title = file.name,
                path = file.path,
                uri = file.toUri().toString(),
                extension = file.extension,
                visibleType = file.getFileVisibleType(),
                fileType = FileType.getFileType(extension = file.extension),
                size = file.length(),
                modifiedDate = file.lastModified(),
                storage = storageVolume
            )
        } catch (exception: Exception) {

            currentCoroutineContext().ensureActive()
            Log.w("StorageExt", exception.message, exception)
            null
        }
    }

    override suspend fun getFileListByExtensions(
        context: Context,
        path: String,
        extensions: Iterable<String>
    ): ImmutableList<FileData> = withContext(context = Dispatchers.IO) {

        return@withContext try {

            val storageVolumes = getStorageVolumeList(context)

            File(path).walkTopDown().filter { file ->

                extensions.any { extension -> file.extension.equals(extension, ignoreCase = true) }
            }.toImmutableList().mapNotNull { file ->

                getFileData(path = file.path, storageVolumes = storageVolumes)?.takeIf { fileData ->

                    fileData.path.isNotEmpty()
                }
            }.toImmutableList()
        } catch (exception: Exception) {

            currentCoroutineContext().ensureActive()
            Log.w("StorageExt", exception.message, exception)
            persistentListOf()
        }
    }

    override fun getTotalMemory(path: String): Long {

        return try {

            File(path).totalSpace
        } catch (exception: Exception) {

            Log.w("StorageExt", exception.message, exception)
            0L
        }
    }

    override fun getFreeMemory(path: String): Long {

        return try {

            File(path).freeSpace
        } catch (exception: Exception) {

            Log.w("StorageExt", exception.message, exception)
            0L
        }
    }

    override fun getUsedMemory(path: String): Long {

        return try {

            val file = File(path)

            file.totalSpace - file.freeSpace
        } catch (exception: Exception) {

            Log.w("StorageExt", exception.message, exception)
            0L
        }
    }

    override suspend fun getFileSize(paths: Iterable<String>): Long = withContext(Dispatchers.IO) {

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

            currentCoroutineContext().ensureActive()
            Log.w("StorageExt", exception.message, exception)
            0L
        }
    }

    override suspend fun makeFolderOrFile(
        destination: String,
        isFolder: Boolean
    ): MakeFileResult = withContext(context = Dispatchers.IO) {

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

            currentCoroutineContext().ensureActive()
            Log.e("StorageExt", exception.message, exception)
            MakeFileResult.Failed(message = exception.message ?: "Unknown Error")
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

        return context.getExternalFilesDirs(null).map { file ->

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
}