package io.bashpsk.emptylibs.serializationxml.annotation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo

/**
 * Marks a property or class to be serialized as an XML element.
 *
 * @property name The name of the XML element. If empty, the property name or class name is used.
 */
@ExperimentalSerializationApi
@SerialInfo
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class XmlElement(val name: String = "")

/**
 * Marks a property to be serialized as an XML attribute.
 *
 * @property name The name of the XML attribute. If empty, the property name is used.
 */
@ExperimentalSerializationApi
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class XmlAttribute(val name: String = "")

/**
 * Marks a property to store the current line number of the XML parser.
 * Useful for mapping XML elements back to their source line.
 */
@ExperimentalSerializationApi
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class XmlIndex