package io.kabu.backend.inout.output

import io.kabu.backend.diagnostic.Diagnostic


sealed class ProcessingOutput {
    abstract val diagnostics: List<Diagnostic>

    data class Success(override val diagnostics: List<Diagnostic> = emptyList()) : ProcessingOutput()
    data class Failure(override val diagnostics: List<Diagnostic> = emptyList()) : ProcessingOutput()
}

