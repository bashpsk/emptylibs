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