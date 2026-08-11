# 📂 Storage

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/storage.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/storage)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A modern, coroutine-based file and storage utility library for Android. Simplifies browsing,
metadata retrieval, and file management with a clean Flow-based API.

---

## ✨ Features

- **Volume Management**: List internal storage, SD cards, and OTG drives with capacity info.
- **Directory Browsing**: Easily list files and folders with dedicated data models.
- **Rich Metadata**: Access detailed `FileData` including MIME types, sizes, and modification dates.
- **Extension Filtering**: Search for files by specific extensions (e.g., `.jpg`, `.pdf`).
- **File Management**: Create folders and files with simple `suspend` functions.
- **Thread Safe**: All operations are optimized for background execution.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:storage:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:storage:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-storage = { group = "com.github.bashpsk.emptylibs", name = "storage", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.storage)
}
```

---

## 🛠️ Usage

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