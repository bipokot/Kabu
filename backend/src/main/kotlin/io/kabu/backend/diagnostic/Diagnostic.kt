package io.kabu.backend.diagnostic

import io.kabu.backend.exception.PatternProcessingException

class Diagnostic(
    val kind: Kind,
    val message: String,
    val sourceCodeDetails: String?
) {

    override fun toString(): String {
        val sourceCodeDetailsStr = sourceCodeDetails?.let { " ($it)" }.orEmpty()
        return "[$kind] $message$sourceCodeDetailsStr"
    }

    enum class Kind {
        WARNING,
        ERROR,
    }
}

fun diagnosticError(message: String): Nothing {
    throw PatternProcessingException(Diagnostic(Diagnostic.Kind.ERROR, message, null))
}

fun diagnosticError(message: String, vararg origins: Origin?): Nothing {
    val sourceCodeDetails = defaultSourceCodeDetails(origins.filterNotNull())
    throw PatternProcessingException(Diagnostic(Diagnostic.Kind.ERROR, message, sourceCodeDetails))
}

fun diagnosticError(message: String, vararg hasOrigins: HasOrigin?): Nothing {
    val sourceCodeDetails = defaultSourceCodeDetails(hasOrigins.mapNotNull { it?.origin })
    throw PatternProcessingException(Diagnostic(Diagnostic.Kind.ERROR, message, sourceCodeDetails))
}

fun diagnosticError(message: String, origins: Iterable<Origin>): Nothing {
    val sourceCodeDetails = defaultSourceCodeDetails(origins)
    throw PatternProcessingException(Diagnostic(Diagnostic.Kind.ERROR, message, sourceCodeDetails))
}

private fun defaultSourceCodeDetails(origins: Iterable<Origin>): String? {
    if (!origins.iterator().hasNext()) return null
    return origins.joinToString(separator = "\n")
}
