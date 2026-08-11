# 📦 LRU Cache Manager

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/lrucache-manager.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/lrucache-manager)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A lightweight and efficient wrapper around Android's `LruCache` for easy, type-safe, in-memory
caching of any object type in Kotlin and Android.

---

## ✨ Features

- **Generic & Type-Safe**: Cache any object type (Bitmaps, Data Models, etc.).
- **Simple API**: Intuitive `add`, `get`, `remove`, and `exist` operations.
- **Dynamic Resizing**: Update the maximum cache size at runtime.
- **Full Control**: Evict individual items or clear the entire cache instantly.
- **Minimal Overhead**: A clean wrapper that preserves the efficiency of the underlying Android
  implementation.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:lrucache-manager:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:lrucache-manager:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-lrucache-manager = { group = "com.github.bashpsk.emptylibs", name = "lrucache-manager", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.lrucache.manager)
}
```

---

## 🛠️ Usage

```kotlin
// Initialize cache
val imageCache = EmptyCacheManager<Bitmap>(maxSize = 20)

// Add item
imageCache.add("profile_pic", bitmap)

// Retrieve item
val cachedBitmap = imageCache.get("profile_pic")

// Check existence
if (imageCache.exist("profile_pic")) {
    // ...
}

// Remove the bitmap from the cache
val wasRemoved = imageCache.remove("profile_pic")
if (wasRemoved) println("Bitmap removed from cache.")

// Clear the entire cache
imageCache.evictAll()
```

---