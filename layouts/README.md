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

### 🌓 Adaptive & Master-Detail (`TwoPaneAdaptiveLayout`)

Automatically switches between side-by-side (Tablet/Landscape) and top-bottom (Phone/Portrait)
layouts.

```kotlin
TwoPaneAdaptiveLayout(
    aspectRatio = 16f / 9f, // Maintain ratio for the first pane
    firstPane = {
        // Video Player or List
    },
    secondPane = {
        // Details or Chat
    }
)
```

### ↕️ Collapsible Panels (`SwipeCollapsibleLayout`)

Implement interactive swipe-to-expand headers or player panels.

```kotlin
val state = rememberSwipeCollapsibleLayoutState(initialValue = CollapsibleLayoutProgress.Collapsed)

SwipeCollapsibleLayout(
    state = state,
    primaryContent = { /* Mini Player / Main Player */ },
    secondaryContent = { /* Mini Player Controls */ },
    tertiaryContent = { /* Player Details/Queue */ }
) { paddingValues ->
    // Main background content (e.g., Video List)
}
```

### 📌 Sticky Components (`StickyRowLayout`)

Keep the first child fixed while the rest of the row scrolls horizontally. Perfect for line numbers.

```kotlin
val scrollState = rememberScrollState()

StickyRowLayout(
    horizontalScroll = scrollState.value,
    verticalAlignment = Alignment.CenterVertically
) {
    // This child stays sticky on the left
    Text("Line 1", modifier = Modifier.padding(8.dp))

    // These children scroll under the sticky element
    Text("Very long content that scrolls...", modifier = Modifier.horizontalScroll(scrollState))
}
```

### 🔍 Interactive Zoom (`ZoomableLayout`)

Add native pinch-to-zoom and panning capabilities to any Composable.

```kotlin
val state = rememberTransformableGesturesState()

ZoomableLayout(
    state = state,
    modifier = Modifier.fillMaxSize()
) {
    // Any content: Images, Canvas, etc.
    Image(
        bitmap = myBitmap,
        contentDescription = null,
        modifier = Modifier.fillMaxSize()
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

[//]: # (https://github.com/user-attachments/assets/07ffb810-a1bb-4db2-b50b-dea5fdc1a626)

[//]: # (https://github.com/user-attachments/assets/07ffb810-a1bb-4db2-b50b-dea5fdc1a626)

[//]: # (https://github.com/user-attachments/assets/07ffb810-a1bb-4db2-b50b-dea5fdc1a626)

---