# 📝 PDF Template

[![JitPack](https://jitpack.io/v/com.github.bashpsk.emptylibs/pdf-template.svg)](https://jitpack.io/#com.github.bashpsk.emptylibs/pdf-template)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A powerful state-driven PDF generation library for Android. Generate complex, paginated PDFs from Compose-friendly templates with support for titles, content, and indices.

---

## ✨ Features

- **Template Driven**: Define PDFs using structured templates (`ContentOnly`, `TitleAndContent`, etc.).
- **Automatic Pagination**: Handles text wrapping and page breaks automatically.
- **Table of Contents**: Generate indices with clickable page references.
- **Previews**: Real-time `ImageBitmap` preview generation for UI integration.
- **Metadata Control**: Customize margins, background colors, and page numbering styles.
- **Coroutine Based**: Optimized for background generation to keep the UI responsive.

---

## 📦 Installation

### Groovy (`build.gradle`)

```groovy
dependencies {
    implementation 'com.github.bashpsk.emptylibs:pdf-template:VERSION'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.github.bashpsk.emptylibs:pdf-template:VERSION")
}
```

### Kotlin DSL (`build.gradle.kts`) + Version Catalog (`libs.versions.toml`)

```toml
[versions]
empty-libs = "VERSION"

[libraries]
emptylibs-pdf-template = { group = "com.github.bashpsk.emptylibs", name = "pdf-template", version.ref = "empty-libs" }
```

```kotlin
dependencies {
    implementation(libs.emptylibs.pdf.template)
}
```

---

## 🛠️ Usage

```kotlin
val state = rememberPdfTemplateState()
val template = PdfTemplateType.TitleAndContent(
    title = PdfTextInput("Document Title"),
    content = PdfTextInput("Detailed content goes here...")
)

// Save to file
state.saveAsPdf(template, destinationFile)

// Get preview
val previewFlow = state.getPdfPreviewImageFlow(template)
```

---