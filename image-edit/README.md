# Image Edit - A Image Editing Library for Jetpack Compose

A powerful and feature-rich image editing library for Jetpack Compose that provides a complete,
all-in-one solution for drawing, adding text, shapes, and other images on top of a base image.

`image-edit` offers a `ImageEdit` composable that acts as a full-screen editor, equipped with a rich
set of tools for creative modifications. Users can draw with customizable brushes, add and style
text, insert shapes and other images, and manipulate each element independently. The library is
built with a robust state management system (`ImageEditState`) and a highly configurable UI, making
it easy to integrate into any project that requires advanced image annotation or decoration
features.

---

## Features

- **All-in-One Editing UI**: A complete `ImageEdit` composable with top and bottom toolbars for a
  seamless editing experience.

- **Multi-Modal Editing**: Supports a variety of editing tools:
    - **Brush & Erase**: Draw or erase with highly customizable strokes (color, thickness, style,
      smoothness).
    - **Text**: Add text with full control over style, color, and size.
    - **Shapes**: Insert various shapes (e.g., rectangles, ovals) with configurable colors and draw
      styles (fill/stroke).
    - **Images**: Overlay other images onto the canvas.

- **Object-Based Manipulation**: Every element added (text, shape, drawing) is an independent object
  that can be selected, moved, resized, and deleted.

- **Advanced State Management**: A comprehensive `rememberImageEditState()` manages the entire
  editing session, including the base image, all drawn items, and tool configurations. The state is
  saveable across configuration changes.

- **Undo/Redo & Clear**: Easily undo the last action or clear the entire canvas.

- **Configurable Appearance**: Use `ImageEditConfig` to customize the look of selection handles,
  borders, and other UI elements.

- **Tool Input Sheets**: A built-in `ModalBottomSheet` provides a clean interface for editing the
  properties of the selected tool or object.

- **Export Functionality**: Generate an `ImageBitmap` of the final composed image.

---

## Installation

**Groovy (`build.gradle`):**

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:image-edit:<latest-version>'
}
```

**Kotlin DSL (`build.gradle`):**

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:image-edit:<latest-version>")
}
```

**Kotlin DSL with Version Catalogs:**

```toml
[versions]
empty-libs = "<latest-version>"

[libraries]
emptylibs-image-edit = { group = "com.github.bashpsk.emptylibs", name = "image-edit", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.image.edit)
}
```

---

## Usage

Integrating the `image-edit` library is straightforward. You need to create and remember an
`ImageEditState`, then pass it to the `ImageEdit` composable.

### 1. Basic Integration

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
    onNavigateBack = onNavigateBack
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