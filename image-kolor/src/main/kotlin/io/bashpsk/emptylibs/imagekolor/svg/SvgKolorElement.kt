package io.bashpsk.emptylibs.imagekolor.svg

import androidx.compose.runtime.Immutable
import io.bashpsk.emptylibs.serializationxml.annotation.XmlAttribute
import io.bashpsk.emptylibs.serializationxml.annotation.XmlElement
import io.bashpsk.emptylibs.serializationxml.annotation.XmlIndex
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

/**
 * Interface representing an SVG element that can be recolored.
 */
@OptIn(ExperimentalSerializationApi::class, ExperimentalUuidApi::class)
@Immutable
@Serializable
sealed interface SvgKolorElement {

    /**
     * The index of the element in the SVG structure.
     */
    val index: Int

    /**
     * The original hex color string of the element.
     */
    val oldHex: String

    /**
     * The new hex color string to be applied to the element.
     */
    val newHex: String

    /**
     * Creates a copy of this element with a new color.
     *
     * @param newHex The new hex color string.
     * @return A new instance of [SvgKolorElement] with the updated color.
     */
    fun copy(newHex: String): SvgKolorElement

    /**
     * Converts this element back to its SVG string representation.
     *
     * @param hex The color hex string to use in the SVG element.
     * @return The SVG element as a string.
     */
    fun toSvgElement(hex: String): String

    /**
     * Represents a path element in an SVG.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Immutable
    @Serializable
    data class Path(
        @XmlIndex
        override val index: Int = 0,
        @XmlAttribute("fill")
        override val oldHex: String = "",
        override val newHex: String = "",
        @XmlAttribute("d")
        val d: String = ""
    ) : SvgKolorElement {

        override fun copy(newHex: String): Path {
            return copy(index = index, newHex = newHex)
        }

        override fun toSvgElement(hex: String): String {
            return """<path d="$d" fill="$hex" />"""
        }
    }

    /**
     * Represents a rect element in an SVG.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Immutable
    @Serializable
    data class Rect(
        @XmlIndex
        override val index: Int = 0,
        @XmlAttribute("fill")
        override val oldHex: String = "",
        override val newHex: String = "",
        @XmlAttribute("x")
        val x: String = "0",
        @XmlAttribute("y")
        val y: String = "0",
        @XmlAttribute("width")
        val width: String = "0",
        @XmlAttribute("height")
        val height: String = "0",
        @XmlAttribute("rx")
        val rx: String = "0",
        @XmlAttribute("ry")
        val ry: String = "0"
    ) : SvgKolorElement {

        override fun copy(newHex: String): Rect {
            return copy(index = index, newHex = newHex)
        }

        override fun toSvgElement(hex: String): String {
            return """<rect x="$x" y="$y" width="$width" height="$height"
                | rx="$rx" ry="$ry" fill="$hex" />""".trimMargin()
        }
    }

    /**
     * Represents a circle element in an SVG.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Immutable
    @Serializable
    data class Circle(
        @XmlIndex
        override val index: Int = 0,
        @XmlAttribute("fill")
        override val oldHex: String = "",
        override val newHex: String = "",
        @XmlAttribute("cx")
        val cx: String = "0",
        @XmlAttribute("cy")
        val cy: String = "0",
        @XmlAttribute("r")
        val r: String = "0"
    ) : SvgKolorElement {

        override fun copy(newHex: String): Circle {
            return copy(index = index, newHex = newHex)
        }

        override fun toSvgElement(hex: String): String {
            return """<circle cx="$cx" cy="$cy" r="$r" fill="$hex" />"""
        }
    }

    /**
     * Represents an ellipse element in an SVG.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Immutable
    @Serializable
    data class Ellipse(
        @XmlIndex
        override val index: Int = 0,
        @XmlAttribute("fill")
        override val oldHex: String = "",
        override val newHex: String = "",
        @XmlAttribute("cx")
        val cx: String = "0",
        @XmlAttribute("cy")
        val cy: String = "0",
        @XmlAttribute("rx")
        val rx: String = "0",
        @XmlAttribute("ry")
        val ry: String = "0"
    ) : SvgKolorElement {

        override fun copy(newHex: String): Ellipse {
            return copy(index = index, newHex = newHex)
        }

        override fun toSvgElement(hex: String): String {
            return """<ellipse cx="$cx" cy="$cy" rx="$rx" ry="$ry" fill="$hex" />"""
        }
    }

    /**
     * Represents a line element in an SVG.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Immutable
    @Serializable
    data class Line(
        @XmlIndex
        override val index: Int = 0,
        @XmlAttribute("stroke")
        override val oldHex: String = "",
        override val newHex: String = "",
        @XmlAttribute("x1")
        val x1: String = "0",
        @XmlAttribute("y1")
        val y1: String = "0",
        @XmlAttribute("x2")
        val x2: String = "0",
        @XmlAttribute("y2")
        val y2: String = "0"
    ) : SvgKolorElement {

        override fun copy(newHex: String): Line {
            return copy(index = index, newHex = newHex)
        }

        override fun toSvgElement(hex: String): String {
            return """<line x1="$x1" y1="$y1" x2="$x2" y2="$y2" stroke="$hex" />"""
        }
    }

    /**
     * Represents a polyline element in an SVG.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Immutable
    @Serializable
    data class Polyline(
        @XmlIndex
        override val index: Int = 0,
        @XmlAttribute("fill")
        override val oldHex: String = "",
        override val newHex: String = "",
        @XmlAttribute("points")
        val points: String = ""
    ) : SvgKolorElement {

        override fun copy(newHex: String): Polyline {
            return copy(index = index, newHex = newHex)
        }

        override fun toSvgElement(hex: String): String {
            return """<polyline points="$points" fill="$hex" />"""
        }
    }

    /**
     * Represents a polygon element in an SVG.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Immutable
    @Serializable
    data class Polygon(
        @XmlIndex
        override val index: Int = 0,
        @XmlAttribute("fill")
        override val oldHex: String = "",
        override val newHex: String = "",
        @XmlAttribute("points")
        val points: String = ""
    ) : SvgKolorElement {

        override fun copy(newHex: String): Polygon {
            return copy(index = index, newHex = newHex)
        }

        override fun toSvgElement(hex: String): String {
            return """<polygon points="$points" fill="$hex" />"""
        }
    }

    /**
     * Represents a text element in an SVG.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Immutable
    @Serializable
    data class Text(
        @XmlIndex
        override val index: Int = 0,
        @XmlAttribute("fill")
        override val oldHex: String = "",
        override val newHex: String = "",
        @XmlAttribute("x")
        val x: String = "0",
        @XmlAttribute("y")
        val y: String = "0",
        @XmlAttribute("font-size")
        val fontSize: String = "12"
    ) : SvgKolorElement {

        override fun copy(newHex: String): Text {
            return copy(index = index, newHex = newHex)
        }

        override fun toSvgElement(hex: String): String {
            return """<text x="$x" y="$y" font-size="$fontSize" fill="$hex">Text</text>"""
        }
    }
}

/**
 * Represents the root `<svg>` element of an SVG document, containing its viewBox and child
 * elements.
 *
 * This class serves as the top-level container for SVG shapes that can be processed for recoloring.
 * It maps various SVG tags (path, rect, circle, etc.) to their respective [SvgKolorElement]
 * representations.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@XmlElement("svg")
data class SvgRoot(
    @XmlAttribute("viewBox")
    val viewBox: String = "0 0 24 24",
    @XmlElement("path")
    val paths: List<SvgKolorElement.Path> = emptyList(),
    @XmlElement("rect")
    val rects: List<SvgKolorElement.Rect> = emptyList(),
    @XmlElement("circle")
    val circles: List<SvgKolorElement.Circle> = emptyList(),
    @XmlElement("ellipse")
    val ellipses: List<SvgKolorElement.Ellipse> = emptyList(),
    @XmlElement("line")
    val lines: List<SvgKolorElement.Line> = emptyList(),
    @XmlElement("polyline")
    val polylines: List<SvgKolorElement.Polyline> = emptyList(),
    @XmlElement("polygon")
    val polygons: List<SvgKolorElement.Polygon> = emptyList(),
    @XmlElement("text")
    val texts: List<SvgKolorElement.Text> = emptyList()
)