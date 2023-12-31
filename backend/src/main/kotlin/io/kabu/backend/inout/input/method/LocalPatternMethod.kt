package io.kabu.backend.inout.input.method

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.parameter.Parameter

class LocalPatternMethod(
    packageName: String,
    name: String,
    returnedType: TypeName,
    receiver: Parameter?,
    parameters: List<Parameter>,
    pattern: String,
    val declaringType: TypeName,
    origin: Origin
) : PatternMethod(
    packageName,
    name,
    returnedType,
    receiver,
    parameters,
    pattern,
    origin
) {

    companion object {

        fun Method.toLocalPatternMethod(declaringType: TypeName, pattern: String) = LocalPatternMethod(
            packageName = packageName,
            name = name,
            returnedType = returnedType,
            receiver = receiver,
            parameters = parameters,
            pattern = pattern,
            declaringType = declaringType,
            origin = origin
        )
    }
}
