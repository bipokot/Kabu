package io.kabu.runtime.exception

/**
 * Describes an error during runtime evaluation of expression
 */
class PatternEvaluationException(message: String?, cause: Throwable? = null) : RuntimeException(message, cause)
