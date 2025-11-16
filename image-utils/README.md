# Image Utils - A Image Shape & Bitmap Utility Library for Jetpack Compose

A powerful and versatile utility library for Jetpack Compose, designed to simplify common image
manipulation tasks, including applying shape masks and handling bitmap conversions.

`image-utils` provides two core sets of functionalities. The Shape Masking feature allows you to
clip an `ImageBitmap` into various geometric forms like circles, stars, polygons, and more, using a
simple and extensible ImageShape system. The `Bitmap` Extensions feature offers a collection of
convenient helper functions for converting bitmaps, calculating fitted sizes, and comparing
`ImageBitmap`instances.

---

## Features

- **Advanced Shape Masking**:
    - Apply a variety of predefined shapes to any `ImageBitmap` using the `.bitmapMask()` extension.
    - Includes a rich set of built-in `ImageShapes`:
        - Circle
        - Rectangle (with rounded corners)
        - Triangle
        - Polygon
        - CutCorner
        - Star
    - Shapes are fully configurable (e.g., number of polygon sides, star points, corner radius).

- **Extensible Shape System**:
    - `ImageShape` is a sealed class, making it easy to define your own custom shapes.
    - The `toPath()` extension function provides a clear blueprint for converting any `ImageShape`
      into a drawable `Path`.

- **Convenient Bitmap Extensions**:
    - `ImageBitmap?.toSize()`: Safely convert an `ImageBitmap` to a `Size` object.
    - `Size.fittedImageSize()`: Calculate the correct size for an image to fit within a canvas while
      preserving its aspect ratio.
    - `ImageBitmap.sameAs()`: Efficiently compare two `ImageBitmap` instances for equality.

- **Predefined Shape List**: Includes `BasicImageShapes`, a ready-to-use list of common shapes for
  quick integration into your UI.

---

## Installation

**Groovy (`build.gradle`):**

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:image-utils:<latest-version>'
}
```

**Kotlin DSL (`build.gradle`):**

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:image-utils:<latest-version>")
}
```

**Kotlin DSL with Version Catalogs:**

```toml
[versions]
empty-libs = "<latest-version>"

[libraries]
emptylibs-image-utils = { group = "com.github.bashpsk.emptylibs", name = "image-utils", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.image.utils)
}
```

---

## Usage

Using the `image-utils` library is straightforward. The core functionalities are provided as
extension functions on `ImageShape` and `ImageBitmap`.

### 1. Applying a Shape Mask to an Image

The primary feature is applying a shape mask. This is done by calling the `.bitmapMask()` extension
function on an `ImageShape` instance and passing the source image.

```kotlin
// 1. Define the shape
val starShape = ImageShape.Star(edges = 5, distance = 2.5f)

// 2. Apply the mask
val shapedBitmap: ImageBitmap = starShape.bitmapMask(imageBitmap = originalBitmap)

// Now 'shapedBitmap' contains the original image clipped into a 5-pointed star.
// You can display it in an Image composable.
Image(bitmap = shapedBitmap, contentDescription = "Star-shaped image")
```

### 2. Calculating Fitted Image Size

This is useful for fitting an image inside a designated area (like a canvas) without distorting it.

```kotlin
val canvasSize = Size(width = 1080f, height = 1080f) // A square canvas
val imageSize = Size(width = 1920f, height = 1080f)  // A 16:9 image

// Calculate the size of the image to fit inside the canvas with a 10% reduction
val newImageSize = canvasSize.fittedImageSize(imageSize = imageSize, reduction = 10)
// newImageSize will be Size(width=972.0, height=546.75), which fits and is 90% of the max size
```

---