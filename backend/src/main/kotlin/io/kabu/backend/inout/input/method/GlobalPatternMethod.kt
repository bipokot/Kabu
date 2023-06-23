package io.kabu.backend.inout.input.method

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.parameter.EntryParameter

class GlobalPatternMethod(
    packageName: String,
    name: String,
    returnedType: TypeName,
    receiverType: TypeName?,
    parameters: List<EntryParameter>,
    pattern: String,
    origin: Origin
) : PatternMethod(
    packageName,
    name,
    returnedType,
    receiverType,
    parameters,
    pattern,
    origin
) {

    companion object {

        fun Method.toGlobalPatternMethod(pattern: String) = GlobalPatternMethod(
            packageName = packageName,
            name = name,
            returnedType = returnedType,
            receiverType = receiverType,
            parameters = parameters,
            pattern = pattern,
            origin = origin
        )
    }
}
