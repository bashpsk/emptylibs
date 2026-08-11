# ⚙️ Datastore UI

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/datastore-ui.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/datastore-ui)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A comprehensive UI library for Jetpack Compose that provides pre-built preference components to
easily create settings screens powered by Jetpack DataStore.

---

## ✨ Features

- **DataStore-Powered**: Components directly link to your `DataStore<Preferences>`.
- **Minimal Boilerplate**: Automatically handles state collection and updates.
- **Rich Component Library**:
    - `SwitchPreference`, `CheckBoxPreference`, `SliderPreference`.
    - `DropDownPreference`, `ListOptionPreference`, `SetOptionPreference`.
    - `ColorPickPreference`, `TextFieldPreference`.
- **CompositionLocal Support**: Inject DataStore once via `LocalDatastore` and use components
  anywhere.
- **Material 3**: Built on top of the latest Material Design components.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:datastore-ui:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:datastore-ui:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-datastore-ui = { group = "com.github.bashpsk.emptylibs", name = "datastore-ui", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.datastore.ui)
}
```

---

## 🛠️ Usage

### 1. Setup DataStore Provider

Inject your `DataStore` instance once at the top level of your UI using `LocalDatastore`.

```kotlin
val Context.datastore by preferencesDataStore(name = "settings")

CompositionLocalProvider(LocalDatastore provides datastore) {
    SettingsScreen()
}
```

### 2. Basic Preferences

Simple toggles for boolean settings.

```kotlin
SwitchPreference(
    datastore = null, // Uses LocalDatastore automatically
    key = booleanPreferencesKey("enable_notifications"),
    initialValue = true,
    title = { Text("Enable Notifications") },
    summary = { isEnabled ->
        Text(if (isEnabled) "Receive daily updates" else "Notifications are muted")
    }
)

CheckBoxPreference(
    datastore = null,
    key = booleanPreferencesKey("sync_data"),
    initialValue = false,
    title = { Text("Background Sync") }
)
```

### 3. Numeric & Option Selection

Adjust values or select from a list of predefined options.

#### Slider & DropDown

```kotlin
SliderPreference(
    datastore = null,
    key = floatPreferencesKey("volume_level"),
    initialValue = 0.5f,
    title = { Text("Volume") },
    valueRange = 0f..1f
)

DropDownPreference(
    datastore = null,
    key = stringPreferencesKey("theme_mode"),
    initialValue = "System",
    entities = persistentMapOf(
        "Light" to "Light",
        "Dark" to "Dark",
        "System" to "System"
    ),
    title = { Text("App Theme") }
)
```

#### List Option (Dialog-based)

```kotlin
ListOptionPreference(
    datastore = null,
    key = intPreferencesKey("refresh_interval"),
    initialValue = 15,
    entities = persistentMapOf(
        "15 Minutes" to 15,
        "30 Minutes" to 30,
        "1 Hour" to 60
    ),
    title = { Text("Refresh Interval") },
    dialogTitle = "Select Interval"
)
```

### 4. Advanced Preferences

Color picking and text input.

```kotlin
ColorPickPreference(
    datastore = null,
    key = intPreferencesKey("accent_color"),
    initialValue = Color.Blue.toArgb(),
    title = { Text("Accent Color") }
)

TextFieldPreference(
    datastore = null,
    key = stringPreferencesKey("user_nickname"),
    initialValue = "User",
    title = { Text("Nickname") },
    textFieldValue = myTextFieldState,
    textFieldContent = {
        OutlinedTextField(
            value = myTextFieldState,
            onValueChange = { myTextFieldState = it },
            label = { Text("Enter Nickname") }
        )
    }
)
```

---

## 📸 Screenshots

| Datastore UI                                      |
|---------------------------------------------------|
| ![Screenshot 01](../screenshots/datastore_ui.jpg) |

https://github.com/user-attachments/assets/64f4902b-2fe0-4a2e-88c9-5851d0ca22e0

---