# ✨ Image Utils

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/image-utils.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/image-utils)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A powerful utility library for Jetpack Compose, designed to simplify image manipulation tasks such
as applying shape masks and performing common bitmap calculations.

---

## ✨ Features

- **Advanced Shape Masking**: Apply predefined shapes (Circle, Star, Polygon, etc.) to any
  `ImageBitmap` via the `.bitmapMask()` extension.
- **Extensible Shape System**: Easily define custom shapes using the `ImageShape` sealed class.
- **Bitmap Extensions**:
    - `toSize()`: Safely convert `ImageBitmap` to `Size`.
    - `fittedImageSize()`: Calculate optimal dimensions to fit a canvas while preserving aspect
      ratio.
    - `sameAs()`: Efficiently compare two `ImageBitmap` instances.
- **Predefined Presets**: Quick access to common shapes via `BasicImageShapes`.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:image-utils:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:image-utils:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-image-utils = { group = "com.github.bashpsk.emptylibs", name = "image-utils", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.image.utils)
}
```

---

## 🛠️ Usage

### ✂️ Shape Masking

Apply unique geometric masks to any `ImageBitmap` using the `bitmapMask` extension.

```kotlin
// 1. Define a shape (Circle, Star, Polygon, etc.)
val starShape = PathShape.Star(edges = 5, distance = 2.5f)

// 2. Apply the mask to your bitmap
val shapedBitmap: ImageBitmap = starShape.bitmapMask(imageBitmap = originalBitmap)

// 3. Use in your Composable
Image(bitmap = shapedBitmap, contentDescription = "Star shaped image")
```

### 📊 Bitmap Info & Conversions

Safely retrieve dimensions and aspect ratios from nullable or non-nullable `ImageBitmap` instances.

```kotlin
val bitmap: ImageBitmap?

val size: Size = bitmap.toSize()           // Size(width, height) or Size.Zero
val intSize: IntSize = bitmap.toIntSize()  // IntSize(width, height) or IntSize.Zero
val ratio: Float? = bitmap.findAspectRatio() // width / height
```

### 📐 Canvas Fitting

Calculate the optimal size for an image to fit within a specific canvas while maintaining aspect
ratio.

```kotlin
val canvasSize = Size(1080f, 1080f)
val imageSize = Size(1920f, 1080f)

// Fit image to canvas with a 10% safety margin (reduction)
val fittedSize = canvasSize.fittedImageSize(
    imageSize = imageSize,
    reduction = 10
)
```

### 🔍 Bitmap Comparison

Efficiently check if two `ImageBitmap` instances are identical.

```kotlin
if (bitmap1.sameAs(bitmap2)) {
    // Bitmaps are pixel-identical
}
```

---

## 📸 Screenshots

| ![Screenshot 01](../screenshots/image_utils_circle_shape.jpg)  | ![Screenshot 02](../screenshots/image_utils_triangle_shape.jpg)  | ![Screenshot 03](../screenshots/image_utils_pentagon_shape.jpg) |
|----------------------------------------------------------------|------------------------------------------------------------------|-----------------------------------------------------------------|
| ![Screenshot 04](../screenshots/image_utils_hexagon_shape.jpg) | ![Screenshot 05](../screenshots/image_utils_cutcorner_shape.jpg) | ![Screenshot 06](../screenshots/image_utils_star_shape.jpg)     |

---