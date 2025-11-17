# Canvas Slate - A Drawing Pad for Jetpack Compose

A powerful and customizable canvas library for Jetpack Compose, designed for an intuitive drawing
and path editing experience.

`canvas-slate` is a feature-rich composable that provides a complete drawing solution out of the
box.
It allows users to draw free-form paths, customize canvas and brush colors, adjust stroke
properties, and even select and edit individual paths after they have been drawn. The library is
built with a robust state management system (`CanvasSlateState`) and is easy to integrate into any
Jetpack Compose project.

---

## Features

- **Free-form Drawing**: Smooth and responsive drawing on the canvas.
- **Advanced Color Customization**: Separate color pickers for both the canvas background and the
  drawing brush.
- **Comprehensive Brush Controls**: Adjust brush thickness, stroke cap (e.g., Round, Square), and
  stroke join (e.g., Bevel, Miter) through built-in dialogs.
- **Path Editing**: An integrated `ModalBottomSheet` allows users to select and modify the
  properties (color, thickness, etc.) of individual paths after they are drawn.
- **State Management**: A robust `CanvasSlateState` that can be remembered across recompositions
  (`rememberCanvasSlateState`) to manage the canvas, tools, and all drawn paths.
- **Undo/Redo & Clear**: Easily undo or redo the last action, or clear the entire canvas.
- **Export**: Capture the final drawing as an `ImageBitmap` to save or share.

---

## Installation

**Groovy (`build.gradle`):**

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:canvas-slate:<latest-version>'
}
```

**Kotlin DSL (`build.gradle`):**

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:canvas-slate:<latest-version>")
}
```

**Kotlin DSL with Version Catalogs:**

```toml
[versions]
empty-libs = "<latest-version>"

[libraries]
emptylibs-canvas-slate = { group = "com.github.bashpsk.emptylibs", name = "canvas-slate", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.canvas.slate)
}
```

---

## Usage

Integrating `CanvasSlate` into your app is straightforward. Use `rememberCanvasSlateState()` to
create and manage the state of the canvas, and then pass it to the `CanvasSlate` composable.

Here is a complete example demonstrating how to set up the canvas and handle the export
functionality:

```kotlin
val canvasSlateState = rememberCanvasSlateState()
val coroutineScope = rememberCoroutineScope()

CanvasSlate(
    modifier = Modifier.fillMaxSize(),
    state = canvasSlateState,
    onDoneClick = {
        coroutineScope.launch(Dispatchers.IO) {
            canvasSlateState.getImageBitmap()?.let { imageBitmap -> }
        }
    },
    onNavigateBack = navController::navigateUp
)
```

---

## Screenshots & Demo

[Canvas Slate Screenshot](https://raw.githubusercontent.com/bashpsk/emptylibs/master/screenshots/images/canvas_slate.jpg)

https://github.com/user-attachments/assets/07ffb810-a1bb-4db2-b50b-dea5fdc1a626

---