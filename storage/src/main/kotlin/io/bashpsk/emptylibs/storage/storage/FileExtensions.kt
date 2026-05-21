package io.bashpsk.emptylibs.storage.storage

import kotlinx.collections.immutable.persistentListOf

internal val AndroidFileExtensions = persistentListOf("apk", "aab")

internal val ArchiveFileExtensions = persistentListOf("zip", "rar", "7z", "tar", "gz", "xz")

internal val AudioFileExtensions = persistentListOf("mp3", "wav", "aac", "flac", "ogg", "m4a")

internal val BackUpFileExtensions = persistentListOf("bak", "backup", "dump", "bkp", "tmp")

internal val BinaryFileExtensions = persistentListOf(
    "bin",
    "jar",
    "dll",
    "so",
    "class",
    "aar",
    "config",
    "ini"
)

internal val DiskImageFileExtensions = persistentListOf("iso", "img", "dmg")

internal val DocumentFileExtensions = persistentListOf("doc", "docx", "odt", "rtf")

internal val CodeFileExtensions = persistentListOf(
    "kt",
    "kts",
    "java",
    "py",
    "cpp",
    "c",
    "html",
    "css",
    "js",
    "json",
    "sh",
    "ts",
    "jsx",
    "tsx",
    "php",
    "rb",
    "swift",
    "go",
    "rs",
    "lua",
    "sql",
    "xml",
    "yaml",
    "toml",
    "ini",
    "bat",
    "cmd",
    "make",
    "gradle",
    "dockerfile",
    "dart",
    "h",
    "hpp",
    "cs",
    "asm",
    "pl",
    "ps1",
    "r",
    "m",
    "vbs",
    "tsql",
    "pwn",
    "scala",
    "groovy",
    "fsharp",
    "prolog",
    "vb",
    "erl",
    "clj"
)

internal val EBookFileExtensions = persistentListOf("epub", "mobi", "azw3")

internal val ExecutableFileExtensions = persistentListOf("exe", "msi", "dmg", "deb")

internal val FontFileExtensions = persistentListOf("ttf", "otf", "woff", "woff2")

internal val GameDataFileExtensions = persistentListOf("dat", "sav", "cfg", "pak", "obb")

internal val GifFileExtensions = persistentListOf("gif")

internal val ImageFileExtensions = persistentListOf("jpg", "jpeg", "png", "bmp", "webp", "svg")

internal val LogFileExtensions = persistentListOf("log", "trace", "audit")

internal val PdfFileExtensions = persistentListOf("pdf")

internal val PresentationFileExtensions = persistentListOf("ppt", "pptx", "odp")

internal val SpreadsheetFileExtensions = persistentListOf("xls", "xlsx", "csv", "ods")

internal val SubTitleFileExtensions = persistentListOf("srt", "sub", "ass", "vtt", "ssa")

internal val ThreeDFileExtensions = persistentListOf("blend", "obj", "fbx", "dae", "gltf", "glb")

internal val TextFileExtensions = persistentListOf("txt", "md", "log", "diff", "patch", "in", "out")

internal val VideoFileExtensions = persistentListOf("mp4", "avi", "mkv", "mov", "wmv", "flv")

internal val VectorFileExtensions = persistentListOf("eps", "cdr")