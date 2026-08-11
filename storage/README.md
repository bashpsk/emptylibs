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

### 💾 Volumes & Capacity

Retrieve all available storage volumes (Internal, SD, OTG) and their memory stats.

```kotlin
// List all volumes with capacity details
val volumes = StorageExt.getStorageVolumeList(context)

volumes.forEach { volume ->
    println("Name: ${volume.title}")
    println("Path: ${volume.path}")
    println("Total: ${volume.totalSize} bytes")
    println("Free: ${volume.availableSize} bytes")
}

// Quick capacity checks for any path
val freeSpace = StorageExt.getFreeMemory("/sdcard")
val totalSpace = StorageExt.getTotalMemory("/sdcard")
```

### 📂 Directory Browsing

List files and subfolders within a directory using `suspend` functions or `Flow`.

```kotlin
// Get contents of a specific folder
val content = StorageExt.getDirectoryFileData(context, "/sdcard/Pictures")

content.folders.forEach { println("Folder: ${it.title}") }
content.files.forEach { println("File: ${it.title}, Type: ${it.fileType}") }

// Reactive stream for directory contents
StorageExt.getDirectoryFileFlow(context, "/sdcard/Documents").collectLatest { content ->
    // Update UI
}
```

### 📊 File Metadata

Retrieve detailed information about specific files or total size of directories.

```kotlin
val fileData = StorageExt.getFileData("/sdcard/image.jpg")

fileData?.let {
    println("MIME: ${it.fileType.label}")
    println("Size: ${it.size} bytes")
    println("Modified: ${it.modifiedDate}")
}

// Calculate recursive size of multiple paths
val totalBytes = StorageExt.getFileSize(listOf("/sdcard/Folder1", "/sdcard/log.txt"))
```

### 🔍 Searching & Filtering

Perform powerful recursive searches by name or extension across one or all volumes.

```kotlin
// Search for PDFs across ALL storage volumes
val searchResult = StorageExt.getSearchDirectoryFileData(
    context = context,
    query = "Invoice",
    extensions = listOf("pdf")
)

// List specific file types in a directory
val images = StorageExt.getFileListByExtensions(
    context = context,
    path = "/sdcard/DCIM",
    extensions = listOf("jpg", "png", "webp")
)
```

### 🛠️ File Management

Create folders and files with simple success/failure handling.

```kotlin
// Create a hidden folder
val result = StorageExt.makeFolderOrFile(
    parentPath = "/sdcard",
    name = "SecretVault",
    isFolder = true,
    visibleType = FileVisibleType.Hidden
)

when (result) {
    is MakeFileResult.Success -> println("Created at: ${result.path}")
    is MakeFileResult.Exist -> println("Already exists")
    is MakeFileResult.Failed -> println("Error: ${result.message}")
}
```

---