# 👆 Gesture UI

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/gesture-ui.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/gesture-ui)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A powerful gesture detection library for Jetpack Compose, specializing in advanced media player
interactions like seeking, volume control, and brightness adjustment.

---

## ✨ Features

- **Video Player Gestures**:
    - **Double-Tap**: Seek backward/forward.
    - **Vertical Swipe**: Adjust brightness (left side) or volume (right side).
    - **Horizontal Swipe**: Timeline scrubbing.
    - **Pinch & Pan**: Interactive zoom for video content.
- **Hoistable State**: Manage gestures via `VideoGestureBoxState`.
- **Highly Configurable**: Enable/disable specific gestures and adjust sensitivity via
  `VideoGestureConfig`.
- **Event Driven**: Clean, sealed-class callbacks for taps and drags.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:gesture-ui:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:gesture-ui:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-gesture-ui = { group = "com.github.bashpsk.emptylibs", name = "gesture-ui", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.gesture.ui)
}
```

---

## 🛠️ Usage

```kotlin
val state = rememberVideoGestureBoxState()

VideoGestureBox(
    modifier = Modifier.fillMaxSize(),
    state = state,
    onTapChanges = { change ->
        when (change) {
            is TapChanges.SingleTap -> { /* Toggle controls visibility */
            }
            is TapChanges.BackwardTap -> { /* Seek backward */
            }
            is TapChanges.ForwardTap -> { /* Seek forward */
            }
            else -> {}
        }
    },
    onDragChanges = { change ->
        when (change) {
            is DragChanges.VerticalLeftChanges -> { /* Adjust brightness */
            }
            is DragChanges.VerticalRightChanges -> { /* Adjust volume */
            }
            is DragChanges.HorizontalTopChanges -> { /* Scrubbing video */
            }
            is DragChanges.HorizontalBottomChanges -> { /* Scrubbing video */
            }
            is DragChanges.TransformChanges -> { /* Handle zoom and pan */
            }
            else -> {}
        }
    }
) {
    // Your Player Composable
}
```

---