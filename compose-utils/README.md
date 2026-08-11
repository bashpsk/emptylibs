# 🛠️ Compose Utils

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/compose-utils.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/compose-utils)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A utility library for Jetpack Compose providing serializable and parcelable data classes for core
types like `Offset`, `Size`, and `IntOffset`. Simplifies state restoration and data transfer.

---

## ✨ Features

- **Serializable & Parcelable**: Easily save and restore Compose-related data across process death.
- **Core Type Support**:
    - `OffsetData`, `DpOffsetData`, `IntOffsetData`.
    - `SizeData`, `DpSizeData`, `IntSizeData`.
- **Seamless Conversion**: Effortless extension functions to convert between Compose types and their
  serializable counterparts.
- **Lightweight**: Zero-dependency overhead beyond core Compose.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:compose-utils:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:compose-utils:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-compose-utils = { group = "com.github.bashpsk.emptylibs", name = "compose-utils", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.compose.utils)
}
```

---

## 🛠️ Usage

### 🔄 Serialization & State Restoration

Save and restore Compose core types across process death or configuration changes using
`rememberSaveable`.

```kotlin
// Offset serialization
val originalOffset = Offset(100f, 200f)
var savedOffset by rememberSaveable { mutableStateOf(originalOffset.toOffsetData()) }

// Convert back to Compose Offset when needed
val currentOffset = savedOffset.toOffset()
```

**Supported Data Classes:**

- `OffsetData`, `IntOffsetData`, `DpOffsetData`
- `SizeData`, `IntSizeData`, `DpSizeData`

### 📏 Clamping & Coercing

Easily constrain offsets and sizes within specific bounds.

```kotlin
val position = Offset(500f, 500f)
val bounds = Offset(300f, 300f)

// Ensure position is at least/at most/within bounds
val constrained = position.coerceIn(minimum = Offset.Zero, maximum = bounds)
```

**Available for:** `Offset`, `IntOffset`, `DpOffset`, `Size`, `IntSize`, `DpSize`.

### 📐 Geometric Shapes (`PathShape`)

Define complex geometric paths as serializable data classes. Perfect for custom drawing or shape
selection UIs.

```kotlin
val starShape = PathShape.Star(edges = 5, distance = 2.5f)
val polygonShape = PathShape.Polygon(sides = 6)

// Convert to a Compose Path for drawing
val path = starShape.toPath(canvasSize = Size(200f, 200f))

Canvas(modifier = Modifier.size(200.dp)) {
    drawPath(path = path, color = Color.Blue)
}
```

**Supported Shapes:**

- `Circle`, `Triangle`, `Star`, `Polygon`, `Rectangle` (Rounded), `CutCorner`.

### 📍 Proximity Utilities

Check if a point is within a certain distance of another.

```kotlin
val userTouch = Offset(105f, 205f)
val targetPoint = Offset(100f, 200f)

if (userTouch.hasNeared(targetPoint, threshold = 10f)) {
    // Action triggered!
}
```

---