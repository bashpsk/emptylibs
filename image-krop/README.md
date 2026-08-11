# ✂️ Image Krop

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/image-krop.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/image-krop)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

An advanced image cropping library for Jetpack Compose. It provides a highly customizable UI for
selecting, resizing, and shaping crop areas with support for predefined aspect ratios and unique
shapes.

---

## ✨ Features

- **Aspect Ratio Control**: Predefined ratios (1:1, 16:9, 4:3, etc.) or free-form cropping.
- **Crop Shapes**: Apply unique masks like Circles, Stars, and Polygons to your crop.
- **Interactive Selection**: Intuitive handles for dragging and resizing the crop area.
- **Flipping & Rotation**: Flip the image horizontally or vertically during cropping.
- **Live Preview**: Built-in preview to see the final result before exporting.
- **Customizable UI**: Configure handle colors, overlay transparency, and more via `KropConfig`.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:image-krop:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:image-krop:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-image-krop = { group = "com.github.bashpsk.emptylibs", name = "image-krop", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.image.krop)
}
```

---

## 🛠️ Usage

### ✂️ Basic Integration

The `ImageKrop` component provides a full-screen cropping experience.

```kotlin
val state = rememberImageKropState(imageBitmap = myImage)

ImageKrop(
    modifier = Modifier.fillMaxSize(),
    state = state,
    onKropFinished = {
        // The final cropped ImageBitmap is stored in state.modifiedImage
        val croppedBitmap = state.modifiedImage
    },
    onNavigateBack = { /* Handle navigation */ }
)
```

---

### ⚙️ Custom Configuration

Customize the look and feel of the crop UI using `KropConfig`.

```kotlin
val customConfig = KropConfig(
    handleColor = Color.Magenta,
    borderColor = Color.White,
    overlayColor = Color.Black.copy(alpha = 0.7f),
    minimumCropSize = 60.dp,
    borderThickness = 3.dp
)

val state = rememberImageKropState(
    imageBitmap = myImage,
    config = customConfig
)
```

---

### 📐 Aspect Ratios & Shapes

`ImageKrop` supports a variety of aspect ratios and masking shapes.

```kotlin
// Lock to a specific aspect ratio
state.updateAspectRatio(KropAspectRatio.Ratio16to9)
state.updateAspectLocked(true)

// Apply a circular crop mask
state.updateKropShape(PathShape.Circle)
```

**Predefined Ratios:** `1:1`, `16:9`, `4:3`, `9:16`, `21:9`, etc.
**Predefined Shapes:** `Circle`, `Star`, `Polygon`, `Triangle`, `CutCorner`.

---

### 🔄 Handling Results & Undo

Manage multiple crop steps and easily revert changes.

```kotlin
// Undo the last crop operation
state.removeLastImage()

// Clear all modifications and start over
state.clearImages()

// Access the full history of crop steps
val history: List<ImageBitmap> = state.imageList
```

---

## 📸 Screenshots

| Image Krop UI                                      | Shape Customization                                           |
|----------------------------------------------------|---------------------------------------------------------------|
| ![Screenshot 01](../screenshots/image_krop_ui.jpg) | ![Screenshot 02](../screenshots/image_krop_customization.jpg) |

https://github.com/user-attachments/assets/d02eaaba-b8cb-41c8-aeb6-1eef8459b8f4

---