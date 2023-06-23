package io.kabu.backend.exception

import io.kabu.backend.diagnostic.Origin

class PatternParsingException(
    message: String?,
    cause: Throwable?,
    val origin: Origin,
) : KabuException(message, cause)
