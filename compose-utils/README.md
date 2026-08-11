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

```kotlin
// Create an Offset
val originalOffset = Offset(100f, 200f)
// Convert Offset to OffsetData for serialization or state saving
val offsetData by rememberSaveable { mutableStateOf(originalOffset.toOffsetData()) }
// ... (pass offsetData through bundles or save it)
// Convert back to an Offset to use in your Composables
val restoredOffset = offsetData.toOffset()
```

### Supported Extensions:

- `Offset.toOffsetData()` / `OffsetData.toOffset()`
- `Size.toSizeData()` / `SizeData.toSize()`
- `IntOffset.toIntOffsetData()` / `IntOffsetData.toIntOffset()`
- `DpSize.toDpSizeData()` / `DpSizeData.toDpSize()`

---