package io.bashpsk.emptylibs.serializationxml.encoding

import io.bashpsk.emptylibs.serializationxml.Xml
import io.bashpsk.emptylibs.serializationxml.annotation.XmlAttribute
import io.bashpsk.emptylibs.serializationxml.annotation.XmlElement
import io.bashpsk.emptylibs.serializationxml.annotation.XmlIndex
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule
import org.xmlpull.v1.XmlPullParser

/**
 * A [kotlinx.serialization.encoding.Decoder] that decodes from an [XmlPullParser].
 *
 * @property serialDescriptor The descriptor of the type being decoded.
 * @property parser The [XmlPullParser] used to read the XML content.
 * @property tagName The name of the XML tag currently being decoded, if any.
 * @property currentIndex The index of the current element in the composite structure.
 */
@OptIn(ExperimentalSerializationApi::class)
class XmlDecoder(
    private val serialDescriptor: SerialDescriptor,
    private val parser: XmlPullParser,
    private val tagName: String? = null,
    private val currentIndex: Int = 0
) : AbstractDecoder() {

    /**
     * The index of the element currently being decoded.
     */
    private var elementIndex = 0

    /**
     * The line number of the last decoded element.
     */
    private var currentLineNumber = 0

    /**
     * The [SerializersModule] used for looking up serializers.
     */
    override val serializersModule: SerializersModule = Xml.serializersModule

    /**
     * Returns the index of the next element to be decoded.
     *
     * @param descriptor The descriptor of the structure being decoded.
     * @return The index of the next element, or [CompositeDecoder.DECODE_DONE] if there are no more
     * elements.
     */
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {

        when (descriptor.kind) {

            StructureKind.LIST -> {

                tagName?.takeIf { tag -> findNextTag(name = tag) }?.run {

                    currentLineNumber = parser.lineNumber
                    return elementIndex++
                }

                return CompositeDecoder.DECODE_DONE
            }

            else -> currentLineNumber = parser.lineNumber
        }

        if (elementIndex >= descriptor.elementsCount) return CompositeDecoder.DECODE_DONE

        return elementIndex++
    }

    /**
     * Decodes a string value from the current XML attribute.
     *
     * @return The decoded string value.
     */
    override fun decodeString(): String {

        val index = elementIndex - 1

        return serialDescriptor.getElementAnnotations(
            index
        ).filterIsInstance<XmlAttribute>().firstOrNull()?.let { attribute ->

            parser.getAttributeValue(
                null,
                attribute.name.ifEmpty { serialDescriptor.getElementName(index) }
            )
        } ?: ""
    }

    /**
     * Decodes an integer value.
     * If the element is marked with [XmlIndex], returns the current line number.
     *
     * @return The decoded integer value.
     */
    override fun decodeInt(): Int {

        return when {

            serialDescriptor.getElementAnnotations(
                elementIndex - 1
            ).any { annotation -> annotation is XmlIndex } -> currentLineNumber

            else -> decodeString().toIntOrNull() ?: 0
        }
    }

    /**
     * Decodes a float value.
     *
     * @return The decoded float value.
     */
    override fun decodeFloat(): Float = decodeString().toFloatOrNull() ?: 0F

    /**
     * Decodes a double value.
     *
     * @return The decoded double value.
     */
    override fun decodeDouble(): Double = decodeString().toDoubleOrNull() ?: 0.0

    /**
     * Decodes a boolean value.
     *
     * @return The decoded boolean value.
     */
    override fun decodeBoolean(): Boolean = decodeString().toBoolean()

    /**
     * Begins decoding a structure (class or list).
     *
     * @param descriptor The descriptor of the structure to decode.
     * @return A [CompositeDecoder] for the structure.
     */
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {

        val nextIndex = (elementIndex - 1).takeIf { index -> index >= 0 }

        val nextTagName = nextIndex?.let { index ->

            when (serialDescriptor.kind) {

                StructureKind.LIST -> tagName

                else -> serialDescriptor.getElementAnnotations(
                    index
                ).filterIsInstance<XmlElement>().firstOrNull()?.let { element ->

                    element.name.ifEmpty { serialDescriptor.getElementName(index) }
                }
            }
        }

        return XmlDecoder(
            serialDescriptor = descriptor,
            parser = parser,
            tagName = nextTagName,
            currentIndex = nextIndex ?: currentIndex
        )
    }

    /**
     * Advances the parser to the next start tag with the given [name].
     *
     * @param name The name of the tag to find.
     * @return `true` if a tag with the given name was found, `false` otherwise.
     */
    private fun findNextTag(name: String): Boolean {

        if (parser.eventType == XmlPullParser.START_TAG && parser.name == name && elementIndex == 0) {
            return true
        }

        while (parser.eventType != XmlPullParser.END_DOCUMENT) {

            if (parser.next() == XmlPullParser.START_TAG && parser.name == name) return true
        }

        return false
    }
}