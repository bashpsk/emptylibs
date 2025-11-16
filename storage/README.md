# Storage - A Comprehensive File & Storage Utility for Android

A powerful and modern utility library for Android that simplifies common file system and storage
management tasks. It provides a clean, coroutine-based API for querying storage volumes, listing
files, reading metadata, and creating new files or folders.

`storage` offers a static `StorageExt` object that acts as a single entry point for all file
operations. It is designed to run expensive I/O operations on background threads, returning data as
either suspend functions or Flows. With a rich set of data classes
(`StorageVolumeData`, `DirectoryData`, `FileData`), it provides detailed, structured information
about the device's storage and its contents.

---

## Features

- **Storage Volume Management**:

    - List all available storage volumes (Internal, SD Card, OTG) with `getStorageVolumeList()` or
      `getStorageVolumeFlow()`.
    - Get detailed information for each volume, including total, free, and used space
      (`StorageVolumeData`).

- **File & Directory Browsing**:

    - List the contents of any directory with `getDirectoryFileData()`, which returns a
      `DirectoryFileData` object containing separate lists of files and folders.
    - Recursively find all files with specific extensions using `getFileListByExtensions()`.

- **Rich Metadata**:

    - Get detailed metadata for any file (`FileData`) or directory (`DirectoryData`), including
      path,
      `URI`, size, last modified date, and visibility.
    - Automatically categorize files into types like Image, Video, Audio, Document, etc., using the
      `FileType` enum.

- **File & Folder Creation**:

    - Easily create new files or folders with `makeFolderOrFile()`, which returns a `MakeFileResult`
      indicating success, failure, or if the item already exists.

- **Coroutine & Flow-Based**:

    - All I/O operations are provided as `suspend` functions or `Flows` to ensure they run off the
      main thread, keeping your UI responsive.

- **Comprehensive Data Models**:

    - Uses immutable, parcelable, and serializable data classes for easy use with Jetpack Compose,
      state management, and data persistence.

---

## Installation

**Groovy (`build.gradle`):**

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:storage:<latest-version>'
}
```

**Kotlin DSL (`build.gradle`):**

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:storage:<latest-version>")
}
```

**Kotlin DSL with Version Catalogs:**

```toml
[versions]
empty-libs = "<latest-version>"

[libraries]
emptylibs-storage = { group = "com.github.bashpsk.emptylibs", name = "storage", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.storage)
}
```

---

## Usage

All functions are accessed via the static `StorageExt` object.

### 1. Listing Storage Volumes

Retrieve a list of all storage volumes on the device.

```kotlin
// Using the Flow-based approach
launch {
    StorageExt.getStorageVolumeFlow(context).collectLatest { volumes ->

    }
}

// Or using the suspend function for a one-shot result
launch {
    val volumes = StorageExt.getStorageVolumeList(context)
}
```

### 2. Listing Files and Folders in a Directory

Get the contents of a specific directory, such as the primary external storage.

```kotlin
val downloadsPath = Environment.getExternalStoragePublicDirectory(
    Environment.DIRECTORY_DOWNLOADS
).path

// Get files and folders in the Downloads directory
launch {
    val directoryContent = StorageExt.getDirectoryFileData(context, downloadsPath)

    println("Directory: ${directoryContent.directory.title}")
    directoryContent.files.forEach { file ->
        println("File: ${file.title} (${file.fileType})")
    }
    directoryContent.folders.forEach { folder ->
        println("Folder: ${folder.title}")
    }
}
```

### 3. Getting Metadata for a Single File

Retrieve detailed information about a specific file.

```kotlin
launch {
    val filePath = "/path/to/your/image.jpg"
    val fileData = StorageExt.getFileData(filePath)

    fileData?.let {
        println("File Name: ${it.title}")
        println("Size: ${it.size} bytes")
        println("Type: ${it.fileType.label}")
        println("Last Modified: ${it.modifiedDate}")
    }
}
```

### 4. Searching for Files by Extension

Find all `.jpg` and `.png` files on the entire device.

```kotlin
val rootPath = Environment.getExternalStorageDirectory().path
val extensionsToFind = persistentListOf("jpg", "png")

launch {
    val imageFiles = StorageExt.getFileListByExtensions(context, rootPath, extensionsToFind)
    println("Found ${imageFiles.size} images.")
}
```

### 5. Creating a New Folder

Create a new directory and handle the result.

```kotlin
val newFolderPath = "/path/to/your/New-Folder"

launch {
    when (val result = StorageExt.makeFolderOrFile(newFolderPath, isFolder = true)) {
        is MakeFileResult.Success -> println("Folder created at: ${result.path}")
        is MakeFileResult.Exist -> println("Folder already exists at: ${result.path}")
        is MakeFileResult.Failed -> println("Failed to create folder: ${result.message}")
    }
}
```

---