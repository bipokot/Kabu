package io.kabu.backend.provider.evaluation

import com.squareup.kotlinpoet.CodeBlock

/**
 * Way of child provider retrieval
 *
 * @property codeBlock retrieval code
 * @property isReentrant if provider retrieval code may be executed more than once
 * (e.g. lambdas should be invoked only once)
 */
data class RetrievalWay(
    val codeBlock: CodeBlock,
    val isReentrant: Boolean,
)
