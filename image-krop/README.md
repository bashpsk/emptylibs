# Image Krop - An Advanced Image Cropping Library for Jetpack Compose

A powerful and highly customizable image cropping library for Jetpack Compose. It provides a
complete, out-of-the-box UI for selecting, resizing, and shaping crop areas with a rich set of
features.

`image-krop` offers a comprehensive `ImageKrop` composable that delivers a full-featured cropping
experience. Users can select from a list of predefined aspect ratios, lock or unlock the aspect
ratio, flip the image, and apply various shapes to the crop area. The library is built with a robust
state management system (`ImageKropState`) and a configurable UI, making it easy to integrate into
any application requiring sophisticated image cropping capabilities.

---

## Features

- **All-in-One Cropping UI**: A complete `ImageKrop` composable with top and bottom toolbars for an
  intuitive user experience.
- **Aspect Ratio Control**:
    - Select from a list of predefined aspect ratios (e.g., 1:1, 16:9, 4:3).
    - Lock or unlock the aspect ratio.
- **Image Flipping**:
    - Flip the image horizontally or vertically.
    - Undo previous crop actions.
- **Crop Shape Customization**:
    - Apply various ImageShapes (e.g., Circle, Star) to the crop area.
    - Customize shape properties like corner radius or point count in a dedicated dialog.
- **Interactive Crop Selection**: Drag to move and resize the crop area using intuitive corner and
  edge handles.
- **Live Preview**: A modal bottom sheet allows users to preview the final cropped image before
  confirming.
- **Robust State Management**: A comprehensive `rememberImageKropState()` manages the entire
  cropping session, including the source image, aspect ratio, selected shape, and crop rectangle
  coordinates.
- **Configurable UI**: Use `KropConfig` to customize the appearance of handles, borders, the
  overlay, and more.
- **Export Functionality**: Generate a high-quality `ImageBitmap` of the final cropped selection.

---

## Installation

**Groovy (`build.gradle`):**

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:image-krop:<latest-version>'
}
```

**Kotlin DSL (`build.gradle`):**

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:image-krop:<latest-version>")
}
```

**Kotlin DSL with Version Catalogs:**

```toml
[versions]
empty-libs = "<latest-version>"

[libraries]
emptylibs-image-krop = { group = "com.github.bashpsk.emptylibs", name = "image-krop", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.image.krop)
}
```

---

## Usage

Integrating `image-krop` is simple. You need to create and remember an `ImageKropState` with your
source image, then pass it to the `ImageKrop` composable.

Here is a complete example of how to set up the `ImageKrop` screen.

```kotlin
val imageKropState = rememberImageKropState(imageBitmap = baseImage)
val coroutineScope = rememberCoroutineScope()

ImageKrop(
    modifier = Modifier.fillMaxSize(),
    state = imageKropState,
    onKropFinished = {
        coroutineScope.launch {
            // Generate the final cropped image
            when (val result = imageKropState.getCroppedImageBitmap()) {
                is KropResult.Init -> {}
                is KropResult.Success -> {
                    val finalImage = result.bitmap
                    // Do something with the finalImage (e.g., save or display)
                }
                is KropResult.Failed -> { /* Handle failure */
                }
            }
        }
    },
    onNavigateBack = navController::navigateUp
)
```

```kotlin
// Create a custom configuration
val customConfig = KropConfig(
    minimumCropSize = 64.dp,
    handleColor = MaterialTheme.colorScheme.primary,
    borderColor = MaterialTheme.colorScheme.secondary,
    overlayColor = Color.Black.copy(alpha = 0.70F)
)

// Pass the config to the state holder
val imageKropState = rememberImageKropState(
    imageBitmap = myBitmap,
    config = customConfig
)
```

---

## Screenshots & Demo

| Image Krop - UI                                   | Shape Customization                               |
|---------------------------------------------------|---------------------------------------------------|
| ![Screenshot 01](../screenshots/image_krop_1.jpg) | ![Screenshot 02](../screenshots/image_krop_2.jpg) |

https://github.com/user-attachments/assets/d02eaaba-b8cb-41c8-aeb6-1eef8459b8f4

---