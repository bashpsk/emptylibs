package io.bashpsk.emptylibs.storage.storage

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Enum class representing different file types.
 * Each file type has a label and a list of associated file extensions.
 *
 * @property label The display name of the file type.
 * @property extension An immutable list of file extensions associated with this file type.
 */
enum class FileType(
    val label: String = "",
    val extension: ImmutableList<String> = persistentListOf()
) {

    /**
     * Android application package files.
     * Includes extensions like "apk" (Android Package Kit) and "aab" (Android App Bundle).
     */
    ANDROID("Android", persistentListOf("apk", "aab")),

    /**
     * Represents archive file types.
     * Includes extensions like "zip", "rar", "7z", "tar", "gz", "xz".
     */
    ARCHIVE("Archive", persistentListOf("zip", "rar", "7z", "tar", "gz", "xz")),

    /**
     * Represents audio files.
     * Includes extensions: mp3, wav, aac, flac, ogg, m4a.
     */
    AUDIO("Audio", persistentListOf("mp3", "wav", "aac", "flac", "ogg", "m4a")),

    /**
     * Represents files that are typically backups or temporary files.
     * Common extensions include "bak", "backup", "dump", "bkp", and "tmp".
     */
    BACK_UP("Back-Up", persistentListOf("bak", "backup", "dump", "bkp", "tmp")),

    /**
     * Represents binary files, including compiled code, libraries, and configuration files.
     * Examples: .bin, .jar, .dll, .so, .class, .aar, .config, .ini
     */
    BINARY("Binary", persistentListOf("bin", "jar", "dll", "so", "class", "aar", "config", "ini")),

    /**
     * Represents disk image files such as ISO, IMG, and DMG.
     * Disk images are often used for distributing software or creating bootable media.
     */
    DISK_IMAGE("Disk Image", persistentListOf("iso", "img", "dmg")),

    /**
     * Represents document files such as .doc, .docx, .odt, .rtf.
     */
    DOCUMENT("Document", persistentListOf("doc", "docx", "odt", "rtf")),

    /**
     * Represents files containing source code in various programming languages.
     * Includes common extensions for languages like Kotlin, Java, Python, JavaScript, etc.
     */
    CODE("Code", CodeFileExtensions),

    /**
     * Represents electronic book files.
     * Common extensions include "epub", "mobi", and "azw3".
     */
    E_BOOK("E-Book", persistentListOf("epub", "mobi", "azw3")),

    /**
     * Executable files, such as applications and installers.
     * Includes extensions like "exe", "msi", "dmg", and "deb".
     */
    EXECUTABLE("Executable", persistentListOf("exe", "msi", "dmg", "deb")),

    /**
     * Font files.
     *
     * Example extensions: `ttf`, `otf`, `woff`, `woff2`.
     */
    FONT("Font", persistentListOf("ttf", "otf", "woff", "woff2")),

    /**
     * Represents game data files with extensions like "dat", "sav", "cfg", "pak", and "obb".
     * These files typically store game progress, settings, or assets.
     */
    GAME_DATA("Game Data", persistentListOf("dat", "sav", "cfg", "pak", "obb")),

    /**
     * Represents a Graphics Interchange Format file.
     * This type is specifically for files with the ".gif" extension.
     */
    GIF("Gif", persistentListOf("gif")),

    /**
     * Represents image files.
     * Includes formats like JPG, JPEG, PNG, BMP, WebP, and SVG.
     */
    IMAGE("Image", persistentListOf("jpg", "jpeg", "png", "bmp", "webp", "svg")),

    /**
     * Log files containing records of events or messages.
     * Extensions: log, trace, audit
     */
    LOG_FILE("Log File", persistentListOf("log", "trace", "audit")),

    /**
     * Portable Document Format (PDF) files.
     * Extension: pdf
     */
    PDF("PDF", persistentListOf("pdf")),

    /**
     * Presentation files, often used for slideshows.
     * Extensions: "ppt", "pptx", "odp"
     */
    PRESENTATION("Presentation", persistentListOf("ppt", "pptx", "odp")),

    /**
     * Represents spreadsheet files.
     * Associated extensions: "xls", "xlsx", "csv", "ods"
     */
    SPREADSHEET("Spreadsheet", persistentListOf("xls", "xlsx", "csv", "ods")),

    /**
     * Represents subtitle files, commonly used for displaying subtitles in videos.
     * Examples: .srt, .sub, .ass, .vtt, .ssa
     */
    SUB_TITLE("Sub-Title", persistentListOf("srt", "sub", "ass", "vtt", "ssa")),

    /**
     * 3D Model: Files used for 3D modeling and animation.
     * Common Extensions: blend, obj, fbx, dae, gltf, glb
     */
    THREE_D_MODEL("3D Model", persistentListOf("blend", "obj", "fbx", "dae", "gltf", "glb")),

    /**
     * Text files are files that contain only plain text data, without any formatting or special
     * characters.
     * Some common examples of text files include:
     * - **.txt:** Basic text files with no formatting.
     * - **.md:** Markdown files, used for creating formatted text using a plain text editor.
     * - **.log:** Log files, which record events and activities of a system or application.
     * - **.diff:** Diff files, which show the differences between two versions of a file.
     * - **.patch:** Patch files, which contain instructions for modifying a file.
     * - **.in:** Input files, often used to provide data to a program.
     * - **.out:** Output files, which store the results of a program's execution.
     */
    TEXT("Text", persistentListOf("txt", "md", "log", "diff", "patch", "in", "out")),

    /**
     * Video files.
     */
    VIDEO("Video", persistentListOf("mp4", "avi", "mkv", "mov", "wmv", "flv")),

    /**
     * Vector graphics files like Encapsulated PostScript (EPS) or CorelDRAW (CDR).
     */
    VECTOR("Vector", persistentListOf("eps", "cdr")),

    /**
     * Represents an unknown or uncategorized file type.
     * This is typically used as a fallback when the file's extension
     * does not match any of the predefined categories.
     */
    UNKNOWN("Unknown", persistentListOf());

    companion object {

        /**
         * Retrieves the [FileType] associated with the given file extension.
         *
         * This function searches through the predefined [FileType] entries and
         * returns the first one whose list of extensions contains the provided
         * `extension` (case-insensitive).
         *
         * If no matching [FileType] is found, it returns [FileType.UNKNOWN].
         *
         * @param extension The file extension string (e.g., "txt", "jpg", "pdf").
         * @return The corresponding [FileType] or [FileType.UNKNOWN] if not found.
         */
        fun getFileType(extension: String): FileType {

            return entries.firstOrNull { fileType ->

                fileType.extension.contains(extension.lowercase())
            } ?: UNKNOWN
        }
    }
}