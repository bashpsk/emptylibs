# 📜 Serialization XML

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/serialization-xml.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/serialization-xml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A lightweight XML serialization and deserialization library for Kotlin, built on top of the
`kotlinx.serialization` framework.

---

## ✨ Features

- **kotlinx.serialization Integration**: Leverages standard Kotlin serialization annotations and
  modules.
- **Pure Kotlin**: Designed specifically for Kotlin projects.
- **Simple API**: Entry point via the `Xml` object for easy string decoding.
- **Namespaces Support**: Configurable handling of XML namespaces.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:serialization-xml:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:serialization-xml:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-serialization-xml = { group = "com.github.bashpsk.emptylibs", name = "serialization-xml", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.serialization.xml)
}
```

---

## 🛠️ Usage

```kotlin
@Serializable
data class Text(
    @XmlIndex
    val index: Int = 0,
    @XmlAttribute("fill")
    val hex: String = "",
    @XmlAttribute("x")
    val x: String = "0",
    @XmlAttribute("y")
    val y: String = "0",
    @XmlAttribute("font-size")
    val fontSize: String = "12"
)

@Serializable
@XmlElement("svg")
data class SvgRoot(
    @XmlAttribute("viewBox")
    val viewBox: String = "0 0 24 24",
    @XmlElement("text")
    val texts: List<Text> = emptyList()
)

val svgRoot = Xml.decodeFromString<SvgRoot>(content = svgContent)
```

---