# Compose Utils - A Utility Library for Jetpack Compose

A powerful and customizable utility library for Jetpack Compose, designed to provide helpful
extensions for common Jetpack Compose classes.

Compose Utils is a feature-rich library that provides helper and extension functions to simplify
common tasks. It offers serializable and parcelable data classes for core Jetpack Compose data types
like `Offset`, `Size`, `IntOffset`, `IntSize`, `DpOffset`, and `DpSize`, making state management and
data transfer more robust.

---

## Features

- **Serializable & Parcelable Data Classes**: Easily handle, save, and restore UI-related data
  across configuration changes or process death.
    - `OffsetData`
    - `SizeData`
    - `IntOffsetData`
    - `IntSizeData`
    - `DpOffsetData`
    - `DpSizeData`
- **Seamless Conversion**: Effortlessly convert between Jetpack Compose's core classes and their
  `compose-utils` data counterparts.
- **State Management**: Simplify the process of saving and restoring UI state in your Jetpack
  Compose applications.
- **Easy Integration**: Built to be lightweight and easy to integrate into any Jetpack Compose
  project.

---

## Installation

**Groovy (`build.gradle`):**

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:compose-utils:<latest-version>'
}
```

**Kotlin DSL (`build.gradle`):**

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:compose-utils:<latest-version>")
}
```

**Kotlin DSL with Version Catalogs:**

```toml
[versions]
empty-libs = "<latest-version>"

[libraries]
emptylibs-compose-utils = { group = "com.github.bashpsk.emptylibs", name = "compose-utils", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.compose.utils)
}
```

---

## Usage

Integrating `compose-utils` into your app is straightforward. The library provides extension
functions to convert between Compose objects and their corresponding data objects.

Here is a complete example demonstrating how to convert an `Offset` to `OffsetData` and back:

```kotlin
// Create an Offset
val originalOffset = Offset(100f, 200f)
// Convert Offset to OffsetData for serialization or state saving
val offsetData by rememberSaveable { mutableStateOf(originalOffset.toOffsetData()) }
// ... (pass offsetData through bundles or save it)
// Convert back to an Offset to use in your Composables
val restoredOffset = offsetData.toOffset()
```

### Key Functions:

The library is organized by the data types it supports. Here are the main extension functions you
will use:

#### Offset

- `Offset.toOffsetData(): OffsetData`
- `OffsetData.toOffset(): Offset`
- `DpOffset.toDpOffsetData(): DpOffsetData`
- `DpOffsetData.toDpOffset(): DpOffset`
- `IntOffset.toIntOffsetData(): IntOffsetData`
- `IntOffsetData.toIntOffset(): IntOffset`

#### Size

- `Size.toSizeData(): SizeData`
- `SizeData.toSize(): Size`
- `DpSize.toDpSizeData(): DpSizeData`
- `DpSizeData.toDpSize(): DpSize`
- `IntSize.toIntSizeData(): IntSizeData`
- `IntSizeData.toIntSize(): IntSize`

---