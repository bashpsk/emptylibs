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

@OptIn(ExperimentalSerializationApi::class)
class XmlDecoder(
    private val serialDescriptor: SerialDescriptor,
    private val parser: XmlPullParser,
    private val tagName: String? = null,
    private val currentIndex: Int = 0
) : AbstractDecoder() {

    private var elementIndex = 0
    private var currentLineNumber = 0

    override val serializersModule: SerializersModule = Xml.serializersModule

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

    override fun decodeInt(): Int {

        return when {

            serialDescriptor.getElementAnnotations(
                elementIndex - 1
            ).any { annotation -> annotation is XmlIndex } -> currentLineNumber

            else -> decodeString().toIntOrNull() ?: 0
        }
    }

    override fun decodeFloat(): Float = decodeString().toFloatOrNull() ?: 0F

    override fun decodeDouble(): Double = decodeString().toDoubleOrNull() ?: 0.0

    override fun decodeBoolean(): Boolean = decodeString().toBoolean()

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