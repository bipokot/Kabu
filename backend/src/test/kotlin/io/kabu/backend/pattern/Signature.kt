package io.kabu.backend.pattern

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.parameter.Parameter

/** Result of parsing the signature string of test string  */
data class Signature(
    val receiverType: TypeName?,
    val parameters: List<Parameter>,
    val returnedType: TypeName
)
