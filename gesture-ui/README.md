# Gesture UI - Custom Gestures for Jetpack Compose

A powerful and intuitive gesture detection library for Jetpack Compose, designed to add complex,
customizable gestures to your UI components. Currently, it specializes in providing a rich set of
gestures for video player controls.

`gesture-ui` wraps your composables in a gesture-detecting container, `VideoGestureBox`, which
recognizes common video player interactions like double-tapping to seek, swiping to control volume
and brightness, and pinch-to-zoom. It is built with a flexible configuration and a robust state
management system, making it easy to integrate and customize for any media playback scenario.

---

## Features

- **Video Player Gestures**: A comprehensive set of gestures optimized for video playback:
    - **Double-Tap to Seek**: Double-tap on the left or right side of the screen to seek backward or
      forward.
    - **Vertical Swipes for Control**: Swipe vertically on the left side to control brightness and
      on the right side to control volume.
    - **Horizontal Swipes for Seeking**: Swipe horizontally at the top or bottom of the screen to
      scrub through the video timeline.
    - **Pinch-to-Zoom & Pan**: Use two-finger gestures to zoom into and pan across the video.

- **Robust State Management**: Comes with `rememberVideoGestureBoxState()` to manage gesture states
  and configurations across recompositions.

- **Highly Configurable**: Use `VideoGestureConfig` to enable or disable specific gestures (e.g.,
  zoom, pan, specific swipes) and fine-tune sensitivity thresholds.

- **Simple Integration**: Wrap any composable (like a video player) with `VideoGestureBox` to
  instantly add gesture controls.

- **Clear Gesture Callbacks**: Receive clear, sealed-class-based events for taps (`TapChanges`) and
  drags (`DragChanges`) to easily handle gesture outcomes.

---

## Installation

**Groovy (`build.gradle`):**

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:gesture-ui:<latest-version>'
}
```

**Kotlin DSL (`build.gradle`):**

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:gesture-ui:<latest-version>")
}
```

**Kotlin DSL with Version Catalogs:**

```toml
[versions]
empty-libs = "<latest-version>"

[libraries]
emptylibs-gesture-ui = { group = "com.github.bashpsk.emptylibs", name = "gesture-ui", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.gesture.ui)
}
```

---

## Usage

Integrating `gesture-ui` is simple. Wrap your video player or any other composable with
`VideoGestureBox` and listen for gesture events via the `onTapChanges` and `onDragChanges`callbacks.

Here is a basic example of how to use `VideoGestureBox` to wrap a player view.

```kotlin
VideoGestureBox(
    modifier = Modifier.fillMaxSize(),
    state = gestureState,
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
    // Your Video Player Composable goes here
    PlayerView(modifier = Modifier.fillMaxSize())
}
```

```kotlin
val customConfig = VideoGestureConfig(
    isZoomEnable = false,
    isHorizontalTopEnable = false,
    horizontalMinimumSwipe = 40,
    verticalMinimumSwipe = 40,
    gestureMargin = 5   // 5% of Each Sides
)

val gestureState = rememberVideoGestureBoxState(config = customConfig)

VideoGestureBox(
    modifier = Modifier.fillMaxSize(),
    state = gestureState,
    onTapChanges = { /* ... */ },
    onDragChanges = { /* ... */ }
) {
    // Your Player
}
```

---