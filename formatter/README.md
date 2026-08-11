# 📝 Formatter

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/formatter.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/formatter)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A lightweight utility library for Kotlin and Java to simplify common formatting tasks, including
decimal rounding and date/time patterns.

---

## ✨ Features

- **Decimal Rounding**: Round `Double` and `Float` values to a fixed number of decimal places.
- **DateTime Patterns**: Predefined patterns for `LocalDateTime` compliant with modern standards.
- **Static Utilities**: All functions are available via the `EmptyFormat` object.
- **Dependency Free**: A pure Kotlin/Java library with no external overhead.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:formatter:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:formatter:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-formatter = { group = "com.github.bashpsk.emptylibs", name = "formatter", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.formatter)
}
```

---

## 🛠️ Usage

### 🔢 Numbers & Math

Utilities for rounding, shortening numbers, and calculating percentages.

```kotlin
// Rounding decimals
19.99123.toRoundedDecimal(fraction = 2) // 19.99
4.85f.toRoundedDecimalString(fraction = 1) // "4.9"

// Shortened notation (K, M, B)
2234L.shortenedNumericalNotation() // "2.2K"
4334567L.shortenedNumericalNotation() // "4.3M"

// Percentages & Aspect Ratios
findPercentage(total = 200, obtained = 50) // 25.0
IntSize(1920, 1080).aspectRatioLabel() // "16:9"
```

### 📅 Date & Time

Flexible formatting using predefined `DateTimePattern` constants.

```kotlin
val millis = System.currentTimeMillis()

// Format Long (millis) or LocalDateTime
millis.toFormattedDateTime(DateTimePattern.LONG_DATE_TIME) // "Sun, Dec 09, 2026 07:30 PM"
millis.toFormattedTime(DateTimePattern.TIME_HH_MM_SS)     // "07:30:33"

// Time utilities
timeToMilliseconds(hours = 1, minutes = 30, seconds = 0) // 5400000L
5.toRoundTime() // "05" (zero-padded string)
```

### ⏱️ Durations

Format time intervals for media players or countdowns.

```kotlin
// Format milliseconds to "MM:SS" or "HH:MM:SS"
val videoPosition = 123000L
formattedDuration(videoPosition, DurationPattern.Separator(":")) // "02:03"

// Extension for kotlin.time.Duration
4500.seconds.formattedDuration(DurationPattern.Separator(":")) // "01:15:00"
```

### 🎨 Colors

Convert between Compose `Color` and Hex strings.

```kotlin
val color = Color.Blue

// Color to Hex
color.toHexString(includeAlpha = true)  // "#FF0000FF"
color.toHexString(includeAlpha = false) // "#0000FF"

// Hex to Color
"#FF0000".parseHexToColor() // Color.Red
```

### 📂 Files & Speed Monitoring

Human-readable file sizes and real-time transfer speed monitoring.

```kotlin
val file = File("video.mp4")

// File sizes (supports Decimal and Binary systems)
file.length().toFileSize(formatSystem = SizeFormatSystem.Decimal) // "3.4 MB"
file.length().toFileSize(formatSystem = SizeFormatSystem.Binary)  // "3.2 MiB"

// Speed Meter for file operations
fileSpeedMeter(source, destination, interval = 1.seconds) { data ->
    println("Speed: ${data.bitrateFormatted}/s, ETA: ${data.etaFormatted}")
}
```

### 🖥️ Resolution Labels

Identify standard display resolutions.

```kotlin
IntSize(1920, 1080).findResolutionLabel() // "FHD"
IntSize(3840, 2160).findResolutionLabel() // "4K UHD"
IntSize(1280, 720).findResolutionLabel()  // "HD"
```

---