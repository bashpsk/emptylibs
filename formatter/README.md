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

### Decimal Rounding

```kotlin
// Round a Double to 2 decimal places
val price = 19.99123
val formattedPrice = price.toRoundedDecimal(2) // Result: 19.99
// Round a Float to 1 decimal place
val rating = 4.85f
val formattedRating = rating.toRoundedDecimal(1) // Result: 4.9
```

### DateTime Formatting

```kotlin
val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
val formattedDate = currentDateTime.toFormattedDateTime(pattern = DateTimePattern.LONG_DATE_TIME)
// Result: "Sun, Dec 09, 2000 07:30 PM"
```

---