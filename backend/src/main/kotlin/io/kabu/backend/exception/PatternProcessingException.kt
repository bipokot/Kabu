package io.kabu.backend.exception

import io.kabu.backend.diagnostic.Diagnostic

class PatternProcessingException(
    val diagnostic: Diagnostic,
) : KabuException(diagnostic.message)
