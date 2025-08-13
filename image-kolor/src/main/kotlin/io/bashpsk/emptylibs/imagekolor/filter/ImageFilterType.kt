package io.bashpsk.emptylibs.imagekolor.filter

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

/**
 * Enum class representing different image filter types.
 * Each filter type has a human-readable [label] and a [colorMatrix]
 * that defines the color transformation for the filter.
 *
 * The [colorFilter] property provides a pre-configured [ColorFilter]
 * instance for easy application of the filter.
 */
enum class ImageFilterType(val label: String) {

    /**
     * Represents the original, unfiltered image.
     * This filter does not apply any transformations to the image.
     */
    Original(label = "Original") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix()
    },

    /**
     * Black and white filter. Sets saturation to 0.
     */
    BlackAndWhite(label = "Black & White") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix().apply {

                setToSaturation(0F)
            }
    },

    /**
     * Applies a sepia tone effect to the image, giving it a warm, brownish, old-fashioned look.
     * This is achieved by first desaturating the image and then applying a specific color matrix
     * that shifts the colors towards shades of brown.
     */
    Sepia(label = "Sepia") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix().apply {

                val sepiaMatrixValues = floatArrayOf(
                    0.393F, 0.769F, 0.189F, 0F, 0F,
                    0.349F, 0.686F, 0.168F, 0F, 0F,
                    0.272F, 0.534F, 0.131F, 0F, 0F,
                    0F, 0F, 0F, 1F, 0F
                )

                setToSaturation(0F)
                timesAssign(ColorMatrix(sepiaMatrixValues))
            }
    },

    /**
     * Inverts the colors of the image.
     * This creates a negative-like effect.
     */
    Invert(label = "Invert") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix(
                floatArrayOf(
                    -1F, 0F, 0F, 0F, 255F,
                    0F, -1F, 0F, 0F, 255F,
                    0F, 0F, -1F, 0F, 255F,
                    0F, 0F, 0F, 1F, 0F
                )
            )
    },

    /**
     * Applies a grayscale filter to the image, converting it to shades of gray.
     * This filter works by calculating the luminance of each pixel and setting the red, green, and
     * blue components to this luminance value.
     */
    Grayscale(label = "Grayscale") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix(
                floatArrayOf(
                    0.299F, 0.587F, 0.114F, 0F, 0F,
                    0.299F, 0.587F, 0.114F, 0F, 0F,
                    0.299F, 0.587F, 0.114F, 0F, 0F,
                    0F, 0F, 0F, 1F, 0F
                )
            )
    },

    /**
     * Applies a vintage filter to the image, giving it an old-fashioned look.
     * This effect is achieved by reducing saturation and applying a specific color matrix.
     */
    Vintage(label = "Vintage") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix().apply {

                val vintageMatrixValues = floatArrayOf(
                    1.1F, 0F, 0F, 0F, -20F,
                    0F, 1.05F, 0F, 0F, -10F,
                    0F, 0F, 0.8F, 0F, 30F,
                    0F, 0F, 0F, 1F, 0F
                )

                setToSaturation(0.2F)
                timesAssign(ColorMatrix(vintageMatrixValues))
            }
    },

    /**
     * Technicolor filter: Simulates the look of old Technicolor films with
     * boosted red and blue channels and reduced green.
     */
    Technicolor(label = "Technicolor") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix(
                floatArrayOf(
                    1.5F, 0F, 0F, 0F, -50F,
                    0F, 0.8F, 0F, 0F, 0F,
                    0F, 0F, 1.5F, 0F, -50F,
                    0F, 0F, 0F, 1F, 0F
                )
            )
    },

    /**
     * Polaroid:
     * This filter emulates the look of a Polaroid photograph, characterized by a slightly
     * desaturated and warm tone. It enhances the red and green channels more than the blue
     * channel, giving it a nostalgic, faded appearance.
     */
    Polaroid(label = "Polaroid") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix().apply {

                val polaroidMatrixValues = floatArrayOf(
                    1.2F, 0.1F, 0.1F, 0F, -30F,
                    0.1F, 1.2F, 0.1F, 0F, -30F,
                    0.1F, 0.1F, 1.0F, 0F, -10F,
                    0F, 0F, 0F, 1F, 0F
                )

                setToSaturation(0.5F)
                timesAssign(ColorMatrix(polaroidMatrixValues))
            }
    },

    /**
     * Cool: Enhances blue tones and slightly increases blue channel brightness.
     * This filter gives the image a cooler, bluish tint.
     */
    Cool(label = "Cool") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix(
                floatArrayOf(
                    0.9F, 0F, 0F, 0F, 0F,
                    0F, 1.0F, 0F, 0F, 0F,
                    0F, 0F, 1.2F, 0F, 10F,
                    0F, 0F, 0F, 1F, 0F
                )
            )
    },

    /**
     * Warm filter: Increases the red and green channels while slightly decreasing the blue channel
     * to give the image a warmer, more yellowish tone.
     * This filter enhances the warmth of the image by boosting red and green components
     * and slightly reducing blue, which results in a cozy, sunlit effect.
     */
    Warm(label = "Warm") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix(
                floatArrayOf(
                    1.2F, 0F, 0F, 0F, 10F,
                    0F, 1.1F, 0F, 0F, 0F,
                    0F, 0F, 0.9F, 0F, 0F,
                    0F, 0F, 0F, 1F, 0F
                )
            )
    },

    /**
     * Enhances the contrast of the image, making dark areas darker and light areas lighter.
     */
    HighContrast(label = "High Contrast") {

        override val colorMatrix: ColorMatrix
            get() {

                val factor = 1.5F
                val offset = 128F * (1F - factor)

                return ColorMatrix(
                    floatArrayOf(
                        factor, 0F, 0F, 0F, offset,
                        0F, factor, 0F, 0F, offset,
                        0F, 0F, factor, 0F, offset,
                        0F, 0F, 0F, 1F, 0F
                    )
                )
            }
    },

    /**
     * Low Contrast: Decreases the difference between light and dark areas.
     * This filter makes the image appear flatter and less defined by reducing the intensity range.
     */
    LowContrast(label = "Low Contrast") {

        override val colorMatrix: ColorMatrix
            get() {

                val factor = 0.7F
                val offset = 128F * (1F - factor)

                return ColorMatrix(
                    floatArrayOf(
                        factor, 0F, 0F, 0F, offset,
                        0F, factor, 0F, 0F, offset,
                        0F, 0F, factor, 0F, offset,
                        0F, 0F, 0F, 1F, 0F
                    )
                )
            }
    },

    /**
     * Represents a brighter filter type, which increases the brightness of the image.
     */
    Brighter(label = "Brighter") {

        override val colorMatrix: ColorMatrix
            get() {

                val brightness = 50F

                return ColorMatrix(
                    floatArrayOf(
                        1F, 0F, 0F, 0F, brightness,
                        0F, 1F, 0F, 0F, brightness,
                        0F, 0F, 1F, 0F, brightness,
                        0F, 0F, 0F, 1F, 0F
                    )
                )
            }
    },

    /**
     * Darker filter: Reduces the brightness of the image.
     */
    Darker(label = "Darker") {

        override val colorMatrix: ColorMatrix
            get() {

                val darkness = -50F

                return ColorMatrix(
                    floatArrayOf(
                        1F, 0F, 0F, 0F, darkness,
                        0F, 1F, 0F, 0F, darkness,
                        0F, 0F, 1F, 0F, darkness,
                        0F, 0F, 0F, 1F, 0F
                    )
                )
            }
    },

    /**
     * Hue Rotate Red filter: Rotates the hue towards red.
     * This filter makes reds more prominent and slightly shifts other colors towards red.
     */
    HueRotateRed(label = "Hue Rotate (Red)") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix(
                floatArrayOf(
                    1.2F, 0.0F, -0.2F, 0F, 0F,
                    -0.1F, 1.0F, 0.1F, 0F, 0F,
                    -0.1F, 0.2F, 0.8F, 0F, 0F,
                    0F, 0F, 0F, 1F, 0F
                )
            )
    },

    /**
     * Hue Rotate (Green) filter: Rotates the hues in the image towards green.
     * This effect can make images appear more lush or vibrant in green tones.
     */
    HueRotateGreen(label = "Hue Rotate (Green)") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix(
                floatArrayOf(
                    0.8F, 0.2F, 0.0F, 0F, 0F,
                    0.1F, 1.1F, -0.1F, 0F, 0F,
                    -0.2F, 0.0F, 1.2F, 0F, 0F,
                    0F, 0F, 0F, 1F, 0F
                )
            )
    },

    /**
     * Hue Rotate (Blue) filter: Rotates the hues in the image with a bias towards blue.
     * This filter adjusts the color channels to shift hues towards blue, creating a cool,
     * blue-tinted effect.
     */
    HueRotateBlue(label = "Hue Rotate (Blue)") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix(
                floatArrayOf(
                    0.9F, -0.1F, 0.2F, 0F, 0F,
                    0.1F, 0.8F, 0.1F, 0F, 0F,
                    0.0F, -0.2F, 1.2F, 0F, 0F,
                    0F, 0F, 0F, 1F, 0F
                )
            )
    },

    /**
     * Night Vision filter.
     * This filter applies a green tint and increases contrast to simulate a night vision effect.
     *
     * The filter first desaturates the image slightly.
     * Then, it applies a color matrix to shift colors towards green and adjust brightness.
     * Finally, it increases the contrast to enhance details.
     */
    NightVision(label = "Night Vision") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix().apply {

                val nightVisionMatrixValues = floatArrayOf(
                    0.1F, 0.4F, 0.1F, 0F, -30F,
                    0.3F, 1.7F, 0.3F, 0F, -40F,
                    0.1F, 0.4F, 0.1F, 0F, -30F,
                    0F, 0F, 0F, 1F, 0F
                )

                setToSaturation(0.1F)
                timesAssign(ColorMatrix(nightVisionMatrixValues))

                val factor = 1.3F
                val offset = 128F * (1F - factor)

                val contrastValues = floatArrayOf(
                    factor, 0F, 0F, 0F, offset,
                    0F, factor, 0F, 0F, offset,
                    0F, 0F, factor, 0F, offset,
                    0F, 0F, 0F, 1F, 0F
                )

                timesAssign(ColorMatrix(contrastValues))
            }
    },

    /**
     * Kodachrome filter: A filter that mimics the iconic look of Kodachrome film, known for its
     * vibrant colors, high contrast, and distinctive color rendition. This filter enhances reds
     * and blues, providing a rich, nostalgic feel.
     */
    Kodachrome(label = "Kodachrome") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix(
                floatArrayOf(
                    1.12855F, -0.39673F, -0.03992F, 0F, 0.24991F,
                    -0.16404F, 1.08352F, -0.05498F, 0F, 0.09698F,
                    -0.16786F, -0.56034F, 1.60148F, 0F, 0.35334F,
                    0F, 0F, 0F, 1F, 0F
                )
            )
    },

    /**
     * Filter that increases the saturation of the image, making colors more vibrant.
     */
    Saturate(label = "Saturate") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix().apply {

                setToSaturation(1.5F)
            }
    },

    /**
     * Applies an alternative sepia tone to the image, giving it a warm, brownish tint.
     * This version of sepia directly applies a color matrix without desaturating the image first,
     * which can result in a different visual effect compared to the standard Sepia filter.
     */
    SepiaAlternative(label = "Sepia Alt") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix(
                floatArrayOf(
                    0.393F, 0.769F, 0.189F, 0F, 0F,
                    0.349F, 0.686F, 0.168F, 0F, 0F,
                    0.272F, 0.534F, 0.131F, 0F, 0F,
                    0F, 0F, 0F, 1F, 0F
                )
            )
    },

    /**
     * Increases the intensity of the red color channel.
     * This filter enhances the red tones in the image, making them appear more vibrant and
     * prominent.
     * It achieves this by scaling the red component of each pixel by a factor of 1.5.
     */
    BoostRed(label = "Boost Red") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix().apply {

                this[0, 0] = 1.5F
            }
    },

    /**
     * A filter that boosts the green channel of the image.
     */
    BoostGreen(label = "Boost Green") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix().apply {

                this[1, 1] = 1.5F
            }
    },

    /**
     * Enhances the blue channel in the image, making blue colors more prominent.
     * This effect is achieved by increasing the multiplier for the blue component in the color
     * matrix.
     */
    BoostBlue(label = "Boost Blue") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix().apply {

                this[2, 2] = 1.5F
            }
    },

    /**
     * Applies a cyanotype filter, giving the image a blue monochrome appearance.
     * This filter desaturates the image and then applies a color transformation
     * to shift the tones towards cyan and blue, mimicking the historical
     * cyanotype photographic printing process.
     */
    Cyanotype(label = "Cyanotype") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix().apply {

                val cyanotypeMatrixValues = floatArrayOf(
                    0.1F, 0.5F, 1.0F, 0F, 0F,
                    0.1F, 0.4F, 0.8F, 0F, 0F,
                    0.0F, 0.3F, 0.6F, 0F, 0F,
                    0F, 0F, 0F, 1F, 0F
                )

                setToSaturation(0F)
                this[0, 4] = 10F
                this[1, 4] = 20F
                this[2, 4] = 30F
                timesAssign(ColorMatrix(cyanotypeMatrixValues))
            }
    },

    /**
     * Moon filter: Applies a cool, slightly desaturated, and darkened effect,
     * reminiscent of moonlight. It reduces overall saturation and adjusts color
     * channels to create a blueish tint, enhancing shadows and highlights.
     */
    Moon(label = "Moon") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix().apply {

                val moonMatrixValues = floatArrayOf(
                    0.8F, 0.1F, 0.1F, 0F, -20F,
                    0.1F, 0.7F, 0.1F, 0F, -20F,
                    0.2F, 0.2F, 1.0F, 0F, 10F,
                    0F, 0F, 0F, 1F, 0F
                )

                setToSaturation(0.1F)
                timesAssign(ColorMatrix(moonMatrixValues))
            }
    },

    /**
     * Lomo filter effect, characterized by high contrast, saturated colors, and often a vignette.
     */
    Lomo(label = "Lomo") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix().apply {

                val lomoMatrixValues = floatArrayOf(
                    1.7F, 0.1F, 0.1F, 0F, -73.1F,
                    0F, 1.7F, 0.1F, 0F, -73.1F,
                    0F, 0.1F, 1.6F, 0F, -73.1F,
                    0F, 0F, 0F, 1F, 0F
                )

                timesAssign(ColorMatrix(lomoMatrixValues))

                val contrast = 1.2F
                val scale = contrast + 1F
                val translate = (-.5F * scale + .5F) * 255F

                val contrastMatrixValues = floatArrayOf(
                    scale, 0F, 0F, 0F, translate,
                    0F, scale, 0F, 0F, translate,
                    0F, 0F, scale, 0F, translate,
                    0F, 0F, 0F, 1F, 0F
                )

                timesAssign(ColorMatrix(contrastMatrixValues))
            }
    },

    /**
     * Clarendon filter: Increases saturation and contrast, adding a cyan tint to highlights
     * and cooling shadows. This filter makes images appear more vibrant and sharp.
     */
    Clarendon(label = "Clarendon") {

        override val colorMatrix: ColorMatrix
            get() = ColorMatrix().apply {

                setToSaturation(1.35F)

                val contrastValue = 1.2F
                val scale = contrastValue
                val translate = (-.5F * scale + .5F) * 255F

                val contrastMatrixValues = floatArrayOf(
                    scale, 0F, 0F, 0F, translate,
                    0F, scale, 0F, 0F, translate,
                    0F, 0F, scale, 0F, translate,
                    0F, 0F, 0F, 1F, 0F
                )

                timesAssign(ColorMatrix(contrastMatrixValues))

                val cyanTintMatrixValues = floatArrayOf(
                    0.9F, 0F, 0F, 0F, 5F,
                    0F, 1.1F, 0F, 0F, 5F,
                    0F, 0F, 1.25F, 0F, 10F,
                    0F, 0F, 0F, 1F, 0F
                )

                timesAssign(ColorMatrix(cyanTintMatrixValues))
            }
    };

    /**
     * The [ColorMatrix] associated with this image filter type.
     * This matrix is used to transform the colors of an image.
     */
    internal abstract val colorMatrix: ColorMatrix

    /**
     * A [ColorFilter] that applies the image filter.
     * This property is lazily initialized to optimize performance.
     */
    val colorFilter: ColorFilter by lazy { ColorFilter.colorMatrix(colorMatrix = colorMatrix) }
}