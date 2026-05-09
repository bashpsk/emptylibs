package io.bashpsk.emptylibs.formatter.resolution

/**
 * Represents a comprehensive list of display resolutions.
 *
 * Each enum constant holds a label, width, and height. The name of the constant
 * is derived from its designation or a sanitized version of its common name.
 *
 * @property label A common or official name for the resolution (e.g., "Full HD", "4K UHD").
 * @property width The width of the resolution in pixels.
 * @property height The height of the resolution in pixels.
 */
@Suppress("spellcheckingInspection", "EnumEntryName")
enum class ResolutionType(val label: String = "", val width: Int = 0, val height: Int = 0) {

    /** A special value representing an unknown or undefined resolution. */
    Unknown(label = "Unknown"),

    /** A single pixel, the smallest unit of a digital display (1x1). */
    Pixel(label = "1x1", width = 1, height = 1),

    /** A low-quality resolution, one-tenth the height of 720p (128x72). */
    _72p(label = "72p", width = 128, height = 72),

    /** A low-quality resolution, one-tenth the height of 1080p (192x108). */
    _108p(label = "108p", width = 192, height = 108),

    /** Quarter Quarter VGA, used in early mobile devices and webcams (160x120). */
    QQVGA(label = "QQVGA", width = 160, height = 120),

    /** The lowest resolution (256x144). */
    _144p(label = "144p", width = 256, height = 144),

    /** Quarter Common Intermediate Format, used for early video conferencing (176x144). */
    QCIF(label = "QCIF", width = 176, height = 144),

    /** Half Quarter VGA, used by the Nintendo Game Boy Advance (240x160). */
    HQVGA(label = "HQVGA", width = 240, height = 160),

    /** Quarter VGA, common in early smartphones and digital cameras (320x240). */
    QVGA(label = "QVGA", width = 320, height = 240),

    /** Wide Quarter VGA, used in many portable devices (400x240). */
    WQVGA(label = "WQVGA", width = 400, height = 240),

    /** A common low-definition resolution (426x240). */
    _240p(label = "240p", width = 426, height = 240),

    /** Standard Interchange Format for NTSC (352x240). */
    SIF(label = "SIF", width = 352, height = 240),

    /** Common Intermediate Format for PAL (352x288). */
    CIF(label = "CIF", width = 352, height = 288),

    /** A standard-definition resolution (480x360). */
    _360p(label = "360p", width = 480, height = 360),

    /** Half-size VGA, used by early iPhones (480x320). */
    HVGA(label = "HVGA", width = 480, height = 320),

    /** ninth of Full HD, (640x360). */
    nHD(label = "nHD", width = 640, height = 360),

    /** Standard-definition digital resolution (NTSC) (720x480). */
    _480p(label = "480p", width = 720, height = 480),

    /** Standard-definition digital resolution (PAL) (720x576). */
    PAL(label = "PAL", width = 720, height = 576),

    /** Video Graphics Array, a foundational PC display standard (640x480). */
    VGA(label = "VGA", width = 640, height = 480),

    /** Wide VGA, a common resolution for early smartphones (800x480). */
    WVGA(label = "WVGA", width = 800, height = 480),

    /** Full Wide VGA (854x480). */
    FWVGA(label = "FWVGA", width = 854, height = 480),

    /** Super VGA, a common PC monitor resolution (800x600). */
    SVGA(label = "SVGA", width = 800, height = 600),

    /** Quarter High Definition, one-quarter the pixels of 1080p (960x540). */
    qHD(label = "qHD", width = 960, height = 540),

    /** Double VGA (960x640). */
    DVGA(label = "DVGA", width = 960, height = 640),

    /** Wide Super VGA, common in netbooks (1024x600). */
    WSVGA(label = "WSVGA", width = 1024, height = 600),

    /** Extended Graphics Array, a standard for 4:3 displays (1024x768). */
    XGA(label = "XGA", width = 1024, height = 768),

    /** Standard High Definition (720p), the minimum for HD (1280x720). */
    HD(label = "HD", width = 1280, height = 720),

    /** Wide XGA, a common widescreen resolution (1280x800). */
    WXGA(label = "WXGA", width = 1280, height = 800),

    /** A common variant of WXGA used by many laptops (1366x768). */
    WXGA_1366(label = "WXGA", width = 1366, height = 768),

    /** Super XGA Minus, a 4:3 aspect ratio resolution (1280x960). */
    SXGA_Minus(label = "SXGA-", width = 1280, height = 960),

    /** Super XGA, a standard for 5:4 aspect ratio (1280x1024). */
    SXGA(label = "SXGA", width = 1280, height = 1024),

    /** High Definition Plus, a step up from 720p (1600x900). */
    HD_Plus(label = "HD+", width = 1600, height = 900),

    /** Wide Super XGA, a 16:10 resolution (1440x900). */
    WSXGA(label = "WSXGA", width = 1440, height = 900),

    /** Super XGA Plus, a 4:3 resolution (1400x1050). */
    SXGA_Plus(label = "SXGA+", width = 1400, height = 1050),

    /** Wide Super XGA Plus, a 16:10 resolution (1680x1050). */
    WSXGA_Plus(label = "WSXGA+", width = 1680, height = 1050),

    /** Ultra XGA, a high-resolution 4:3 display standard (1600x1200). */
    UXGA(label = "UXGA", width = 1600, height = 1200),

    /** Full High Definition (1080p), the standard for modern HD (1920x1080). */
    FHD(label = "FHD", width = 1920, height = 1080),

    /** Digital Cinema Initiatives 2K, the standard for cinema projection (2048x1080). */
    _2K_DCI(label = "2K DCI", width = 2048, height = 1080),

    /** Wide Ultra XGA, a 16:10 version of FHD (1920x1200). */
    WUXGA(label = "WUXGA", width = 1920, height = 1200),

    /** Quad Wide XGA, a 16:9 high-resolution display (2048x1152). */
    QWXGA(label = "QWXGA", width = 2048, height = 1152),

    /** Full HD Plus, a 3:2 aspect ratio version of FHD (1920x1280). */
    FHD_Plus(label = "FHD+", width = 1920, height = 1280),

    /** Ultra-Wide Full HD, a 21:9 aspect ratio for cinematic viewing (2560x1080). */
    UW_FHD(label = "UW-FHD", width = 2560, height = 1080),

    /** Quad XGA, four times the resolution of XGA (2048x1536). */
    QXGA(label = "QXGA", width = 2048, height = 1536),

    /** Quad High Definition (1440p), four times the pixels of 720p HD (2560x1440). */
    QHD(label = "QHD", width = 2560, height = 1440),

    /** Wide Quad High Definition, the common name for 16:9 QHD displays (2560x1440). */
    WQHD(label = "WQHD", width = 2560, height = 1440),

    /** Wide Quad XGA, the 16:10 variant (2560x1600). */
    WQXGA(label = "WQXGA", width = 2560, height = 1600),

    /** Quad HD Plus, a high-pixel-density 16:9 resolution (3200x1800). */
    QHD_Plus(label = "QHD+", width = 3200, height = 1800),

    /** Ultra-Wide Quad HD, offering a 21:9 cinematic aspect ratio (3440x1440). */
    UW_QHD(label = "UW-QHD", width = 3440, height = 1440),

    /** Quad Super XGA, a high-resolution 5:4 display format (2560x2048). */
    QSXGA(label = "QSXGA", width = 2560, height = 2048),

    /** 3K (16:9), a mid-tier resolution above QHD (2880x1620). */
    _3K(label = "3K", width = 2880, height = 1620),

    /** 4K Ultra High Definition, a 16:9 display resolution (3840x2160). */
    _4K_UHD(label = "4K UHD", width = 3840, height = 2160),

    /** Digital Cinema Initiatives 4K, the standard for professional 4K production (4096x2160). */
    _4K_DCI(label = "4K DCI", width = 4096, height = 2160),

    /** Wide Quad Ultra XGA, a 16:10 4K resolution (3840x2400). */
    WQUXGA(label = "WQUXGA", width = 3840, height = 2400),

    /** 5K Ultra High Definition, a 16:9 display resolution (5120x2880). */
    _5K_UHD(label = "5K UHD", width = 5120, height = 2880),

    /** Ultra-Wide 5K (or WUHD), a 21:9 display resolution (5120x2160). */
    UW_5K(label = "UW-5K", width = 5120, height = 2160),

    /** Hex Super XGA, a professional-grade 5:4 resolution (5120x4096). */
    HSXGA(label = "HSXGA", width = 5120, height = 4096),

    /** 6K Ultra High Definition, offering more detail than 5K (5760x3240). */
    _6K_UHD(label = "6K UHD", width = 5760, height = 3240),

    /** 8K Ultra High Definition, the next generation of consumer display technology (7680x4320). */
    _8K_UHD(label = "8K UHD", width = 7680, height = 4320),

    /** Digital Cinema Initiatives 8K, for professional cinema production (8192x4320). */
    _8K_DCI(label = "8K DCI", width = 8192, height = 4320),

    /** Wide Hex Ultra XGA, a 16:10 8K-level resolution (7680x4800). */
    WHUXGA(label = "WHUXGA", width = 7680, height = 4800),

    /** 10K Ultra High Definition, a resolution for massive screens (10240x5760). */
    _10K_UHD(label = "10K UHD", width = 10240, height = 5760),

    /** 12K Ultra High Definition, a resolution for high-definition (11520x6480). */
    _12K_UHD(label = "12K UHD", width = 11520, height = 6480),

    /** 16K Ultra High Definition, a resolution for ultra-high-definition (15360x8640). */
    _16K_UHD(label = "16K UHD", width = 15360, height = 8640),

    /** 32K Ultra High Definition, a resolution for ultra-high-definition (30720x17280). */
    _32K_UHD(label = "32K UHD", width = 30720, height = 17280);

    companion object {

        /**
         * Checks if the given width and height form a valid resolution.
         *
         * @param width The width in pixels.
         * @param height The height in pixels.
         * @return `true` if width and height are greater than 0, `false` otherwise.
         */
        fun hasValid(width: Int, height: Int): Boolean {

            return width > 0 && height > 0
        }

        /**
         * Finds a [ResolutionType] that matches the given width and height.
         *
         * @param width The width in pixels.
         * @param height The height in pixels.
         * @return The matching [ResolutionType], or [Unknown] if no match is found.
         */
        fun find(width: Int, height: Int): ResolutionType {

            return findOrNull(width = width, height = height) ?: Unknown
        }

        /**
         * Finds a [ResolutionType] that matches the given width and height.
         *
         * @param width The width in pixels.
         * @param height The height in pixels.
         * @return The matching [ResolutionType], or `null` if no match is found.
         */
        fun findOrNull(width: Int, height: Int): ResolutionType? {

            return hasValid(width = width, height = height).takeIf { isValid -> isValid }?.let {

                entries.find { type ->

                    (type.width == width && type.height == height)
                            || (type.width == height && type.height == width)
                }
            }
        }
    }
}