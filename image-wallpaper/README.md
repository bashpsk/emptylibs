# Image Wallpaper - An Android Wallpaper Library for Jetpack Compose

A seamless, all-in-one Jetpack Compose library for cropping an image to the device's screen
dimensions and setting it as the Android wallpaper.

`image-wallpaper` provides a single `ImageWallpaper` composable that integrates a powerful cropping
UI (`image-krop`) with the native Android `WallpaperManager`. The library automatically detects the
device's screen aspect ratio, locks the cropper to that ratio, and then presents a simple dialog for
the user to choose whether to set the image on the home screen, lock screen, or both. It is built on
top of `image-krop` to provide a familiar and robust cropping experience.

---

## Features

- **All-in-One Wallpaper Workflow**: A single `ImageWallpaper` composable manages the entire process
  from cropping to setting the wallpaper.

- **Automatic Aspect Ratio Detection**: Intelligently determines the device's screen aspect ratio
  and automatically locks the cropper to match it, ensuring a perfect fit.

- **Integrated Cropping UI**: Leverages the powerful `image-krop` library to provide an intuitive
  and interactive cropping experience.

- **Wallpaper Type Selection**: After cropping, a clean dialog prompts the user to select the
  wallpaper destination:
    - Home Screen
    - Lock Screen
    - Both Home & Lock Screen

- **Asynchronous Wallpaper Setting**: The wallpaper is set on a background thread to keep the UI
  responsive, with a loading indicator displayed during the process.

- **Simple Integration**: Requires only an `ImageBitmap` to start the process, with minimal
  boilerplate.

- **Customizable**: The crop UI's appearance can be customized using `KropConfig`.

---

## Installation

**Groovy (`build.gradle`):**

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:image-wallpaper:<latest-version>'
}
```

**Kotlin DSL (`build.gradle`):**

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:image-wallpaper:<latest-version>")
}
```

**Kotlin DSL with Version Catalogs:**

```toml
[versions]
empty-libs = "<latest-version>"

[libraries]
emptylibs-image-wallpaper = { group = "com.github.bashpsk.emptylibs", name = "image-wallpaper", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.image.wallpaper)
}
```

---

## Usage

Integrating `image-wallpaper` is straightforward. You only need to provide the `ImageWallpaper`
composable with an `ImageBitmap`.

```kotlin
ImageWallpaper(
    modifier = Modifier.fillMaxSize(),
    imageBitmap = baseImage,
    onWallpaperResult = { type, result ->
        // Handle the result, e.g., show a toast and navigate away
    },
    onNavigateBack = navController::navigateUp
)
```

```kotlin
val customConfig = KropConfig.surfaceBased().copy(
    handleColor = MaterialTheme.colorScheme.primary,
    borderColor = MaterialTheme.colorScheme.secondary
)

ImageWallpaper(
    modifier = Modifier.fillMaxSize(),
    imageBitmap = baseImage,
    config = customConfig,
    dialogContainerColor = AlertDialogDefaults.containerColor,
    onNavigateBack = navController::navigateUp
)
```

---