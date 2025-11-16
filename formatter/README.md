# Formatter - A Kotlin & Java Utility Library

A lightweight and efficient utility library for Kotlin and Java, designed to simplify common
formatting tasks, including rounding decimals and working with predefined format patterns.

`formatter` provides a collection of static methods to handle everyday formatting needs. Whether you
need to round a `Double` or `Float` to a specific number of decimal places or apply a standard
date/time pattern, this library offers a simple and consistent API to get the job done with minimal
code.

---

## Features

- **Decimal Rounding**: Easily round `Double` and `Float` values to a specified number of decimal
  places.
- **Predefined Format Patterns**: Includes a set of common date, time, and date-time patterns
  compliant with Kotlin's `LocalDateTime` API.
- **Easy Integration**: A simple, dependency-free library that is easy to add to any Kotlin or Java
  project.
- **Static and Accessible**: All utilities are available through a static `EmptyFormat` object,
  making them easy to call from anywhere in your code.
- **Resolution Presets**: Comes with predefined `ResolutionType` enums for common aspect ratios,
  simplifying calculations.

---

## Installation

**Groovy (`build.gradle`):**

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:formatter:<latest-version>'
}
```

**Kotlin DSL (`build.gradle.kts`):**

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:formatter:<latest-version>")
}
```

**Kotlin DSL with Version Catalogs (`libs.versions.toml`):**

```toml
[versions]
empty-libs = "<latest-version>"
[libraries]
emptylibs-formatter = { group = "com.github.bashpsk.emptylibs", name = "formatter", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.formatter)
}
```

---

## Usage

Using the `formatter` library is straightforward. All functions are accessible via the `EmptyFormat`
object.

### Decimal Rounding

Use `toRoundedDecimal()` to format `Double` or `Float` numbers to a fixed number of decimal places.
This is useful for displaying prices, measurements, or ratings.

```kotlin
// Round a Double to 2 decimal places
val price = 19.99123
val formattedPrice = EmptyFormat.toRoundedDecimal(price, 2) // Result: 19.99
// Round a Float to 1 decimal place
val rating = 4.85f
val formattedRating = EmptyFormat.toRoundedDecimal(rating, 1) // Result: 4.9
```

### DateTime

The `DateTimePattern` object contains predefined `String` patterns that can be used with `Kotlin`'s
datetime formatting APIs.

```kotlin
val dateString = EmptyFormat.dateTime(
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    DateTimePattern.LONG_DATE_TIME
) // Result: "Sun, Dec 09, 2000 07:30 PM"
```

---