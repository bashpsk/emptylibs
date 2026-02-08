package io.bashpsk.emptylibs.serializationxml.annotation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo

@ExperimentalSerializationApi
@SerialInfo
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class XmlElement(val name: String = "")

@ExperimentalSerializationApi
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class XmlAttribute(val name: String = "")

@ExperimentalSerializationApi
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class XmlIndex