# Image View - A Transformable & Swipeable Image Viewer for Jetpack Compose

A powerful and easy-to-use Jetpack Compose library that provides a transformable image view with
support for zooming, panning, and rotation, built on top of Coil. It also includes a swipeable image
gallery for displaying multiple images.

`image-view` offers `TransformImageView`, a composable that brings a rich, interactive viewing
experience to your app. Users can seamlessly pinch-to-zoom, double-tap to zoom, pan around a zoomed
image, and even rotate it. When provided with a list of images, it automatically becomes a swipeable
gallery, allowing users to navigate between images with horizontal swipes.

---

## Features

**Advanced Image Transformation**:

- **Pinch-to-Zoom**: Smoothly zoom in and out of an image with a two-finger gesture.
- **Double-Tap to Zoom**: Intelligently cycles through predefined zoom levels on double-tap.
- **Pan**: Effortlessly drag to navigate around a zoomed-in image.
- **Rotation**: Supports two-finger rotation gestures.

**Swipeable Image Gallery**:

- Displays a list of images in a `HorizontalPager`.
- Allows horizontal swiping between images when not zoomed in.

**Built on Coil 3**: Leverages the power and performance of `Coil3` for asynchronous image loading
includes built-in loading and error states.

**Robust State Management**: Uses `rememberImageTransformState()` to create a hoistable, saveable
state (`ImageTransformState`) that manages zoom, pan, and rotation while surviving configuration
changes.

**Highly Configurable**:

- Enable or disable individual gestures (zoom, pan, rotation, double-tap).
- Set custom zoom ranges.
- Optionally display built-in controls for navigation and resetting transformations.

**Simple API**: Two straightforward overloads for displaying a single image or list of images.

---

## Installation

**Groovy (`build.gradle`):**

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:image-view:<latest-version>'
}
```

**Kotlin DSL (`build.gradle`):**

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:image-view:<latest-version>")
}
```

**Kotlin DSL with Version Catalogs:**

```toml
[versions]
empty-libs = "<latest-version>"

[libraries]
emptylibs-image-view = { group = "com.github.bashpsk.emptylibs", name = "image-view", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.image.view)
}
```

---

## Usage

Using `image-view` is simple. The primary composable is `TransformImageView`, which has two main
overloads: one for a single image and another one for a list of images.

### 1. Displaying a Single Transformable Image

```kotlin
val state = rememberImageTransformState()

TransformImageView(
    modifier = Modifier.fillMaxSize(),
    state = state,
    imageModel = imagePath,
    onClick = { /* Handle single tap */ },
    onLongClick = { /* Handle long press */ }
)
```

### 2. Displaying a Swipeable Image Gallery

To create a gallery, provide an `ImmutableList` of image models. The composable will automatically
use a `HorizontalPager`.

```kotlin
val state = rememberImageTransformState()
val imageList: ImmutableList<Any?> = imagePaths.toImmutableList()

TransformImageView(
    modifier = Modifier.fillMaxSize(),
    state = state,
    imageModelList = imageList,
    initialImage = imageList.firstOrNull(),
    enableControls = true // Optional: Show built-in navigation controls
)
```

---