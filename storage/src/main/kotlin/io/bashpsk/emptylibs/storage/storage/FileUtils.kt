package io.bashpsk.emptylibs.storage.storage

import androidx.compose.runtime.Stable

/**
 * Checks if a given string is a valid folder name.
 *
 * A valid folder name:
 * - Does not contain any of the following characters: `<`, `>`, `:`, `"`, `/`, `\`, `|`, `?`, `*`.
 * - Does not start with a dot (`.`).
 * - Has a length of 127 characters or less.
 *
 * @param name The string to check.
 * @return `true` if the string is a valid folder name, `false` otherwise.
 */
@Stable
fun hasValidFolderName(name: String): Boolean {

    val folderRegex = Regex("[<>:\"/\\\\|?*]")
    val ignoreDotRegex = Regex("^\\.")

    return !name.contains(folderRegex) && !name.matches(ignoreDotRegex) && name.length <= 127
}

/**
 * Checks if a given string is a valid file name.
 *
 * A valid file name:
 * - Does not contain any of the following characters: `<`, `>`, `:`, `"`, `/`, `\`, `|`, `?`, `*`.
 * - Does not start with a dot (`.`).
 * - Has a length of 127 characters or less.
 *
 * @param name The string to check.
 * @return `true` if the string is a valid file name, `false` otherwise.
 */
@Stable
fun hasValidFileName(name: String): Boolean {

    val folderRegex = Regex("[<>:\"/\\\\|?*]")
    val ignoreDotRegex = Regex("^\\.")

    return !name.contains(folderRegex) && !name.matches(ignoreDotRegex) && name.length <= 127
}