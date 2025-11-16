# 👜 Empty Libs - A Collection of Modern Android Libraries

Welcome to **Empty Libs**, a curated suite of modern, lightweight, and easy-to-integrate libraries
for native Android development with Jetpack Compose. Each library is designed to solve a specific
problem with a clean API, robust state management, and a focus on simplicity.

---

## Libraries

This repository contains the following libraries. Each is independently versioned and can be used on
its own.

| Library                                                                             | Version                                                                                                                                                 | Description                                                                     |
|:------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------|
| **[✍️ Canvas Slate](#-canvas-slate---a-drawing-pad-for-jetpack-compose)**           | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/canvas-slate.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/canvas-slate)         | A complete drawing pad with path editing, brush controls, and export.           |
| **[🛠️ Compose Utils](#-compose-utils---a-utility-library-for-jetpack-compose)**    | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/compose-utils.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/compose-utils)       | Serializable/Parcelable data classes for core Compose types (`Offset`, `Size`). |
| **[⚙️ Datastore UI](#-datastore-ui---a-settings-ui-library-for-jetpack-datastore)** | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/datastore-ui.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/datastore-ui)         | Pre-built preference components (switches, sliders) for DataStore.              |
| **[📝 Formatter](#-formatter---a-kotlin--java-utility-library)**                    | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/formatter.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/formatter)               | A simple utility for rounding decimals and using common date/time patterns.     |
| **[👆 Gesture UI](#-gesture-ui---custom-gestures-for-jetpack-compose)**             | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/gesture-ui.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/gesture-ui)             | Custom gestures for Compose, starting with video player controls.               |
| **[🎨 Image Edit](#-image-edit---a-comprehensive-image-editing-library)**           | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/image-edit.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/image-edit)             | A full-screen image editor with tools for drawing, text, shapes, and more.      |
| **[🌈 Image Kolor](#-image-kolor---image-filtering--color-adjustment)**             | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/image-kolor.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/image-kolor)           | Apply pre-built template filters and fine-tune colors with sliders.             |
| **[✂️ Image Krop](#-image-krop---an-advanced-image-cropping-library)**              | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/image-krop.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/image-krop)             | An advanced image cropper with aspect ratio, shape, and preview support.        |
| **[✨ Image Utils](#-image-utils---a-shape-and-bitmap-utility-library)**             | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/image-utils.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/image-utils)           | Apply shape masks to bitmaps and perform common bitmap calculations.            |
| **[🖼️ Image View](#-image-view---a-transformable--swipeable-image-viewer)**        | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/image-view.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/image-view)             | A zoomable, pannable, and swipeable image viewer built on Coil 3.               |
| **[📱 Image Wallpaper](#-image-wallpaper---an-android-wallpaper-cropper--setter)**  | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/image-wallpaper.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/image-wallpaper)   | A complete workflow for cropping an image and setting it as a wallpaper.        |
| **[💎 Jetpack UI](#-jetpack-ui---a-collection-of-modern-ui-components)**            | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/jetpack-ui.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/jetpack-ui)             | A collection of adaptive layouts, animated nav bars, pickers, and more.         |
| **[🎨 Kolor Picker](#-kolor-picker---an-advanced-color-picker)**                    | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/kolor-picker.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/kolor-picker)         | An advanced color picker with HSL panels, an image dropper, and dialogs.        |
| **[📦 LRUCache Manager](#-lrucache-manager---a-generic-lru-cache-for-kotlin)**      | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/lrucache-manager.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/lrucache-manager) | A simple, generic in-memory LRU cache wrapper.                                  |
| **[📁 Storage](#-storage---a-comprehensive-file--storage-utility)**                 | [![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/storage.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/storage)                   | A coroutine-based utility for browsing files and managing storage.              |

---

### ✍️ Canvas Slate - A Drawing Pad for Jetpack Compose

A feature-rich drawing solution that allows free-form drawing, path editing, customizable brushes
and canvas colors, and exporting the final drawing as an `ImageBitmap`.

- **Key Features**: Free-form drawing, path selection & editing, undo/redo, export.
- **[View README](./canvas-slate/README.md)**

### 🛠️ Compose Utils - A Utility Library for Jetpack Compose

Provides serializable and parcelable data classes for core Jetpack Compose types like `Offset`,
`Size`, `IntOffset`, `IntSize`, and their `Dp` variants, simplifying state restoration and data
transfer.

- **Key Features**: Serializable & Parcelable data classes, seamless conversion, state management
  helpers.
- **[View README](./compose-utils/README.md)**

### ⚙️ Datastore UI - A Settings UI Library for Jetpack Datastore

A collection of pre-built preference components (`SwitchPreference`, `SliderPreference`, etc.) that
automatically read from and write to Jetpack DataStore, making settings screens a breeze to create.

- **Key Features**: DataStore-powered components, minimal boilerplate, variety of preference types.
- **[View README](./datastore-ui/README.md)**

### 📝 Formatter - A Kotlin & Java Utility Library

A dependency-free library to simplify common formatting tasks, including rounding `Double` or
`Float` values and providing predefined `SimpleDateFormat` patterns.

- **Key Features**: Decimal rounding, predefined format patterns, lightweight and static.
- **[View README](./formatter/README.md)**

### 👆 Gesture UI - Custom Gestures for Jetpack Compose

A gesture detection library to add complex interactions to your UI. Currently specializes in
providing a rich set of gestures for video players (double-tap to seek, swipe for volume/brightness,
pinch-to-zoom).

- **Key Features**: Video player gestures, robust state management, highly configurable.
- **[View README](./gesture-ui/README.md)**

### 🎨 Image Edit - A Comprehensive Image Editing Library

A complete `ImageEdit` composable with top and bottom toolbars for a seamless editing experience.
Supports drawing, text, shapes, and image overlays with object-based selection, manipulation, and
undo/redo.

- **Key Features**: Free-form drawing, text & shape tools, object manipulation, state management,
  export.
- **[View README](./image-edit/README.md)**

### 🌈 Image Kolor - Image Filtering & Color Adjustment

Provides two main features: fine-tuning image properties (brightness, contrast, saturation) with
sliders, and applying a wide variety of one-tap artistic and corrective filters.

- **Key Features**: Manual color adjustment, pre-built template filters, live previews, export.
- **[View README](./image-kolor/README.md)**

### ✂️ Image Krop - An Advanced Image Cropping Library

A full-featured cropping UI with support for predefined aspect ratios, custom crop shapes (circles,
stars), image flipping, and a live preview.

- **Key Features**: Aspect ratio control, shape masking, interactive crop selection, live preview.
- **[View README](./image-krop/README.md)**

### ✨ Image Utils - A Shape and Bitmap Utility Library

A utility to apply shape masks (Circle, Star, Polygon, etc.) to any `ImageBitmap` and perform common
bitmap calculations, like fitting an image to a canvas.

- **Key Features**: Advanced shape masking, extensible shape system, bitmap extensions.
- **[View README](./image-utils/README.md)**

### 🖼️ Image View - A Transformable & Swipeable Image Viewer

A powerful image viewer built on Coil 3. Supports pinch-to-zoom, pan, rotation, and double-tap
gestures. It automatically becomes a swipeable gallery when given a list of images.

- **Key Features**: Zoom/Pan/Rotate, swipeable gallery, Coil 3 integration, configurable controls.
- **[View README](./image-view/README.md)**

### 📱 Image Wallpaper - An Android Wallpaper Cropper & Setter

A seamless, all-in-one workflow to crop an image to the device's screen dimensions and set it as the
home screen, lock screen, or both.

- **Key Features**: Automatic aspect ratio detection, integrated crop UI, wallpaper type selection.
- **[View README](./image-wallpaper/README.md)**

### 💎 Jetpack UI - A Collection of Modern UI Components

A suite of powerful and reusable composables, including an adaptive `TwoPaneLayout`, an
`AnimatedBottomNavBar`, an adaptive `BottomOptionBar` with an overflow menu, and unique
`DialTextPicker` and `WheelTextPicker` components.

- **Key Features**: Adaptive layouts, animated navigation, custom pickers, overflow menus.
- **[View README](./jetpack-ui/README.md)**

### 🎨 Kolor Picker - An Advanced Color Picker

A versatile color picker with an HSL panel, an image-based dropper tool, and a pre-built dialog.
Features robust state management and optional copy/paste functionality.

- **Key Features**: Classic HSL picker, image dropper, ready-to-use dialog.
- **[View README](./kolor-picker/README.md)**

### 📦 LRUCache Manager - A Generic LRU Cache for Kotlin

A lightweight and efficient wrapper around Android's `LruCache` for easy, type-safe, in-memory
caching of any object type.

- **Key Features**: Generic & type-safe, simple API, dynamic resizing, full cache control.
- **[View README](./lrucache-manager/README.md)**

### 📁 Storage - A Comprehensive File & Storage Utility

A modern, coroutine-based utility for Android that simplifies browsing the file system, querying
storage volumes, reading metadata, and creating files or folders.

- **Key Features**: Storage volume management, file/directory browsing, rich metadata, Flow-based
  API.
- **[View README](./storage/README.md)**

---