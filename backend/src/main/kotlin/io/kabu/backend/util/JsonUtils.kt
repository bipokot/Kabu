package io.kabu.backend.util

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import java.io.IOException


private val objectMapper: ObjectMapper by lazy {
    ObjectMapper()
        .registerModule(KotlinModule())
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
//            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
//            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
//            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//            .registerModule(KotlinModule())
//            .registerModule(JsonOrgModule())

}

private val objectWriter by lazy {
    val indenter = DefaultIndenter("  ", DefaultIndenter.SYS_LF)
    val printer = DefaultPrettyPrinter().apply {
        indentObjectsWith(indenter)
        indentArraysWith(indenter)
    }
    objectMapper.writer(printer)
}


fun Any.toJSON(): String {
    try {
        return objectWriter.writeValueAsString(this)
    } catch (e: JsonProcessingException) {
        throw RuntimeException(e)
    }
}

fun <T> String.fromJSON(typeReference: TypeReference<T>): T {
    try {
        return objectMapper.readValue<T>(this, typeReference)
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}

inline fun <reified T> String.fromJSON(): T {
    return fromJSON(object : TypeReference<T>() {})
}

fun <T> load(path: String, typeReference: TypeReference<T>): T {
    val contents = File(path).readText()
    return contents.fromJSON(typeReference)
}

inline fun <reified T> load(path: String): T {
    val contents = File(path).readText()
    return contents.fromJSON(object : TypeReference<T>() {})
}

fun <T : Any> save(obj: T, path: String) {
    File(path).writeText(obj.toJSON())
}