package io.bashpsk.emptylibs.serializationxml

import android.util.Xml
import io.bashpsk.emptylibs.serializationxml.encoding.XmlDecoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

/**
 * Main entry point for XML serialization and deserialization.
 *
 * This object provides the primary API for converting XML data into Kotlin objects.
 * Currently, it supports decoding from XML strings using an underlying [XmlPullParser].
 */
@ExperimentalSerializationApi
object Xml {

    /**
     * The [SerializersModule] used for looking up serializers.
     */
    val serializersModule: SerializersModule = EmptySerializersModule()

    /**
     * Decodes the given XML [content] into an object of type [T].
     *
     * @param T The type to decode into.
     * @param content The XML string to decode.
     * @return The decoded object of type [T].
     */
    inline fun <reified T> decodeFromString(
        content: String
    ): T = serializersModule.serializer<T>().run {

        val parser = Xml.newPullParser().apply {

            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(StringReader(content))

            while (eventType != XmlPullParser.START_TAG && eventType != XmlPullParser.END_DOCUMENT) {
                next()
            }
        }

        deserialize(decoder = XmlDecoder(serialDescriptor = descriptor, parser = parser))
    }
}