package io.kabu.runtime

import io.kabu.runtime.exception.PatternEvaluationException
import java.util.*

open class WatcherContext {

    val stack: Stack<Any?> = Stack<Any?>()

    @Suppress("UNCHECKED_CAST")
    fun <T> safeCast(value: Any?): T =
        value as? T ?: throw PatternEvaluationException("Unexpected type of $this in watcher stack.")
}
