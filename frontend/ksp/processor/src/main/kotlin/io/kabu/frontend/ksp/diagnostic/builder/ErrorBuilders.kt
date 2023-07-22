package io.kabu.frontend.ksp.diagnostic.builder

import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.backend.exception.PatternProcessingException

internal fun unexpectedLocalPatternError(origin: Origin, parentOrigin: Origin?): Nothing {
    diagnosticError(
        "LocalPattern methods must be enclosed in a class/interface",
        origin, parentOrigin
    )
}

internal fun parameterProcessingError(
    name: String,
    methodName: String,
    e: PatternProcessingException,
    parameterOrigin: Origin,
): Nothing {
    diagnosticError(
        "Error while processing parameter '$name' of function '$methodName': " + e.message,
        parameterOrigin
    )
}

internal fun notSupportedError(message: String, origin: Origin): Nothing {
    diagnosticError(message, origin)
}
