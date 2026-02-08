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

    override val serializersModule: SerializersModule = Xml.serializersModule

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {

        when (descriptor.kind) {

            StructureKind.LIST -> {

                if (tagName == null) return CompositeDecoder.DECODE_DONE
                if (findNextTag(tagName)) return elementIndex++

                return CompositeDecoder.DECODE_DONE
            }

            else -> {}
        }

        if (elementIndex >= descriptor.elementsCount) return CompositeDecoder.DECODE_DONE

        return elementIndex++
    }

    override fun decodeString(): String {

        val index = elementIndex - 1
        val annotations = serialDescriptor.getElementAnnotations(index)

        annotations.filterIsInstance<XmlAttribute>().firstOrNull()?.let { attribute ->

            val name = attribute.name.ifEmpty { serialDescriptor.getElementName(index) }

            return parser.getAttributeValue(null, name) ?: ""
        }

        return ""
    }

    override fun decodeInt(): Int {

        val index = elementIndex - 1
        val annotations = serialDescriptor.getElementAnnotations(index)

        if (annotations.any { annotation -> annotation is XmlIndex }) return currentIndex

        return decodeString().toIntOrNull() ?: 0
    }

    override fun decodeFloat(): Float = decodeString().toFloatOrNull() ?: 0F

    override fun decodeDouble(): Double = decodeString().toDoubleOrNull() ?: 0.0

    override fun decodeBoolean(): Boolean = decodeString().toBoolean()

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {

        var nextTagName: String? = null
        var nextIndex = currentIndex

        when {

            elementIndex > 0 -> {

                val index = elementIndex - 1
                val annotations = serialDescriptor.getElementAnnotations(index)

                annotations.filterIsInstance<XmlElement>().firstOrNull()?.let { element ->

                    nextTagName = element.name.ifEmpty { serialDescriptor.getElementName(index) }
                }

                when (serialDescriptor.kind) {

                    StructureKind.LIST -> {

                        nextTagName = tagName
                        nextIndex = index
                    }

                    else -> {}
                }
            }
        }

        return XmlDecoder(
            serialDescriptor = descriptor,
            parser = parser,
            tagName = nextTagName,
            currentIndex = nextIndex
        )
    }

    private fun findNextTag(name: String): Boolean {

        var eventType = parser.eventType

        if (eventType == XmlPullParser.START_TAG && parser.name == name && elementIndex == 0) {
            return true
        }

        while (eventType != XmlPullParser.END_DOCUMENT) {

            eventType = parser.next()
            if (eventType == XmlPullParser.START_TAG && parser.name == name) return true
        }

        return false
    }
}