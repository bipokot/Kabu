package io.kabu.backend.exception

open class KabuException(
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause)
