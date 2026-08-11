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

### 📺 Video Player Gestures

The `VideoGestureBox` provides a complete suite of gestures typical for video players.

#### Basic Setup

```kotlin
val state = rememberVideoGestureBoxState()

VideoGestureBox(
    modifier = Modifier.fillMaxSize(),
    state = state,
    onTapChanges = { change ->
        when (change) {
            is TapChanges.SingleTap -> { /* Toggle controls */
            }
            is TapChanges.BackwardTap -> { /* Seek -10s */
            }
            is TapChanges.ForwardTap -> { /* Seek +10s */
            }
            else -> {}
        }
    },
    onDragChanges = { change ->
        when (change) {
            is DragChanges.VerticalLeftChanges -> { /* Brightness: change.changes.value */
            }
            is DragChanges.VerticalRightChanges -> { /* Volume: change.changes.value */
            }
            is DragChanges.HorizontalBottomChanges -> { /* Scrubbing: change.changes */
            }
            is DragChanges.TransformChanges -> { /* Zoom/Pan change.zoom, change.pan */
            }
            else -> {}
        }
    }
) {
    // Your Video Player Composable
}
```

#### Custom Configuration

Tune sensitivities and toggle specific gestures using `VideoGestureConfig`.

```kotlin
val config = VideoGestureConfig(
    isDoubleTapEnable = true,
    doubleTapTimeoutMillis = 400,
    isZoomEnable = true,
    horizontalMinimumSwipe = 30,
    gestureMargin = 10 // 10% margin from edges
)

val state = rememberVideoGestureBoxState(config = config)
```

### 🔄 General Transformable Gestures

Use the `transformableGestures` modifier for any component that needs standalone zoom, pan, and
rotation support.

```kotlin
val transformState = rememberTransformableGesturesState(
    initialZoom = 1.0f,
    zoomRange = 0.5f..5.0f,
    enableRotation = true
)

Box(
    modifier = Modifier
        .size(300.dp)
        .transformableGestures(
            state = transformState,
            onClick = { offset -> /* Handle Click */ },
            onLongClick = { offset -> /* Handle Long Click */ }
        )
        .graphicsLayer(
            scaleX = transformState.zoom,
            scaleY = transformState.zoom,
            rotationZ = transformState.rotation,
            translationX = transformState.position.x,
            translationY = transformState.position.y
        )
)
```

---