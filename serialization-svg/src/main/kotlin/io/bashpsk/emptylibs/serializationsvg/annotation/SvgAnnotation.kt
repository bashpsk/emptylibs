package io.bashpsk.emptylibs.serializationsvg.annotation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo

@ExperimentalSerializationApi
@SerialInfo
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class SvgElement(val name: String = "")

@ExperimentalSerializationApi
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class SvgAttribute(val name: String = "")

@ExperimentalSerializationApi
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class SvgIndex