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

### 1. Provide DataStore

```kotlin
val Context.datastore by preferencesDataStore(name = "settings")

CompositionLocalProvider(LocalDatastore provides datastore) {
    SettingsScreen()
}
```

### 2. Use Preferences

#### SwitchPreference

```kotlin
SwitchPreference(
    modifier = Modifier.fillMaxWidth(),
    datastore = datastore,
    key = booleanPreferencesKey("SWITCH-PREFERENCE"),
    initialValue = false,
    title = { PreferenceTitle(title = "Switch Preference") },
    summary = {
        PreferenceSummary(
            summary = "${
                if (it) "Enable" else "Disable"
            } the switch preference."
        )
    }
)
```

#### CheckBoxPreference

```kotlin
CheckBoxPreference(
    modifier = Modifier.fillMaxWidth(),
    datastore = datastore,
    key = booleanPreferencesKey("CHECK-BOX-PREFERENCE"),
    initialValue = false,
    title = { PreferenceTitle(title = "Check Box Preference") },
    summary = { checked ->

        PreferenceSummary(
            summary = "${
                if (checked) "Enable" else "Disable"
            } the check box preference."
        )
    }
)
```

#### SliderPreference

```kotlin
SliderPreference(
    modifier = Modifier.fillMaxWidth(),
    datastore = datastore,
    key = floatPreferencesKey("SLIDER-PREFERENCE"),
    initialValue = 0.0F,
    title = { PreferenceTitle(title = "Slider Preference") },
    summary = { PreferenceSummary(summary = "Adjust slider value.") },
    valueRange = 0.0F..1.0F
)
```

---

## 📸 Screenshots

| Datastore UI                                      |
|---------------------------------------------------|
| ![Screenshot 01](../screenshots/datastore_ui.jpg) |

https://github.com/user-attachments/assets/64f4902b-2fe0-4a2e-88c9-5851d0ca22e0

---