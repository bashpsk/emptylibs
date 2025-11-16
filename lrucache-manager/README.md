# LRUCache Manager - A Generic LRU Cache for Kotlin & Android

A lightweight and efficient wrapper around Android's LruCache for easy in-memory caching in Kotlin
and Android projects.

`lrucache-manager` provides a simple, generic class, `EmptyCacheManager<T>`, that simplifies the
process of storing, retrieving, and managing objects in a memory-sensitive cache. It follows the
`LRU (Least Recently Used)` policy, meaning when the cache is full, it automatically discards the
least recently accessed items to make space for new ones. This makes it an ideal solution for
caching bitmaps, network responses, or any other data that is expensive to create or fetch.

---

## Features

- **Generic & Type-Safe**: Can be used to cache any type of object
  (`EmptyCacheManager<Bitmap>`, `EmptyCacheManager<DataObject>`).

- **Simple API**: Provides a clean and intuitive API for common cache operations:

    - `add(key: String, value: T)`: Add an item to the cache.
    - `get(key: String): T?`: Retrieve an item from the cache.
    - `remove(key: String)`: Remove a specific item.
    - `exist(key: String)`: Check if an item is in the cache.

- **Dynamic Sizing**: The cache can be resized on the fly using the `resize(maxSize: Int)` method.

- **Full Cache Control**: Easily clear the entire cache with `evictAll()`.

- **Lightweight**: A minimal wrapper that adds convenience without adding overhead.

---

## Installation

**Groovy (`build.gradle`):**

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:lrucache-manager:<latest-version>'
}
```

**Kotlin DSL (`build.gradle`):**

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:lrucache-manager:<latest-version>")
}
```

**Kotlin DSL with Version Catalogs:**

```toml
[versions]
empty-libs = "<latest-version>"

[libraries]
emptylibs-lrucache-manager = { group = "com.github.bashpsk.emptylibs", name = "lrucache-manager", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.lrucache.manager)
}
```

---

## Usage

Using `lrucache-manager` is simple. First, create an instance of `EmptyCacheManager` for the data
type you want to cache.

You can create a new cache instance and specify its maximum size. If no size is provided, it
defaults to 10 items.

```kotlin

// Create a cache for Bitmaps with a maximum size of 20
val imageCache = EmptyCacheManager<Bitmap>(maxSize = 20)

// Create a cache for data objects with the default size of 10
val dataCache = EmptyCacheManager<MyDataObject>()

val bitmap: Bitmap = getImageBitmap(id)
val bitmapKey = "user_profile_picture"

// Add the bitmap to the cache
val wasAdded = imageCache.add(key = bitmapKey, value = bitmap)
if (wasAdded) println("Bitmap added successfully!")

// Check if the bitmap exists in the cache
if (imageCache.exist(key = bitmapKey)) {
    // Retrieve the bitmap from the cache
    val cachedBitmap: Bitmap? = imageCache.get(key = bitmapKey)
    println("Retrieved bitmap from cache.")
}

// Remove the bitmap from the cache
val wasRemoved = imageCache.remove(key = bitmapKey)
if (wasRemoved) println("Bitmap removed from cache.")

// Clear the entire cache
imageCache.evictAll()
println("Image cache cleared.")
```

---