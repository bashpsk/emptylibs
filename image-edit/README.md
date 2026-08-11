# 🎨 Image Edit

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/image-edit.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/image-edit)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A comprehensive image editing library for Jetpack Compose. It provides a full-screen editor equipped
with a rich set of tools for creative modifications, including drawing, text annotation, shapes, and
image overlays.

---

## ✨ Features

- **All-in-One Editor**: Complete UI with top and bottom toolbars for a seamless experience.
- **Multi-Modal Editing**:
    - **Brush & Erase**: Highly customizable strokes (color, thickness, style).
    - **Text**: Add and style text with full control over size and color.
    - **Shapes**: Insert rectangles, ovals, and more with configurable fill/stroke.
    - **Image Overlays**: Overlay and manipulate multiple images on the canvas.
- **Object-Based Selection**: Move, resize, and rotate every element independently.
- **State Management**: Robust `ImageEditState` with full undo/redo support.
- **Export**: Generate high-quality `ImageBitmap` of the edited project.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:image-edit:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:image-edit:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-image-edit = { group = "com.github.bashpsk.emptylibs", name = "image-edit", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.image.edit)
}
```

---

## 🛠️ Usage

Create an `ImageEditState` with your base image and pass it to the `ImageEdit` composable.

```kotlin
val imageEditState = rememberImageEditState(imageBitmap = baseImage)
val coroutineScope = rememberCoroutineScope()

ImageEdit(
    modifier = Modifier.fillMaxSize(),
    state = imageEditState,
    onDoneClick = {
        coroutineScope.launch {
            // Generate the final image bitmap
            val finalImage = imageEditState.getEditedImageBitmap()
            // Do something with the finalImage (e.g., save or display it)
        }
    },
    onNavigateBack = { /* Handle back navigation */ }
)
```

### 2. Customizing the Editor

```kotlin
// Create a custom configuration
val customConfig = ImageEditConfig(
    handleColor = MaterialTheme.colorScheme.primary,
    borderColor = MaterialTheme.colorScheme.secondary,
    borderThickness = 2.dp
)

// Pass the config to the state holder
val imageEditState = rememberImageEditState(
    imageBitmap = baseImage,
    config = customConfig
)
```

---

## 📸 Screenshots

| Image Edit                                      |
|-------------------------------------------------|
| ![Screenshot 01](../screenshots/image_edit.jpg) |

[//]: # (https://github.com/user-attachments/assets/07ffb810-a1bb-4db2-b50b-dea5fdc1a626)

---