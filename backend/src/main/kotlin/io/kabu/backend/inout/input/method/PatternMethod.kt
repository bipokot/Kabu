package io.kabu.backend.inout.input.method

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.parameter.Parameter

abstract class PatternMethod(
    packageName: String,
    name: String,
    returnedType: TypeName,
    receiverType: TypeName?,
    parameters: List<Parameter>,
    val pattern: String, //todo get origin of pattern annotation as well
    origin: Origin
) : Method(
    packageName,
    name,
    returnedType,
    receiverType,
    parameters,
    origin
)
