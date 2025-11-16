# Jetpack UI - A Collection of Modern UI Components for Jetpack Compose

A versatile and modern UI component library for Jetpack Compose, offering a collection of adaptive
layouts, animated navigation bars, custom pickers, and more.

`jetpack-ui` is a curated suite of powerful and reusable composables designed to accelerate UI
development. It includes solutions for common patterns like adaptive two-pane layouts, animated
navigation, overflow menus, and unique text pickers. Each component is self-contained, easy to
integrate, and built with customization in mind.

---

## Features

- **TwoPaneAdaptiveLayout**: A layout that automatically adapts to screen size, showing a two
  pane in Column for smaller screens otherwise show in Row.

- **AnimatedBottomNavBar**: A visually engaging bottom navigation bar with smooth animations for
  item selection, including icon scaling and label transitions.

- **BottomOptionBar**: An adaptive bottom bar that displays as many primary actions as can fit on
  one line and elegantly tucks the rest into a "More" overflow menu.

- **DialTextPicker**: A unique, circular text picker that allows users to select an item by rotating
  a dial, perfect for time or numerical input.

- **WheelTextPicker**: A classic "slot-machine" style wheel picker for selecting an item from a
  vertical list, with haptic feedback and snapping behavior.

- **BasicTextEditor**: A simple, ready-to-use text field composable styled with `OutlinedTextField`
  and designed for easy integration.

---

## Installation

**Groovy (`build.gradle`):**

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:jetpack-ui:<latest-version>'
}
```

**Kotlin DSL (`build.gradle`):**

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:jetpack-ui:<latest-version>")
}
```

**Kotlin DSL with Version Catalogs:**

```toml
[versions]
empty-libs = "<latest-version>"

[libraries]
emptylibs-jetpack-ui = { group = "com.github.bashpsk.emptylibs", name = "jetpack-ui", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.jetpack.ui)
}
```

---

## Usage

### 1. TwoPaneAdaptiveLayout

An adaptive layout for list-detail views. It takes two composable slots: `firstPane` and
`secondPane`.

```kotlin
TwoPaneAdaptiveLayout(
    firstPane = { Image() },
    secondPane = { ImageInfo() },
    aspectRatio = imageRatio
)
```

### 2. AnimatedBottomNavBar

A bottom navigation bar with smooth animations.

```kotlin
Scaffold(
    bottomBar = {
        AnimatedBottomNavBar {
            navItems.forEach { item ->
                BottomNavItem(
                    isSelected = item == selectedItem,
                    label = item.label,
                    icon = item.icon,
                    onItemClick = { selectedItem = item }
                )
            }
        }
    }
) { paddingValues ->
    // Screen content
}
```

### 3. BottomOptionBar

An adaptive bottom bar that displays as many primary actions as can fit on one line and elegantly
tucks the rest into a "More" overflow menu.

```kotlin
val options = listOf(
    OptionBarData(label = "Edit", icon = Icons.Default.Edit),
    OptionBarData(label = "Favorite", icon = Icons.Default.Favorite),
    OptionBarData(label = "Share", icon = Icons.Default.Share),
    OptionBarData(label = "Delete", icon = Icons.Default.Delete),
    OptionBarData(label = "Info", icon = Icons.Default.Info)
).toImmutableList()

// This will show as many items as can fit on one line,
// and the rest will be in a "More" menu.
BottomOptionBar(
    optionList = options,
    onOptionClick = { option ->
        // Handle option click
    },
    maxLines = 1
)
```

### 4. DialTextPicker

A circular picker for selecting from a list of strings.

```kotlin
val hours = (0..23).map { it.toString().padStart(2, '0') }.toImmutableList()
val dialState = rememberDialTextPickerState(textList = hours, initial = "08")

DialTextPicker(state = dialState)
// Observe selected item
Text("Selected Hour: ${dialState.selectedText}")
```

### 5. WheelTextPicker

A vertical wheel-style picker.

```kotlin
val minutes = (0..59).map { it.toString() }.toImmutableList()
val wheelState = rememberWheelTextPickerState(textList = minutes)

WheelTextPicker(
    state = wheelState,
    visibleCount = 5, // Show 5 items at a time
    textStyle = MaterialTheme.typography.bodyMedium,
    dividerFraction = 0.5F, //  50% of width from 'WheelTextPicker' width
    dividerColor = MaterialTheme.colorScheme.primary,
    dividerThickness = 4.dp
)

// Observe selected item
Text("Selected Minute: ${wheelState.selectedText}")
```

### 6. BasicTextEditor

A simple, line-numbered BasicTextField wrapper.

```kotlin
var code by remember { mutableStateOf("fun main() {\n    println(\"Hello, World!\")\n}") }

BasicTextEditor(
    modifier = Modifier.fillMaxSize(),
    inputContent = code,
    onContentChange = { newContent ->
        code = newContent
    }
)
```

---