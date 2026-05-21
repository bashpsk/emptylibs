package io.bashpsk.emptylibs.storage.storage

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Enum class representing different file types.
 * Each file type has a label and a list of associated file extensions.
 */
enum class FileType {

    /**
     * Android application package files.
     * Includes extensions like "apk" (Android Package Kit) and "aab" (Android App Bundle).
     */
    Android,

    /**
     * Represents archive file types.
     * Includes extensions like "zip", "rar", "7z", "tar", "gz", "xz".
     */
    Archive,

    /**
     * Represents audio files.
     * Includes extensions: mp3, wav, aac, flac, ogg, m4a.
     */
    Audio,

    /**
     * Represents files that are typically backups or temporary files.
     * Common extensions include "bak", "backup", "dump", "bkp", and "tmp".
     */
    BackUp,

    /**
     * Represents binary files, including compiled code, libraries, and configuration files.
     * Examples: .bin, .jar, .dll, .so, .class, .aar, .config, .ini
     */
    Binary,

    /**
     * Represents disk image files such as ISO, IMG, and DMG.
     * Disk images are often used for distributing software or creating bootable media.
     */
    DiskImage,

    /**
     * Represents document files such as .doc, .docx, .odt, .rtf.
     */
    Document,

    /**
     * Represents files containing source code in various programming languages.
     * Includes common extensions for languages like Kotlin, Java, Python, JavaScript, etc.
     */
    Code,

    /**
     * Represents electronic book files.
     * Common extensions include "epub", "mobi", and "azw3".
     */
    EBook,

    /**
     * Executable files, such as applications and installers.
     * Includes extensions like "exe", "msi", "dmg", and "deb".
     */
    Executable,

    /**
     * Font files.
     *
     * Example extensions: `ttf`, `otf`, `woff`, `woff2`.
     */
    Font,

    /**
     * Represents game data files with extensions like "dat", "sav", "cfg", "pak", and "obb".
     * These files typically store game progress, settings, or assets.
     */
    GameData,

    /**
     * Represents a Graphics Interchange Format file.
     * This type is specifically for files with the ".gif" extension.
     */
    Gif,

    /**
     * Represents image files.
     * Includes formats like JPG, JPEG, PNG, BMP, WebP, and SVG.
     */
    Image,

    /**
     * Log files containing records of events or messages.
     * Extensions: log, trace, audit
     */
    Log,

    /**
     * Portable Document Format (PDF) files.
     * Extension: pdf
     */
    Pdf,

    /**
     * Presentation files, often used for slideshows.
     * Extensions: "ppt", "pptx", "odp"
     */
    Presentation,

    /**
     * Represents spreadsheet files.
     * Associated extensions: "xls", "xlsx", "csv", "ods"
     */
    Spreadsheet,

    /**
     * Represents subtitle files, commonly used for displaying subtitles in videos.
     * Examples: .srt, .sub, .ass, .vtt, .ssa
     */
    SubTitle,

    /**
     * 3D Model: Files used for 3D modeling and animation.
     * Common Extensions: blend, obj, fbx, dae, gltf, glb
     */
    ThreeDModel,

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
    Text,

    /**
     * Video files.
     */
    Video,

    /**
     * Vector graphics files like Encapsulated PostScript (EPS) or CorelDRAW (CDR).
     */
    Vector,

    /**
     * Represents an unknown or uncategorized file type.
     * This is typically used as a fallback when the file's extension
     * does not match any of the predefined categories.
     */
    Unknown;

    companion object {

        /**
         * Retrieves the [FileType] associated with the given file extension.
         *
         * This function searches through the predefined [FileType] entries and
         * returns the first one whose list of extensions contains the provided
         * `extension` (case-insensitive).
         *
         * If no matching [FileType] is found, it returns [FileType.Unknown].
         *
         * @param extension The file extension string (e.g., "txt", "jpg", "pdf").
         * @return The corresponding [FileType] or [FileType.Unknown] if not found.
         */
        fun getFileType(extension: String): FileType {

            return entries.firstOrNull { fileType ->

                fileType.extension.contains(extension.lowercase())
            } ?: Unknown
        }

        val FileType.extension: ImmutableList<String>
            get() = when (this) {

                Android -> AndroidFileExtensions
                Archive -> ArchiveFileExtensions
                Audio -> AudioFileExtensions
                BackUp -> BackUpFileExtensions
                Binary -> BinaryFileExtensions
                DiskImage -> DiskImageFileExtensions
                Document -> DocumentFileExtensions
                Code -> CodeFileExtensions
                EBook -> EBookFileExtensions
                Executable -> ExecutableFileExtensions
                Font -> FontFileExtensions
                GameData -> GameDataFileExtensions
                Gif -> GifFileExtensions
                Image -> ImageFileExtensions
                Log -> LogFileExtensions
                Pdf -> PdfFileExtensions
                Presentation -> PresentationFileExtensions
                Spreadsheet -> SpreadsheetFileExtensions
                SubTitle -> SubTitleFileExtensions
                ThreeDModel -> ThreeDFileExtensions
                Text -> TextFileExtensions
                Video -> VideoFileExtensions
                Vector -> VectorFileExtensions
                Unknown -> persistentListOf()
            }
    }
}