# ✨ Animations

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/animations.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/animations)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A collection of fluid and customizable Jetpack Compose animations, featuring music visualizers and
dynamic wave effects.

---

## ✨ Features

- **Music Playing Animation**: Customizable music visualizer with vertical bars and adjustable
  easing.
- **Wave Animation**: Fluid, animated wave modifier that can be applied to any Composable.
- **Visibility Aware**: Automatically manages animation states based on component visibility.
- **High Performance**: Optimized using low-level Compose animation APIs.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:animations:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:animations:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-animations = { group = "com.github.bashpsk.emptylibs", name = "animations", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.animations)
}
```

---

## 🛠️ Usage

### Music Playing Animation

```kotlin
MusicPlayingAnimation(
    modifier = Modifier.size(width = 40.dp, height = 30.dp),
    isPlaying = true,
    barCount = 5,
    boxCount = 4,
    easing = EaseInOutBounce
)
```

### Wave Animation Modifier

```kotlin
val waveOffset by infiniteTransition.animateFloat(
    initialValue = 0F,
    targetValue = 1F,
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 5000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    ),
    label = "Wave Offset"
)

Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .waveAnimation(progress = 0.5F, waveOffset = waveOffset, waveColor = Color.Blue)
)
```

---

## 📸 Screenshots

| Music Playing                                                 | Wave Effect                                                 |
|---------------------------------------------------------------|-------------------------------------------------------------|
| ![Screenshot 01](../screenshots/animations_music_playing.jpg) | ![Screenshot 01](../screenshots/animations_wave_effect.jpg) |

---