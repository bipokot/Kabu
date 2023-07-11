package io.kabu.backend.parameter

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.diagnostic.Origin

class Parameter(
    val name: String,
    val type: TypeName,
    val origin: Origin? = null,
) {
    override fun toString(): String {
        val sourceLocationPart = origin?.sourceLocation?.toString()?.let { " ($it)" } ?: ""
        return "'$name: $type'$sourceLocationPart"
    }
}
