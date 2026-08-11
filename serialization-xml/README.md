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

### 1. Define your Models

Use standard `kotlinx.serialization` annotations along with the library's XML-specific annotations.

```kotlin
@Serializable
data class Text(
    @XmlIndex // Captures the line number from the XML parser
    val index: Int = 0,
    @XmlAttribute("fill") // Maps to 'fill' attribute of the <text> element
    val color: String = "#000000",
    @XmlAttribute("x")
    val x: Float = 0f,
    @XmlAttribute("y")
    val y: Float = 0f,
    @XmlAttribute("font-size")
    val fontSize: String = "12"
)

@Serializable
@XmlElement("svg") // Maps to the <svg> root element
data class SvgRoot(
    @XmlAttribute("viewBox")
    val viewBox: String = "0 0 24 24",
    @XmlElement("text") // Maps child <text> elements to this list
    val texts: List<Text> = emptyList()
)
```

### 2. Decode from XML String

Simply call `Xml.decodeFromString<T>(content)` to parse your XML.

```kotlin
val xmlContent = """
    <svg viewBox="0 0 100 100">
        <text x="10" y="20" fill="#FF0000" font-size="16" />
        <text x="50" y="50" fill="#00FF00" />
    </svg>
""".trimIndent()

val svgRoot = Xml.decodeFromString<SvgRoot>(content = xmlContent)

println(svgRoot.viewBox) // "0 0 100 100"
println(svgRoot.texts.size) // 2
```

---