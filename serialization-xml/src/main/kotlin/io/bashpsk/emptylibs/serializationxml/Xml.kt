package io.bashpsk.emptylibs.serializationxml

import android.util.Xml
import io.bashpsk.emptylibs.serializationxml.encoding.XmlDecoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

@ExperimentalSerializationApi
object Xml {

    val serializersModule: SerializersModule = EmptySerializersModule()

    inline fun <reified T> decodeFromString(string: String): T {

        val serializer = serializersModule.serializer<T>()

        val parser = Xml.newPullParser().apply {

            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(StringReader(string))

            var eventType = eventType

            while (eventType != XmlPullParser.START_TAG && eventType != XmlPullParser.END_DOCUMENT) {

                eventType = next()
            }
        }

        val decoder = XmlDecoder(serialDescriptor = serializer.descriptor, parser = parser)

        return serializer.deserialize(decoder = decoder)
    }
}