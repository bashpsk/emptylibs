package io.bashpsk.emptylibs.storage.storage

import kotlinx.collections.immutable.persistentListOf

/**
 * A list of common file extensions for code files.
 * This list is used to identify code files when scanning directories.
 */
internal val CodeFileExtensions = persistentListOf(
    "kt", "kts", "java", "py", "cpp", "c", "html", "css", "js", "json", "sh",
    "ts", "jsx", "tsx", "php", "rb", "swift", "go", "rs", "lua", "sql",
    "xml", "yaml", "toml", "ini", "bat", "cmd", "make", "gradle", "dockerfile",
    "dart", "h", "hpp", "cs", "asm", "pl", "ps1", "r", "m", "vbs", "tsql",
    "pwn", "scala", "groovy", "fsharp", "prolog", "vb", "erl", "clj"
)