package io.kabu.backend.inout.input.method

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.parameter.Parameter

class GlobalPatternMethod(
    packageName: String,
    name: String,
    returnedType: TypeName,
    receiver: Parameter?,
    parameters: List<Parameter>,
    pattern: String,
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

        fun Method.toGlobalPatternMethod(pattern: String) = GlobalPatternMethod(
            packageName = packageName,
            name = name,
            returnedType = returnedType,
            receiver = receiver,
            parameters = parameters,
            pattern = pattern,
            origin = origin
        )
    }
}
