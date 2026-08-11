# 📐 Layouts

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/layouts.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/layouts)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A collection of specialized Jetpack Compose layouts designed for adaptive interfaces, zoomable
content, and interactive UI patterns.

---

## ✨ Features

- **TwoPaneAdaptiveLayout**: Adapts between Row and Column for master-detail views based on screen
  width.
- **SwipeCollapsibleLayout**: Interactive swipe-to-collapse behavior for headers or panels.
- **StickyRowLayout**: Rows with sticky headers that stay visible during scrolling.
- **ZoomableLayout**: A container that provides native zoom and pan capabilities to any child
  content.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:layouts:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:layouts:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-layouts = { group = "com.github.bashpsk.emptylibs", name = "layouts", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.layouts)
}
```

---

## 🛠️ Usage

### Swipe Collapsible Layout

```kotlin
val state = rememberSwipeCollapsibleLayoutState(initialValue = CollapsibleLayoutProgress.Dismissed)

SwipeCollapsibleLayout(
    modifier = Modifier.fillMaxSize(),
    state = state,
    primaryContent = { /* Player */ },
    secondaryContent = { /* Controls for minimized state */ },
    tertiaryContent = { /* Details & Recommendation section */ }
) { layoutPaddingValues ->
    /* Feed/Videos List Screen */
}
```

### TwoPane Adaptive Layout

```kotlin
TwoPaneAdaptiveLayout(
    aspectRatio = 1.0F,
    firstPane = { /* List */ },
    secondPane = { /* Details */ }
)
```

### Zoomable Layout

```kotlin
val transformableState = rememberTransformableGesturesState(
    initialZoom = 1.0F,
    enableZoom = true,
    enablePan = true,
    enableDoubleTapZoom = true
)

val layoutPosition by remember(transformableState) {
    derivedStateOf { transformableState.position.round().copy(y = 0) }
}

ZoomableLayout(
    modifier = Modifier
        .fillMaxWidth()
        .offset { layoutPosition },
    zoomScale = transformableState.zoom
) {

    Image(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio),
        bitmap = sampleImage,
        contentScale = ContentScale.Fit,
        contentDescription = null
    )
}
```

---

## 📸 Screenshots

| Layouts                                                           | Transform                                                                |
|-------------------------------------------------------------------|--------------------------------------------------------------------------|
| ![Screenshot 01](../screenshots/layouts_swipe_collapsible_ui.jpg) | ![Screenshot 01](../screenshots/layouts_swipe_collapsible_transform.jpg) |
| ![Screenshot 02](../screenshots/layouts_sticky_row_ui.jpg)        | ![Screenshot 02](../screenshots/layouts_sticky_row_transform.jpg)        |
| ![Screenshot 03](../screenshots/layouts_two_pane_ui.jpg)          | ![Screenshot 03](../screenshots/layouts_two_pane_transform.jpg)          |
| ![Screenshot 04](../screenshots/layouts_zoomable_ui.jpg)          | ![Screenshot 04](../screenshots/layouts_zoomable_transform.jpg)          |

[//]: # (https://github.com/user-attachments/assets/07ffb810-a1bb-4db2-b50b-dea5fdc1a626)

---