# 🖼️ Image View

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/image-view.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/image-view)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A powerful transformable image viewer and gallery for Jetpack Compose, built on top of Coil 3. Supports pinch-to-zoom, panning, and seamless swiping between multiple images.

---

## ✨ Features

- **Advanced Transformations**: Pinch-to-zoom, pan, and two-finger rotation gestures.
- **Double-Tap Zoom**: Intelligently cycle through zoom levels.
- **Image Gallery**: Swipeable `HorizontalPager` for lists of images.
- **Coil 3 Integration**: Native support for asynchronous loading with lifecycle awareness.
- **Hoistable State**: Manage zoom and pan via `ImageTransformState` that survives config changes.
- **Highly Configurable**: Toggle individual gestures and set custom zoom constraints.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:image-view:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:image-view:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-image-view = { group = "com.github.bashpsk.emptylibs", name = "image-view", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.image.view)
}
```

---

## 🛠️ Usage

### Single Image

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

### Image Gallery

```kotlin
val state = rememberImageTransformState()
val imageList: ImmutableList<Any?> = imagePaths.toImmutableList()

TransformImageView(
    modifier = Modifier.fillMaxSize(),
    state = state,
    imageModelList = imageList,
    initialImage = imageList.firstOrNull()
)
```

---

## 📸 Screenshots

| Image View                                                      |
|-----------------------------------------------------------------|
| ![Screenshot 01](../screenshots/image_view_transform_image.jpg) |

[//]: # (https://github.com/user-attachments/assets/07ffb810-a1bb-4db2-b50b-dea5fdc1a626)

---