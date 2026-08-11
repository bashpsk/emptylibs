# 📱 Image Wallpaper

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/image-wallpaper.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/image-wallpaper)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A seamless, all-in-one Jetpack Compose library for cropping images to device dimensions and setting
them as Android wallpapers.

---

## ✨ Features

- **Integrated Workflow**: Manage the entire process from cropping to setting the wallpaper in one
  composable.
- **Auto Aspect Ratio**: Automatically detects device screen dimensions and locks the cropper for a
  perfect fit.
- **Multi-Destination Support**: Set wallpapers for Home Screen, Lock Screen, or both.
- **Async Processing**: Wallpapers are set on background threads to keep the UI responsive.
- **Customizable UI**: Leverages `image-krop` configurations for a consistent look.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:image-wallpaper:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:image-wallpaper:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-image-wallpaper = { group = "com.github.bashpsk.emptylibs", name = "image-wallpaper", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.image.wallpaper)
}
```

---

## 🛠️ Usage

### ✂️ Basic Usage

The `ImageWallpaper` component handles the full workflow: auto-detecting screen aspect ratio,
cropping, and providing a destination selection dialog.

```kotlin
ImageWallpaper(
    modifier = Modifier.fillMaxSize(),
    imageBitmap = myImageBitmap,
    onNavigateBack = { /* Handle back navigation */ }
)
```

### ✅ Handling Results

Listen for success or failure events when a wallpaper is set.

```kotlin
ImageWallpaper(
    imageBitmap = myImageBitmap,
    onWallpaperResult = { type, isSuccess ->
        when (type) {
            WallpaperType.Home -> println("Home Screen Result: $isSuccess")
            WallpaperType.Lock -> println("Lock Screen Result: $isSuccess")
            WallpaperType.HomeAndLock -> println("Both Result: $isSuccess")
        }
    }
)
```

### ⚙️ Custom Configuration

Customize the internal `image-krop` UI and the destination selection dialog.

```kotlin
ImageWallpaper(
    imageBitmap = myImageBitmap,
    config = KropConfig(
        handleColor = Color.Cyan,
        borderColor = Color.White,
        overlayColor = Color.Black.copy(alpha = 0.6f)
    ),
    dialogContainerColor = MaterialTheme.colorScheme.surfaceVariant
)
```

---

## 📸 Screenshots

| Wallpaper Cropping                                        | Destination Selection                                            |
|-----------------------------------------------------------|------------------------------------------------------------------|
| ![Screenshot 01](../screenshots/image_wallpaper_crop.jpg) | ![Screenshot 02](../screenshots/image_wallpaper_destination.jpg) |

https://github.com/user-attachments/assets/5f5a510b-4e7a-41d2-aa43-1d456199b609

---